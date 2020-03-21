package com.example.android.todolist.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.android.todolist.R;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.todolist.MainActivity.SHARED_PREFS;
import static com.example.android.todolist.MainActivity._DoneChecked;
import static com.example.android.todolist.MainActivity._ProjectChecked;
import static com.example.android.todolist.MainActivity._TodoChecked;
import static com.example.android.todolist.MainActivity.onItemClickedDefault;
import static com.example.android.todolist.MainActivity.onItemClickedDone;
import static com.example.android.todolist.MainActivity.onItemClickedProject;
import static com.example.android.todolist.MainActivity.onItemClickedTodo;

public class DemoHeaderFooterAdapter
        extends AbstractHeaderFooterWrapperAdapter<DemoHeaderFooterAdapter.HeaderViewHolder, DemoHeaderFooterAdapter.FooterViewHolder>
        implements View.OnClickListener {
    private static final String TAG = "DemoHeaderFooterAdapter";
    private boolean doneChecked;
    private boolean projectChecked;
    private boolean todoChecked;

    private OnListItemClickMessageListener mOnItemClickListener;
    private CheckBox todoCheckbox;
    private CheckBox doneCheckbox;
    private CheckBox projektCheckbox;
    private Context mContext;

    //Vielleicht diese Klasse wieder static machen? hier stand mal das
    /*
    todoCheckbox = itemView.findViewById(R.id.todoCheckboxID);
            doneCheckbox = itemView.findViewById(R.id.doneCheckboxID);
            projektCheckbox = itemView.findViewById(R.id.projektCheckboxID);
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
            Log.i(TAG, "onCreate");
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);

        }
    }

    public DemoHeaderFooterAdapter(RecyclerView.Adapter adapter, OnListItemClickMessageListener clickListener, Context mContext) {
        Log.i(TAG, "DemoHeaderFooterAdapter");
        setAdapter(adapter);
        mOnItemClickListener = clickListener;
        this.mContext = mContext;
        GetBooleansStatefromSharedPrefs(mContext);
        //macht fehler
        //SetCheckBoxesState();
    }

    private void GetBooleansStatefromSharedPrefs(Context mContext) {
        Log.i(TAG, "GetBooleansStatefromSharedPrefs");
        //Reload State of SharedPreferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        doneChecked = sharedPreferences.getBoolean(_DoneChecked, false);
        projectChecked = sharedPreferences.getBoolean(_ProjectChecked, false);
        todoChecked = sharedPreferences.getBoolean(_TodoChecked, false);
    }

    @Override
    public int getHeaderItemCount() {
        return 1;
    }

    @Override
    public int getFooterItemCount() {
        return 1;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateHeaderItemViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "HeaderViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
        HeaderViewHolder vh = new HeaderViewHolder(v);
        if (mOnItemClickListener != null) {
            vh.itemView.setOnClickListener(this);
        }

        return vh;
    }

    @NonNull
    @Override
    public FooterViewHolder onCreateFooterItemViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
        FooterViewHolder vh = new FooterViewHolder(v);
        if (mOnItemClickListener != null) {
            vh.itemView.setOnClickListener(this);
        }
        return vh;
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

        // get segment
        long segmentedPosition = getSegmentedPosition(localPosition);
        int segment = extractSegmentPart(segmentedPosition);
        int offset = extractSegmentOffsetPart(segmentedPosition);

        String message;

        if (segment == SEGMENT_TYPE_HEADER) {
            message = "CLICKED: Header item " + offset;
        } else if (segment == SEGMENT_TYPE_FOOTER) {
            message = "CLICKED: Footer item " + offset;
        } else {
            throw new IllegalStateException("Something wrong.");
        }

        mOnItemClickListener.onItemClicked(message);
    }

    // --------------------------------------------
    // [ OPTIONAL ]
    // Set full-span for Grid layout and Staggered Grid layout

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        setupFullSpanForGridLayoutManager(recyclerView);
    }

    @Override
    public void onBindHeaderItemViewHolder(@NonNull HeaderViewHolder holder, int localPosition) {
        applyFullSpanForStaggeredGridLayoutManager(holder);
        Log.i(TAG, "onBindHeaderItemViewHolder");
        todoCheckbox = holder.itemView.findViewById(R.id.todoCheckboxID);
        doneCheckbox = holder.itemView.findViewById(R.id.doneCheckboxID);
        projektCheckbox = holder.itemView.findViewById(R.id.projektCheckboxID);

        SetOnClickListener();
        SetCheckBoxesState();
    }


    private void SetCheckBoxesState() {
        Log.i(TAG, "SetCheckBoxesState");
        todoCheckbox.setChecked(todoChecked);
        doneCheckbox.setChecked(doneChecked);
        projektCheckbox.setChecked(projectChecked);
    }

    private void SetOnClickListener() {
        Log.i(TAG, "SetOnClickListener");
        todoCheckbox.setOnClickListener(v -> {
            if (todoChecked) {
                todoChecked = false;
                projectChecked = false;
                //nicht nötig
                doneChecked = false;
                mOnItemClickListener.onItemClicked(onItemClickedDefault);
            } else {
                todoChecked = true;
                doneChecked = false;
                //nicht nötig
                projectChecked = false;
                mOnItemClickListener.onItemClicked(onItemClickedTodo);
            }
            SetCheckBoxesState();
        });

        doneCheckbox.setOnClickListener(v -> {
            if (doneChecked) {
                doneChecked = false;
                //nicht relevant aber lass die 2 zeilen drin
                todoChecked = false;
                projectChecked = false;
                mOnItemClickListener.onItemClicked(onItemClickedDefault);
            } else {
                doneChecked = true;
                todoChecked = false;
                projectChecked = false;
                mOnItemClickListener.onItemClicked(onItemClickedDone);
            }
            SetCheckBoxesState();
        });

        projektCheckbox.setOnClickListener(v -> {
            if (projectChecked) {
                projectChecked = false;
                doneChecked = false;

                //nicht relevant aber schreibe ich hin:
                todoChecked = true;
                mOnItemClickListener.onItemClicked(onItemClickedTodo);
            } else {
                projectChecked = true;
                todoChecked = true;
                doneChecked = false;
                mOnItemClickListener.onItemClicked(onItemClickedProject);
            }
            SetCheckBoxesState();
        });


    }

    @Override
    public void onBindFooterItemViewHolder(@NonNull FooterViewHolder holder, int localPosition) {
        applyFullSpanForStaggeredGridLayoutManager(holder);
    }

    // Filling span for GridLayoutManager
    private void setupFullSpanForGridLayoutManager(RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (!(lm instanceof GridLayoutManager)) {
            return;
        }

        final GridLayoutManager glm = (GridLayoutManager) lm;
        final GridLayoutManager.SpanSizeLookup origSizeLookup = glm.getSpanSizeLookup();
        final int spanCount = glm.getSpanCount();

        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                final long segmentedPosition = getSegmentedPosition(position);
                final int segment = extractSegmentPart(segmentedPosition);
                final int offset = extractSegmentOffsetPart(segmentedPosition);

                if (segment == SEGMENT_TYPE_NORMAL) {
                    return origSizeLookup.getSpanSize(offset);
                } else {
                    return spanCount; // header or footer
                }
            }
        });
    }

    private void applyFullSpanForStaggeredGridLayoutManager(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        // Filling span for StaggeredGridLayoutManager
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
        }
    }
    // --------------------------------------------
}