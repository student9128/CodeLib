package com.kevin.codelib.customview;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.kevin.codelib.R;
import com.kevin.codelib.util.DisplayUtils;
import com.kevin.codelib.util.LogUtils;

/**
 * Created by Kevin on 2020/7/21<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
public class ToggleView extends View {
    private static final int rectangle = 100;
    private static final int circle = 101;
    private static final int circleWithThin = 102;
    private static final int circleWithThinner = 103;
    private static final int circleWithThinnest = 104;

    private int mToggleColor,
            mCheckedColor,
            mUnCheckedColor;
    private float mBorderWidth;//使用浮点型，不然mBorderWidth/2回去整数导致绘制效果有差距
    private boolean mChecked;
    private int mCheckType = circle;
    private final int defaultBackgroundColor = 0xffebebeb,
            defaultToggleColor = 0xffffffff,
            defaultCheckedColor = 0xffff6363;
    private Paint mPaintBackground, mPaintToggle, mPaintBorder, mPaintShadow, mPaintText;
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
    private int mRectangleCorner;
    private boolean mToggleShadow;

    /**
     * rectangle,//矩形
     * oval,//椭圆形
     * ovalWithThin//椭圆形横向比较细
     */
    enum ToggleType {
        rectangle,//矩形
        circle,//椭圆形
        circleWithThin//椭圆形横向比较细
    }

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
            mBorderWidth = (int) typedArray.getDimension(R.styleable.ToggleView_toggleBorderWidth, DisplayUtils.dip2px(context, 2));
            mCheckedColor = typedArray.getColor(R.styleable.ToggleView_toggleCheckedColor, defaultCheckedColor);
            mUnCheckedColor = typedArray.getColor(R.styleable.ToggleView_toggleUnCheckedColor, defaultBackgroundColor);
            mChecked = typedArray.getBoolean(R.styleable.ToggleView_toggleChecked, false);
            mCheckType = typedArray.getInt(R.styleable.ToggleView_toggleType, circle);
            mToggleShadow = typedArray.getBoolean(R.styleable.ToggleView_toggleShadow, false);
            typedArray.recycle();
        }
        mDefaultWidth = DisplayUtils.dip2px(getContext(), 50);
        mDefaultHeight = DisplayUtils.dip2px(getContext(), 20);
        mDefaultPadding = DisplayUtils.dip2px(getContext(), 5);
        mRectangleCorner = DisplayUtils.dip2px(getContext(), 2);
        argbEvaluator = new ArgbEvaluator();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        startAnimation();
    }

    private void initPaint() {
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintToggle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        mPaddingRight = Math.max(mPaddingRight, mDefaultPadding);
        mPaddingBottom = Math.max(mPaddingBottom, mDefaultPadding);
        switch (mCheckType) {
            case rectangle:
                mR = (height - mPaddingTop - mPaddingBottom) / 2 - mBorderWidth;
                break;
            case circle:
            case circleWithThin:
            case circleWithThinner:
            case circleWithThinnest:
            default:
                mR = (height - mPaddingTop - mPaddingBottom) / 2;
                break;
        }
        LogUtils.INSTANCE.logD(TAG, "mR===" + mR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintToggle.setColor(mToggleColor);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(mBorderWidth);
        mPaintText.setColor(Color.RED);
//        mPaintShadow.setColor(Color.parseColor("#7f2196f3"));
//        mPaintShadow.setStyle(Paint.Style.FILL);
//        mPaintShadow.setStrokeWidth(2f);
        LogUtils.INSTANCE.logD(TAG, "mCheckType=" + mCheckType);
        if (mToggleShadow) {
            mPaintToggle.setShadowLayer(5f, 3, 3, Color.LTGRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        switch (mCheckType) {
            case circleWithThin:
            case circleWithThinner:
            case circleWithThinnest:
                drawCircleWithThin(canvas, mCheckType);
                break;
            case rectangle:
                drawRectangle(canvas);
                break;
            case circle:
            default:
                drawCircle(canvas);
                break;
        }
//        mPaintBackground.setColor(Color.parseColor("#ff1e56"));
//        mPaintBackground.setStrokeWidth(1);
//        canvas.save();
//        canvas.translate(-left, -top);
//        canvas.drawLine(513,0,513,500,mPaintBackground);
//        canvas.drawLine(520.5f,0,520.5f,500,mPaintBackground);
    }

    private void drawCircle(Canvas canvas) {
        int left = mLeft + mPaddingLeft;
        int top = mTop + mPaddingTop;
        int right = mRight - mPaddingRight;
        int bottom = mBottom - mPaddingBottom;
        rectBackground = new RectF(left, top, right, bottom);
        float cy = top + mR;
        float cxUnChecked = left + mR;//unChecked时候圆形按钮在做成
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
            canvas.drawCircle(cxUnChecked + fraction * moveWidth, cy, mR - mBorderWidth / 2, mPaintBorder);
            canvas.restore();
        } else {
            int evaluate = (int) argbEvaluator.evaluate(fraction, mCheckedColor, mUnCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
            canvas.save();
            canvas.translate(-left, -top);
            canvas.drawRoundRect(rectBackground, mR, mR, mPaintBackground);
            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR, mPaintToggle);
            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR - mBorderWidth / 2, mPaintBorder);
            canvas.restore();
        }
    }

    private void drawCircleWithThin(Canvas canvas, int thinLevel) {
        // 1/2 1/3
        int left = mLeft + mPaddingLeft;
        float top = mTop + mPaddingTop;
        int right = mRight - mPaddingRight;
        float bottom = mBottom - mPaddingBottom;
        float containerTop = 0;
        float containerBottom = 0;
        if (thinLevel == circleWithThin) {
            containerTop = top + mR / 3;
            containerBottom = bottom - mR / 3;
        } else if (thinLevel == circleWithThinner) {
            containerTop = top + mR * 3 / 4;
            containerBottom = bottom - mR * 3 / 4;
        } else if (thinLevel == circleWithThinnest) {
            containerTop = top + mR * 5 / 6;
            containerBottom = bottom - mR * 5 / 6;
            LogUtils.INSTANCE.logD(TAG, "containerBottom=" + containerBottom + "," + containerTop);
        }
        rectBackground = new RectF(left, containerTop, right, containerBottom);
        float containerRadius = (containerBottom - containerTop) / 2;
        LogUtils.INSTANCE.logD(TAG, "containerRadius=" + thinLevel + "," + containerRadius);
        float cy = top + mR;
        float cxUnChecked = left + mR;//unChecked时候圆形按钮在做成
        float cxChecked = right - mR;
        float moveWidth = getWidth() - mPaddingLeft - mPaddingRight - mR * 2;
        if (mChecked) {
            int evaluate = (int) argbEvaluator.evaluate(fraction, mUnCheckedColor, mCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
//            RectF rectBorder = new RectF(left + mBorderWidth / 2 + fraction * moveWidth, top + mBorderWidth / 2, left + mBorderWidth / 2 + mR * 2 - mBorderWidth + fraction * moveWidth, bottom - mBorderWidth / 2);
            canvas.save();
            canvas.translate(-left, -top);
            canvas.drawRoundRect(rectBackground, containerRadius, containerRadius, mPaintBackground);//Toggle椭圆背景
            canvas.drawCircle(cxUnChecked + fraction * moveWidth, cy, mR + 1, mPaintToggle);//Toggle悬浮圆形
//            mPaintShadow.setShadowLayer(5f,3,3,Color.RED);
//            canvas.drawCircle(cxUnChecked + fraction * moveWidth, cy, mR + 10, mPaintShadow);
            canvas.restore();
        } else {
            int evaluate = (int) argbEvaluator.evaluate(fraction, mCheckedColor, mUnCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
            canvas.save();
            canvas.translate(-left, -top);
            canvas.drawRoundRect(rectBackground, containerRadius, containerRadius, mPaintBackground);
            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR + 1, mPaintToggle);
//            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR + 10, mPaintShadow);
            canvas.restore();
        }
    }

    private void drawRectangle(Canvas canvas) {
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
        float containerLeft = mLeft + mPaddingLeft;
        float containerTop = mTop + mPaddingTop;
        float containerRight = mRight - mPaddingRight;
        float containerBottom = mBottom - mPaddingBottom;
        rectBackground = new RectF(containerLeft, containerTop, containerRight, containerBottom);
        float left = mLeft + mPaddingLeft + mBorderWidth;
        float top = mTop + mPaddingTop + mBorderWidth;
        float right = mRight - mPaddingRight - mBorderWidth;
        float bottom = mBottom - mPaddingBottom - mBorderWidth;
        float cy = top + mR;
        float cxUnChecked = left + mR;//unChecked时候圆形按钮在做成
        float cxChecked = right - mR;
        float moveWidth = getWidth() - mPaddingLeft - mPaddingRight - mR * 2 - mBorderWidth * 2;
        float centerY = (top + bottom) / 2;
        mPaintText.setTextSize(25);
        Rect bounds = new Rect();
        mPaintText.getTextBounds("ON", 0, 2, bounds);
//                canvas.drawText("On", left + getWidth() / 2 - bounds.width() / 2, centerY - (bounds.top + bounds.bottom) / 2, mPaintText);
        float textLeft = containerLeft + (containerRight - containerLeft) / 4 - bounds.width() / 2;

        RectF rectF1 = new RectF(containerLeft, containerTop, containerLeft + (containerRight - containerLeft) / 2, containerBottom);
        Rect boundR = new Rect();
        mPaintText.getTextBounds("OFF", 0, 3, boundR);
//                canvas.drawText("On", left + getWidth() / 2 - bounds.width() / 2, centerY - (bounds.top + bounds.bottom) / 2, mPaintText);
//        mPaintText.setColor(Color.YELLOW);
        float textLRight = containerRight - (containerRight - containerLeft) / 4 - boundR.width() / 2;
        int evaluate;
        if (mChecked) {
            evaluate = (int) argbEvaluator.evaluate(fraction, mUnCheckedColor, mCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
            canvas.save();
            canvas.translate(-containerLeft, -containerTop);
            canvas.drawRoundRect(rectBackground, mRectangleCorner, mRectangleCorner, mPaintBackground);//Toggle椭圆背景
            mPaintToggle.setShadowLayer(5f, 2, 2, Color.GRAY);
            RectF rectF = new RectF(left + fraction * moveWidth, top, left + fraction * moveWidth + mR * 2, bottom);//方形
            canvas.drawRoundRect(rectF, mRectangleCorner, mRectangleCorner, mPaintToggle);//方形
            if (fraction == 1) {
                mPaintText.setColor(Color.WHITE);
                canvas.drawText("ON", textLeft, centerY - (bounds.top + bounds.bottom) / 2, mPaintText);
            }
        } else {
            evaluate = (int) argbEvaluator.evaluate(fraction, mCheckedColor, mUnCheckedColor);
            mPaintBackground.setColor(evaluate);
            mPaintBorder.setColor(evaluate);
//            Log.d(TAG, "x=" + (right - mR * 2 + mBorderWidth / 2 - fraction * moveWidth));
//            RectF rectBorder = new RectF(right - mR * 2 + mBorderWidth / 2 - fraction * moveWidth, top + mBorderWidth / 2, right - mBorderWidth / 2 - fraction * moveWidth, bottom - mBorderWidth / 2);
            canvas.save();
            canvas.translate(-containerLeft, -containerTop);
            canvas.drawRoundRect(rectBackground, mRectangleCorner, mRectangleCorner, mPaintBackground);
            mPaintToggle.setShadowLayer(5f, 2, 2, Color.GRAY);
            RectF rectF = new RectF(right - fraction * moveWidth, top, right - fraction * moveWidth - mR * 2, bottom);//方形
            canvas.drawRoundRect(rectF, mRectangleCorner, mRectangleCorner, mPaintToggle);//方形
            if (fraction == 1) {
                mPaintText.setColor(Color.DKGRAY);
                canvas.drawText("OFF", textLRight, centerY - (bounds.top + bounds.bottom) / 2, mPaintText);
            }
//            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR, mPaintToggle);
//            Log.d(TAG, "borderWidth=" + mBorderWidth + ",w/2=" + mBorderWidth / 2);
//            Log.d(TAG, "cx---left=" + (cxChecked - fraction * moveWidth - mR + mBorderWidth / 2));
//            canvas.drawCircle(cxChecked - fraction * moveWidth, cy, mR - mBorderWidth / 2, mPaintBorder);
        }
        canvas.restore();
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
