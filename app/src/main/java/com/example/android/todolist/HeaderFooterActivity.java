package com.example.android.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.android.todolist.adapters.AbstractHeaderFooterWrapperAdapter;
import com.example.android.todolist.adapters.DemoHeaderFooterAdapter;
import com.example.android.todolist.adapters.MyItemFilteringAdapter;
import com.example.android.todolist.adapters.OnListItemClickMessageListener;
import com.example.android.todolist.adapters.SimpleDemoItemAdapter;
import com.example.android.todolist.adapters.SwipeToDeleteCallback;
import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

/*
 * This example shows very very minimal implementation of header and footer feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */
public class HeaderFooterActivity extends AppCompatActivity {

    //Todo zuerst Swipe to Deleate programmieren, danach kommmt filtering.
    //private MyItemFilteringAdapter mFilteringAdapter;
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
                Intent intent = new Intent(HeaderFooterActivity.this, AddTaskActivity.class);
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
            public void startActivitySettings() {
                PerformStartFilterActivity();
            }
        };

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

        RecyclerView.Adapter adapter = new SimpleDemoItemAdapter(clickListener);

        //Todo kommentar entfernen wenn es so weit ist.
        //adapter = mFilteringAdapter = new MyItemFilteringAdapter(adapter);
        setUpViewModel(adapter);
        adapter = new DemoHeaderFooterAdapter(adapter, clickListener);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enableSwipeToDelete(recyclerView);

        setFabButton();


        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void PerformStartFilterActivity() {
        Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsActivity);
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
                    Toast.makeText(getApplicationContext(), "Swiped", Toast.LENGTH_SHORT).show();

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
            Intent addTaskIntent = new Intent(HeaderFooterActivity.this, AddTaskActivity.class);
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

        if (id == R.id.test_screen) {
            Intent startTestActity = new Intent(this, HeaderFooterActivity.class);
            startActivity(startTestActity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}