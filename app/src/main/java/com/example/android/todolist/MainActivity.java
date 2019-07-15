/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.todolist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, new CustomItemClickListener() {

            @Override
            public void onItemClickListener(int itemId) {
                // Launch AddTaskActivity adding the itemId as an extra in the intent
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
                startActivity(intent);
            }

            @Override
            public void OnFlashClicked(int itemId) {
                PerformOnItemClicked(itemId, getString(R.string.ClickflashhStatus));
            }

            @Override
            public void OnTopClicked(int itemId) {
                PerformOnItemClicked(itemId, getString(R.string.ClicktopStatus));
            }

            @Override
            public void OnProjektClicked(int itemId) {
                PerformOnItemClicked(itemId, getString(R.string.ClickprojektStatus));
            }
        });
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks = mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();

        setupSharedPreferences();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    private String setupSharedPreferences() {
        String lvlfilter = "";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_1), true))
        {
            lvlfilter += "1";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_2), true))
        {
            lvlfilter += "2";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_3), true))
        {
            lvlfilter += "3";
        }if(sharedPreferences.getBoolean(getString(R.string.key_lvl_4), true))
        {
            lvlfilter += "4";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_5), true))
        {
            lvlfilter += "5";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_6), true))
        {
            lvlfilter += "6";
        }
        return lvlfilter;
    }

    private void setupViewModel() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                //Hier wird die Liste sortiert nach dem Namen der Kategorie der Routen
                Collections.sort(taskEntries, new Comparator<TaskEntry>() {
                    @Override
                    public int compare(TaskEntry o1, TaskEntry o2) {
                        return o1.getKategorieWall().compareTo(o2.getKategorieWall());
                    }
                });
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");

                mAdapter.setTasks(taskEntries);
                String lvlfilter = setupSharedPreferences();

                CreateJsonAndFilter(lvlfilter);
            }
        });
    }

    public void PerformOnItemClicked(final int itemId, final String aktionString) {

        final LiveData<TaskEntry> task = mDb.taskDao().loadTaskById(itemId);

        task.observe(this, new Observer<TaskEntry>() {

            @Override
            public void onChanged(@Nullable final TaskEntry taskEntry) {
                task.removeObserver(this);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "TaskId of Task is:" + taskEntry.getId());
                        Log.d(TAG, "Old Projekt status:" + taskEntry.getStatus());
                        Log.d(TAG, "Change Projekt status to:" + getStatus(taskEntry, aktionString));
                        taskEntry.setId(itemId);
                        taskEntry.setStatus(getStatus(taskEntry, aktionString));
                        mDb.taskDao().updateTask(taskEntry);
                    }
                });
                Log.d(TAG, "Update performed");
            }

            private String getStatus(TaskEntry taskEntry, String aktionString) {
                if (!taskEntry.getStatus().contains(aktionString)) {
                    return aktionString;
                } else {
                    return "x";
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        if(id == R.id.test_screen)
        {
            Intent startTestActity = new Intent(this, HeaderFooterActivity.class);
            startActivity(startTestActity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String lvlfilter = "";

        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_1), true))
        {
            lvlfilter += "1";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_2), true))
        {
            lvlfilter += "2";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_3), true))
        {
            lvlfilter += "3";
        }if(sharedPreferences.getBoolean(getString(R.string.key_lvl_4), true))
        {
            lvlfilter += "4";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_5), true))
        {
            lvlfilter += "5";
        }
        if(sharedPreferences.getBoolean(getString(R.string.key_lvl_6), true))
        {
            lvlfilter += "6";
        }

        CreateJsonAndFilter(lvlfilter);
    }

    private void CreateJsonAndFilter(String lvlfilter) {

        //Todo hier muss noch ein Filter für Todo
        JSONObject jsonData = new JSONObject();
        try{
            jsonData.put(getString(R.string.pref_key_all_lvl),lvlfilter);
            //Todo mache x den Wert der Buttons oben mit x, p, d,
            jsonData.put(getString(R.string.pref_key_status), getResources().getString(R.string.boulderitem_status_default_value));
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Fehler filter", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Json Data is: " + jsonData.toString());

        mAdapter.getFilter().filter(jsonData.toString());
    }
}

