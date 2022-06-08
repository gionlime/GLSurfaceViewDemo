#include <map>
#include <android/log.h>
#include "CppGLSurfaceView.h"

std::map<int, CppGLSurfaceView*> gpSufacesLists;


#ifdef __cplusplus
extern "C" {
#endif

void Java_com_test_cppglsurfaceview_NativeFunc_create(JNIEnv *pEnv, jclass type, jint id) {
    gpSufacesLists[id] = new CppGLSurfaceView(id);
}

void Java_com_test_cppglsurfaceview_NativeFunc_surfaceCreated(JNIEnv *pEnv, jclass type, jint id) {
    CppGLSurfaceView *psurface = gpSufacesLists[id];
    if(psurface != NULL) {
        psurface->initGL();
        psurface->predrawGL();
    }
}

#include <unistd.h>
void Java_com_test_cppglsurfaceview_NativeFunc_surfaceChanged(JNIEnv *pEnv, jclass type, jint id, jint width, jint height) {
    glViewport(0,0,width,height);
    CppGLSurfaceView *psurface = gpSufacesLists[id];
    if(psurface != NULL) {
        psurface->DispX = width;
        psurface->DispY = height;
    }
}

void Java_com_test_cppglsurfaceview_NativeFunc_onDrawFrame(JNIEnv *env, jclass type, jint id) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    CppGLSurfaceView *psurface = gpSufacesLists[id];
    if(psurface == NULL) return;

    psurface->mxPos += psurface->mMoveX;
    psurface->myPos += psurface->mMoveY;
    if((psurface->mxPos > (2*psurface->DispX)) || (psurface->mxPos < 0)) psurface->mMoveX = -psurface->mMoveX;
    if((psurface->myPos > (2*psurface->DispY)) || (psurface->myPos < 0)) psurface->mMoveY = -psurface->mMoveY;
    float translateMatrix[] = {
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            psurface->mxPos/psurface->DispX-1, psurface->myPos/psurface->DispY-1, 1, 1
    };
    glUniformMatrix4fv(psurface->mu_rotMatrixHandle, 1, false, translateMatrix);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 3);
}

void Java_com_test_cppglsurfaceview_NativeFunc_surfaceDestroyed(JNIEnv *pEnv, jclass type, jint id) {
    CppGLSurfaceView *psurface = gpSufacesLists[id];
    psurface->destroy();
}

#ifdef __cplusplus
}
#endif

CppGLSurfaceView::CppGLSurfaceView(jint id) : mId(id) {
}

void CppGLSurfaceView::initGL() {
    mProgram = createProgram(VERTEXSHADER, FRAGMENTSHADER);
}

GLuint CppGLSurfaceView::createProgram(const char *vertexshader, const char *fragmentshader) {
    GLuint vhandle = loadShader(GL_VERTEX_SHADER, vertexshader);
    if(vhandle == GL_FALSE) return GL_FALSE;

    GLuint fhandle = loadShader(GL_FRAGMENT_SHADER, fragmentshader);
    if(fhandle == GL_FALSE) return GL_FALSE;

    GLuint programhandle = glCreateProgram();
    if(programhandle == GL_FALSE) {
        checkGlError("glCreateProgram");
        return GL_FALSE;
    }

    glAttachShader(programhandle, vhandle);
    checkGlError("glAttachShader(VERTEX_SHADER)");
    glAttachShader(programhandle, fhandle);
    checkGlError("glAttachShader(FRAGMENT_SHADER)");

    glLinkProgram(programhandle);
    GLint linkStatus = GL_FALSE;
    glGetProgramiv(programhandle, GL_LINK_STATUS, &linkStatus);
    if(linkStatus != GL_TRUE) {
        GLint bufLen = 0;
        glGetProgramiv(programhandle, GL_INFO_LOG_LENGTH, &bufLen);
        if(bufLen) {
            char *logstr = (char*)malloc(bufLen);
            glGetProgramInfoLog(mProgram, bufLen, NULL, logstr);
            __android_log_print(ANDROID_LOG_ERROR, "CNativeSurface", "%d glLinkProgram() Fail!!\n %s", mId, logstr);
            free(logstr);
        }
        glDeleteProgram(programhandle);
        programhandle = GL_FALSE;
    }

    return programhandle;
}

GLuint CppGLSurfaceView::loadShader(int shadertype, const char *sourcestring) {
    GLuint shaderhandle = glCreateShader(shadertype);
    if(!shaderhandle) return GL_FALSE;

    glShaderSource(shaderhandle, 1, &sourcestring, NULL);
    glCompileShader(shaderhandle);

    GLint compiled = GL_FALSE;
    glGetShaderiv(shaderhandle, GL_COMPILE_STATUS, &compiled);
    if(!compiled) {
        GLint infoLen = 0;
        glGetShaderiv(shaderhandle, GL_INFO_LOG_LENGTH, &infoLen);
        if(infoLen) {
            char *logbuf = (char*)malloc(infoLen);
            if(logbuf) {
                glGetShaderInfoLog(shaderhandle, infoLen, NULL, logbuf);
                __android_log_print(ANDROID_LOG_ERROR, "CNativeSurface", "%d shader failuer!!\n%s", mId, logbuf);
                free(logbuf);
            }
        }
        glDeleteShader(shaderhandle);
        shaderhandle = GL_FALSE;
    }

    return shaderhandle;
}

void CppGLSurfaceView::checkGlError(const char *argstr) {
    for(GLuint error = glGetError(); error; error = glGetError())
        __android_log_print(ANDROID_LOG_ERROR, "CNativeSurface", "%d after %s errcode=%d", mId, argstr, error);
}

void CppGLSurfaceView::predrawGL() {
    GLuint ma_PositionHandle = glGetAttribLocation(mProgram, "vPosition");
    mu_rotMatrixHandle = glGetUniformLocation(mProgram, "u_rotMatrix");

    glUseProgram(mProgram);
    static const GLfloat vertexes[] = {0,0.5, -0.5,-0.5, 0.5,-0.5};
    glVertexAttribPointer(ma_PositionHandle, 2, GL_FLOAT, GL_FALSE, 0, vertexes);
    glEnableVertexAttribArray(ma_PositionHandle);

    mxPos = 0;
    myPos = 0;
    glClearColor(0, 0, 0, 0);
}

void CppGLSurfaceView::destroy() {

}
