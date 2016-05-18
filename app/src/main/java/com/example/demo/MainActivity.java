package com.example.demo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import utils.BitmapSampleUtils;

public class MainActivity extends AppCompatActivity {

    public static final int SELECT_PICTURE = 1;
    public static final String IMG_PATH ="img";
    private ImageView iv;
    private String imgPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv= (ImageView) findViewById(R.id.iv);
    }
    public void taller(View view){
        if(imgPath==null){
            Toast.makeText(this,"请先选取图片",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent =new Intent(this,TallActivity.class);
        intent.putExtra(IMG_PATH, imgPath);
        startActivity(intent);
    }
    public void warping(View view){
        if(imgPath==null){
            Toast.makeText(this,"请先选取图片",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent =new Intent(this,WarpingActivity.class);
        intent.putExtra(IMG_PATH, imgPath);
        startActivity(intent);
    }
    public void beautify(View view){
        if(imgPath==null){
            Toast.makeText(this,"请先选取图片",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent =new Intent(this,BeautifyActivity.class);
        intent.putExtra(IMG_PATH,imgPath);
        startActivity(intent);
    }
    public void selectPic(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri uri = data.getData();
                imgPath = getPath(uri);
                int width=iv.getMeasuredWidth();
                Bitmap bitmap= BitmapSampleUtils.decodeSampledBitmapFromFile(imgPath, width);
                iv.setImageBitmap(bitmap);
            }
        }
    }
    private String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
}
