package com.llorieb.ahorcado.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout liviano que distribuye bloques de palabras en varias filas y centra cada fila.
 */
public class FlowLayout extends ViewGroup {

    private final int horizontalSpacing;
    private final int verticalSpacing;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        horizontalSpacing = (int) (10 * density + 0.5f);
        verticalSpacing = (int) (10 * density + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = Math.max(0, widthSize - getPaddingLeft() - getPaddingRight());

        int lineWidth = 0;
        int lineHeight = 0;
        int maxLineWidth = 0;
        int totalHeight = getPaddingTop() + getPaddingBottom();
        boolean hasLine = false;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            int prospective = hasLine ? lineWidth + horizontalSpacing + childWidth : childWidth;
            if (hasLine && prospective > availableWidth) {
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
                totalHeight += lineHeight + verticalSpacing;
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth = prospective;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            hasLine = true;
        }

        if (hasLine) {
            maxLineWidth = Math.max(maxLineWidth, lineWidth);
            totalHeight += lineHeight;
        }

        int desiredWidth = maxLineWidth + getPaddingLeft() + getPaddingRight();
        int measuredWidth = widthMode == MeasureSpec.EXACTLY
                ? widthSize
                : resolveSize(desiredWidth, widthMeasureSpec);
        int measuredHeight = resolveSize(totalHeight, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int availableWidth = right - left - getPaddingLeft() - getPaddingRight();
        List<View> row = new ArrayList<>();
        int rowWidth = 0;
        int rowHeight = 0;
        int y = getPaddingTop();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            int prospective = row.isEmpty() ? childWidth : rowWidth + horizontalSpacing + childWidth;

            if (!row.isEmpty() && prospective > availableWidth) {
                layoutRow(row, rowWidth, rowHeight, y, availableWidth);
                y += rowHeight + verticalSpacing;
                row.clear();
                rowWidth = 0;
                rowHeight = 0;
            }

            rowWidth = row.isEmpty() ? childWidth : rowWidth + horizontalSpacing + childWidth;
            rowHeight = Math.max(rowHeight, childHeight);
            row.add(child);
        }

        if (!row.isEmpty()) {
            layoutRow(row, rowWidth, rowHeight, y, availableWidth);
        }
    }

    private void layoutRow(List<View> row, int rowWidth, int rowHeight, int y, int availableWidth) {
        int x = getPaddingLeft() + Math.max(0, (availableWidth - rowWidth) / 2);

        for (View child : row) {
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            x += lp.leftMargin;
            int childTop = y + lp.topMargin + Math.max(0,
                    (rowHeight - child.getMeasuredHeight() - lp.topMargin - lp.bottomMargin) / 2);
            child.layout(x, childTop, x + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            x += child.getMeasuredWidth() + lp.rightMargin + horizontalSpacing;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
