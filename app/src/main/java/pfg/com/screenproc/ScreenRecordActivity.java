package pfg.com.screenproc;

import android.app.Activity;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Choreographer;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by FPENG3 on 2018/7/26.
 */

public class ScreenRecordActivity extends Activity implements SurfaceHolder.Callback, Choreographer.FrameCallback{

    private final static String TAG = "ScreenRecord";

    SurfaceView mSurfaceView;
    private static final String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"test.mp4";
    private static final String RECORD_VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"record.mp4";


    private Surface outputSurface;
    VideoDecoderCore decoderCore;


    private static final int RECMETHOD_DRAW_TWICE = 0;
    private static final int RECMETHOD_FBO = 1;
    private static final int RECMETHOD_BLIT_FRAMEBUFFER = 2;
    private int mSelectedRecordMethod;
    private static boolean mRecordingEnabled = false;
    private RenderThread mRenderThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fbo);
        mSurfaceView = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        mSelectedRecordMethod = RECMETHOD_FBO;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Choreographer.getInstance().postFrameCallback(this);

    }

    public void clickTogglePlaying(@SuppressWarnings("unused") View unused) {
        MyLog.logd(TAG, "clickTogglePlaying");
        decoderCore.start();
        decoderCore.waitForInit();

        Choreographer.getInstance().postFrameCallback(this);
    }

    public void clickToggleRecording(@SuppressWarnings("unused") View unused) {
        MyLog.logd(TAG, "clickToggleRecording");
        Choreographer.getInstance().postFrameCallback(this);
        RenderHandler rh = mRenderThread.getHandler();
        if(rh != null) {
            mRecordingEnabled = !mRecordingEnabled;
            rh.setRecordingEnabled(mRecordingEnabled);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        MyLog.logd(TAG,"surfaceCreated isCurrentThread:"+Looper.myLooper().isCurrentThread());
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        File outputFile = new File(RECORD_VIDEO_FILE_PATH);
        mRenderThread = new RenderThread(sv.getHolder(), outputFile);
        mRenderThread.start();
        mRenderThread.waitUntilReady();
        mRenderThread.setRecordMethod(mSelectedRecordMethod);
        RenderHandler rh = mRenderThread.getHandler();
        if(rh != null) {
            rh.sendSurfaceCreated();
        }
        /*RenderHandler rh = mRenderThread.getHandler();
        if(rh != null) {
            rh.sendSurfaceCreated();
        }*/


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                               int height) {
        MyLog.logd(TAG,"surfaceChanged isCurrentThread:"+Looper.myLooper().isCurrentThread());
        outputSurface = surfaceHolder.getSurface();
        decoderCore = new VideoDecoderCore(VIDEO_FILE_PATH, outputSurface);

        mRenderThread.getHandler().sendSurfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        MyLog.logd(TAG,"surfaceDestroyed isCurrentThread:"+Looper.myLooper().isCurrentThread());
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendShutdown();
            try {
                mRenderThread.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("join was interrupted", ie);
            }
        }
        mRenderThread = null;
        mRecordingEnabled = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        decoderCore.stop();
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doFrame(long var1) {
        MyLog.logd(TAG,"doFrame isCurrentThread:"+Looper.myLooper().isCurrentThread());
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame();
        }
    }

    private static class RenderThread extends Thread {

        private static final String TAG = "RenderThread";
        // Used to wait for the thread to start.
        private Object mStartLock = new Object();
        private boolean mReady = false;
        private RenderHandler mRenderHandler;
        private EGLCore mEglCore;
        private VideoEncoderCore mEncoderCore;

        private SurfaceHolder mSurfaceHolder;
        private File mOutputFile;
        private int mRecordMethod;
        private WindowSurface mWindowSurface;
        private boolean mRecordedPrevious;

        private WindowSurface mInputWindowSurface;

        public RenderThread(SurfaceHolder holder, File outputFile) {
            mSurfaceHolder = holder;
            mOutputFile = outputFile;
        }

        @Override
        public void run() {
            Looper.prepare();
            mRenderHandler = new RenderHandler(this);
            mEglCore = new EGLCore(EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);


            synchronized (mStartLock) {
                mStartLock.notifyAll();
                mReady = true;
            }
            Looper.loop();
            MyLog.logd(TAG, "looper quit");
            releaseGl();
            mEglCore.release();
            synchronized (mStartLock) {
                mReady = false;
            }
        }

        public void waitUntilReady() {
            synchronized (mStartLock) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public RenderHandler getHandler() {
            return mRenderHandler;
        }

        public void surfaceCreated() {
            MyLog.logd(TAG, "surfaceCreated");
            Surface surface = mSurfaceHolder.getSurface();
            //prepareGl(surface);
        }

        public void prepareGl(Surface surface) {
            MyLog.logd(TAG, "prepareGl");
            mWindowSurface = new WindowSurface(mEglCore, surface, false);
            mWindowSurface.makeCurrent();
        }

        public void surfaceChanged(int width, int height) {
            MyLog.logd(TAG, "surfaceChanged");
            prepareFramebuffer(width, height);
        }

        public void prepareFramebuffer(int width, int height) {
            MyLog.logd(TAG, "prepareFramebuffer");
        }

        public void releaseGl() {
            MyLog.logd(TAG, "releaseGl");
            if (mWindowSurface != null) {
                mWindowSurface.release();
                mWindowSurface = null;
            }

            mEglCore.makeNothingCurrent();
        }

        public void setRecordingEnabled(boolean enabled) {
            MyLog.logd(TAG, "setRecordingEnabled enabled:"+enabled);

            if(enabled) {
                startEncoder();
            } else {
                stopEncoder();
            }
        }

        public void setRecordMethod(int recordMethod) {
            MyLog.logd(TAG, "setRecordMethod " + recordMethod);
            mRecordMethod = recordMethod;
        }

        public void startEncoder() {
            // MyLog.logd(TAG, "startEncoder");
            if(mWindowSurface != null) {
                int windowWidth = mWindowSurface.getWidth();
                int windowHeight = mWindowSurface.getHeight();
                MyLog.logd(TAG, "startEncoder windowWidth:"+windowWidth+" windowHeight:"+windowHeight);
            }

            mEncoderCore = new VideoEncoderCore(1280, 720, mOutputFile.toString());
            mInputWindowSurface = new WindowSurface(mEglCore, mEncoderCore.getInputSurface(), true);
        }

        public void stopEncoder() {
            MyLog.logd(TAG, "stopEncoder");
            if(mEncoderCore != null) {
                mEncoderCore.drainEncoder(true);
                mEncoderCore.release();
                mEncoderCore = null;
            }

            if (mInputWindowSurface != null) {
                mInputWindowSurface.release();
                mInputWindowSurface = null;
            }
        }

        public void shutdown() {
            MyLog.logd(TAG, "shutdown");
            stopEncoder();
            Looper.myLooper().quit();
        }

        public void doFrame() {
            MyLog.logd(TAG, "doFrame mRecordingEnabled:"+mRecordingEnabled+" mRecordedPrevious:"+mRecordedPrevious);
            boolean swapResult;
            if (!mRecordingEnabled || mRecordedPrevious) {
                mRecordedPrevious = false;
                // Render the scene, swap back to front.
                draw();
                //swapResult = mWindowSurface.swapBuffers();
            } else {
                mRecordedPrevious = true;
                if(mRecordMethod == RECMETHOD_FBO) {
                    //swapResult = mWindowSurface.swapBuffers();

                    // Blit to encoder.
                    mEncoderCore.drainEncoder(false);
                    mInputWindowSurface.makeCurrent();
                    /*GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // again, only really need to
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);     //  clear pixels outside rect
                    GLES20.glViewport(mVideoRect.left, mVideoRect.top,
                            mVideoRect.width(), mVideoRect.height());*/

                    // mInputWindowSurface.setPresentationTime(timeStampNanos);
                    mInputWindowSurface.swapBuffers();

                    // Restore previous values.
                    //GLES20.glViewport(0, 0, mWindowSurface.getWidth(), mWindowSurface.getHeight());
                    //mWindowSurface.makeCurrent();
                }
            }
        }

        public void update() {

        }

        public void draw() {

        }

    }

    private static class RenderHandler extends Handler {

        private static final int MSG_SURFACE_CREATED = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_DO_FRAME = 2;
        private static final int MSG_RECORDING_ENABLED = 3;
        private static final int MSG_RECORD_METHOD = 4;
        private static final int MSG_SHUTDOWN = 5;

        WeakReference<RenderThread> renderThreadRef;

        public RenderHandler(RenderThread renderThread) {
            renderThreadRef = new WeakReference<RenderThread>(renderThread);
        }

        public void sendSurfaceCreated() {
            sendMessage(obtainMessage(MSG_SURFACE_CREATED));
        }

        public void sendSurfaceChanged(int width, int height) {
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        public void sendDoFrame() {
            sendMessage(obtainMessage(MSG_DO_FRAME));
        }

        public void setRecordingEnabled(boolean enabled) {
            sendMessage(obtainMessage(MSG_RECORDING_ENABLED, enabled ? 1:0, 0));
        }

        public void setRecordMethod(int recordMethod) {
            sendMessage(obtainMessage(MSG_RECORD_METHOD, recordMethod, 0));
        }

        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SURFACE_CREATED:
                    renderThreadRef.get().surfaceCreated();
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThreadRef.get().surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_DO_FRAME:
                    renderThreadRef.get().doFrame();
                    break;
                case MSG_RECORDING_ENABLED:
                    boolean enabled = (msg.arg1 == 1);
                    renderThreadRef.get().setRecordingEnabled(enabled);
                    break;
                case MSG_RECORD_METHOD:
                    renderThreadRef.get().setRecordMethod(msg.arg1);
                    break;
                case MSG_SHUTDOWN:
                    renderThreadRef.get().shutdown();
                    break;
                default:
                    break;
            }
        }
    };


}
