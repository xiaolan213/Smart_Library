package com.example.xiaolan.myapplication.Clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.example.xiaolan.myapplication.DensityUtils;
import com.example.xiaolan.myapplication.R;


import java.util.Calendar;


public class MiClockView extends View {

    /* 时钟半径，不包括padding值 */
    private float mRadius;

    /*当前画布*/
    private Canvas mCanvas;

    /*刻度线画笔*/
    private Paint linePaint;
    /* 刻度线长度 */
    private float mLineLength;
    /* 刻度线颜色 */
    private int mLineColor;

    /* 外层圆弧画笔 */
    private Paint mOutCirclePaint;
    /* 外层圆弧画笔 颜色 */
    private int mOutArcColor;
    /* 外层圆弧的外接矩形 */
    private RectF mOutRect = new RectF();

    /* 外环字体画笔 */
    private TextPaint mTextPaint;
    /* 外环字体颜色 */
    private int mTextColor;
    /*外环字体尺寸*/
    private float mTextSize;
    /* 字体外接矩形 */
    private Rect mTextRect = new Rect();


    /* 梯度扫描渐变 */
    private SweepGradient mSweepGradient;
    /* 渐变矩阵，作用在SweepGradient */
    private Matrix mGradientMatrix;

    /* 刻度圆弧画笔 */
    private Paint mLineArcPaint;
    /* 刻度圆弧的外接矩形 */
    private RectF mLineArcRectF = new RectF();


    /*用于分针、秒针、时针色 */
    private int mHourNeedleColor;
    private int mMinuteNeedleColor;
    private int mSecondNeedleColor;


    /* 加一个默认的padding值，为了防止用camera旋转时钟时造成四周超出view大小 */
    private float mDefaultPadding;
    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;

    /* 时针画笔 */
    private Paint mHourHandPaint;
    /* 分针画笔 */
    private Paint mMinuteHandPaint;
    /* 秒针画笔 */
    private Paint mSecondHandPaint;

    /* 时针角度 */
    private float mHourDegree;
    /* 分针角度 */
    private float mMinuteDegree;
    /* 秒针角度 */
    private float mSecondDegree;

    /* 时针路径 */
    private Path mHourHandPath = new Path();
    /* 分针路径 */
    private Path mMinuteHandPath = new Path();
    /* 秒针路径 */
    private Path mSecondHandPath = new Path();

    public MiClockView(Context context) {
        super(context);
    }

    public MiClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0);
        mOutArcColor = ta.getColor(R.styleable.ClockView_clock_outArcColor, Color.parseColor("#237EAD"));
        mHourNeedleColor = ta.getColor(R.styleable.ClockView_clock_hourNeedletColor, Color.parseColor("#4169E1"));
        mMinuteNeedleColor = ta.getColor(R.styleable.ClockView_clock_minuteNeedletColor, Color.parseColor("#4169E1"));
        mSecondNeedleColor = ta.getColor(R.styleable.ClockView_clock_secondNeedletColor, Color.parseColor("#4169E1"));
        mLineColor = ta.getColor(R.styleable.ClockView_clock_lineColor, Color.parseColor("#4169E1"));
        mTextColor = ta.getColor(R.styleable.ClockView_clock_textColor, Color.parseColor("#000000"));
        mTextSize = ta.getDimension(R.styleable.ClockView_clock_textSize, DensityUtils.sp2px(context, 14));
        ta.recycle();


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(mLineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(3);

        mOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setStrokeWidth(1);
        mOutCirclePaint.setColor(mOutArcColor);

        mLineArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineArcPaint.setStyle(Paint.Style.STROKE);

        mGradientMatrix = new Matrix();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(mTextColor);
        //居中绘制文字,不然外环的字体会往右偏
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);


        mSecondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondHandPaint.setStyle(Paint.Style.FILL);
        mSecondHandPaint.setColor(mSecondNeedleColor);

        mHourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourHandPaint.setStyle(Paint.Style.FILL);
        mHourHandPaint.setColor(mHourNeedleColor);

        mMinuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinuteHandPaint.setStyle(Paint.Style.FILL);
        mMinuteHandPaint.setColor(mMinuteNeedleColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(),
                h - getPaddingTop() - getPaddingBottom()) / 2;
        mDefaultPadding = 0.12f * mRadius;
        //这里已有默认的padding值，所以在布局中可以不设置padding的话，取得就是mDefaultPadding
        mPaddingLeft = mDefaultPadding + getPaddingLeft();
        mPaddingRight = mDefaultPadding + getPaddingRight();
        mPaddingTop = mDefaultPadding + getPaddingTop();
        mPaddingBottom = mDefaultPadding + getPaddingBottom();


        mLineLength = 0.12f * mRadius;//根据比例确定刻度线长度

        mLineArcPaint.setStrokeWidth(mLineLength);

        //梯度扫描渐变，以(w/2,h/2)为中心点，两种起止颜色梯度渐变
        //float数组表示，[0,0.75)为起始颜色所占比例，[0.75,1}为起止颜色渐变所占比例
        mSweepGradient = new SweepGradient(w / 2, h / 2,
                new int[]{mOutArcColor, mHourNeedleColor}, new float[]{0.75f, 1});
    }

    public int measureSize(int measureSpec) {
        int defaultSize = 800;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
                Log.i("", "----Width------AT_MOST:");
                return Math.min(size, defaultSize);
            case MeasureSpec.EXACTLY:
                Log.i("", "----Width------EXACTLY:");
                return size;
            case MeasureSpec.UNSPECIFIED:
                Log.i("", "----Width------UNSPECIFIED:");
                return defaultSize;
            default:
                return defaultSize;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        getCurrentTime();
        drawOutSide();
        drawLine(canvas);
        drawSecondNeedle();
        drawMinuteNeedle();
        drawHourHand();
        invalidate();
    }

    /*获取系统当前时间，并将其转换成对应的角度*/
    private void getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        float milliSecord = calendar.get(Calendar.MILLISECOND);
        float second = calendar.get(Calendar.SECOND) + milliSecord / 1000;
        float minute = calendar.get(Calendar.MINUTE) + second / 60;
        float hour = calendar.get(Calendar.HOUR) + minute / 60;
        Log.i("", "----second:" + second + "---minute" + minute + "----hour:" + hour);
        mSecondDegree = second / 60 * 360;
        mMinuteDegree = minute / 60 * 360;
        mHourDegree = hour / 12 * 360;

    }


    /*绘制外环的字体和4段弧线*/
    public void drawOutSide() {
        String strList[] = new String[]{"12", "3", "6", "9",};
        mTextPaint.getTextBounds(strList[1], 0, strList[1].length(), mTextRect);
        mOutRect.set(mPaddingLeft + mTextRect.width() / 2, mPaddingTop + mTextRect.height() / 2, getWidth() - mPaddingRight - mTextRect.width() / 2, getHeight() - mPaddingBottom - mTextRect.height() / 2);

        mCanvas.drawText(strList[0], getWidth() / 2, mOutRect.top + mTextRect.height() / 2, mTextPaint);
        mCanvas.drawText(strList[1], mOutRect.right, getHeight() / 2 + mTextRect.height() / 2, mTextPaint);
        mCanvas.drawText(strList[2], getWidth() / 2, mOutRect.bottom + mTextRect.height() / 2, mTextPaint);
        mCanvas.drawText(strList[3], mOutRect.left, getHeight() / 2 + mTextRect.height() / 2, mTextPaint);


        for (int i = 0; i < strList.length; i++) {
            mCanvas.drawArc(mOutRect, 90 * i + 5, 80, false, mOutCirclePaint);
        }
    }


    /*绘制刻度线，以及渐变效果*/
    public void drawLine(Canvas canvas) {
        canvas.save();
        canvas.translate(0, 0);
        mLineArcRectF.set(mPaddingLeft + 1f * mLineLength + mTextRect.height(),
                mPaddingTop + mTextRect.height() + mLineLength * 1f,
                getWidth() - mPaddingRight - mTextRect.height() - 1f * mLineLength,
                getHeight() - mPaddingBottom - mTextRect.height() - 1f * mLineLength);


        //matrix默认会在三点钟方向开始颜色的渐变，为了吻合钟表十二点钟顺时针旋转的方向，把秒针旋转的角度减去90度
        mGradientMatrix.setRotate(mSecondDegree - 90, getWidth() / 2, getHeight() / 2);
        mSweepGradient.setLocalMatrix(mGradientMatrix);
        mLineArcPaint.setShader(mSweepGradient);
        //绘制矩形内接弧形时弧形的宽度刚好位于矩形边长中心线的两边
        mCanvas.drawArc(mLineArcRectF, 0, 360, false, mLineArcPaint);

        for (int i = 0; i < 200; i++) {
            canvas.drawLine(getWidth() / 2, mPaddingTop + mTextRect.height() + mLineLength * 0.5f,
                    getWidth() / 2, mPaddingTop + mTextRect.height() + mLineLength * 1.5f, linePaint);
            canvas.rotate(1.8f, getWidth() / 2, getHeight() / 2);
        }
        mCanvas.restore();
    }


    /*绘制秒针*/
    private void drawSecondNeedle() {
        mCanvas.save();
        mCanvas.rotate(mSecondDegree, getWidth() / 2, getHeight() / 2);
        mSecondHandPath.reset();
        float offset = mPaddingTop + mTextRect.height() / 2;

        mSecondHandPath.moveTo(getWidth() / 2, offset + mRadius * 0.27f);
        mSecondHandPath.lineTo(getWidth() / 2 - 0.05f * mRadius, offset + mRadius * 0.33f);
        mSecondHandPath.lineTo(getWidth() / 2 + 0.05f * mRadius, offset + mRadius * 0.33f);
        mSecondHandPath.close();
        mCanvas.drawPath(mSecondHandPath, mSecondHandPaint);
        mCanvas.restore();
    }


    /**
     * 绘制分针
     */
    private void drawMinuteNeedle() {
        mCanvas.save();
        mCanvas.rotate(mMinuteDegree, getWidth() / 2, getHeight() / 2);
        mMinuteHandPath.reset();

        float offset = mPaddingTop + mTextRect.height() / 2;
        mMinuteHandPath.moveTo(getWidth() / 2 - mRadius * 0.01f, getHeight() / 2 - mRadius * 0.03f);
        mMinuteHandPath.lineTo(getWidth() / 2 - mRadius * 0.008f, offset + mRadius * 0.435f);
        //这里是绘制二阶贝塞尔曲线，将分针的头部绘制圆弧
        mMinuteHandPath.quadTo(getWidth() / 2, offset + mRadius * 0.415f, getWidth() / 2 + mRadius * 0.008f, offset + mRadius * 0.435f);
        mMinuteHandPath.lineTo(getWidth() / 2 + mRadius * 0.01f, getHeight() / 2 - mRadius * 0.03f);
        mMinuteHandPath.close();

        mMinuteHandPaint.setStyle(Paint.Style.FILL);
        mMinuteHandPaint.setColor(mMinuteNeedleColor);
        mCanvas.drawPath(mMinuteHandPath, mMinuteHandPaint);

        mOutRect.set(getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius,
                getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius);
        mMinuteHandPaint.setStyle(Paint.Style.STROKE);
        mMinuteHandPaint.setStrokeWidth(0.02f * mRadius);
        mMinuteHandPaint.setColor(Color.WHITE);
        mCanvas.drawArc(mOutRect, 0, 360, false, mMinuteHandPaint);
        mCanvas.restore();

    }

    /**
     * 绘制时针
     */
    private void drawHourHand() {
        mCanvas.save();
        mCanvas.rotate(mHourDegree, getWidth() / 2, getHeight() / 2);
        mHourHandPath.reset();
        float offset = mPaddingTop + mTextRect.height() / 2;
        mHourHandPath.moveTo(getWidth() / 2 - 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 - 0.009f * mRadius, offset + 0.58f * mRadius);
        mHourHandPath.quadTo(getWidth() / 2, offset + 0.56f * mRadius,
                getWidth() / 2 + 0.009f * mRadius, offset + 0.58f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 + 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mHourHandPath.close();
        mHourHandPaint.setStyle(Paint.Style.FILL);
        mHourHandPaint.setColor(mHourNeedleColor);
        mCanvas.drawPath(mHourHandPath, mHourHandPaint);

        mOutRect.set(getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius,
                getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius);
        mHourHandPaint.setStyle(Paint.Style.STROKE);
        mHourHandPaint.setStrokeWidth(0.02f * mRadius);
        mHourHandPaint.setColor(Color.WHITE);
        mCanvas.drawArc(mOutRect, 0, 360, false, mHourHandPaint);
        mCanvas.restore();
    }

}
