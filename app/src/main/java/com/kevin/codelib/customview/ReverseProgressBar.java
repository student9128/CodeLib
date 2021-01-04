package com.kevin.codelib.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.kevin.codelib.R;
import com.kevin.codelib.util.DensityUtil;

import java.text.DecimalFormat;

/**
 * author：lkt
 * 时间：2020/10/29 9:25
 * 功能：字体颜色反转进度条
 */
public class ReverseProgressBar extends View {
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private int mWidth;
    private int mHeight;
    private int boundWidth = 6;
    private RectF mRoundRectF;
    private RectF mRect;
    private Paint mPaintbg;
    private Paint mPaintTextbg;
    private Paint mPaintProgress;
    private Paint mPaintTextProgress;
    private String content = "下载进度0.0%";
    /**
     * 默认最大进度
     */
    private float maxProgress = 100f;
    private float progress = 0f;
    private float radius = 12f;
    private float textSize = 16f;
    private int mProgressBgColor;
    private int mProgressBgTextColor;
    private int mProgressScColor;
    private int mProgressScTextColor;

    public ReverseProgressBar(Context context) {
        this(context, null);
    }

    public ReverseProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReverseProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ReverseProgressBar);
        try {

            progress = array.getFloat(R.styleable.ReverseProgressBar_progressX, 0f);
            radius = array.getDimension(R.styleable.ReverseProgressBar_radiusX, 12f);
            textSize = array.getDimension(R.styleable.ReverseProgressBar_contentTextSize, 16f);
            //第一层背景和边框的颜色
            mProgressBgColor = array.getColor(R.styleable.ReverseProgressBar_progressBgColor, Color.parseColor("#00ff00"));
            mProgressBgTextColor = array.getColor(R.styleable.ReverseProgressBar_progressBgTextColor, Color.parseColor("#ff00ff"));
            mProgressScColor = array.getColor(R.styleable.ReverseProgressBar_progressScColor, Color.parseColor("#ff0000"));
            mProgressScTextColor = array.getColor(R.styleable.ReverseProgressBar_progressScTextColor, Color.parseColor("#0000ff"));
        } finally {
            array.recycle();
        }

        mPaintbg = new Paint();
        mPaintbg.setStrokeWidth(boundWidth);
        mPaintbg.setAntiAlias(true);
        mPaintbg.setColor(mProgressBgColor);
        mPaintbg.setStyle(Paint.Style.STROKE);

        mPaintTextbg = new Paint();
        mPaintTextbg.setAntiAlias(true);
        mPaintTextbg.setColor(mProgressBgTextColor);
        mPaintTextbg.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaintProgress = new Paint();
        mPaintProgress.setStrokeWidth(boundWidth);
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setColor(mProgressScColor);
        mPaintProgress.setStyle(Paint.Style.STROKE);

        mPaintTextProgress = new Paint();
        mPaintTextProgress.setAntiAlias(true);
        mPaintTextProgress.setColor(mProgressScTextColor);
        mPaintTextProgress.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = DensityUtil.dp2px(getContext(), 36);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);
        mWidth = widthSpecSize;
        mHeight = height;
        if (progress >= maxProgress) {
            progress = maxProgress;
        }
        mRect = new RectF(boundWidth / 2, boundWidth / 2, mWidth - boundWidth / 2, mHeight - boundWidth / 2);
        mRoundRectF = new RectF(boundWidth / 2, boundWidth / 2, mWidth * (progress / maxProgress) - boundWidth / 2, mHeight - boundWidth / 2);
        mPaintTextbg.setTextSize(textSize);
        mPaintTextProgress.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.parseColor("#000000"));
        drawRectF(canvas);
        drawText(canvas);
        drawProgress(canvas);
        drawColorText(canvas);
    }

    /**
     * 画第一层背景
     *
     * @param canvas
     */
    private void drawRectF(Canvas canvas) {
        mPaintbg.setColor(mProgressBgColor);
        mPaintbg.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(mRect, radius, radius, mPaintbg);
    }

    /**
     * 画进度相关的文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        content = getStr();
        float v = mPaintTextbg.measureText(content);
        canvas.drawText(content, mWidth / 2 - v / 2, mHeight / 2 + mPaintTextbg.getTextSize() / 3, mPaintTextbg);
    }

    /**
     * 画进度
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        mPaintbg.setColor(mProgressScColor);
        mPaintbg.setStyle(Paint.Style.FILL_AND_STROKE);
        mRoundRectF.right = mWidth * (progress / maxProgress) - boundWidth / 2;
        mRoundRectF.left = boundWidth / 2;
        mRoundRectF.top = boundWidth / 2;
        mRoundRectF.bottom = mHeight - boundWidth / 2;
        canvas.drawRoundRect(mRoundRectF, radius, radius, mPaintbg);
    }

    /**
     * 画变色的文字
     *
     * @param canvas
     */
    private void drawColorText(Canvas canvas) {
        canvas.save();
        content = getStr();
        float v = mPaintTextProgress.measureText(content);
        canvas.clipRect(mRoundRectF);
        canvas.drawText(content, mWidth / 2 - v / 2, mHeight / 2 + +mPaintTextbg.getTextSize() / 3, mPaintTextProgress);
        canvas.restore();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (progress > 100 || progress < 0) {
            return;
        }
        postInvalidate();
    }

    private String getStr() {
        float rate = progress / maxProgress;
        String rateStr = decimalFormat.format(100 * rate);
        content = "下载进度" + rateStr + "%";
        return content;
    }
}
