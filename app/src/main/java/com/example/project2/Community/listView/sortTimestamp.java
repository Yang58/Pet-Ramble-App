package com.example.project2.Community.listView;

import android.util.Log;

import java.util.Comparator;

public class sortTimestamp implements Comparator<listViewClass> {
    @Override
    public int compare(listViewClass o1, listViewClass o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}