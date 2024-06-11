package com.example.form;

public class SearchNameForm {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SearchNameForm{" +
                "name='" + name + '\'' +
                '}';
    }
}