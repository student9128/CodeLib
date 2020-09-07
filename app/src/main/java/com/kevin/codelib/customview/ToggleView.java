package com.kevin.codelib.customview;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.kevin.codelib.R;
import com.kevin.codelib.util.DisplayUtils;

/**
 * Created by Kevin on 2020/7/21<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
public class ToggleView extends View {
    private int mToggleColor,
            mCheckedColor,
            mUnCheckedColor;
    private float mBorderWidth;//使用浮点型，不然mBorderWidth/2回去整数导致绘制效果有差距
    private boolean mChecked;
    private final int defaultBackgroundColor = 0xffebebeb,
            defaultToggleColor = 0xffffffff,
            defaultCheckedColor = 0xffff6363;
    private Paint mPaintBackground, mPaintToggle, mPaintBorder;
    private int mLeft, mTop, mRight, mBottom;
    private float mR;
    private int mDefaultWidth, mDefaultHeight;
    private static final String TAG = "ToggleView";
    private ValueAnimator mAnimator;
    private float fraction;
    private ArgbEvaluator argbEvaluator;
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;
    private int mDefaultPadding;
    private RectF rectBackground;

    public ToggleView(Context context) {
        this(context, null);
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        if (typedArray != null) {
            mToggleColor = typedArray.getColor(R.styleable.ToggleView_toggleColor, defaultToggleColor);
            mBorderWidth = (int) typedArray.getDimension(R.styleable.ToggleView_borderWidth, DisplayUtils.dip2px(context, 2));
            mCheckedColor = typedArray.getColor(R.styleable.ToggleView_checkedColor, defaultCheckedColor);
            mUnCheckedColor = typedArray.getColor(R.styleable.ToggleView_unCheckedColor, defaultBackgroundColor);
            mChecked = typedArray.getBoolean(R.styleable.ToggleView_checked, false);
            typedArray.recycle();
        }
        mDefaultWidth = DisplayUtils.dip2px(getContext(), 50);
        mDefaultHeight = DisplayUtils.dip2px(getContext(), 20);
        mDefaultPadding = DisplayUtils.dip2px(getContext(), 5);
        argbEvaluator = new ArgbEvaluator();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        startAnimation();
    }

    private void initPaint() {
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintToggle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int measureSpec) {
        int result = mDefaultHeight;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d(TAG, "specSizeHeight=" + specSize + ",mode=" + specMode);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int result = mDefaultWidth;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d(TAG, "specSizeWidth=" + specSize + ",mode=" + specMode);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        Log.i(TAG, "width:\t" + width + ",\theight:\t" + height);
        Log.i(TAG, "measuredWidth:\t" + measuredWidth + ",\tmeasuredHeight:\t" + measuredHeight);
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        Log.w(TAG, "left:\t" + mLeft + ",\tright:\t" + mRight + ",\ttop:\t" + mTop + "," +
                "\tbottom:\t" +
                mBottom);
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mPaddingLeft = Math.max(mPaddingLeft, mDefaultPadding);
        mPaddingTop = Math.max(mPaddingTop, mDefaultPadding);
        ;
        mPaddingRight = Math.max(mPaddingRight, mDefaultPadding);
        ;
        mPaddingBottom = Math.max(mPaddingBottom, mDefaultPadding);
        ;
        mR = (height - mPaddingTop - mPaddingBottom) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintToggle.setColor(mToggleColor);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(mBorderWidth);
        int left = mLeft + mPaddingLeft;
        int top = mTop + mPaddingTop;
        int right = mRight - mPaddingRight;
        int bottom = mBottom - mPaddingBottom;
        rectBackground = new RectF(left, top, right, bottom);
        float cy = top + mR;
        float cxUnChecked = left + mR;
        float cxChecked = right - mR;
        float moveWidth = getWidth() - mPaddingLeft - mPaddingRight - mR * 2;
        if (mChecked) {
            int evaluate = (int) argbEvaluator.evaluate(fraction, mUnCheckedColor, mCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
//            RectF rectBorder = new RectF(left + mBorderWidth / 2 + fraction * moveWidth, top + mBorderWidth / 2, left + mBorderWidth / 2 + mR * 2 - mBorderWidth + fraction * moveWidth, bottom - mBorderWidth / 2);
            canvas.save();
            canvas.translate(-left, -top);
            canvas.drawRoundRect(rectBackground, mR, mR, mPaintBackground);//Toggle椭圆背景
            canvas.drawCircle(cxUnChecked + fraction * moveWidth, cy, mR - 1, mPaintToggle);//Toggle悬浮圆形
//            canvas.drawRoundRect(rectBorder, mR - mBorderWidth / 2, mR - mBorderWidth / 2, mPaintBorder);//Toggle悬浮圆形边线，这一个可以不要，修改圆形半径，这样无法修改边线颜色
            canvas.drawCircle(cxUnChecked + fraction * moveWidth, cy, mR - mBorderWidth / 2, mPaintBorder);
            canvas.restore();
        } else {
            int evaluate = (int) argbEvaluator.evaluate(fraction, mCheckedColor, mUnCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
//            Log.d(TAG, "x=" + (right - mR * 2 + mBorderWidth / 2 - fraction * moveWidth));
//            RectF rectBorder = new RectF(right - mR * 2 + mBorderWidth / 2 - fraction * moveWidth, top + mBorderWidth / 2, right - mBorderWidth / 2 - fraction * moveWidth, bottom - mBorderWidth / 2);
            canvas.save();
            canvas.translate(-left, -top);
            canvas.drawRoundRect(rectBackground, mR, mR, mPaintBackground);
            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR, mPaintToggle);
//            Log.d(TAG, "borderWidth=" + mBorderWidth + ",w/2=" + mBorderWidth / 2);
//            Log.d(TAG, "cx---left=" + (cxChecked - fraction * moveWidth - mR + mBorderWidth / 2));
            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR - mBorderWidth / 2, mPaintBorder);
            canvas.restore();
        }
//        mPaintBackground.setColor(Color.parseColor("#ff1e56"));
//        mPaintBackground.setStrokeWidth(1);
//        canvas.save();
//        canvas.translate(-left, -top);
//        canvas.drawLine(513,0,513,500,mPaintBackground);
//        canvas.drawLine(520.5f,0,520.5f,500,mPaintBackground);
    }

    private void startAnimation() {
        mAnimator.setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();
                invalidate();
            }
        });
        mAnimator.start();
        if (fraction >= 1) {
            stopAnimation();
        }
    }

    private void stopAnimation() {
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    //Warning:(237, 20) Custom view `ToggleView` overrides `onTouchEvent` but not `performClick`
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                mChecked = !mChecked;
                Log.i("ToggleView", "isOn  " + mChecked);
                requestLayout();//调用onLayout
                if (listener != null) {
                    listener.onToggleClick(mChecked);
                }
                startAnimation();
                break;
        }
        invalidate();
        return true;
    }

    private OnToggleClickListener listener;

    public void setOnToggleClickListener(OnToggleClickListener l) {
        listener = l;

    }

    public interface OnToggleClickListener {
        void onToggleClick(boolean checked);
    }

    public int getToggleColor() {
        return mToggleColor;
    }

    public void setToggleColor(int mToggleColor) {
        this.mToggleColor = mToggleColor;
        invalidate();
    }

    public int getCheckedColor() {
        return mCheckedColor;
    }

    public void setCheckedColor(int mCheckedColor) {
        this.mCheckedColor = mCheckedColor;
        invalidate();
    }

    public int getUnCheckedColor() {
        return mUnCheckedColor;
    }

    public void setUnCheckedColor(int mUnCheckedColor) {
        this.mUnCheckedColor = mUnCheckedColor;
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        invalidate();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
        invalidate();
    }
}
