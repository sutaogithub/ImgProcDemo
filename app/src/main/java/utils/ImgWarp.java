package utils;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ImgWarp {


    //参数封装
    public static class WarpControl {

        public double orig_x,orig_y;//圆心
        public double max_dist;//半径
    //    double mou_dx_norm,mou_dy_norm;//x，y方向上的增量除以半径后的值
        public double max_dist_sq;//半径的平方
        public double mou_dx,mou_dy;//x，y方向上的增量
    }


    /**
     * @desc 图像局部放大，可用于对眼睛的放大
     * @param src 原图像
     * @param col 控制参数
     * @param strength 增大强度，范围（0-1）
     * @return 处理后的图片
     */
    public static Bitmap imageScale(Bitmap src,WarpControl col,double strength){
        final int width=src.getWidth();
        final int height=src.getHeight();
        int[] src_pixels=new int[width*height];
        int[] res_pixels=new int[width*height];
        src.getPixels(src_pixels, 0, width, 0, 0, width, height);
        src.getPixels(res_pixels, 0, width, 0, 0, width, height);
        for(int y = (int) (col.orig_y-col.max_dist); y<=col.orig_y+col.max_dist; y++){
            for(int x = (int) (col.orig_x-col.max_dist); x<=col.orig_x+col.max_dist; x++){
                int inkx=edgeHandle(x,width);
                int inky=edgeHandle(y,height);
                int fu=inkx;
                int fv=inky;
                double dx=fu-col.orig_x;
                double dy=fv-col.orig_y;
                // dx>0-col.max_dist&&dx<col.max_dist&&dy>0-col.max_dist&&dy<col.max_dist
                if(hypotsq(dx,dy)<col.max_dist_sq){
                    double rsq=hypotsq(dx, dy);
                    double rnorm=Math.sqrt(rsq/col.max_dist_sq);
                    double a=1-strength*(rnorm-1)*(rnorm-1);
                    fu= (int) (col.orig_x+a*dx);
                    fv= (int) (col.orig_y+a*dy);
                    fu=edgeHandle(fu,width);
                    fv=edgeHandle(fv,height);
                    res_pixels[inky*width+inkx]=src_pixels[fv*width+fu];
                }
            }
        }
        Bitmap resImg=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        resImg.setPixels(res_pixels, 0, width, 0, 0, width, height);
        return resImg;
    }


    /**
     * @desc 图像局部扭曲，方向强度都可调，WarpControl中的半径可控制变形的范围，WarpControl中mou_dx,mou_dy的矢量和确定变形方向和变形强度。
     * 注意要保证x，y方向上的增量的平方和小于等于半径的平方
     * @param src 原图
     * @param col 控制参数
     * @return 处理后的图片
     */
    public static Bitmap imageTranslate(Bitmap src, WarpControl col){
            final int width=src.getWidth();
            final int height=src.getHeight();
            int[] src_pixels=new int[width*height];
            int[] res_pixels=new int[width*height];
            src.getPixels(src_pixels, 0, width, 0, 0, width, height);
            src.getPixels(res_pixels, 0, width, 0, 0, width, height);
            for(int y = (int) (col.orig_y-col.max_dist); y<=col.orig_y+col.max_dist; y++){
                for(int x = (int) (col.orig_x-col.max_dist); x<=col.orig_x+col.max_dist; x++){
                    int inkx=edgeHandle(x,width);
                    int inky=edgeHandle(y,height);
                    int fu=inkx;
                    int fv=inky;
                    double dx=fu-col.orig_x;
                    double dy=fv-col.orig_y;
                   // dx>0-col.max_dist&&dx<col.max_dist&&dy>0-col.max_dist&&dy<col.max_dist
                    if(hypotsq(dx,dy)<col.max_dist_sq){
                        double rsq=hypotsq(dx,dy);

                        double msq=hypotsq(dx-col.mou_dx,dy-col.mou_dy);
                        double edge_dist=col.max_dist_sq-rsq;
                        double a=edge_dist/(edge_dist+msq);
                        a*=a;
                        fu-=a*col.mou_dx;
                        fv-=a*col.mou_dy;
                        fu=edgeHandle(fu,width);
                        fv=edgeHandle(fv,height);
                        res_pixels[inky*width+inkx]=src_pixels[fv*width+fu];
                    }
                }
        }
        Bitmap resImg=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        resImg.setPixels(res_pixels, 0, width, 0, 0, width, height);
        return resImg;
    }

    //计算平方和
    public static double hypotsq(double x,double y){
        return x*x+y*y;
    }
    //边缘处理
    public static int edgeHandle(int index, int w)
    {
        if(index<0)
            return 0;
        else
        if(index>=w)
            return w-1;
        else
            return index;
    }
}
