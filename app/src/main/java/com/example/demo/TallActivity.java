package com.example.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import utils.AreaSelectImageView;
import utils.BitmapSampleUtils;
import utils.ResizeUtils;

/**
 * Created by zhangsutao on 2016/5/18.
 */
public class TallActivity extends Activity{

    private AreaSelectImageView iv;
    private Bitmap bitmap;
    private String imgPath;
    private SeekBar seekBar;
    private float ratio=1;
    private ResizeUtils resizeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tall_layout);
        resizeUtils=new ResizeUtils();
        initView();
        initEvent();

    }
    public void start(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                float[] area=iv.getStartAndEndY();
                bitmap=resizeUtils.resize(bitmap, area, ratio);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    private void initEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged( SeekBar seekBar,  int progress,  boolean fromUser) {

                ratio=1+(float)progress/seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initView() {
        imgPath=getIntent().getStringExtra(MainActivity.IMG_PATH);
        iv= (AreaSelectImageView) findViewById(R.id.tall_iv);
        iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bitmap= BitmapSampleUtils.decodeSampledBitmapFromFile(imgPath, iv.getMeasuredWidth());
                iv.setImageBitmap(bitmap);
                iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        seekBar= (SeekBar) findViewById(R.id.tall_seekbar);
        seekBar.setMax(20);
    }
}
