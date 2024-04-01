package com.abdelrahman.rafaat.notesapp.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class NavigationIconClickListener implements View.OnClickListener {

    private final AnimatorSet animatorSet = new AnimatorSet();
    private final View sheet;
    private final Interpolator interpolator;
    private final int width;
    private boolean backdropShown = false;
    private final Drawable openIcon;
    private final Drawable closeIcon;

    public NavigationIconClickListener(
            Context context, View sheet, @Nullable Interpolator interpolator,
            @Nullable Drawable openIcon, @Nullable Drawable closeIcon) {
        this.sheet = sheet;
        this.interpolator = interpolator;
        this.openIcon = openIcon;
        this.closeIcon = closeIcon;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
        } else {
            width = 0;
        }
    }

    @Override
    public void onClick(View view) {
        startAnimation();
        updateIcon(view);
    }

    public void startAnimation() {
        backdropShown = !backdropShown;
        cancelExistingAnimation();

        int translateY = width / 2;
        if (Utils.isRTL()) {
            translateY *= -1;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationX", backdropShown ? translateY : 0);
        animator.setDuration(500);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animatorSet.play(animator);
        animator.start();
    }

    private void cancelExistingAnimation(){
        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();
    }

    private void updateIcon(View view) {
        if (openIcon != null && closeIcon != null) {
            if (!(view instanceof ImageView)) {
                throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
            }
            if (backdropShown) {
                ((ImageView) view).setImageDrawable(closeIcon);
            } else {
                ((ImageView) view).setImageDrawable(openIcon);
            }
        }
    }

    public void close() {
        backdropShown = false;
        cancelExistingAnimation();

        int translateY = width / 2;
        if (Utils.isRTL()) {
            translateY *= -1;
        }
        sheet.setTranslationX(backdropShown ? translateY : 0);
    }
}
