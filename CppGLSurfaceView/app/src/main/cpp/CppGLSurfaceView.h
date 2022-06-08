#ifndef CPPGLSURFACEVIEW_CPPGLSURFACEVIEW_H
#define CPPGLSURFACEVIEW_CPPGLSURFACEVIEW_H

#include <jni.h>
#include <android/native_window.h>
#include <pthread.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_test_cppglsurfaceview_NativeFunc_create(JNIEnv *env, jclass type, jint id);
JNIEXPORT void JNICALL Java_com_test_cppglsurfaceview_NativeFunc_surfaceCreated(JNIEnv *env, jclass type, jint id);
JNIEXPORT void JNICALL Java_com_test_cppglsurfaceview_NativeFunc_surfaceChanged(JNIEnv *env, jclass type, jint id, jint width, jint height);
JNIEXPORT void JNICALL Java_com_test_cppglsurfaceview_NativeFunc_onDrawFrame(JNIEnv *env, jclass type, jint mId);
JNIEXPORT void JNICALL Java_com_test_cppglsurfaceview_NativeFunc_surfaceDestroyed(JNIEnv *env, jclass type, jint id);

#ifdef __cplusplus
}
#endif

class CppGLSurfaceView {
private:
    const char *VERTEXSHADER =
        "attribute vec4 vPosition;\n"
        "uniform mat4 u_rotMatrix;\n"
        "void main() {\n"
        "    gl_Position = u_rotMatrix * vPosition;\n"
        "}\n";

    const char *FRAGMENTSHADER =
        "precision mediump float;\n"
        "void main() {\n"
        "    gl_FragColor = vec4(0.0, 1.0, 1.0, 0.7);\n"
        "}\n";

private:
    GLuint createProgram(const char *vertexshader, const char *fragmentshader);
    GLuint loadShader(int shadertype, const char *vertexshader);
    void checkGlError(const char *argstr);

public:
    int mId = -1;
    GLuint mProgram = -1;
    /* 移動 */
    static const int AMOUNTOFMOVE = -10;
    float mMoveX = AMOUNTOFMOVE;
    float mMoveY = AMOUNTOFMOVE;
    float mxPos = 100;
    float myPos = 130;
    int DispX = 0;
    int DispY = 0;
    GLuint mu_rotMatrixHandle = -1;

public:
    CppGLSurfaceView(jint id);
    void initGL();
    void predrawGL();
    void destroy();
};

#endif //CPPGLSURFACEVIEW_CPPGLSURFACEVIEW_H
