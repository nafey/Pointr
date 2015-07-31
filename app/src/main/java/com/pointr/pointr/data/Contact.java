package com.pointr.pointr.data;

import android.support.annotation.NonNull;

/**
 * Represents an entry in client phonebook
 */
public class Contact implements Comparable<Contact> {
    private String name;
    private String num;

    public Contact(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getNum();
    }

    @Override
    public int compareTo(@NonNull Contact another) {
        return this.name.compareTo(another.getName());
    }
}