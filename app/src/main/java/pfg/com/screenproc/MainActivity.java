package pfg.com.screenproc;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity{

    final String TAG = "ScreenProc";
    MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.logd(TAG, "onCreate");
        /*setContentView(R.layout.activity_main_new);
        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.surface_view);*/
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        MyLog.logd(TAG, "The device supported opengl es version:"+configurationInfo.getGlEsVersion()+ " int value:"+configurationInfo.reqGlEsVersion);
        if(configurationInfo.reqGlEsVersion >= 0x20000) {
            mGLSurfaceView = new MyGLSurfaceView(this);
            setContentView(mGLSurfaceView);

        } else {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.logd(TAG,"onResume");
        mGLSurfaceView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.logd(TAG,"onPause");
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Log.d(TAG,"onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" data:"+data);
        if(Activity.RESULT_OK == resultCode && REQUEST_CODE_SCREENRECORD == requestCode) {
            projection = manager.getMediaProjection(resultCode, data);

        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
