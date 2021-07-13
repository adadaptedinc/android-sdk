package com.adadapted.sdktestapp.ext;

import com.adadapted.sdktestapp.core.todo.TodoList;

import java.util.List;

public interface TodoLoader {
    interface Listener {
        void onDataLoaded(List<TodoList> lists);
    }

    void loadData();
    void saveData(List<TodoList> lists);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
