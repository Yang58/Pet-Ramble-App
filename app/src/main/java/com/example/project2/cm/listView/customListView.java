package com.example.project2.cm.listView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class customListView extends ListView {
    private boolean touchCheck;

    public boolean getTouchCheck() {
        return touchCheck;
    }

    public void setTouchCheck(boolean touchCheck) {
        this.touchCheck = touchCheck;
    }

    public customListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTouchCheck(true);
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(!touchCheck){
            super.onInterceptTouchEvent(e);
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }
}
