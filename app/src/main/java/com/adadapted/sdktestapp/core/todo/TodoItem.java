package com.adadapted.sdktestapp.core.todo;

import java.util.UUID;

/**
 * Created by chrisweeden on 4/1/15.
 */
public class TodoItem {
    private UUID id;
    private String name;
    private boolean isComplete;

    public TodoItem(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.isComplete = false;
    }

    public TodoItem(UUID id, String name, boolean isComplete) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void toggleComplete() {
        setComplete(!isComplete);
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isComplete=" + isComplete +
                '}';
    }
}
