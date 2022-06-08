package com.test.cppglsurfaceview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jun on 2017/04/28.
 */
public class CppGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    int mId = -1;
    private static Random mRnd = new Random(System.currentTimeMillis());

    public CppGLSurfaceView(Context context, int id) {
        super(context);
        mId = id;
        setId(55000+mId);
        setEGLContextClientVersion(2);

        /* 背景色をランダムに設定 */
        int bkclr = mRnd.nextInt();
        bkclr &= 0xffffff00;
        bkclr |= 0x30;
        setBackgroundColor(bkclr);

        /* 透過設定 */
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8,8,8,8,0,0);
        setZOrderOnTop(true);
        setRenderer(this);

        /* C++ */
        NativeFunc.create(id);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NativeFunc.surfaceCreated(mId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        NativeFunc.surfaceChanged(mId, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try { Thread.sleep(10); } catch (InterruptedException e) { }
        NativeFunc.onDrawFrame(mId);
    }
}
