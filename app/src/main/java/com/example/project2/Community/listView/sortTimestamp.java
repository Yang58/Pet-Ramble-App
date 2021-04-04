package com.example.project2.Community.listView;

import android.util.Log;

import java.util.Comparator;

public class sortTimestamp implements Comparator<recyclerClass> {
    @Override
    public int compare(recyclerClass o1, recyclerClass o2) {
        try {
            return o1.getUpTime().compareTo(o2.getUpTime());
        } catch (NullPointerException e) {
            Log.wtf("경고", e.getMessage());
            return 1;
        }
    }
}