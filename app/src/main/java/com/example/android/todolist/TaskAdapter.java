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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.database.TaskEntry;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> implements Filterable {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private CustomItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context

    //erstes element von orginalData ist ein leeres element
    private List<TaskEntry> originalData;
    private List<TaskEntry> filteredData;
    private Context mContext;
    private String statusFiltering;

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public TaskAdapter(Context context, CustomItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(viewType, parent, false);

       return new TaskViewHolder(view);

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            //Todo später kann hier man noch die Top Layout hinzufügen
            return R.layout.task_layout_top;
            //return R.layout.task_layout;
        } else {
            return R.layout.task_layout;
        }
        //return super.getItemViewType(position);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        if(position == 0)
        {
            //CheckBox checkBox1 = find
        }

        // Determine the values of the wanted data
        TaskEntry taskEntry = filteredData.get(position);
        String description = taskEntry.getName();
        int priority = taskEntry.getPriority();
        String startWall = taskEntry.getStartWall();
        String endWall = taskEntry.getEndWall();
        String tags = taskEntry.getTags();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        String setter = taskEntry.getSetter();
        String stoneColorName = taskEntry.getStoneColor();


        //Set values
        holder.stone_color.setImageResource(getImageColor(stoneColorName));
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.wallTowall.setText(startWall + " -->\n" + endWall);
        holder.setter.setText(setter);
        holder.tags.setText(tags);

        if (taskEntry.getStatus().equals(mContext.getString(R.string.ClickflashhStatus))) {
            holder.btn_flash.setImageResource(R.drawable.flash_on);
        } else {
            holder.btn_flash.setImageResource(R.drawable.flash_off);
        }
        if (taskEntry.getStatus().equals(mContext.getString(R.string.ClicktopStatus))) {
            holder.btn_top.setImageResource(R.drawable.top_on);
        } else {
            holder.btn_top.setImageResource(R.drawable.top_off);
        }
        if (taskEntry.getStatus().equals(mContext.getString(R.string.ClickprojektStatus))) {
            holder.btn_projekt.setImageResource(R.drawable.projekt_on);
        } else {
            holder.btn_projekt.setImageResource(R.drawable.projekt_off);
        }


        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    }



    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (filteredData == null) {
            return 0;
        }
        return filteredData.size();
    }




    public List<TaskEntry> getTasks() {
        return filteredData;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskEntry> taskEntries) {
        //Todo die Toolbar sinnvoll in den RecyclerView einbinden.
        //1 extra TaskEntry only for the Toolbar at the Top
        //Date date = new Date();
        //TaskEntry task = new TaskEntry("toolbar", 1, "toolbar", "status", "toolbar", "toolbar", "toolbar",  "toolbar", "toolbar", date );
        //filteredData.add(task);
        //originalData.add(task);


        filteredData = taskEntries;
        originalData = taskEntries;
        notifyDataSetChanged();
    }

    //Todo Filter für Schwierigkeitsgrade implementieren :)
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = "";
                String filterStatus  = "";

                try{
                    JSONObject aJson = new JSONObject((String) constraint);

                    filterString = aJson.getString(mContext.getString(R.string.pref_key_all_lvl));
                    filterStatus = aJson.getString(mContext.getResources().getString(R.string.pref_key_status));
                    Log.d("TaskAdapter", "filterString is: " + filterString);
                    Log.d("TaskAdapter", "filterStatus is: " + filterStatus);
                }catch (Exception E)
                {
                    Toast.makeText(mContext, "Fehler Adapter", Toast.LENGTH_SHORT).show();
                    Log.d("TaskAdapter", "Fehler filtersting is: " + filterString);
                }
                FilterResults results1 = new FilterResults();

                List<TaskEntry> list = new ArrayList<>(originalData);

                int count = list.size();
                final List<TaskEntry> nlist = new ArrayList<>();

                String itemLvl;
                String itemStatus;

                for (int i = 0; i < count; i++) {
                    itemLvl = String.valueOf(list.get(i).getPriority());
                    itemStatus = String.valueOf(list.get(i).getStatus().toLowerCase());
                    if (filterString.toLowerCase().contains(itemLvl)) {
                        //todo hier könnte man noch den filter für todo projekt done machen
                        //filterStatus.toLowerCase().contains(itemStatus)
                        if (true) {
                            nlist.add(list.get(i));
                        }
                    }
                }

                results1.values = nlist;
                results1.count = nlist.size();

                return results1;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<TaskEntry>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /*
    Die Namen von den Strings werden im Strings.xml unter mStoneColors gespeichert. Die Farben werden in der colors.xml unter der
    StoneColor gespeichert.
     */
    private int getImageColor(String stoneColorName) {
        if (stoneColorName.equals("weiß")) {
            return R.color.Stone_weiß;
        }
        if (stoneColorName.equals("gelb")) {
            return R.color.Stone_gelb;
        }
        if (stoneColorName.equals("hellgruen")) {
            return R.color.Stone_hellgruen;
        }
        if (stoneColorName.equals("hellblue")) {
            return R.color.Stone_hellblue;
        }
        if (stoneColorName.equals("blue")) {
            return R.color.Stone_blue;
        }
        if (stoneColorName.equals("rot")) {
            return R.color.Stone_rot;
        }
        if (stoneColorName.equals("schwarzer")) {
            return R.color.Stone_schwarzer;
        }
        if (stoneColorName.equals("lila")) {
            return R.color.Stone_lila;
        }
        if (stoneColorName.equals("orange")) {
            return R.color.Stone_orange;
        }
        if (stoneColorName.equals("pink")) {
            return R.color.Stone_pink;
        }

        return R.color.materialBlue;
    }

    /*
    Helper method for selecting the correct priority circle color.
    P1 = white, P2 = yellow, P3 = green, P4 = blue, P5 = red, P6 = black
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialWhite);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialGreen);
                break;
            case 4:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialBlue);
                break;
            case 5:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 6:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialBlack);
                break;
            default:
                break;
        }
        return priorityColor;
    }


/*
    //Todo für später nachdem Schwierigkeitsfilter funktioniert.
    // Inner class for creating ViewHolders
    public class TopViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxTodo;
        CheckBox checkBoxDone;
        CheckBox checkBoxProjekt;

        public TopViewHolder(View itemView) {
            super(itemView);

            checkBoxTodo = itemView.findViewById(R.id.checkBox);
            checkBoxDone = itemView.findViewById(R.id.checkBox2);
            checkBoxProjekt = itemView.findViewById(R.id.checkBox3);

            checkBoxTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBoxTodo.isChecked()) {
                        checkBoxDone.setChecked(false);
                        checkBoxProjekt.setChecked(false);
                    }else {
                        checkBoxProjekt.setChecked(false);
                    }
                }
            });

            checkBoxDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBoxDone.isChecked()) {
                        checkBoxProjekt.setChecked(false);
                        checkBoxTodo.setChecked(false);
                    }
                }
            });

            checkBoxProjekt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBoxProjekt.isChecked()) {
                        checkBoxTodo.setChecked(true);
                        checkBoxDone.setChecked(false);
                    }
                }
            });

        }
    }*/

    // Inner class for creating ViewHolders
    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        //Todo Shared Preferences für Filter der Schwierigkeitsgrade einführen.
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;
        TextView wallTowall;
        TextView tags;
        TextView setter;
        ImageView stone_color;
        ImageView btn_flash;
        ImageView btn_top;
        ImageView btn_projekt;
        int elementId;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            btn_flash = itemView.findViewById(R.id.btn_flash);
            btn_top = itemView.findViewById(R.id.btn_top);
            btn_projekt = itemView.findViewById(R.id.btn_projekt);
            stone_color = itemView.findViewById(R.id.stomeColor);
            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            tags = itemView.findViewById(R.id.tags);
            setter = itemView.findViewById(R.id.setter);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);
            wallTowall = itemView.findViewById(R.id.taskWallToWall);
            itemView.setOnClickListener(this);

            btn_flash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int elementId = filteredData.get(getAdapterPosition()).getId();
                        mItemClickListener.OnFlashClicked(elementId);
                    }
                }
            });
            btn_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elementId = filteredData.get(getAdapterPosition()).getId();
                    mItemClickListener.OnTopClicked(elementId);
                }
            });
            btn_projekt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elementId = filteredData.get(getAdapterPosition()).getId();
                    mItemClickListener.OnProjektClicked(elementId);
                }
            });
        }


        @Override
        public void onClick(View view) {
            elementId = filteredData.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}