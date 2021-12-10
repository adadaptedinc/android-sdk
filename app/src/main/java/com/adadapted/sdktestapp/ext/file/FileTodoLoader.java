package com.adadapted.sdktestapp.ext.file;

import android.content.Context;
import android.util.Log;

import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.ext.JsonTodoMapper;
import com.adadapted.sdktestapp.ext.TodoLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTodoLoader implements TodoLoader {
    private static final String TAG = FileTodoLoader.class.getName();

    private static final String FILENAME = "todo_list.json";

    private final Context context;
    private final Set<Listener> listeners;
    private final JsonTodoMapper mapper;

    public FileTodoLoader(Context context) {
        this.context = context;
        this.listeners = new HashSet<>();
        this.mapper = new JsonTodoMapper();
    }

    @Override
    public void loadData() {
        List<TodoList> lists = new ArrayList<>();

        TodoList defaultList = new TodoList("Default List");
        defaultList.addNewItem("Item 1");
        defaultList.addNewItem("Item 2");
        defaultList.addNewItem("Item 3");
        defaultList.addNewItem("Item 4");
        defaultList.addNewItem("Item 5");
        defaultList.addNewItem("Item 6");
        defaultList.addNewItem("Item 7");
        defaultList.addNewItem("Item 8");
        defaultList.addNewItem("Item 9");
        defaultList.addNewItem("Item 10");
        defaultList.addNewItem("Item 11");
        defaultList.addNewItem("Item 12");
        defaultList.addNewItem("Item 13");
        defaultList.addNewItem("Item 14");
        defaultList.addNewItem("Item 15");
        lists.add(defaultList);

        TodoList groceryList = new TodoList("Grocery List");
        groceryList.addNewItem("Milk");
        groceryList.addNewItem("Bread");
        groceryList.addNewItem("Eggs");
        groceryList.addNewItem("Cheese");
        lists.add(groceryList);

        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            fis.close();
        }
        catch(FileNotFoundException ex) {
            Log.e(TAG, "Problem opening file for reading.", ex);
        }
        catch(IOException ex) {
            Log.e(TAG, "Problem interacting with file for reading.", ex);
        }

        notifyOnDataLoaded(lists);
    }

    @Override
    public void saveData(List<TodoList> lists) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            //fos.write();
            fos.close();
        }
        catch(FileNotFoundException ex) {
            Log.e(TAG, "Problem opening file for writing.", ex);
        }
        catch(IOException ex) {
            Log.e(TAG, "Problem interacting with file for writing.", ex);
        }
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    private void notifyOnDataLoaded(List<TodoList> lists) {
        for(Listener listener : listeners) {
            listener.onDataLoaded(lists);
        }
    }
}
