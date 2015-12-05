package com.app.mjpegstreamer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.PictureCallback;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.Handler;
import android.os.IBinder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.app.mjpegstreamer.R;
import com.app.mjpegstreamer.http.IMJpegHttpCallback;
import com.app.mjpegstreamer.http.IMJpegHttpService;
import com.app.mjpegstreamer.http.MJpegHttpServer;

//import com.google.android.gms.ads.*;

public class MJpegStreamerActivity extends Activity
        implements CameraView.CameraReadyCallback {
    public static String TAG="CameraStreamerActivity";
    private final int ServerPort = 8080;
    private final int StreamingPort = 8088;
    private final int PictureWidth = 640;
    private final int PictureHeight = 480;
    private final int MediaBlockNumber = 3;
    private final int MediaBlockSize = 1024*512;
    private final int EstimatedFrameNumber = 30;
    private final int StreamingInterval = 100;

    private OverlayView overlayView = null;
    private CameraView cameraView = null;
    
    private MJpegHttpServerConnection mConnection; 
    private IMJpegHttpService mService;
    private Intent mIntent;
    
    private JpegEncoder mJpegEncoder;

    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private VideoEncodingTask videoTask = new  VideoEncodingTask();
    private ReentrantLock previewLock = new ReentrantLock();
    boolean inProcessing = false;

    byte[] yuvFrame = new byte[1920*1280*2];

    private IMJpegHttpCallback.Stub mCallback = new  IMJpegHttpCallback.Stub() {
		
		@Override
		public void read(byte[] buffer) throws RemoteException {
			
		}
		
	};
    
    
    private class MJpegHttpServerConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = IMJpegHttpService.Stub.asInterface(service);
			try {
				mService.registerCallback(mCallback);
			} catch(RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			try{
				mService.unregisterCallback();
			} catch(RemoteException e) {
				e.printStackTrace();
			}
			mService = null;
		}
    	
    }


    //
    //  Activiity's event handler
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // application setting
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // load and setup GUI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* removed AD
        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-7979468066645196/6325058260");
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout)findViewById(R.id.layout_ad);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */

        // init audio and camera
        if ( initWebServer() ) {
            initCamera();
        } else {
            return;
        }
/*
        streamingHandler = new Handler();
        streamingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doStreaming();
            }
        }, StreamingInterval);
*/
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        if ( mConnection != null) {
        	unbindService(mConnection);
        	mConnection = null;
        }
        
        if ( cameraView != null) {
            previewLock.lock();
            cameraView.StopPreview();
            cameraView.Release();
            previewLock.unlock();
            cameraView = null;
           	mJpegEncoder.close();
           	mJpegEncoder = null;
        }

        finish();
        //System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //
    //  Interface implementation
    //
    public void onCameraReady() {
        cameraView.StopPreview();
        cameraView.setupCamera(PictureWidth, PictureHeight, 4, 15.0, previewCb);
       	mJpegEncoder = new JpegEncoder(cameraView.Width(), cameraView.Height() );
        
        cameraView.StartPreview();
    }
    
    private void initService() {
    	mConnection = new MJpegHttpServerConnection();
    	mIntent = new Intent();
    	mIntent.setClassName("com.app.mjpegstreamer", "com.app.mjpegstreamer.http.MJpegHttpService");
    	mIntent.setAction("com.app.mjpegstreamer.STARTWEBSERVER");
    	boolean ret = bindService(mIntent, mConnection,  Context.BIND_AUTO_CREATE);
    	if(!ret) {
    		finish();
    	}
    	startService(mIntent);
    }

    //
    //  Internal help functions
    //
    private boolean initWebServer() {

        String ipAddr = wifiIpAddress(this);
        if ( ipAddr != null ) {
        	initService();
         }

        TextView tv = (TextView)findViewById(R.id.tv_message);
        if ( mIntent != null) {
            tv.setText( getString(R.string.msg_access_local) + " http://" + ipAddr  + ":8080" );
            return true;
        } else {
            if ( ipAddr == null) {
                tv.setText( getString(R.string.msg_wifi_error) );
            } else {
                tv.setText( getString(R.string.msg_port_error) );
            }
            return false;
        }
    }
    private void initCamera() {
        SurfaceView cameraSurface = (SurfaceView)findViewById(R.id.surface_camera);
        cameraView = new CameraView(cameraSurface);
        cameraView.setCameraReadyCallback(this);

        overlayView = (OverlayView)findViewById(R.id.surface_overlay);
        //overlayView_.setOnTouchListener(this);
        //overlayView_.setUpdateDoneCallback(this);
    }

	protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    //
    //  Internal help class and object definment
    //
    private PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] frame, Camera c) {
            previewLock.lock();
            boolean flag = false;
            try {
				flag = mService.isStreaming();
				//Log.d(TAG, "flag = " + flag);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            if(flag) doVideoEncode(frame);
            c.addCallbackBuffer(frame);
            previewLock.unlock();
        }
    };

    private void doVideoEncode(byte[] frame) {
        if ( inProcessing == true) {
            return;
        }
        inProcessing = true;

        int picWidth = cameraView.Width();
        int picHeight = cameraView.Height();
        int size = picWidth*picHeight + picWidth*picHeight/2;
        System.arraycopy(frame, 0, yuvFrame, 0, size);

        executor.execute(videoTask);
    };

    private class VideoEncodingTask implements Runnable {
        private byte[] resultNal =new byte[1280*720*3];
    	//private byte[] resultNal = null;
    	
        public VideoEncodingTask() {
        }

        public void run() {
            int millis = (int)(System.currentTimeMillis() % 65535);
            int ret;
           	ret= mJpegEncoder.offerEncoder(yuvFrame, resultNal);
            if ( ret <= 0) {
            	Log.w(TAG, "no encoded frame");
            	inProcessing = false;
                return;
            }
            
            synchronized(MJpegStreamerActivity.this) {
            	try {
            		mService.sendMedia(resultNal, ret);
					while(!mService.isWriteable());
            	} catch (RemoteException e) {
            		e.printStackTrace();
            	}
            }
            inProcessing = false;
        }
    };
 
}
