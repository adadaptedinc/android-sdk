package com.adadapted.sdktestapp.ext;

import com.adadapted.sdktestapp.core.todo.TodoList;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonTodoMapper {
    List<TodoList> toTodoList(JSONArray jsonArray) {
        List<TodoList> lists = new ArrayList<>();

        return lists;
    }

    JSONArray toJsonArray(List<TodoList> lists) {
        JSONArray jsonArray = new JSONArray();

        return jsonArray;
    }
}
