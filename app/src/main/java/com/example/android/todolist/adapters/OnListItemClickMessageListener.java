package com.example.android.todolist.adapters;

public interface OnListItemClickMessageListener {
    void onItemClickListener(int itemId);

    void OnFlashClicked(int itemId);

    void OnTopClicked(int itemId);

    void OnProjektClicked(int itemId);

    void startActivitySettings();
}
