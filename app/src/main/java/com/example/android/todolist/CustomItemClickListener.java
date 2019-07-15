package com.example.android.todolist;

interface CustomItemClickListener {
    void onItemClickListener(int itemId);

    void OnFlashClicked(int itemId);

    void OnTopClicked(int itemId);

    void OnProjektClicked(int itemId);
}