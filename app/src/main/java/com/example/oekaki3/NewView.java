package com.example.oekaki3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class NewView extends View {

    MainActivity mainActivity;
    AppBarLayout appBarLayout;
    //フィールド
    //描画基本変数
    private int mW;
    private int mH;
    private int wBmp;
    private int hBmp;
    public Bitmap mBmp = null;

    Canvas canvas2;
    Canvas canvas3;
    private Rect mRctSrc = new Rect();
    private RectF mRctDst = new RectF();
    private  float mWDw;
    private  float mHDw;

    //お絵かき
    private boolean mFirstDraw = true;//初回描画
    Path mPath;
    Paint mPaint;
    private float mX,mY;
    private Bitmap mBmpBuf;
    Bitmap mBmpBuf2;

    //コンストラクタ

    public NewView(Context context) {
        super(context);
        mainActivity = (MainActivity)context;
        init();
    }
    public NewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mainActivity = (MainActivity)context;
        init();
    }
    public NewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mainActivity = (MainActivity)context;
        init();
    }
    void init(){
        setBackgroundColor(Color.WHITE);
        canvas2 = new Canvas();
        canvas3 = new Canvas();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.w("onSizeChanged","jdioa");
        mW = w;
        mH = h;
        mBmpBuf2 = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        canvas3.setBitmap(mBmpBuf2);

        mBmpBuf = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        canvas2.setBitmap(mBmpBuf);
    }
///////////////////////描画//////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBmp != null ){
            canvas.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
        }
        canvas3.drawPath(mPath,mPaint);
        canvas.drawBitmap(mBmpBuf2,0,0,null);

    }
    //画像の設定
    public void setBmp(Bitmap bmp){
        if(bmp == null)return;
        mFirstDraw = true;
        mBmp = bmp;
        wBmp = mBmp.getWidth();
        hBmp = mBmp.getHeight();
        float wRate = 1.0f * mW / wBmp;
        float hRate = 1.0f * mH / hBmp;
        float rate = wRate < hRate ? wRate: hRate;
        mWDw = wBmp * rate;
        mHDw = hBmp * rate;

        mRctSrc.left=0;
        mRctSrc.top = 0;
        mRctSrc.right = wBmp;
        mRctSrc.bottom = hBmp;

        mRctDst.left = (mW - mWDw) / 2;
        mRctDst.top = (mH - mHDw) / 2;
        mRctDst.right = mRctDst.left + mWDw;
        mRctDst.bottom = mRctDst.top + mHDw;

//        mRctDst.left = 0;
//        mRctDst.top = 0;
//        mRctDst.right = wBmp;
//        mRctDst.bottom = hBmp;

        mBmpBuf = Bitmap.createBitmap((int)mWDw,(int)mHDw, Bitmap.Config.ARGB_8888);
        canvas2.setBitmap(mBmpBuf);
        //canvas2.drawBitmap(mBmp, mRctSrc, mRctDst, null);
        canvas2.drawBitmap(mBmp, -(int)mRctDst.left,-(int)mRctDst.top, null);
        invalidate();
        //mainActivity.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    //全消し
    public void onMenuClearAll(){
        mFirstDraw = true;
        mPath.reset();
        mBmp = null;
        canvas3.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    //画像消し
    public void onMenuClearImage(){
        mBmp = null;
        canvas2.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    //ライン消し
    public void onMenuClearLine(){
        mPath.reset();
        canvas3.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    //画面保存
    public void onMenuSave(OutputStream out){
        Bitmap bitmap = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        if(mBmp != null){
            c.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
            Log.w("jio8882","kjo");
        }
        c.drawBitmap(mBmpBuf2,0,0, null);

        //PNG, クオリティー100としてbyte配列にデータを格納
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
    }
    //画像回転
    public void onMenuImageTurn() {

        if(mBmp ==null)return;
        Matrix matrix = new Matrix();
        matrix.setRotate(90,mBmp.getWidth()/2,mBmp.getHeight()/2);

        mBmp = Bitmap.createBitmap(mBmp,0,0,mBmp.getWidth(),mBmp.getHeight(),matrix,true);
        setBmp(mBmp);
        //canvas2.drawBitmap(mBmp, (int)mRctDst.left,(int)mRctDst.top, null);
        invalidate();
    }
    //色選択
    public void onColorSelect(int colorId){
        switch (colorId){
            //ブラック
            case R.id.colorBlack:
                mPaint.setColor(Color.BLACK);
                break;
            //ホワイト
            case R.id.colorWhite:
                mPaint.setColor(Color.WHITE);
                break;
            //ブルー
            case R.id.colorBlue:
                mPaint.setColor(Color.BLUE);
                break;
            //レッド
            case R.id.colorRed:
                mPaint.setColor(Color.RED);
                break;
            //グリーン
            case R.id.colorGreen:
                mPaint.setColor(Color.GREEN);
                break;
            //イエロー
            case R.id.colorYellow:
                mPaint.setColor(Color.YELLOW);
                break;
        }
    }
    //筆の太さ選択
    public void onLineSelect(int colorId) {
        switch (colorId) {
            //極細
            case R.id.lineGokuboso:
                mPaint.setStrokeWidth(1);
                break;
            //細い
            case R.id.lineHosoi:
                mPaint.setStrokeWidth(3);
                break;
            //普通
            case R.id.lineFutu:
                mPaint.setStrokeWidth(6);
                break;
            //太い
            case R.id.lineFutoi:
                mPaint.setStrokeWidth(10);
                break;
            //太い
            case R.id.lineGokubuto:
                mPaint.setStrokeWidth(20);
                break;
        }
    }
    //お絵かき処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            //ダウン時
            case  MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x,y);
                mX = x;
                mY = y;
                invalidate();
                break;
            //移動時
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(x-mX) >= 2 || Math.abs(y - mY) >= 2){
                    mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
                    mX =x;
                    mY = y;
                }
                mainActivity.getSupportActionBar().hide();
                invalidate();
                break;
            //アップ時
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX,mY);

                //canvas3.drawPath(mPath,mPaint);
                mainActivity.getSupportActionBar().show();
                //mPath.reset();
                invalidate();

                break;

        }

        return true;
    }
}
