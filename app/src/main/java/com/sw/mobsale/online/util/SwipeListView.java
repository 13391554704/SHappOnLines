package com.sw.mobsale.online.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * ListView
 */
public class SwipeListView extends ListView{
    private static final String TAG = "SwipeListView";

    public static SwipeView mSwipeView;
    private int mPosition;

    public SwipeListView(Context context) {
        super(context);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void shrinkItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SwipeView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                Log.e(TAG, "postion=" + position);
                if (position != INVALID_POSITION && position != 1 && position != 0) {
                    int firstPos = getFirstVisiblePosition();
                    mSwipeView = (SwipeView) getChildAt(position- firstPos);
                    Log.d(TAG, "position=" + position +",firstPos ="+firstPos);
                }
            }
            default:
                break;
        }

        if (mSwipeView != null) {
            mSwipeView.onSlideTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

}
