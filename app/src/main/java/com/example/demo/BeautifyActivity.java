package com.example.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import utils.BeautifyMultiThread;
import utils.BitmapSampleUtils;

/**
 * Created by zhangsutao on 2016/5/18.
 */
public class BeautifyActivity extends Activity{
    private ImageView res_img;
    private Bitmap bitmap;
    private boolean isFinish=true;
    private SeekBar seekBar;
    private int sigma=1;
    private String img_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beautify_layout);

        initView();
        initEvent();
    }

    private void initView() {
        res_img= (ImageView) findViewById(R.id.res_img);
        seekBar=(SeekBar)findViewById(R.id.seek_bar);
        img_path=getIntent().getStringExtra(MainActivity.IMG_PATH);
        seekBar.setMax(20);
        res_img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                 bitmap=BitmapSampleUtils.decodeSampledBitmapFromFile(img_path,res_img.getMeasuredWidth());
                res_img.setImageBitmap(bitmap);
                res_img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initEvent() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sigma = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void beauty(View view){
        if(!isFinish){
            Toast.makeText(getApplicationContext(), "正在美颜请稍后", Toast.LENGTH_SHORT).show();
            return;
        }
        isFinish=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BeautifyMultiThread build=new BeautifyMultiThread();
                final Bitmap bit=build.beautifyImg(bitmap, sigma);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        res_img.setImageBitmap(bit);
                        isFinish = true;
                        Toast.makeText(getApplicationContext(), "美颜完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

}
