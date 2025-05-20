package com.foodapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom HorizontalScrollView that synchronizes scrolling with other SynchronizedScrollViews
 */
public class SynchronizedScrollView extends HorizontalScrollView {

    // List of views to sync with
    private List<SynchronizedScrollView> scrollViewList = new ArrayList<>();

    // Flag to prevent infinite scroll loop
    private boolean isSyncing = false;

    // Interface for scroll event
    public interface OnScrollListener {
        void onScroll(int x);
    }

    private OnScrollListener onScrollListener;

    public SynchronizedScrollView(Context context) {
        super(context);
    }

    public SynchronizedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SynchronizedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Add another scroll view to sync with
     */
    public void addScrollView(SynchronizedScrollView scrollView) {
        if (scrollView != null && !scrollViewList.contains(scrollView)) {
            scrollViewList.add(scrollView);
        }
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void synchronizeWith(SynchronizedScrollView otherScrollView) {
        if (otherScrollView != null) {
            this.addScrollView(otherScrollView);
            otherScrollView.addScrollView(this);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (onScrollListener != null) {
            onScrollListener.onScroll(l);
        }

        if (!isSyncing) {
            isSyncing = true;
            for (SynchronizedScrollView scrollView : scrollViewList) {
                scrollView.scrollTo(l, t);
            }
            isSyncing = false;
        }
    }
}