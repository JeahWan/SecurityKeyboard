package com.makise.mk_keyboard.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

/**
 * 绘制logo的自定义view
 * <p>
 * 成功
 * point1(52,29)
 * point2(64,56)
 * point3(97,0)
 * point4(0,0)
 * point4(60,104)
 * point4(120,0)
 * <p>
 * 失败
 * point1(34,86)
 * point2(90,0)
 * point3(120,104)
 * point4(0,104)
 * point5(30,0)
 * point6(86,86)
 * Created by Makise on 2016/8/26.
 */
public class LogoView extends View {
    private Context mContext;
    private Paint mPaint;
    private float xxx;
    private float yyy;
    private AnimatorSet animSet;

    private Point[] points;

    private int lineDrawState;

    private LogoViewListener listener;

    private Path path;
    //可绘制
    private boolean canDraw;

    public LogoView(Context context) {
        super(context);
    }

    public LogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dpToPx(2));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new CornerPathEffect(3));
        points = new Point[6];
        animSet = new AnimatorSet();
        path = new Path();
    }

    public void setListener(LogoViewListener listener) {
        this.listener = listener;
    }

    public void start(boolean isSuccess) {
        if (isSuccess) {
            points[0] = new Point(dpToPx(52) / 2f, dpToPx(29) / 2f);
            points[1] = new Point(dpToPx(64) / 2f, dpToPx(56) / 2f);
            points[2] = new Point(dpToPx(97) / 2f, dpToPx(0) / 2f);
            points[3] = new Point(dpToPx(0) / 2f, dpToPx(0) / 2f);
            points[4] = new Point(dpToPx(60) / 2f, dpToPx(104) / 2f);
            points[5] = new Point(dpToPx(120) / 2f, dpToPx(0) / 2f);
        } else {
            points[0] = new Point(dpToPx(34) / 2f, dpToPx(86) / 2f);
            points[1] = new Point(dpToPx(90) / 2f, dpToPx(0) / 2f);
            points[2] = new Point(dpToPx(120) / 2f, dpToPx(104) / 2f);
            points[3] = new Point(dpToPx(0) / 2f, dpToPx(104) / 2f);
            points[4] = new Point(dpToPx(30) / 2f, dpToPx(0) / 2f);
            points[5] = new Point(dpToPx(86) / 2f, dpToPx(86) / 2f);
        }
        ValueAnimator[] va = addAnim(points[0], points[1], 1);
        ValueAnimator[] va2 = addAnim(points[1], points[2], 2);
        ValueAnimator[] va3 = addAnim(points[2], points[3], 3);
        ValueAnimator[] va4 = addAnim(points[3], points[4], 4);
        ValueAnimator[] va5 = addAnim(points[4], points[5], 5);


        animSet.play(va[0]).with(va[1]);
        animSet.play(va2[0]).after(va[0]);
        animSet.play(va2[0]).with(va2[1]);
        animSet.play(va3[0]).after(va2[0]);
        animSet.play(va3[0]).with(va3[1]);

        animSet.play(va4[0]).after(va3[0]);
        animSet.play(va4[0]).with(va4[1]);

        animSet.play(va5[0]).after(va4[0]);
        animSet.play(va5[0]).with(va5[1]);

        animSet.start();

        canDraw = true;
    }

    public ValueAnimator[] addAnim(Point fromPoint, Point toPoint, final int position) {
        ValueAnimator xVA = ObjectAnimator.ofFloat(fromPoint, "x", fromPoint.x, toPoint.x);
        xVA.setDuration(150);
        xVA.setInterpolator(new DecelerateInterpolator());
        xVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xxx = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        xVA.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lineDrawState = position;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        ValueAnimator yVA = ObjectAnimator.ofFloat(fromPoint, "y", fromPoint.y, toPoint.y);
        yVA.setDuration(150);
        yVA.setInterpolator(new DecelerateInterpolator());
        yVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                yyy = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        yVA.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lineDrawState = position;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return new ValueAnimator[]{xVA, yVA};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!canDraw) return;
        //画布向下向左移动2 防止绘制出界
        canvas.translate(3, 3);
        path.moveTo(points[0].x, points[0].y);
        switch (lineDrawState) {
            case 1:
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(xxx, yyy);
                canvas.drawPath(path, mPaint);
                break;
            case 2:
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(points[2].x, points[2].y);
                path.lineTo(xxx, yyy);
                canvas.drawPath(path, mPaint);
                break;
            case 3:
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(points[2].x, points[2].y);
                path.lineTo(points[3].x, points[3].y);
                path.lineTo(xxx, yyy);
                canvas.drawPath(path, mPaint);
                break;
            case 4:
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(points[2].x, points[2].y);
                path.lineTo(points[3].x, points[3].y);
                path.lineTo(points[4].x, points[4].y);
                path.lineTo(xxx, yyy);
                canvas.drawPath(path, mPaint);
                break;
            case 5:
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(points[2].x, points[2].y);
                path.lineTo(points[3].x, points[3].y);
                path.lineTo(points[4].x, points[4].y);
                path.lineTo(points[5].x, points[5].y);
                path.lineTo(xxx, yyy);
                canvas.drawPath(path, mPaint);
                if (listener != null && xxx == points[5].x)
                    listener.done();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = dpToPxInt(65);
        int desiredHeight = dpToPxInt(65);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public float dpToPx(float dp) {
        if (mContext == null) {
            return -1;
        }
        return dp * mContext.getResources().getDisplayMetrics().density;
    }

    public float pxToDp(float px) {
        if (mContext == null) {
            return -1;
        }
        return px / mContext.getResources().getDisplayMetrics().density;
    }

    public int dpToPxInt(float dp) {
        return (int) (dpToPx(dp) + 0.5f);
    }

    public int pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(px) + 0.5f);
    }

    public interface LogoViewListener {
        //提供给外部使用的接口，表示图形完整绘制结束
        void done();
    }

    public class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}