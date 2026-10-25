package io.android.screen.caster

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageView

class MainActivity : Activity() {

    private lateinit var preview: ImageView
    private lateinit var btnToggle: Button
    private var capturing = false

    private var service: CaptureService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val lb = binder as CaptureService.LocalBinder
            service = lb.getService()
            service?.setListener(object : CaptureService.FrameListener {
                override fun onFrame(bitmap: Bitmap) {
                    runOnUiThread { preview.setImageBitmap(bitmap) }
                }
            })
            bound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preview = findViewById(R.id.preview)
        btnToggle = findViewById(R.id.btnToggle)
        btnToggle.setOnClickListener {
            if (!capturing) startCapture() else stopCapture()
        }
    }

    private fun startCapture() {
        val mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ_PROJECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_PROJECTION) return
        if (resultCode != Activity.RESULT_OK || data == null) return

        val svc = Intent(this, CaptureService::class.java)
        svc.putExtra("resultCode", resultCode)
        svc.putExtra("data", data)
        startForegroundService(svc)
        bindService(svc, connection, Context.BIND_AUTO_CREATE)

        capturing = true
        btnToggle.text = getString(R.string.stop)
    }

    private fun stopCapture() {
        if (bound) {
            unbindService(connection)
            bound = false
        }
        service?.stopCapture()
        service = null
        stopService(Intent(this, CaptureService::class.java))
        capturing = false
        btnToggle.text = getString(R.string.start)
        preview.setImageBitmap(null)
    }

    override fun onDestroy() {
        stopCapture()
        super.onDestroy()
    }

    companion object {
        private const val REQ_PROJECTION = 1001
    }
}
