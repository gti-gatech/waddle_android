package com.appzoro.milton.ui.view.activity.notification;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public SpacesItemDecoration(int spacing) {
        mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NotNull View view, @NotNull RecyclerView recyclerView,
                               @NotNull RecyclerView.State state) {
//        outRect.left = mSpacing;
//        outRect.left = -7;
//        outRect.right = -7;
//        outRect.top = -20;
//        outRect.bottom = -10;
        outRect.bottom = mSpacing;
    }
}
