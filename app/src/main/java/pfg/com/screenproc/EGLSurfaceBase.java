package pfg.com.screenproc;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/**
 * Created by FPENG3 on 2018/7/27.
 */

public abstract class EGLSurfaceBase {

    EGLCore mEglCore;
    EGLSurface mEglSurface;
    private int mWidth = -1;
    private int mHeight = -1;

    public EGLSurfaceBase(EGLCore eglCore) {
        mEglCore = eglCore;
    }

    protected EGLSurface createWindowSurface(Object surface) {
        mEglSurface = mEglCore.createWindowSurface(surface);
        return mEglSurface;
    }

    protected void createOffscreenSurface(int width, int height) {
        if (mEglSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        mEglSurface = mEglCore.createOffscreenSurface(width, height);
        mWidth = width;
        mHeight = height;
    }

    protected void makeCurrent() {
        mEglCore.makeCurrent(mEglSurface);
    }

    protected void makeCurrentReadFrom(EGLSurfaceBase readSurface) {
        mEglCore.makeCurrent(mEglSurface, readSurface.mEglSurface);
    }

    protected boolean swapBuffers() {
        return mEglCore.swapBuffers(mEglSurface);
    }

    protected int getWidth() {
        if(mWidth < 0) {
            return mEglCore.querySurface(mEglSurface, EGL14.EGL_WIDTH);
        }
        return mWidth;
    }

    protected int getHeight() {
        if(mHeight < 0) {
            return mEglCore.querySurface(mEglSurface, EGL14.EGL_HEIGHT);
        }
        return mHeight;
    }

    protected void releaseEglSurface() {
        mEglCore.releaseSurface(mEglSurface);
        mEglSurface = EGL14.EGL_NO_SURFACE;
        mWidth = mHeight = -1;
    }

}
