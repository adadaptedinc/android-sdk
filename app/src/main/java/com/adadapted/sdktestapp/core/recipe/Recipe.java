package com.adadapted.sdktestapp.core.recipe;

import java.util.UUID;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class Recipe {
    private final UUID id;
    private String name;

    public Recipe(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
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

    @Override
    public String toString() {
        return getName();
    }
}
