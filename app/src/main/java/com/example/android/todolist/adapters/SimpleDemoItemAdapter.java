package com.example.android.todolist.adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.todolist.R;
import com.example.android.todolist.database.TaskEntry;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
public class SimpleDemoItemAdapter  extends RecyclerView.Adapter<SimpleDemoItemAdapter.MyViewHolder> implements View.OnClickListener {
    List<TaskEntry> filteredData;

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout containerView;
        //TextView textView;
        // Class variables for the task description and priority TextViews
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

        public MyViewHolder(View itemView) {
            super(itemView);
            //textView = itemView.findViewById(android.R.id.text1);
            containerView = itemView.findViewById(R.id.container);
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

        }
    }

    OnListItemClickMessageListener mOnItemClickListener;

    public SimpleDemoItemAdapter(OnListItemClickMessageListener clickListener) {
        mOnItemClickListener = clickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return filteredData.get(position).getId();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_minimal, parent, false);
        mContext = parent.getContext();
        MyViewHolder vh = new MyViewHolder(v);
        vh.itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.textView.setText("Item " + position);
        // Determine the values of the wanted data
        TaskEntry taskEntry = filteredData.get(position);
        String description = taskEntry.getName();
        int priority = taskEntry.getPriority();
        String startWall = taskEntry.getStartWall();
        String endWall = taskEntry.getEndWall();
        String tags = taskEntry.getTags();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        String setter = taskEntry.getSetter();
        Integer stoneColorInt = taskEntry.getImageColor();


        //Set values
        holder.stone_color.setImageResource(stoneColorInt);
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

        holder.btn_flash.setOnClickListener(v -> {
            int elementId = filteredData.get(position).getId();
            mOnItemClickListener.OnFlashClicked(elementId);
        });

        holder.btn_top.setOnClickListener(v -> {
            int elementId = filteredData.get(position).getId();
            mOnItemClickListener.OnTopClicked(elementId);
        });

        holder.btn_projekt.setOnClickListener(v -> {
            int elementId = filteredData.get(position).getId();
            mOnItemClickListener.OnProjektClicked(elementId);
        });

        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
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
        filteredData = taskEntries;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(@NonNull View v) {
        RecyclerView rv = RecyclerViewAdapterUtils.getParentRecyclerView(v);
        RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);

        int rootPosition = vh.getAdapterPosition();
        if (rootPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // need to determine adapter local position like this:
        RecyclerView.Adapter rootAdapter = rv.getAdapter();
        int localPosition = WrapperAdapterUtils.unwrapPosition(rootAdapter, this, rootPosition);

        if (mOnItemClickListener != null) {
            int elementId = filteredData.get(localPosition).getId();
            mOnItemClickListener.onItemClickListener(elementId);
        }
    }
}
