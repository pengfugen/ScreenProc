package pfg.com.screenproc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by FPENG3 on 2018/7/30.
 */

public class OpenGLVideoPlayer extends Activity implements View.OnClickListener{

    private final static String TAG = "OpenGLVideoPlayer";

    VideoGLSurfaceView mSurfaceView;
    GLVideoRenderer mRenderer;
    Button btn_record;

    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        mSurfaceView = (VideoGLSurfaceView) findViewById(R.id.surface_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
        btn_record = (Button) findViewById(R.id.btn_start_record);
        btn_record.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
        if(isRecording) {
            btn_record.setText("Start Record");
            mSurfaceView.stopRecord();
            isRecording = !isRecording;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        isRecording = !isRecording;
        if(isRecording) {
            btn_record.setText("Stop Record");
            mSurfaceView.startRecord();
        } else {
            btn_record.setText("Start Record");
            mSurfaceView.stopRecord();
        }
    }
}
