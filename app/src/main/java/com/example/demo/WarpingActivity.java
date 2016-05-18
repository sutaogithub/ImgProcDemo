package com.example.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import utils.BitmapSampleUtils;
import utils.ImgWarp;
import utils.WrapImageView;

public class WarpingActivity extends AppCompatActivity {

	public WrapImageView iv;
	private Bitmap bitmap;
	private SeekBar seekBar,strengthBar;
	private int radius =50;
	private double strength=0.5;
	private boolean isBigEyes;
	private CheckBox checkBox;
	private String img_path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
	}

	private void initView() {
		setContentView(R.layout.warping_layout);
		iv=(WrapImageView) findViewById(R.id.iv);
		iv.setRadius(radius);
		seekBar=(SeekBar)findViewById(R.id.seekbar);
		seekBar.setMax(150);
		strengthBar=(SeekBar)findViewById(R.id.strength);
		strengthBar.setMax(100);
		checkBox=(CheckBox)findViewById(R.id.checkbox);
		img_path=getIntent().getStringExtra(MainActivity.IMG_PATH);
		iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				bitmap = BitmapSampleUtils.decodeSampledBitmapFromFile(img_path, iv.getMeasuredWidth());
				iv.setImageBitmap(bitmap);
				iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

	}
	private void initEvent() {
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isBigEyes=isChecked;
			}
		});

		strengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				strength = (double)progress / seekBar.getMax();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(isBigEyes)
						radius=progress;
					else
					    radius=progress+30;
					iv.setRadius(radius);
					iv.showCircle();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				iv.reset();
			}
		});
		iv.setOnTouchListener(new View.OnTouchListener() {
			float downx, downy;
			ImgWarp.WarpControl bean = new ImgWarp.WarpControl();
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downx = event.getX();
						downy = event.getY();
						iv.setStart(downx, downy);
						iv.invalidate();
						int x = (int) (bitmap.getWidth() * event.getX() / iv.getWidth());
						int y = (int) (bitmap.getHeight() * event.getY() / iv.getHeight());
						bean.orig_x = x;
						bean.orig_y = y;
						bean.max_dist = radius;
						bean.max_dist_sq = bean.max_dist * bean.max_dist;
						break;
					case MotionEvent.ACTION_MOVE:
						float movex = event.getX();
						float movey = event.getY();
						iv.setStop(movex, movey);
						iv.invalidate();
						break;
					case MotionEvent.ACTION_UP:
						iv.reset();
						float dx = event.getX() - downx;
						float dy = event.getY() - downy;
						float squ = dx * dx + dy * dy;
						float ratio = 0;
						if (squ > bean.max_dist_sq)
							ratio = (float) (bean.max_dist / Math.sqrt(squ));
						else
							ratio = 1;
						//强度在这里设置
						bean.mou_dx = dx * ratio / 8;
						bean.mou_dy = dy * ratio / 8;
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (isBigEyes)
									bitmap = ImgWarp.imageScale(bitmap, bean, strength);
								else
									bitmap = ImgWarp.imageTranslate(bitmap, bean);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										iv.setImageBitmap(bitmap);
									}
								});
							}
						}).start();
						break;
				}
				return true;
			}
		});
	}


}
