package com.app.mjpegstreamer.http;

import com.app.mjpegstreamer.http.IMJpegHttpCallback;

interface IMJpegHttpService {
	void registerCallback(IMJpegHttpCallback callback);
	void unregisterCallback();
	void sendMedia(in byte[] buffer, in int size);
	boolean isStreaming();
	boolean isWriteable();
}
