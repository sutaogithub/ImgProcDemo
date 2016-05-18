package utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zhangsutao on 2016/5/9.
 */
public class WrapImageView extends MyImageView {
    private final Paint paint;
    private boolean down=false,move=false,showCircle;
    private float radius,startY,startX,stopX,stopY;


    public WrapImageView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true); //消除锯齿
        paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        paint.setColor(Color.RED);
    }

    public WrapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true); //消除锯齿
        paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        paint.setColor(Color.RED);
    }

    public WrapImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true); //消除锯齿
        paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        paint.setColor(Color.RED);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(down)
            canvas.drawCircle(startX,startY,radius,paint);

        if(move){
            canvas.drawLine(startX,startY,stopX,stopY,paint);
            canvas.drawCircle(stopX, stopY, radius, paint);
        }
        //展示圆圈有多大
        if(showCircle){
            canvas.drawCircle(this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2, radius, paint);
            showCircle=false;
        }

    }

    public void setStop(float x,float y){
        this.stopX=x;
        this.stopY=y;
        move=true;

    }

    public void setStart(float x,float y ){
        this.startX=x;
        this.startY=y;
        down=true;
    }
    public void setRadius(int radius){
        this.radius=radius;

    }
    public void reset(){
        down=false;
        move=false;
        showCircle=false;
        invalidate();
    }

    public void showCircle(){
        showCircle=true;
        invalidate();
    }

}
