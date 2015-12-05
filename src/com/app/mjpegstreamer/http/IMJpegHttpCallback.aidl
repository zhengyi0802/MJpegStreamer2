package com.app.mjpegstreamer.http;

interface IMJpegHttpCallback {
	void  read(in byte[] buffer);
}