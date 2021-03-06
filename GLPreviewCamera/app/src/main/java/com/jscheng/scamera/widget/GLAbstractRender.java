package com.jscheng.scamera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.jscheng.scamera.util.LogUtil.TAG;

public abstract class GLAbstractRender implements GLSurfaceView.Renderer {
    protected Context mContext;
    protected int width;
    protected int height;
    protected int mVertexShader;
    protected int mFragmentShader;
    protected int mProgram;

    public GLAbstractRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        createProgram();
        onCreate();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        this.height = height;
        this.width = width;
        onChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glUseProgram(mProgram);
        onDraw();
    }

    protected int loadShader(int shaderType, String shaderSource) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        int status[] = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e(TAG, "loadShader: compiler error");
            Log.e(TAG, "loadShader: " + GLES20.glGetShaderInfoLog(shader) );
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    protected int loadTexture(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
        if (bitmap == null) {
            return -1;
        }

        int[] textureIds = new int[1];
        //????????????
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return -1;
        }
        //????????????
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        //??????????????????S????????????????????????[1/2n,1-1/2n]???????????????????????????border??????
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        //??????????????????T????????????????????????[1/2n,1-1/2n]???????????????????????????border??????
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        //??????????????????????????????????????????2D??????
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }

    // TODO: ????????????
    protected int loadExternelTexture() {
            int[] texture = new int[1];
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            return texture[0];
    }

    protected void createProgram() {
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexSource());
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentSource());
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragmentShader);
        GLES20.glLinkProgram(mProgram);
        int [] status = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "createProgam: link error");
            Log.e(TAG, "createProgam: " + GLES20.glGetProgramInfoLog(mProgram));
            GLES20.glDeleteProgram(mProgram);
            return;
        }
    }

    protected abstract String getVertexSource();

    protected abstract String getFragmentSource();

    protected abstract void onChanged();

    protected abstract void onDraw();

    protected abstract void onCreate();
}
