package pfg.com.screenproc;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by FPENG3 on 2018/7/30.
 */

public class VideoGLSurfaceView extends GLSurfaceView {

    private GLVideoRenderer mRenderer;
    private static final String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"test.mp4";

    Context mContex;

    public VideoGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(3);

        // Set the Renderer for drawing on the GLSurfaceView
        // mRenderer = new MyGLRenderer(context);
        mRenderer = new GLVideoRenderer(context, this, VIDEO_FILE_PATH);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mContex = context;
    }

    public VideoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(3);

        // Set the Renderer for drawing on the GLSurfaceView
        // mRenderer = new MyGLRenderer(context);
        mRenderer = new GLVideoRenderer(context, this, VIDEO_FILE_PATH);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mContex= context;
    }

    public GLVideoRenderer getRenderer() {
        return mRenderer;
    }

    public void startRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                // 传入当前线程的EGL上下文环境作为record线程的共享EGLContext
                mRenderer.startRecord(EGL14.eglGetCurrentContext());
            }
        });
    }

    public void stopRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.stopRecord();
            }
        });
    }
}
