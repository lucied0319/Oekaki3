package com.example.oekaki3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.Nullable;

public class NewView extends View {

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

        init();
    }

    public NewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        Log.w("fjsio","jdioa");
        mW = w;
        mH = h;
        mBmpBuf2 = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);

        canvas3.setBitmap(mBmpBuf2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //if(mFirstDraw) {




            if (mBmp != null){


            mBmpBuf = Bitmap.createBitmap((int)mWDw,(int)mHDw, Bitmap.Config.ARGB_8888);
            canvas2.setBitmap(mBmpBuf);
            //canvas2.drawBitmap(mBmp, mRctSrc, mRctDst, null);
                canvas2.drawBitmap(mBmp, -(int)mRctDst.left,-(int)mRctDst.top, null);
                canvas.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
            //canvas.drawBitmap(mBmpBuf,mRctSrc,mRctDst,null);
           }
        mFirstDraw =false;
        //canvas.clipRect(mRctDst);

        canvas.drawBitmap(mBmpBuf2,0,0, null);
        canvas.drawPath(mPath,mPaint);
        //canvas.drawBitmap(mBmpBuf,0,0,null);
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


    }
    //全消し
    public void clear(){
        mFirstDraw = true;
        mPath.reset();
        mBmp = null;
        canvas3.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //画像保存


    public void save(Uri uri){


       // View view = findViewById(R.id.view2);
    //    File file = new File(uri.toString());
        Bitmap bitmap = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        //view.draw(c);
        if(mBmp != null){
            c.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
            Log.w("jio8882","kjo");
        }
        c.drawBitmap(mBmpBuf2,0,0, null);


        if(bitmap != null){
            Log.w("7777772","kjo");
        }

        try(FileOutputStream fos =new FileOutputStream(new File(uri.toString()))) {
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);


        } catch (IOException e) {
            e.printStackTrace();
            Log.w("djo777a","jda");
        }
    }
    public void save2(OutputStream out){

        // View view = findViewById(R.id.view2);
        //    File file = new File(uri.toString());
        Bitmap bitmap = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        //view.draw(c);
        if(mBmp != null){
            c.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
            Log.w("jio8882","kjo");
        }
        c.drawBitmap(mBmpBuf2,0,0, null);



        //bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //PNG, クオリティー100としてbyte配列にデータを格納
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        try(OutputStream outputStream =out;
                    //getContentResolver().openOutputStream(uri)
        ) {

            outputStream.write(bytes);


        } catch (IOException e) {
            e.printStackTrace();
            Log.w("djo777a","jda");
        }
    }
    public void save3(OutputStream out){

        // View view = findViewById(R.id.view2);
        //    File file = new File(uri.toString());
        Bitmap bitmap = Bitmap.createBitmap(mW,mH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        //view.draw(c);
        if(mBmp != null){
            c.drawBitmap(mBmpBuf,(mW - mWDw) / 2, (mH - mHDw) / 2, null);
            Log.w("jio8882","kjo");
        }
        c.drawBitmap(mBmpBuf2,0,0, null);



        //bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //PNG, クオリティー100としてbyte配列にデータを格納
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);


    }
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
                invalidate();
                break;
            //アップ時
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX,mY);

                canvas3.drawPath(mPath,mPaint);

                mPath.reset();
                invalidate();

                break;

        }

        return true;
    }
}
