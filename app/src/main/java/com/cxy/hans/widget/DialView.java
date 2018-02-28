package com.cxy.hans.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hasee on 2017/11/6.
 */
public class DialView extends View {
    private int width;
    private int height;
    private int vleft;
    private int vtop;
    private int vright;
    private int vbottom;

    private Paint bgPaint;
    private TextPaint tvPaint;
    private Paint PointerPaint;
    private Paint scalePaint;
    private int scale = 10;

    private int BgColor = 0xffffffff;
    private int TvColor = 0xff000000;
    private int PointerColor = 0xffff0000;
    private int ScaleColor = 0xff000000;
    private int BorderLineColor = 0xEE1C1C1C;
    private int value = 0;

    public DialView(Context context) {
        super(context);
        initView();
    }

    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setBgColor(int bgColor) {
        BgColor = bgColor;
    }

    public void setTvColor(int tvColor) {
        TvColor = tvColor;
    }

    public void setPointerColor(int pointerColor) {
        PointerColor = pointerColor;
    }

    public void setScaleColor(int scaleColor) {
        ScaleColor = scaleColor;
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public void setIsNight(boolean isnight){
        if(isnight){
            /*BgColor = 0xEE000080;
            TvColor = 0xffC2C2C2;
            PointerColor = 0xff8B2323;
            ScaleColor = 0xffA6A6A6;
            BorderLineColor = 0xEECD2626;*/
            BgColor = 0xFFBDBDBD;
            TvColor = 0xFF212121;
            PointerColor = 0xEE303F9F;
            ScaleColor = 0xFF212121;
            BorderLineColor = 0xEE303F9F;
        }else{
            BgColor = 0xffffffff;
            TvColor = 0xff000000;
            PointerColor = 0xffff0000;
            ScaleColor = 0xff000000;
            BorderLineColor = 0xEE1C1C1C;
        }
        initView();
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = right - left;
        int h = bottom - top;
        int min = w > h ? h : w;
        width = height = min;
        vleft = w - min/2;
        vright= w + min/2;
        vtop  = 0;
        vbottom = min;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDial(canvas);
        drawPointer(canvas);
        bgPaint.setStrokeWidth(scale);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(BorderLineColor);
        canvas.drawCircle(width/2, height / 2 , scale, bgPaint);
    }

    private void initView(){
        bgPaint = new Paint();
        bgPaint.setColor(BgColor);
        bgPaint.setAntiAlias(true);

        tvPaint = new TextPaint();
        tvPaint.setAntiAlias(true);
        tvPaint.setColor(TvColor);
        tvPaint.setStrokeWidth(20);
        tvPaint.setTextAlign(Paint.Align.CENTER);

        scalePaint = new Paint();
        scalePaint.setAntiAlias(true);
        scalePaint.setStrokeWidth(2);
        scalePaint.setColor(ScaleColor);

        PointerPaint = new Paint(bgPaint);
        PointerPaint.setColor(PointerColor);
        PointerPaint.setAntiAlias(true);
        PointerPaint.setStyle(Paint.Style.FILL);
    }

    private void drawDial(Canvas canvas){
        tvPaint.setTextSize(15);
        bgPaint.setColor(BorderLineColor);
        canvas.drawCircle(width/2, height / 2 , width/2, bgPaint);
        bgPaint.setColor(BgColor);
        canvas.drawCircle(width/2, height / 2 , width/2 - scale/2, bgPaint);
        for (int i = 0; i < 121; i++) {
            canvas.save();
            canvas.rotate(-120 + (240 / 120f) *i , width/2, width/2);
            canvas.drawLine(width/2, scale, width/2 , scale *2 , scalePaint);
            if(i % 10 == 0){
                canvas.drawLine(width/2, scale, width/2 , scale *4 , scalePaint);
                canvas.drawText(""+i, width/2, scale *8, tvPaint);
            }
            canvas.restore();
        }
        Path tpath = new Path();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tpath.addArc(vleft - 40, vtop - 40, vright - 40, vbottom - 40, 60f, 120f);
        }else{
            RectF rect = new RectF(vleft - 40, vtop - 40, vright - 40, vbottom - 40);
            tpath.addArc(rect, 60f, 120f);
        }
//        canvas.drawTextOnPath("噪音记录", tpath, 10f, 10f, tvPaint);
//        canvas.drawText("噪音记录", width/2, width - width/4, tvPaint);
        tvPaint.setTextSize(25);
        canvas.drawText(value+"db", width/2, width - width/8, tvPaint);
    }

    private void drawPointer(Canvas canvas){
        canvas.rotate(-120 + (240 / 120f) *value , width/2, width/2);
        canvas.save();
        Path tpath = new Path();
        tpath.moveTo(width/2, scale *9);
        tpath.lineTo(width/2 + scale, width/2);
        tpath.lineTo(width/2 - scale, width/2);
        tpath.lineTo(width/2, scale *9);
        tpath.close();
        canvas.drawPath(tpath, PointerPaint);
        canvas.restore();

    }
}
