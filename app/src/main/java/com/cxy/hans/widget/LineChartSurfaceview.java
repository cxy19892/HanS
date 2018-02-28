package com.cxy.hans.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.cxy.hans.Bean.HansBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/11/8.
 */
public class LineChartSurfaceview extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Paint bgPaint;
    private TextPaint tvPaint;
    private Paint ScalePaint;
    private Paint LinsPaint;

    private int BgColor = 0xffffffff;
    private int TvColor = 0xff000000;
    private int PointerColor = 0xffff0000;
    private int ScaleColor = 0xff000000;
    private int BorderLineColor = 0xEE1C1C1C;

    private int width;
    private int height;
    private int vleft;
    private int vtop;
    private int vright;
    private int vbottom;
    private int cleft;
    private int cbottom;

    private SurfaceHolder mHolder; // 用于控制SurfaceView
    private Thread drawThread; // 声明一条线程
    private boolean mIsDrawing ; // 线程运行的标识，用于控制线程
    private Canvas mCanvas; // 声明一张画布
    /** 是否可以滚动，当不在范围时候不可以滚动 */
    private boolean isScroll = true;
    private int lastX;
    private boolean mIsTouch = false;
    /** 偏移量，用来实现平滑移动 */
    private int mOffset = 0;
    /** 移动速度，用来实现速度递减 */
    private int mSpeed = 0;
    /** 移动时候X方向上的速度 */
    private double xVelocity = 0;
    /** 每个x轴刻度的宽度 */
    private int mXScaleWidth = 2;
    /** 时间计数器，用来快速滚动时候减速 */
    private int time = 0;

    private String lastTime;

    private List<HansBean> mlist = new ArrayList<>();

    public LineChartSurfaceview(Context context) {
        super(context);
        initView();
    }

    public LineChartSurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LineChartSurfaceview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public List<HansBean> getMlist() {
        return mlist;
    }

    public void setMlist(List<HansBean> mlist) {
        this.mlist = mlist;
        invalidate();
    }

    private void initView(){
        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听
        setFocusable(true); // 设置焦点
        initPaint();


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 开始绘画
        mIsDrawing = true;
        vleft = 0;
        vbottom = getHeight();
        vright = getWidth();
        vtop = 0;
        width = getWidth();
        height = getHeight();
        cleft = width/10;
        cbottom = height - height/10;
        // 启动绘画线程
        drawThread =  new Thread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 停止绘画
        mIsDrawing = false;
    }
    private void initPaint(){
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(BgColor);

        LinsPaint = new Paint();
        LinsPaint.setAntiAlias(true);
        LinsPaint.setStrokeWidth(2);

        ScalePaint = new Paint();
        ScalePaint.setAntiAlias(true);
        ScalePaint.setStrokeWidth(1);
        ScalePaint.setColor(ScaleColor);

        tvPaint = new TextPaint();
        tvPaint.setAntiAlias(true);
        tvPaint.setColor(TvColor);
        tvPaint.setTextSize(15);
        tvPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            // 设置滚动减速
            setSpeedCut();
            // 绘制方法
            draw();
            try {
                Thread.sleep(2000); // 让线程休息50毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置快速滚动时，末尾的减速
     */
    private void setSpeedCut() {
        if (!mIsTouch && isScroll) {
            // 通过当前速度计算所对应的偏移量
            mOffset = mOffset + mSpeed;
            //当处于边缘时候使值不在变化
            setOffsetRange();
        }
        // 每次偏移量的计算
        if (mSpeed != 0) {
            time++;
            //这里设置的是快速滚动时间不操作两秒
            //可以看成是一个Y轴交点为（0，xVelocity）
            //          x轴交点为（40，xVelocity）
            //          对称轴为40<每个单位时间为50毫秒，40对应两秒>
            mSpeed = (int) (xVelocity + time * time *
                    (xVelocity / 1600.0) - (xVelocity / 20.0) * time);
        } else {
            time = 0;
            mSpeed = 0;
        }

    }

    /** 具体的绘制方法 */
    private void draw() {
        mCanvas = mHolder.lockCanvas();
//        mCanvas.drawColor(BgColor);
        drawBg(mCanvas);
        drawChart(mCanvas);
        drawScal(mCanvas);

        if (mCanvas != null) {
            // 保证每次都将绘制的内容提交到服务器
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) (event.getRawX());
        // 计算当前速度
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        // 计算速度的单位时间
        velocityTracker.computeCurrentVelocity(50);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录触摸点坐标
                lastX = rawX;
                mIsTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算便宜量
                int offsetX = rawX - lastX;
                // 在当前偏移量的基础上增加偏移量
                mOffset = mOffset + offsetX;
                setOffsetRange();
                // 偏移量修改后下次重绘会有变化
                lastX = rawX;
                // 获取X方向上的速度
                xVelocity = velocityTracker.getXVelocity();
                mSpeed = (int) xVelocity;
                break;
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                break;
        }
        // 计算完成后回收内存
        velocityTracker.clear();
        velocityTracker.recycle();
        return true;
    }

    private void drawBg(Canvas canvas){
        canvas.drawColor(BgColor);
    }

    private void drawChart(Canvas canvas){
        for (int i = 0; i < mlist.size(); i++) {
//            LinearGradient linearGradient1 = new LinearGradient(i*2 + mOffset, mlist.get(i).getValue() * cbottom / 120, i*2 + mOffset, cbottom, View.SECTION_COLORS, null,
//                    Shader.TileMode.REPEAT);
//            LinsPaint.setShader(linearGradient1);
            LinsPaint.setColor(Color.RED);
            if((width - width/10) < mlist.size()*2){
//                if(!startMove && isRecord){
                    mOffset = mlist.size()* 2 + 10 - width + cleft;
                    Log.d("chen", "drawChart: mOffset="+mOffset);
//                }
            }
            canvas.drawLine(cleft + i*2  + mOffset, cbottom - (mlist.get(i).getValue() * cbottom / 120), cleft + i*2 + mOffset, cbottom, LinsPaint);
            if(!TextUtils.equals(lastTime, mlist.get(i).getTime())) {
                canvas.drawText(mlist.get(i).getTime(), cleft + i*2 + mOffset, height - height / 20, tvPaint);
                lastTime = mlist.get(i).getTime();
            }
        }
    }

    private void drawScal(Canvas canvas){
        canvas.drawRect(0, 0 , width / 10, height - height/10, bgPaint);
        canvas.drawLine(width / 10, height - height/10, width, height - height/10, ScalePaint);
        canvas.drawLine(width / 10, 0, width / 10, height - height/10, ScalePaint);
        tvPaint.setColor(Color.BLACK);
        for (int i = 0; i < 13; i++) {
            canvas.drawText((i * 10) + "db", width / 20, cbottom - i * (cbottom / 12), tvPaint);
            canvas.drawLine(width / 10, cbottom - i * (cbottom / 12), width / 10 + 20, cbottom - i * (cbottom / 12), ScalePaint);
        }

    }

    /** 对偏移量进行边界值判定 */
    private void setOffsetRange() {
        int offsetMax = -mXScaleWidth * (mlist.size()) + getWidth();
        int offsetMin = 2 * mXScaleWidth;
        if (mOffset > offsetMin) {
            isScroll = false;
            mOffset = offsetMin;
        } else if (mOffset < offsetMax) {// 如果划出最大值范围
            isScroll = false;
            mOffset = offsetMax;
        } else {
            isScroll = true;
        }
    }

}
