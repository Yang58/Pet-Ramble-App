package com.example.project2.Friend;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class FriendListItem {
    private ImageView iconDrawable ;
    private String titleStr ;


    public void setIcon(ImageView icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }


    public ImageView getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
}