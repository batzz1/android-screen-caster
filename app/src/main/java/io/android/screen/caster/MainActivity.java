package io.android.screen.caster;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.ImageView;

import android.app.Activity;

public class MainActivity extends Activity {

    private static final int REQ_PROJECTION = 1001;
    private ImageView preview;
    private Button btnToggle;
    private boolean capturing = false;

    private CaptureService service;
    private boolean bound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            CaptureService.LocalBinder lb = (CaptureService.LocalBinder) binder;
            service = lb.getService();
            service.setListener(bitmap -> runOnUiThread(() -> preview.setImageBitmap(bitmap)));
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = findViewById(R.id.preview);
        btnToggle = findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (!capturing) startCapture();
                else stopCapture();
            }
        });
    }

    private void startCapture() {
        MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PROJECTION) return;
        if (resultCode != Activity.RESULT_OK || data == null) return;

        Intent svc = new Intent(this, CaptureService.class);
        svc.putExtra("resultCode", resultCode);
        svc.putExtra("data", data);
        startForegroundService(svc);
        bindService(svc, connection, Context.BIND_AUTO_CREATE);

        capturing = true;
        btnToggle.setText(R.string.stop);
    }

    private void stopCapture() {
        if (bound) {
            unbindService(connection);
            bound = false;
        }
        if (service != null) {
            service.stopCapture();
            service = null;
        }
        stopService(new Intent(this, CaptureService.class));
        capturing = false;
        btnToggle.setText(R.string.start);
        preview.setImageBitmap(null);
    }

    @Override
    protected void onDestroy() {
        stopCapture();
        super.onDestroy();
    }
}
