package com.cxy.hans.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.cxy.hans.Bean.HansBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 表盘
 * Created by hasee on 2017/11/6.
 */
public class LineChartView extends View{

    private int BgColor = 0xffffffff;
    private int TvColor = 0xff000000;
    private int PointerColor = 0xffff0000;
    private int ScaleColor = 0xff000000;
    private int BorderLineColor = 0xEE1C1C1C;
    /*private int BgColor = 0xEE455A64;
    private int TvColor = 0xFFCFD8DC;
    private int PointerColor = 0xff8B2323;
    private int ScaleColor = 0xFF757575;
    private int BorderLineColor = 0xEECD2626;*/
    private int width;
    private int height;
    private int vleft;
    private int vtop;
    private int vright;
    private int vbottom;
    private int cleft;
    private int cbottom;

    private Paint bgPaint;
    private TextPaint tvPaint;
    private Paint ScalePaint;
    private Paint LinsPaint;

    private List<HansBean> mlist = new ArrayList<>();

    private float offsetX = 0;
    private float oldX;
    private float startX;
    private float startY;
    //改变值大小可改变滑动速度
    private float offsetValue = 15;
    private boolean startMove = false;
    private boolean isRecord = false;
    private String lastTime;


    private static final int[] SECTION_COLORS_DAYS = {0xFF3F51B5,0xFF3F51B5};
    private static final int[] SECTION_COLORS_NIGHT = {0xFFF57C00,0xFFF57C00};
    private int[] SECTION_COLORS = SECTION_COLORS_DAYS;

    public LineChartView(Context context) {
        super(context);
        initPaint();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public List<HansBean> getMlist() {
        return mlist;
    }

    public void setMlist(List<HansBean> mlist) {
        this.mlist = mlist;
        invalidate();
    }

    public void setIsNight(boolean isnight){
        if(isnight){
            BgColor = 0xFFBDBDBD;
            TvColor = 0xFFCFD8DC;
            PointerColor = 0xff8B2323;
            ScaleColor = 0xFF212121;
            BorderLineColor = 0xEECD2626;
            SECTION_COLORS = SECTION_COLORS_NIGHT;
        }else{
            BgColor = 0xffffffff;
            TvColor = 0xff000000;
            PointerColor = 0xffff0000;
            ScaleColor = 0xff000000;
            BorderLineColor = 0xEE1C1C1C;
            SECTION_COLORS = SECTION_COLORS_DAYS;
        }
        initPaint();
        postInvalidate();
    }
    
    public void addData(HansBean data){
        isRecord = true;
        if(mlist == null){
            mlist = new ArrayList<>();
        }
        mlist.add(data);
        invalidate();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        vleft = left;
        vbottom = bottom;
        vright = right;
        vtop = top;
        width = right - left;
        height = bottom - top;
        cleft = width/10;
        cbottom = height - height/10;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawChart(canvas);
        drawScal(canvas);
    }

    VelocityTracker velocityTracker;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = oldX = event.getRawX();
                startY = event.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                velocityTracker = VelocityTracker.obtain();
                startMove = true;
//                Log.d("chen", "dispatchTouchEvent: ACTION_DOWN offsetX="+offsetX);
                break;
            case MotionEvent.ACTION_MOVE:
                float rawX = event.getX();
                float rawY = event.getY();
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                float deltaX=rawX - startX;
                float deltaY=rawY - startY;
                if(Math.abs(deltaX) < Math.abs(deltaY)){
                    getParent().requestDisallowInterceptTouchEvent(false);
                }else {
                    if (mlist != null && mlist.size()*2 > (width - 10)) {
                        if ((rawX - oldX) < 0) {
                            offsetX -= Math.abs(deltaX) * 10;
//                            Log.d("chen", "dispatchTouchEvent: 向左滑"+ (- mlist.size()* 2));
                        }
                        else {
                            offsetX += Math.abs(deltaX) * 10;
                        }
                        if(offsetX < - mlist.size()* 2+ width){
                            offsetX = - mlist.size()* 2 + width;
                        }
                        invalidate(cleft, vtop, vright, vbottom);
//                        scrollBy((int)-deltaX,0);
                    } else {
                        offsetX = 0;
                    }
                    if (offsetX > 0)
                        offsetX = 0;
//                    Log.d("chen", "dispatchTouchEvent: offsetX="+offsetX);
                    startX = rawX;
                    startY = rawY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startMove = false;
                velocityTracker.clear();
                velocityTracker.recycle();
                break;
            default:
                break;
        }
        return true;
    }

    private void drawBg(Canvas canvas){
        canvas.drawColor(BgColor);
    }

    private void drawChart(Canvas canvas){
        if(!isRecord && offsetX / 2 < mlist.size()){
            for (int i = (int) -(offsetX / 2); i < ((offsetX / 2 + width * 9 /20 < mlist.size()) ? (- offsetX / 2 + width * 9 /20) : mlist.size()); i++) {
//                Log.d("chen", "if drawChart: i = "+i);
                LinearGradient linearGradient1 = new LinearGradient(i*2 + offsetX, mlist.get(i).getValue() * cbottom / 100, i*2 + offsetX, cbottom, SECTION_COLORS, null,
                        Shader.TileMode.REPEAT);
                LinsPaint.setShader(linearGradient1);
                canvas.drawLine(cleft + i*2  + offsetX, cbottom - (mlist.get(i).getValue() * cbottom / 100), cleft + i*2 + offsetX, cbottom, LinsPaint);
                if(!TextUtils.equals(lastTime, mlist.get(i).getTime())) {
                    canvas.drawText(mlist.get(i).getTime(), cleft + i*2 + offsetX, height - height / 20, tvPaint);
                    lastTime = mlist.get(i).getTime();
                }
            }
        }else if(isRecord) {
            if (mlist.size()*2 < width * 9 / 10) {
                for (int i = 0; i < mlist.size(); i++) {
//                    Log.d("chen", "drawChart: not fill offsetX="+offsetX+"mlist.size()="+mlist.size()+"width * 9 / 10="+width * 9 / 10+"  ------"+i);
                    LinearGradient linearGradient1 = new LinearGradient(i * 2 + offsetX, mlist.get(i).getValue() * cbottom / 100, i * 2 + offsetX, cbottom, SECTION_COLORS, null,
                            Shader.TileMode.REPEAT);
                    LinsPaint.setShader(linearGradient1);
                    canvas.drawLine(cleft + i * 2 + offsetX, cbottom - (mlist.get(i).getValue() * cbottom / 100), cleft + i * 2 + offsetX, cbottom, LinsPaint);
                    if (!TextUtils.equals(lastTime, mlist.get(i).getTime())) {
                        canvas.drawText(mlist.get(i).getTime(), cleft + i * 2 + offsetX, height - height / 20, tvPaint);
                        lastTime = mlist.get(i).getTime();
                    }
                }
            }else{
                for (int i = (int) (mlist.size()*2 - width * 9 / 10 + offsetX); i < mlist.size(); i++) {
//                    Log.d("chen", "drawChart: fill offsetX="+offsetX+"  ------"+i);
                    LinearGradient linearGradient1 = new LinearGradient(i * 2 + offsetX, mlist.get(i).getValue() * cbottom / 100, i * 2 + offsetX, cbottom, SECTION_COLORS, null,
                            Shader.TileMode.REPEAT);
                    LinsPaint.setShader(linearGradient1);
                    if (!startMove) {
                        offsetX = width * 9 / 10 /*10 + width - cleft*/ - mlist.size() * 2;
                    }
                    canvas.drawLine(cleft + i * 2 + offsetX, cbottom - (mlist.get(i).getValue() * cbottom / 100), cleft + i * 2 + offsetX, cbottom, LinsPaint);
                    if (!TextUtils.equals(lastTime, mlist.get(i).getTime())) {
                        canvas.drawText(mlist.get(i).getTime(), cleft + i * 2 + offsetX, height - height / 20, tvPaint);
                        lastTime = mlist.get(i).getTime();
                    }
                }
            }

        }
        /*for (int i = 0; i < mlist.size(); i++) {
            LinearGradient linearGradient1 = new LinearGradient(i*2 + offsetX, mlist.get(i).getValue() * cbottom / 120, i*2 + offsetX, cbottom, SECTION_COLORS, null,
                    Shader.TileMode.REPEAT);
            LinsPaint.setShader(linearGradient1);
            if((width - width/10) < mlist.size()*2){
                if(!startMove && isRecord){
                    offsetX = mlist.size()* 2 + 10 - width + cleft;
                    Log.d("chen", "drawChart: offsetX="+offsetX);
                }
            }
            canvas.drawLine(cleft + i*2  + offsetX, cbottom - (mlist.get(i).getValue() * cbottom / 120), cleft + i*2 + offsetX, cbottom, LinsPaint);
            if(!TextUtils.equals(lastTime, mlist.get(i).getTime())) {
                canvas.drawText(mlist.get(i).getTime(), cleft + i*2 + offsetX, height - height / 20, tvPaint);
                lastTime = mlist.get(i).getTime();
            }
        }*/
    }

    private void drawScal(Canvas canvas){
        canvas.drawRect(0, 0 , width / 10, height - height/10, bgPaint);
        canvas.drawLine(width / 10, height - height/10, width, height - height/10, ScalePaint);
        canvas.drawLine(width / 10, 0, width / 10, height - height/10, ScalePaint);
        tvPaint.setColor(Color.BLACK);
        for (int i = 0; i < 11; i++) {
            canvas.drawText((i * 10) + "db", width / 20, cbottom - i * (cbottom / 10), tvPaint);
            canvas.drawLine(width / 10, cbottom - i * (cbottom / 10), width / 10 + 20, cbottom - i * (cbottom / 10), ScalePaint);
        }
        
    }
}
