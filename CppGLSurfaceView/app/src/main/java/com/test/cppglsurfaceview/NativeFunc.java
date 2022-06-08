package com.test.cppglsurfaceview;

import android.view.Surface;

/**
 * Created by jun on 2017/04/28.
 */
public class NativeFunc {
    static{ System.loadLibrary("testlib"); }

    public native static void create(int id);
    public native static void surfaceCreated(int id);
    public native static void surfaceChanged(int id, int width, int height);
    public native static void onDrawFrame(int mId);
    public native static void surfaceDestroyed(int id);
}
