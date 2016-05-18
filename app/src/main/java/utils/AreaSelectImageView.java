package utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangsutao on 2016/5/18.
 */
public class AreaSelectImageView extends MyImageView{

    private final int SELECT_AREA_SIZE=30;
    private int startY;
    private int  endY;
    private Paint paint=new Paint();
    private boolean isFirstDraw=true;
    private boolean isSelectiong=false;
    private boolean isStartSelect=false;
    public AreaSelectImageView(Context context) {
        super(context);
    }

    public AreaSelectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AreaSelectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isSelected(event.getY(),startY)||isSelected(event.getY(),endY)){
                    if(isSelected(event.getY(),startY))
                        isStartSelect=true;
                    else
                        isStartSelect=false;
                    isSelectiong=true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isSelectiong=false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(isSelectiong){
                    if(isStartSelect)
                        startY= edgeHandle((int) event.getY(),getMeasuredHeight());
                    else
                        endY= edgeHandle((int) event.getY(),getMeasuredHeight());
                    invalidate();
                }
                break;
        }

        return true;

    }
    private boolean isSelected(float y,int centerY){
        if(edgeHandle(centerY-SELECT_AREA_SIZE,getMeasuredHeight())<y&&edgeHandle(centerY+SELECT_AREA_SIZE,getMeasuredHeight())>y)
            return true;
        else
            return false;
    }


    private int edgeHandle(int x,int max){
        if(x<0)
            return 0;
        else
            if(x>max)
                return max;
            else
                return x;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isFirstDraw){
            startY=getMeasuredHeight()/3;
            endY=getMeasuredHeight()/2;
            drawRect(canvas);
            isFirstDraw=false;
        }
        drawLine(canvas);
        if(isSelectiong){
            drawRect(canvas);
        }
    }

    private void drawLine(Canvas canvas) {
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(4f);
        canvas.drawLine(0, startY, getMeasuredWidth(), startY, paint);
        canvas.drawLine(0,endY,getMeasuredWidth(),endY,paint);
    }

    private void drawRect(Canvas canvas) {
        paint.setColor(0x8887ceff);
        canvas.drawRect(0, Math.min(endY, startY), getMeasuredWidth(), Math.max(endY, startY), paint);
        paint.setColor(0xffffffff);
        paint.setTextSize(60f);
        canvas.drawText("蓝色为增高区域",getMeasuredWidth()/3,Math.min(endY, startY)+Math.abs(startY-endY)/2,paint);
    }

    public float[] getStartAndEndY(){
        float[] res=new float[2];
        res[0]=(float)(startY<endY?startY:endY)/getMeasuredHeight();
        res[1]=(float)(endY>startY?endY:startY)/getMeasuredHeight();
        return res;
    }

}
