package utils;

import android.graphics.Bitmap;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResizeUtils {

	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

	public  Bitmap resize(Bitmap src,float[] area,float ratio){

		final int width=src.getWidth();
		final int height=src.getHeight();
		int startRow= (int) (src.getHeight()*area[0]);
		int endRow=(int) (src.getHeight()*area[1]);
		int handleRowsNum=endRow-startRow+1;
		int resHeight= (int) (height-handleRowsNum+handleRowsNum*ratio);
		int[] topPixels=new int[startRow*width];
		int[] handlePixels=new int[handleRowsNum*width];
		int[] bottomPixels=new int[(height-endRow-1)*width];
		src.getPixels(topPixels,0,width,0,0,width,startRow);
		src.getPixels(handlePixels,0,width,0,startRow,width,endRow-startRow+1);
		src.getPixels(bottomPixels,0,width,0,endRow+1,width,height-endRow-1);

		int[] afterHandle=onResizeMultiThread(handlePixels, 1, ratio, width, handleRowsNum);
		handlePixels=null;

		int[] res=new int[resHeight*width];

		for(int y=0;y<startRow;y++){
			for(int x=0;x<width;x++){
				int index=y*width+x;
				res[index]=topPixels[index];
			}
		}
		int handleEndHeight=startRow+afterHandle.length/width;
		for(int y=startRow,hy=0;y<handleEndHeight;y++,hy++){
			for(int x=0;x<width;x++){
				res[y*width+x]=afterHandle[hy*width+x];
			}
		}
		for(int y=handleEndHeight,hy=0;y<resHeight;y++,hy++){
			for(int x=0;x<width;x++){
				res[y*width+x]=bottomPixels[hy*width+x];
			}
		}
		Bitmap resImg=Bitmap.createBitmap(width, resHeight, Bitmap.Config.RGB_565);
		resImg.setPixels(res, 0, width, 0, 0,  width,  resHeight);
		return resImg;
	}
	//双线性插值算法，可以用于对图片整体的伸缩
	/**
	* @return 处理后的像素数组
	* @params src 源像素数组
	* 		  xratio 宽度伸缩比例 yratio 高度伸缩比例 
	*		  width 原图像宽度   height原图像高度
	*
	*/
	public   int[] onResize(int[] src,float xratio,float yratio,int width,int height){
		if(src==null||xratio<0||yratio<0||width<0||height<0)
			return null;
		int resHeight=(int) (height*yratio);
		int resWidth=(int)(width*xratio);
		int[] res=new int[resHeight*resWidth];
		for(int i=0;i<resHeight;i++){
			float srcdY=i/yratio;
			int srcY=(int) ((i+0.5)/yratio-0.5);		
			for(int j=0;j<resWidth;j++){
				float srcdX=j/xratio;
				int srcX=(int) ((j+0.5)/xratio-0.5);
				int u=(int) ((srcdX-(int)srcdX)*2048);
				int v=(int) ((srcdY-(int)srcdY)*2048);
				int[] xy=getRgb(src,srcY*width+srcX);
                int[] x_1y=getRgb(src,srcY*width+edgeHandle(srcX+1, width));
                int[] xy_1=getRgb(src,edgeHandle(srcY+1, height)*width+srcX);
                int[] x_1y_1=getRgb(src,edgeHandle(srcY+1, height)*width+edgeHandle(srcX+1, width));            
                int new_r=((2048-u)*(2048-v)*xy[0]+(2048-u)*v*xy_1[0]+u*(2048-v)*x_1y[0]+u*v*x_1y_1[0])>>22;
                int new_g=((2048-u)*(2048-v)*xy[1]+(2048-u)*v*xy_1[1]+u*(2048-v)*x_1y[1]+u*v*x_1y_1[1])>>22;
                int new_b=((2048-u)*(2048-v)*xy[2]+(2048-u)*v*xy_1[2]+u*(2048-v)*x_1y[2]+u*v*x_1y_1[2])>>22;
                res[i*resWidth+j]=((new_r & 0xff) << 16) | ((new_g & 0xff) << 8) | (new_b & 0xff);
			}
		}
		return res;
	}

	public   int[] onResizeMultiThread(int[] src,float xratio,float yratio,int width,int height){
		if(src==null||xratio<0||yratio<0||width<0||height<0)
			return null;
		int resHeight=(int) (height*yratio);
		int resWidth=(int)(width*xratio);
		int[] res=new int[resHeight*resWidth];
		int div=resHeight/3;
		CyclicBarrier barrier=new CyclicBarrier(3);
		Thread t1=new Thread(new ResizeTask(src,res,xratio,yratio,width,height,resWidth,0,div,barrier));
		Thread t2=new Thread(new ResizeTask(src,res,xratio,yratio,width,height,resWidth,div+1,2*div,barrier));
		Thread t3=new Thread(new ResizeTask(src,res,xratio,yratio,width,height,resWidth,2*div+1,resHeight-1,barrier));
		t1.start();
		t2.start();
		t3.start();
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return res;
	}
	public  class ResizeTask implements Runnable{
		private int[] src,res;
		private float xratio,yratio;
		private int width,height,resWidth,startRow,endRow;
		private CyclicBarrier barrier;
		public ResizeTask(int[] src,int[] res,float xratio,float yratio,int width,int height,int resWidth,int startRow,int endRow,CyclicBarrier barrier){
			this.src=src;
			this.res=res;
			this.xratio=xratio;
			this.yratio=yratio;
			this.width=width;
			this.height=height;
			this.resWidth=resWidth;
			this.startRow=startRow;
			this.endRow=endRow;
			this.barrier=barrier;
		}
		@Override
		public void run() {
			for(int i=startRow;i<=endRow;i++){
				float srcdY=i/yratio;
				int srcY=(int) ((i+0.5)/yratio-0.5);
				for(int j=0;j<resWidth;j++){
					float srcdX=j/xratio;
					int srcX=(int) ((j+0.5)/xratio-0.5);
					int u=(int) ((srcdX-(int)srcdX)*2048);
					int v=(int) ((srcdY-(int)srcdY)*2048);
					int[] xy=getRgb(src,srcY*width+srcX);
					int[] x_1y=getRgb(src,srcY*width+edgeHandle(srcX+1, width));
					int[] xy_1=getRgb(src,edgeHandle(srcY+1, height)*width+srcX);
					int[] x_1y_1=getRgb(src,edgeHandle(srcY+1, height)*width+edgeHandle(srcX+1, width));
					int new_r=((2048-u)*(2048-v)*xy[0]+(2048-u)*v*xy_1[0]+u*(2048-v)*x_1y[0]+u*v*x_1y_1[0])>>22;
					int new_g=((2048-u)*(2048-v)*xy[1]+(2048-u)*v*xy_1[1]+u*(2048-v)*x_1y[1]+u*v*x_1y_1[1])>>22;
					int new_b=((2048-u)*(2048-v)*xy[2]+(2048-u)*v*xy_1[2]+u*(2048-v)*x_1y[2]+u*v*x_1y_1[2])>>22;
					res[i*resWidth+j]=((new_r & 0xff) << 16) | ((new_g & 0xff) << 8) | (new_b & 0xff);
				}
			}
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
	
	public  int edgeHandle(int index, int w)
    {
        if(index<0)
            return 0;
        else
        if(index>=w)
            return w-1;
        else
            return index;
    }
	//以rgb顺序返回
	public   int[] getRgb(int[] src,int index){
		int[] res=new int[3];
		//b
		res[2] = src[index] & 0xff;
		//g
		res[1] = (src[index] >> 8) & 0xff;
		//r
		res[0] = (src[index] >> 16) & 0xff;
		return res;
	}

}
