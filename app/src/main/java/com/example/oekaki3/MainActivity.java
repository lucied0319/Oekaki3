package com.example.oekaki3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.CalendarContract.CalendarCache.URI;

public class MainActivity extends AppCompatActivity {

    NewView newView;
    Toolbar toolbar;

    private Uri uriSave;

    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //ステータスバー消去
        decorView= getWindow().getDecorView();
        //Hide the status bar.
        //int uiOptions= View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN| View.SYSTEM_UI_FLAG_IMMERSIVE);
//        findViewById(android.R.id.content).setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //ナビゲーションバー・ステータスバー透明化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //var window = getWindow();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //Toolbarを取得
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("お絵かきカメラ");
        setSupportActionBar(toolbar);

        newView = findViewById(R.id.view2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //////////////////////////////////Intent結果処理//////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //画像読み込みの結果
        if(requestCode == 200 && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            Bitmap bitmap =null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }catch (IOException e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.setRotate(90,bitmap.getWidth()/2,bitmap.getHeight()/2);

            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            Log.w("Bitmap.createBitmap","jdioa");

            newView.setBmp(bitmap);

        //画面保存の結果
        }else if(requestCode == 100 && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            if(uri!=null){
                OutputStream outputStream =null;
                try {
                    outputStream = getContentResolver().openOutputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                newView.onMenuSave(outputStream);
            }
        //カメラ保存の結果
        }else if(requestCode == 50 && resultCode == RESULT_OK){
            //Uri uri = data.getData();
            Bitmap bitmap =null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriSave);
            }catch (IOException e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.setRotate(90,bitmap.getWidth()/2,bitmap.getHeight()/2);

            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            Log.w("Bitmap.createBitmap","jdioa");

            newView.setBmp(bitmap);
        }
    }
    //画像読み込み
    public void onMenuImageLoad() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_TITLE,"memo.jpg");
        startActivityForResult(i,200);
    }
    //画面保存
    public void onMenuSave() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_TITLE,"oe"+sdf.format(date)+".png");
        startActivityForResult(i,100);
    }
    //カメラ
    public  void onMenuCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permission,2000);
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date(System.currentTimeMillis());
        String nowStr = dateFormat.format(now);
        String fileName = "UseCameraPhoto_" + nowStr + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        ContentResolver resolver = getContentResolver();
        uriSave = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uriSave);
        startActivityForResult(intent,50);
    }

    //パーミッション許可後の処理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onMenuCamera();
        }
    }

    ////////////////////////////メニュー選択//////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            //画像取込：メニュー
            case R.id.menuImageLoad:
                onMenuImageLoad();
                break;
            //全消去：メニュー
            case R.id.menuClearAll:
                newView.onMenuClearAll();
                break;
            //画像消去：メニュー
            case R.id.menuClearImage:
                newView.onMenuClearImage();
                break;
            //ライン消去：メニュー
            case R.id.menuClearLine:
                newView.onMenuClearLine();
                break;
            //画面保存：メニュー
            case R.id.menuSave:
                onMenuSave();
                break;
            //画像回転：メニュー
            case R.id.menuImageTurn:
                newView.onMenuImageTurn();
                break;
            //カメラ：メニュー
            case R.id.menuCamera:
                onMenuCamera();
                break;

            //色選択
            case R.id.colorBlack:
            case R.id.colorWhite:
            case R.id.colorBlue:
            case R.id.colorRed:
            case R.id.colorGreen:
            case R.id.colorYellow:
                newView.onColorSelect(itemId);
                break;
            //筆の太さ選択
            case R.id.lineGokuboso:
            case R.id.lineHosoi:
            case R.id.lineFutu:
            case R.id.lineFutoi:
            case R.id.lineGokubuto:
                newView.onLineSelect(itemId);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}