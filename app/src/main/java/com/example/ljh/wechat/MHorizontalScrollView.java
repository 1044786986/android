package com.example.ljh.wechat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;

/**
 * Created by ljh on 2017/11/24.
 */

public class MHorizontalScrollView extends HorizontalScrollView{
    private int downX;
    private int downY;
    private int mTouchSlop;

    public MHorizontalScrollView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public MHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public MHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                Log.i("aa","----------------downX = " +  downX);
                Log.i("aa","----------------downY = " +  downY);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                Log.i("aa","-------------moveX = "+ moveX);
                if (Math.abs(moveX - downX) > 10) {
                    Log.i("aa","----------ReturnTrue");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int moveX2 = (int) ev.getRawX();
                Log.i("aa","-------------moveX = "+ moveX2);
                if (Math.abs(moveX2 - downX) > 10) {
                    Log.i("aa","----------ReturnTrue");
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
