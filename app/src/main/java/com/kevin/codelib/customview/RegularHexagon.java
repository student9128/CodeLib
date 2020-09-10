package com.kevin.codelib.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kevin.codelib.R;


/**
 * auther：lkt
 * 时间：2020/8/20 10:18
 * 功能：
 */
public class RegularHexagon extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint, mPaint, sexPaint, percentegPaint;

    /**
     * 画笔路径
     */
    private Path path, mPath;

    /**
     * 环的颜色
     */
    private int roundColor;

    /**
     * 环进度的颜色
     */
    private int roundProgressColor;
    /**
     * 文字的颜色
     */
    private int contentColor;
    /**
     * 要显示的文字
     */
    private String content;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 环的宽度
     */
    private float roundWidth;

    /**
     * 当前进度
     */
    private double progress;

    public RegularHexagon(Context context) {
        this(context, null);
    }

    public RegularHexagon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegularHexagon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        mPaint = new Paint();
        sexPaint = new Paint();
        percentegPaint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HexagonProgressBar);

        // 获取自定义属性和默认值
        roundColor = mTypedArray.getColor(
                R.styleable.HexagonProgressBar_hexagonColor, getResources().getColor(R.color.FF514D50));
        roundProgressColor = mTypedArray.getColor(
                R.styleable.HexagonProgressBar_hexagonProgressColor, getResources().getColor(R.color.FF13E7FF));
        contentColor = mTypedArray.getColor(
                R.styleable.HexagonProgressBar_contentColor, getResources().getColor(R.color.FF13E7FF));
        content = mTypedArray.getString(R.styleable.HexagonProgressBar_text);
        textColor = mTypedArray.getColor(
                R.styleable.HexagonProgressBar_textColor, getResources().getColor(R.color.FF13E7FF));
        textSize = mTypedArray.getDimension(
                R.styleable.HexagonProgressBar_textSize, 12);
        roundWidth = mTypedArray.getDimension(
                R.styleable.HexagonProgressBar_hexagonWidth, 4);
        progress = mTypedArray.getInt(
                R.styleable.HexagonProgressBar_progress, 0);

        mPaint.setColor(roundColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(roundWidth);
        mPaint.setAntiAlias(true);

        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundProgressColor);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = (int) (center - roundWidth / 2);

        mPath = new Path();
        path = new Path();

        mPath.moveTo(center - radius, center);
        mPath.lineTo(center - radius / 2, (float) (center - Math.sqrt(3) * radius / 2));
        mPath.lineTo(center + radius / 2, (float) (center - Math.sqrt(3) * radius / 2));
        mPath.lineTo(center + radius, center);
        mPath.lineTo(center + radius / 2, (float) ((Math.sqrt(3) * radius / 2) + center));
        mPath.lineTo(center - radius / 2, (float) ((Math.sqrt(3) * radius / 2) + center));
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        /**
         * 画六边形进度
         */
        linePath(progress, center, radius);
        canvas.drawPath(path, paint);

        /**
         * 画进度百分比
         */
        sexPaint.setStrokeWidth(10);
        percentegPaint.setStrokeWidth(10);
        sexPaint.setColor(contentColor);
        percentegPaint.setColor(textColor);
        sexPaint.setTextSize(textSize);
        percentegPaint.setTextSize(textSize);
        sexPaint.setTypeface(Typeface.DEFAULT_BOLD);
        percentegPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int) (((float) progress / (float) 100) * 100);
        float textWidth = sexPaint.measureText(String.valueOf(percent) + "%");
        float textWidthSex = sexPaint.measureText(content);
        Log.d("textWidthSex", String.valueOf(textWidthSex/(content.length())));
        Log.d("textWidthSex=", String.valueOf(textSize));
        canvas.drawText(content, (float) (center - textWidthSex *0.5), (float) (center - textSize/2), sexPaint);
        canvas.drawText(percent + "%", (float) (center - textWidth *0.5), center + textSize, percentegPaint);


    }


    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(double progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 100) {
            progress = 100;
        }
        if (progress <= 100) {
            this.progress = progress;
            postInvalidate();
        }
    }


    private void linePath(double progress, float centre, float radius) {
        double k = progress / 100 * 12;
        if (k <= 0) {
            return;
        }

        if (k <= 1) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2 * k), (float) (centre - radius / 2 * Math.sqrt(3)));
        } else if (k > 1 && k <= 3) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2 + (k - 1) * (radius / 2) / 2), (float) (centre - radius / 2 * Math.sqrt(3) + (k - 1) * (radius / 2) / 2 * Math.sqrt(3)));
        } else if (k > 3 && k <= 5) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius), (float) centre);
            path.lineTo((float) (centre + radius - (k - 3) * (radius / 2) / 2), (float) (centre + ((k - 3) * (radius / 2) / 2 * Math.sqrt(3))));
        } else if (k > 5 && k <= 7) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius), (float) centre);
            path.lineTo((float) (centre + radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2 - (k - 5) * (radius / 2)), (float) (centre + radius / 2 * Math.sqrt(3)));
        } else if (k > 7 && k <= 9) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius), (float) centre);
            path.lineTo((float) (centre + radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius / 2 - (k - 7) * (radius / 2) / 2), (float) (centre + radius / 2 * Math.sqrt(3) - ((k - 7) * (radius / 2) / 2) * Math.sqrt(3)));
        } else if (k > 9 && k <= 11) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius), (float) centre);
            path.lineTo((float) (centre + radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius), (float) centre);
            path.lineTo((float) (centre - radius + (k - 9) * (radius / 2) / 2), (float) (centre - ((k - 9) * (radius / 2) / 2 * Math.sqrt(3))));
        } else if (k > 11 && k <= 12) {
            path.moveTo(centre, (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre + radius), (float) centre);
            path.lineTo((float) (centre + radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius / 2), (float) (centre + radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius), (float) centre);
            path.lineTo((float) (centre - radius / 2), (float) (centre - radius / 2 * Math.sqrt(3)));
            path.lineTo((float) (centre - radius / 2 + (k - 11) * (radius / 2)), (float) (centre - radius / 2 * Math.sqrt(3)));
        } else {
            path.close();
        }
    }
}
