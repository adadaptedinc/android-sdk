package com.adadapted.sdktestapp.core.todo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.adadapted.android.sdk.AdAdaptedListManager;
import com.adadapted.sdktestapp.ext.TodoLoader;
import com.adadapted.sdktestapp.ext.file.FileTodoLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class TodoListManager implements TodoLoader.Listener {
    private static String TAG = TodoListManager.class.getName();

    private static TodoListManager ourInstance;

    public static synchronized TodoListManager getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new TodoListManager(context);
            ourInstance.loadData();
        }

        return ourInstance;
    }

    public interface Listener {
        void onTodoListsAvailable();
    }

    private final Set<Listener> listeners;

    private final Context context;
    private final TodoLoader todoLoader;
    private boolean isLoaded;
    private final List<TodoList> lists;

    private final Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Loading Todo List data.");
            todoLoader.loadData();
        }
    };

    private final Runnable saveRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Saving Todo List data.");
            todoLoader.saveData(lists);
        }
    };

    private final Handler handler = new Handler();

    private TodoListManager(Context context) {
        this.context = context;

        this.listeners = new HashSet<>();

        this.todoLoader = new FileTodoLoader(context);
        this.todoLoader.addListener(this);

        this.isLoaded = false;
        this.lists = new ArrayList<>();
    }

    public List<TodoList> getLists() {
        return lists;
    }

    public TodoList getList(UUID listId) {
        for(TodoList list : lists) {
            if(list.getId().equals(listId)) {
                return list;
            }
        }

        return null;
    }

    public TodoList getDefaultList() {
        if(lists.size() > 0) {
            return lists.get(0);
        }
        else {
            final TodoList newList = new TodoList("Addit Default List");
            lists.add(newList);

            return newList;
        }
    }

    public void addNewList(String listName) {
        lists.add(new TodoList(listName));

        saveData();
    }

    public void addItemToList(UUID listId, String itemName) {
        Log.d(TAG, "Called addItemToList() " + listId + " " + itemName);
        TodoList list = getList(listId);
        list.addNewItem(itemName);

        AdAdaptedListManager.itemAddedToList(itemName);

        saveData();
    }

    public void toggleListItem(UUID listId, UUID itemId) {
        TodoList list = getList(listId);
        list.toggleItemComplete(itemId);

        saveData();
    }

    public void setLists(List<TodoList> lists) {
        //this.lists.clear();
        this.lists.addAll(lists);
    }

    private void loadData() {
        handler.post(loadRunnable);
    }

    private void saveData() {
        handler.post(saveRunnable);
    }

    @Override
    public void onDataLoaded(List<TodoList> lists) {
        Log.d(TAG, "onDataLoaded() called.");

        setLists(lists);
        isLoaded = true;

        notifyOnTodoListsAvailable();
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);

        if(isLoaded) {
            listener.onTodoListsAvailable();
        }
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void notifyOnTodoListsAvailable() {
        for(Listener listener : listeners) {
            listener.onTodoListsAvailable();
        }
    }
}
