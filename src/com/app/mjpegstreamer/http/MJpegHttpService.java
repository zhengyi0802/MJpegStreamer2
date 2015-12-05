package com.app.mjpegstreamer.http;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MJpegHttpService extends Service {
	private final static String TAG = "MJpegHttpService";
	
	private MJpegHttpServer mServer = null;
	private MJpegStream mStream;
	
	public static int httpport = 8080;
	
	private IMJpegHttpCallback mCallback;
	
	
	public static void start(Context context) {
		Intent i = new Intent(context, MJpegHttpService.class);  
		i.putExtra("port", httpport);
		context.startService(i);
	}

	public static boolean stop(Context context) {
		Intent i = new Intent(context, MJpegHttpService.class);  
		return context.stopService(i);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if(mServer == null) {
			try {
					mServer = new MJpegHttpServer(httpport, MJpegHttpService.this);
					mStream = new MJpegStream();
					mServer.setInputStream(mStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(mServer != null)
			mServer.stop();
		mServer = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private IMJpegHttpService.Stub mBinder = new IMJpegHttpService.Stub() {

		@Override
		public void registerCallback(IMJpegHttpCallback callback) throws RemoteException {
			mCallback = callback;			
		}

		@Override
		public void unregisterCallback() throws RemoteException {
			mCallback = null;			
		}

		@Override
		public void sendMedia(byte[] buffer, int size) throws RemoteException {
			//Log.d(TAG, "sendMedia");
			if(size > 0) {
					mStream.saveBuffer(buffer, size);
			}
		}

		@Override
		public boolean isStreaming() throws RemoteException {
			boolean flag = mServer.isStreaming();
			return flag;
		}

		@Override
		public boolean isWriteable() throws RemoteException {			
			return mStream.getWriteable();
		}
		
	};
	
}
