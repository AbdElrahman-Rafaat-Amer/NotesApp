package com.abdelrahman.rafaat.notesapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.interfaces.OnItemSwipedListener;

public class SwipeItemTouchHelper extends ItemTouchHelper.Callback {

    private int buttonWidth = 0;
    private int buttonHeight = 0;
    private final Context context;
    private final OnItemSwipedListener onItemSwiped;

    public SwipeItemTouchHelper(Context context, OnItemSwipedListener onItemSwiped) {
        this.context = context;
        this.onItemSwiped = onItemSwiped;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int swipedPosition = viewHolder.getAdapterPosition();
        onItemSwiped.onSwiped(swipedPosition, direction);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                            float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            boolean isRightSideToDraw = false;
            String text = "";
            int textLeft = 0;
            int backgroundColor = 0;
            int itemViewHeight = itemView.getHeight() / 2;
            int itemViewCenter = itemView.getTop() + itemViewHeight;
            if (dX > 0) {
                // Swipe left
                backgroundColor = context.getResources().getColor(R.color.mainColor, null);
                text = context.getString(R.string.unarchive);
                textLeft = itemView.getLeft();
            } else if (dX < 0) {
                // Swipe right
                isRightSideToDraw = true;
                backgroundColor = context.getResources().getColor(R.color.red, null);
                text = context.getString(R.string.delete);
                textLeft = itemView.getRight() - buttonWidth;
            }
            drawBackGround(itemView, c, backgroundColor);
            drawButton(c, itemView.getLeft(), itemView.getRight(), itemViewCenter, isRightSideToDraw);
            drawText(text, c, context.getResources().getColor(R.color.white, null), textLeft, itemViewCenter);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawText(String text, Canvas canvas, @ColorInt int color, int itemViewLeft, float itemViewCenter) {
        Paint paint = new Paint();
        paint.setColor(color);

        paint.setTextSize(50);
        paint.setAntiAlias(true);

        float textX = itemViewLeft + buttonWidth / 2f - paint.measureText(text) / 2;
        float textY = itemViewCenter + buttonHeight + context.getResources().getDimension(R.dimen.margin_between_image_text);

        canvas.drawText(text, textX, textY, paint);
    }

    private void drawBackGround(View itemView, Canvas canvas, @ColorInt int color) {
        RectF background = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(background, paint);
    }

    private void drawButton(Canvas canvas, int left, int right, int itemViewCenter, boolean isRightSideToDraw) {
        Drawable icon;
        if (isRightSideToDraw) {
            icon = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_delete, context.getTheme());
        } else {
            icon = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_unarchive, context.getTheme());
        }
        drawIcon(icon, canvas, left, right, itemViewCenter, isRightSideToDraw);

    }

    private void drawIcon(Drawable icon, Canvas canvas, int left, int right, int itemViewCenter, boolean isRightSideToDraw) {
        if (icon != null) {
            int iconHeight = icon.getIntrinsicHeight();
            buttonHeight = iconHeight;
            int iconWidth = icon.getIntrinsicWidth();
            buttonWidth = iconWidth * 3;
            int margin = iconWidth / 2;
            int iconLeft = isRightSideToDraw ? right - (iconWidth * 2 + margin) : left + margin;
            int iconRight = isRightSideToDraw ? right - margin : iconWidth * 2 + margin;
            Rect rect = new Rect(iconLeft,
                    itemViewCenter - iconHeight,
                    iconRight,
                    itemViewCenter + iconHeight);
            icon.setBounds(rect);
            icon.draw(canvas);
        }
    }
}