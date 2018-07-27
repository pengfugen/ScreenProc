package pfg.com.screenproc;
import pfg.com.screenproc.EGLCore;

import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * Created by FPENG3 on 2018/7/27.
 */

public class WindowSurface extends EGLSurfaceBase {

    boolean releaseSurface;
    Surface mSurfacce;

    public WindowSurface(EGLCore eglCore, Surface surface, boolean releaseSurface) {
        super(eglCore);
        createWindowSurface(surface);
        mSurfacce = surface;
    }

    public WindowSurface(EGLCore eglCore, SurfaceTexture surfaceTexture) {
        super(eglCore);
        createWindowSurface(surfaceTexture);
    }

    public void release() {
        releaseEglSurface();
        if(mSurfacce != null) {
            if(releaseSurface) {
                mSurfacce.release();
            }
            mSurfacce = null;
        }
    }
}
