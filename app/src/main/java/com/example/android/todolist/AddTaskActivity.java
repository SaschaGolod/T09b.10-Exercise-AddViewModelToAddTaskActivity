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
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import java.util.Date;


public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int Lvl1 = 1;
    public static final int Lvl2 = 2;
    public static final int Lvl3 = 3;
    public static final int Lvl4 = 4;
    public static final int Lvl5 = 5;
    public static final int Lvl6 = 6;

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    Spinner mStoneColor;
    String mStoneColorString;
    Integer mColorInteger; //<--das könnte ich vielleicht nicht brauchen?
    Spinner mwallStart;
    Spinner mwallEnd;
    String mWallStart;
    String mWallEnd;

    private int mTaskId = DEFAULT_TASK_ID;

    // Member variable for the Database
    private AppDatabase mDb;
    private String compareValue;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                final LiveData<TaskEntry> task = mDb.taskDao().loadTaskById(mTaskId);
                // Declare a AddTaskViewModelFactory using mDb and mTaskId
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                // Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                final AddTaskViewModel viewModel = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);
                // for that use the factory created above AddTaskViewModel

                // Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(@Nullable TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        Log.d(TAG, "Receiving database update from LiveData");
                        populateUI(taskEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
        mwallStart = findViewById(R.id.spinnerStart);
        mwallEnd = findViewById(R.id.spinnerEnd);
        mStoneColor = findViewById(R.id.spinner_color);
        SpinnerInflate();

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void SpinnerInflate() {
        //Spinner für Steinfarben
        ArrayAdapter<CharSequence> adapterColors = ArrayAdapter.createFromResource(this, R.array.mStoneColors, android.R.layout.simple_spinner_item);
        adapterColors.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mStoneColor.setAdapter(adapterColors);
        mStoneColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStoneColorString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner für Wandbereiche
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Walls, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mwallStart.setAdapter(adapter);
        mwallEnd.setAdapter(adapter);
        mwallStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWallStart = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mwallEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWallEnd = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }

        mEditText.setText(task.getName());
        setPriorityInViews(task.getPriority());

        //Hier wird der Spinner von der Steinfarbe gesetzt, wenn im Modus Update gesetzt.
        compareValue = task.getStoneColor();
        if (compareValue != null) {
            ArrayAdapter<CharSequence> adapterColors = ArrayAdapter.createFromResource(this, R.array.mStoneColors, android.R.layout.simple_spinner_item);
            adapterColors.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            int spinnerPosition = adapterColors.getPosition(compareValue);

            mStoneColor.setSelection(spinnerPosition);
        }

        //Hier wird der Spinner von der Startwand gesetzt, wenn im Modus Update gesetzt.
        compareValue = task.getStartWall();
        if (compareValue != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Walls, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            int spinnerPosition = adapter.getPosition(compareValue);

            mwallStart.setSelection(spinnerPosition);
        }

        //Hier wird der Spinner von der Startwand gesetzt, wenn im Modus Update gesetzt.
        compareValue = task.getEndWall();
        if (compareValue != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Walls, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            int spinnerPosition = adapter.getPosition(compareValue);

            mwallEnd.setSelection(spinnerPosition);
        }
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = getNameFromView();
        int priority = getPriorityFromViews();
        Date date = new Date();

        final TaskEntry task = new TaskEntry(description, priority, "tags", getResources().getString(R.string.boulderitem_status_default_value), "score", mStoneColorString, mWallStart, mWallEnd, "setter", date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    mDb.taskDao().insertTask(task);
                } else {
                    //update task
                    task.setId(mTaskId);
                    mDb.taskDao().updateTask(task);
                }
                finish();
            }
        });
    }

    private String getNameFromView() {

        if (mEditText.getText().toString().isEmpty()) {
            return "^";
        } else {
            return mEditText.getText().toString();
        }
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = Lvl1;
                break;
            case R.id.radButton2:
                priority = Lvl2;
                break;
            case R.id.radButton3:
                priority = Lvl3;
                break;
            case R.id.radButton4:
                priority = Lvl4;
                break;
            case R.id.radButton5:
                priority = Lvl5;
                break;
            case R.id.radButton6:
                priority = Lvl6;
                break;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case Lvl1:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case Lvl2:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case Lvl3:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
                break;
            case Lvl4:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton4);
                break;
            case Lvl5:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton5);
                break;
            case Lvl6:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton6);
        }
    }
}
