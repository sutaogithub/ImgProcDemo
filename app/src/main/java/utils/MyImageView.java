package utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zhangsutao on 2016/5/18.
 */
public class MyImageView extends ImageView{

    public static final int RATIO_WITH_WIDTH=1,RATIO_WITH_HEIGHT=2;
    private int mode=1;
    public MyImageView(Context context) {
        super(context);
    }

    public void setMode(int mode){
        if(mode==1||mode==2){
            this.mode=mode;
        }
    }
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private float getImageRatio(){
        Drawable drawable=getDrawable();
        if(drawable==null)
            return -1;
        else{
//            Rect rect=drawable.getBounds();
            return (float)drawable.getIntrinsicWidth()/drawable.getIntrinsicHeight();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float ratio=getImageRatio();
        if(ratio==-1){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }else{
            int width=MeasureSpec.getSize(widthMeasureSpec);
            int height=MeasureSpec.getSize(heightMeasureSpec);
            if(mode==RATIO_WITH_WIDTH){
                height= (int) (width/ratio);
            }else{
                width= (int) (height*ratio);
            }
            setMeasuredDimension(width,height);
        }
    }
}
