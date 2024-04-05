package com.abdelrahman.rafaat.notesapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;

public abstract class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private int buttonWidth;
    private int buttonHeight;
    private final Context context;
    private float deleteButtonLeft;
    private float deleteButtonRight;
    private float archiveButtonLeft;
    private float archiveButtonRight;
    private float pinButtonRight;
    private float pinButtonLeft;
    private View itemView;
    private int swipedPosition = -1;
    private final GestureDetector gestureDetector;

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (isTapOnDeleteButton(x, y)) {
                deleteButtonPressed(swipedPosition);
                return true;
            }

            if (isTapOnArchiveButton(x, y)) {
                archiveButtonPressed(swipedPosition);
                return true;
            }

            if (isTapOnPinButton(x, y)) {
                pinButtonPressed(swipedPosition);
                return true;
            }

            return false;
        }

        private boolean isTapOnDeleteButton(float x, float y) {
            return x >= deleteButtonLeft && x <= deleteButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }

        private boolean isTapOnArchiveButton(float x, float y) {
            return x >= archiveButtonLeft && x <= archiveButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }

        private boolean isTapOnPinButton(float x, float y) {
            return x >= pinButtonLeft && x <= pinButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }
    };

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {

            if (itemView != null) {
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());
                Rect rect = new Rect();
                itemView.getGlobalVisibleRect(rect);

                if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y || rect.bottom > point.y)
                        gestureDetector.onTouchEvent(e);
                }
            }
            return false;
        }
    };

    public MyItemTouchHelperCallback(Context context, RecyclerView recyclerView) {
        this.context = context;
        gestureDetector = new GestureDetector(context, gestureListener);
        recyclerView.setOnTouchListener(onTouchListener);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        float translationX = dX;
        View itemView = viewHolder.itemView;
        swipedPosition = viewHolder.getAdapterPosition();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                // swapLeft
                drawButtonsOnRight(canvas, itemView);
                translationX = dX * 2 * buttonWidth / itemView.getWidth();
            } else if (dX > 0) {
                // swapRight
                drawButtonsOnLeft(canvas, itemView);
                translationX = dX * buttonWidth / itemView.getWidth();
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButtonsOnLeft(Canvas canvas, View itemView) {
        // Calculate the button width based on your requirements
        this.itemView = itemView;
        pinButtonLeft = itemView.getLeft();
        pinButtonRight = pinButtonLeft + buttonWidth;
        // Draw the first button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_pin), context.getString(R.string.pin), pinButtonLeft, pinButtonRight, ContextCompat.getColor(context, R.color.mainColor));
    }

    private void drawButtonsOnRight(Canvas canvas, View itemView) {
        // Calculate the button width based on your requirements
        this.itemView = itemView;

        deleteButtonRight = itemView.getRight();
        deleteButtonLeft = deleteButtonRight - buttonWidth;

        archiveButtonRight = deleteButtonLeft;
        archiveButtonLeft = archiveButtonRight - buttonWidth;
        // Calculate the positions of the buttons on the right side

        // Draw the first button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_archive), context.getString(R.string.archive), archiveButtonLeft, deleteButtonLeft, ContextCompat.getColor(context, R.color.mainColor));

        // Draw the second button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_delete), context.getString(R.string.delete), deleteButtonLeft, deleteButtonRight, ContextCompat.getColor(context, R.color.red));
    }

    private void drawButton(Canvas canvas, View itemView, Drawable icon, String text, float left, float right, int color) {

        // Draw the button background
        drawBackGround(itemView, canvas, color, left, right);

        int itemViewHeight = itemView.getHeight() / 2;
        int itemViewCenter = itemView.getTop() + itemViewHeight;
        // Draw the button image
        drawIcon(icon, canvas, left, right, itemViewCenter);
        // Draw the button text
        drawText(text, canvas, context.getResources().getColor(R.color.white, null), left, right, itemViewCenter);

    }

    private void drawBackGround(View itemView, Canvas canvas, @ColorInt int color, float left, float right) {
        RectF background = new RectF(left, itemView.getTop(), right, itemView.getBottom());
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(background, paint);
    }

    private void drawText(String text, Canvas canvas, @ColorInt int color,
                          float itemViewLeft, float right, int itemViewCenter) {
        Paint paint = new Paint();
        paint.setColor(color);
        double textSize = buttonWidth * 0.25;
        paint.setTextSize((float) textSize);
        paint.setAntiAlias(true);

        paint.setColor(ContextCompat.getColor(context, R.color.white));
        float textX = itemViewLeft + (right - itemViewLeft) / 2f - paint.measureText(text) / 2f;
        float textY = itemViewCenter + buttonHeight + context.getResources().getDimension(R.dimen.margin_between_image_text);
        canvas.drawText(text, textX, textY, paint);
    }

    private void drawIcon(Drawable icon, Canvas canvas, float left, float right, int itemViewCenter) {
        if (icon != null) {
            int iconHeight = icon.getIntrinsicHeight();
            buttonHeight = iconHeight;
            int iconWidth = icon.getIntrinsicWidth();
            buttonWidth = iconWidth * 3;
            int margin = iconWidth / 2;
            int iconLeft = (int) (left + margin);
            int iconRight = (int) (right - margin);
            Rect rect = new Rect(iconLeft,
                    itemViewCenter - iconHeight,
                    iconRight,
                    itemViewCenter + iconHeight);
            icon.setBounds(rect);
            icon.draw(canvas);
        }
    }

    public abstract void deleteButtonPressed(int position);

    public abstract void archiveButtonPressed(int position);

    public abstract void pinButtonPressed(int position);
}
//public abstract class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
//    public static final int BUTTON_WIDTH = 250;
//    public Context context;
//    private float deleteButtonLeft;
//    private float deleteButtonRight;
//    private float archiveButtonLeft;
//    private float archiveButtonRight;
//    private float pinButtonRight;
//    private float pinButtonLeft;
//    private View itemView;
//    private int swipedPosition = -1;
//    private final GestureDetector gestureDetector;
//
//    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            float x = e.getX();
//            float y = e.getY();
//
//            if (isTapOnDeleteButton(x, y)) {
//                deleteButtonPressed(swipedPosition);
//                return true;
//            }
//
//            if (isTapOnArchiveButton(x, y)) {
//                archiveButtonPressed(swipedPosition);
//                return true;
//            }
//
//            if (isTapOnPinButton(x, y)) {
//                pinButtonPressed(swipedPosition);
//                return true;
//            }
//
//            return false;
//        }
//
//        private boolean isTapOnDeleteButton(float x, float y) {
//            return x >= deleteButtonLeft && x <= deleteButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
//        }
//
//        private boolean isTapOnArchiveButton(float x, float y) {
//            return x >= archiveButtonLeft && x <= archiveButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
//        }
//
//        private boolean isTapOnPinButton(float x, float y) {
//            return x >= pinButtonLeft && x <= pinButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
//        }
//    };
//
//    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent e) {
//
//            if (itemView != null) {
//                Point point = new Point((int) e.getRawX(), (int) e.getRawY());
//                Rect rect = new Rect();
//                itemView.getGlobalVisibleRect(rect);
//
//                if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
//                    if (rect.top < point.y || rect.bottom > point.y)
//                        gestureDetector.onTouchEvent(e);
//                }
//            }
//            return false;
//        }
//    };
//
//    public MyItemTouchHelperCallback(Context context, RecyclerView recyclerView) {
//        this.context = context;
//        gestureDetector = new GestureDetector(context, gestureListener);
//        recyclerView.setOnTouchListener(onTouchListener);
//    }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//        return makeMovementFlags(0, swipeFlags);
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
//                          @NonNull RecyclerView.ViewHolder target) {
//        return false;
//    }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//    }
//
//    @Override
//    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
//                            @NonNull RecyclerView.ViewHolder viewHolder,
//                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        float translationX = dX;
//        View itemView = viewHolder.itemView;
//        swipedPosition = viewHolder.getAdapterPosition();
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            if (dX < 0) {
//                // swapLeft
//                translationX = dX * 2 * BUTTON_WIDTH / itemView.getWidth();
//                drawButtonsOnRight(canvas, itemView);
//            } else if (dX > 0) {
//                // swapRight
//                translationX = dX * BUTTON_WIDTH / itemView.getWidth();
//                drawButtonsOnLeft(canvas, itemView);
//            }
//        }
//        super.onChildDraw(canvas, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
//    }
//
//    private void drawButtonsOnLeft(Canvas canvas, View itemView) {
//        // Calculate the button width based on your requirements
//        this.itemView = itemView;
//        pinButtonLeft = itemView.getLeft();
//        pinButtonRight = pinButtonLeft + BUTTON_WIDTH;
//        // Draw the first button
//        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_pin), context.getString(R.string.pin), pinButtonLeft, pinButtonRight, ContextCompat.getColor(context, R.color.mainColor));
//    }
//
//    private void drawButtonsOnRight(Canvas canvas, View itemView) {
//        // Calculate the button width based on your requirements
//        this.itemView = itemView;
//
//        deleteButtonRight = itemView.getRight();
//        deleteButtonLeft = deleteButtonRight - BUTTON_WIDTH;
//
//        archiveButtonRight = deleteButtonLeft;
//        archiveButtonLeft = archiveButtonRight - BUTTON_WIDTH;
//        // Calculate the positions of the buttons on the right side
//
//        // Draw the first button
//        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_archive), context.getString(R.string.archive), archiveButtonLeft, deleteButtonLeft, ContextCompat.getColor(context, R.color.mainColor));
//
//        // Draw the second button
//        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_delete), context.getString(R.string.delete), deleteButtonLeft, deleteButtonRight, ContextCompat.getColor(context, R.color.red));
//    }
//
//    private void drawButton(Canvas canvas, View itemView, Drawable imageResId, String text, float left, float right, int color) {
//        Paint paint = new Paint();
//        paint.setColor(color);
//        paint.setTextSize(50);
//        paint.setAntiAlias(true);
//
//        // Draw the button background
//        RectF background = new RectF(left, itemView.getTop(), right, itemView.getBottom());
//        canvas.drawRect(background, paint);
//
//        // Draw the button text
//        paint.setColor(ContextCompat.getColor(context, R.color.white));
//        float textX = left + (right - left) / 2f - paint.measureText(text) / 2f;
//        float textY = itemView.getTop() + itemView.getHeight() / 3f;
//        canvas.drawText(text, textX, textY, paint);
//
//        float cHeight = background.height();
//        float cWidth = background.width() / 2;
//        // Draw the button image
//        if (imageResId != null) {
//            int top = (int) (background.top + (cHeight / 2f));
//            int bottom = (int) (background.bottom - (cHeight / 10f));
//            int imageLeft = (int) (left + cWidth - 60);
//            int imageRight = (int) (right - cWidth + 60);
//            imageResId.setBounds(imageLeft, top, imageRight, bottom);
//            imageResId.draw(canvas);
//        }
//    }
//
//    public abstract void deleteButtonPressed(int position);
//
//    public abstract void archiveButtonPressed(int position);
//
//    public abstract void pinButtonPressed(int position);
//}