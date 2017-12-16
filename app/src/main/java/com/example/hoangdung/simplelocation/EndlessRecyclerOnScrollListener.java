package com.example.hoangdung.simplelocation;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by hoangdung on 12/12/17.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {


    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    protected int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 20; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    protected int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //Number of Recents visible items
        visibleItemCount = recyclerView.getChildCount();
        //Total number of items in the data set
        totalItemCount = mLinearLayoutManager.getItemCount();
        //The position of the first visible item in the data set
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        //If we are loading some data
        if (loading) {
            //If the total number of items now increased, meaning that we already loaded the data
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        //If there are no loading data and user scrolls to new page, let onLoadMore loads new data
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // Do something
            current_page++;
            onLoadMore(current_page);
            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);


}
