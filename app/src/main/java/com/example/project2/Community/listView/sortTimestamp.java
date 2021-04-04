package com.example.project2.Community.listView;

import android.util.Log;

import java.util.Comparator;

public class sortTimestamp implements Comparator<recyclerClass> {
    @Override
    public int compare(recyclerClass o1, recyclerClass o2) {
        return o1.getUpTime().compareTo(o2.getUpTime());
    }
}