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

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import com.example.android.todolist.adapters.DemoHeaderFooterAdapter;
import com.example.android.todolist.adapters.OnListItemClickMessageListener;
import com.example.android.todolist.adapters.SimpleDemoItemAdapter;
import com.example.android.todolist.adapters.SwipeToDeleteCallback;
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
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

/*
 * This example shows very very minimal implementation of header and footer feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppDatabase mDb;
    private List<TaskEntry> listTaskEntries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_header_footer);

        OnListItemClickMessageListener clickListener = new OnListItemClickMessageListener() {
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

            @Override
            public void onItemClicked(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        };

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new SimpleDemoItemAdapter(clickListener);

        setUpViewModel(adapter);
        adapter = new DemoHeaderFooterAdapter(adapter, clickListener);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enableSwipeToDelete(recyclerView);

        setFabButton();
        mDb = AppDatabase.getInstance(getApplicationContext());
    }


    private void PerformFiltering(boolean todo, boolean done, boolean projekt) {
        if(todo)
        {
            Toast.makeText(getApplicationContext(), "Todo", Toast.LENGTH_SHORT).show();
        }else if(done)
        {
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Projekt", Toast.LENGTH_SHORT).show();
        }

    }

    private void enableSwipeToDelete(RecyclerView recyclerView) {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        //Es funktioniert also lass ich das so mit "position -1"
                        mDb.taskDao().deleteTask(listTaskEntries.get(position-1));
                    }
                });
                //Toast.makeText(getApplicationContext(), "Swiped", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void setFabButton() {
    /*
    Set the Floating Action Button (FAB) to its corresponding View.
    Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
    to launch the AddTaskActivity.
    */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(view -> {
            // Create a new intent to start an AddTaskActivity
            Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(addTaskIntent);
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
                        taskEntry.setId(itemId);
                        taskEntry.setStatus(getStatus(taskEntry, aktionString));
                        mDb.taskDao().updateTask(taskEntry);
                    }
                });
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

    private void setUpViewModel(RecyclerView.Adapter adapter) {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        RecyclerView.Adapter finalAdapter = adapter;
        viewModel.getTasks().observe(this, taskEntries -> {
            //Hier wird die Liste sortiert nach dem Namen der Kategorie der Routen
            Collections.sort(taskEntries, (o1, o2) -> o1.getKategorieWall().compareTo(o2.getKategorieWall()));

            ((SimpleDemoItemAdapter) finalAdapter).setTasks(taskEntries);
            listTaskEntries = taskEntries;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}