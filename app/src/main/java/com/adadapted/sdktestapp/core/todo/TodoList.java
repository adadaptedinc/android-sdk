package com.adadapted.sdktestapp.core.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class TodoList {
    private UUID id;
    private String name;
    private List<TodoItem> items;

    public TodoList(String listName) {
        name = listName;
        id = UUID.randomUUID();
        items = new ArrayList<>();
    }

    public TodoList(UUID id, String name, List<TodoItem> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TodoItem> getItems() {
        return items;
    }

    public TodoItem getItem(UUID itemId) {
        for(TodoItem item : items) {
            if(item.getId().equals(itemId)) {
                return item;
            }
        }

        return null;
    }

    public void setItems(List<TodoItem> items) {
        this.items = items;
    }

    public void addNewItem(String itemName) {
        items.add(new TodoItem(itemName));
    }

    @Override
    public String toString() {
        return getName();
    }

    public void toggleItemComplete(UUID itemId) {
        getItem(itemId).toggleComplete();
    }
}
