package com.example.android.todolist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

// Make this class extend ViewModel
public class AddTaskViewModel extends ViewModel {
    // Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<TaskEntry> task;

    // Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId

    public AddTaskViewModel(AppDatabase appDatabase, int taskID) {
        task = appDatabase.taskDao().loadTaskById(taskID);
    }


    // Create a getter for the task variable
    public LiveData<TaskEntry> getTask() {
        return task;
    }
}
