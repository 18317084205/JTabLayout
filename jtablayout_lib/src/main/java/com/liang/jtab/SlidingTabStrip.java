package com.liang.jtab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.liang.jtab.indicator.Indicator;

public class SlidingTabStrip extends LinearLayout {

    private ValueAnimator mIndicatorAnimator;
    private int mSelectedPosition = -1;
    private float mIndicatorLeft;
    private float mIndicatorRight;
    private TimeInterpolator interpolator = new FastOutSlowInInterpolator();
    private Indicator indicator;

    private int dividerWidth;
    private int dividerHeight;
    private int dividerColor;
    private Paint dividerPaint;

    public SlidingTabStrip(Context context) {
        super(context);
        setWillNotDraw(false);
        dividerPaint = new Paint();
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mSelectedPosition = -1;
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        if (getChildCount() == 0) {
            mSelectedPosition = -1;
        }
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (indicator == null) {
            return;
        }

        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
//            final long duration = mIndicatorAnimator.getDuration();
//            animateIndicatorToPosition(mSelectedPosition,
//                    Math.round((1f - mIndicatorAnimator.getAnimatedFraction()) * duration));
        }
        updateIndicatorPosition(-1, 0);
    }

    public void animateIndicatorToPosition(final int newPosition, int duration) {
        if (indicator == null) {
            mSelectedPosition = newPosition;
            return;
        }

        if (newPosition != mSelectedPosition) {
            if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
                mIndicatorAnimator.cancel();
            }
            ValueAnimator animator = mIndicatorAnimator = new ValueAnimator();
            animator.setInterpolator(interpolator);
            animator.setDuration(duration);
            animator.setFloatValues(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    float selectionOffset = animator.getAnimatedFraction();
                    updateIndicatorPosition(newPosition, selectionOffset);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mSelectedPosition = newPosition;
                }
            });
            animator.start();
        }
    }

    public void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        if (indicator == null) {
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }
        mSelectedPosition = position;
        updateIndicatorPosition(-1, positionOffset);
    }

    private void updateIndicatorPosition(int position, float offset) {
        if (position < 0) {
            position = mSelectedPosition + 1;
        }

        float left = -1, right = -1;
        View currentTab = getChildAt(mSelectedPosition);
        if (currentTab != null && currentTab.getWidth() > 0) {
            left = currentTab.getLeft();
            int indicatorWidth = getIndicatorWidth(currentTab);
            left += (currentTab.getWidth() - indicatorWidth) / 2;
            right = left + indicatorWidth;
        }
        if (position < getChildCount()) {
            View nextTabView = getChildAt(position);
            int indicatorWidth = getIndicatorWidth(nextTabView);
            float nextLeft = nextTabView.getLeft();
            float nextRight;
            nextLeft += (nextTabView.getWidth() - indicatorWidth) / 2;
            nextRight = nextLeft + indicatorWidth;

            if (indicator.isTransitionScroll()) {
                float offR = offset * 2 - 1;
                float offL = offset * 2;

                if (position < mSelectedPosition && offset > 0) {

                    if (offR < 0) {
                        offR = 0;
                    }
                    if (1 - offL < 0) {
                        offL = 1;
                    }
                } else {
                    offL = offset * 2 - 1;
                    offR = offset * 2;
                    if (offL < 0) {
                        offL = 0;
                    }
                    if (1 - offR < 0) {
                        offR = 1;
                    }
                }
                left += ((nextLeft - left) * offL);
                right += ((nextRight - right) * offR);
            } else {
                left += ((nextLeft - left) * offset);
                right += ((nextRight - right) * offset);
            }
        }
        setIndicatorPosition(left, right);
    }

    private int getIndicatorWidth(View currentTab) {
        if (indicator == null) {
            return 0;
        }
        int indicatorWidth = indicator.getWidth();
        if (indicatorWidth <= 0) {
            indicatorWidth = (int) (currentTab.getWidth() * indicator.getWidthScale());
        }
        return indicatorWidth;
    }

    private void setIndicatorPosition(float left, float right) {
        if (left != mIndicatorLeft || right != mIndicatorRight) {
            mIndicatorLeft = left;
            mIndicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (isInEditMode() || getChildCount() <= 0) {
            super.draw(canvas);
            return;
        }

        if (dividerWidth > 0) {
            dividerPaint.setStrokeWidth(dividerWidth);
            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < getChildCount() - 1; i++) {
                View tab = getChildAt(i);
                canvas.drawLine(tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2, tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2 + dividerHeight, dividerPaint);
            }
        }

        if (indicator != null && mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
            if (indicator.isForeground()) {
                super.draw(canvas);
                indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
            } else {
                indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
                super.draw(canvas);
            }
        } else {
            super.draw(canvas);
        }
    }
}
