/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/chevy/workspace/MJpegStreamer2/src/com/app/mjpegstreamer/http/IMJpegHttpService.aidl
 */
package com.app.mjpegstreamer.http;
public interface IMJpegHttpService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.app.mjpegstreamer.http.IMJpegHttpService
{
private static final java.lang.String DESCRIPTOR = "com.app.mjpegstreamer.http.IMJpegHttpService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.app.mjpegstreamer.http.IMJpegHttpService interface,
 * generating a proxy if needed.
 */
public static com.app.mjpegstreamer.http.IMJpegHttpService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.app.mjpegstreamer.http.IMJpegHttpService))) {
return ((com.app.mjpegstreamer.http.IMJpegHttpService)iin);
}
return new com.app.mjpegstreamer.http.IMJpegHttpService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.app.mjpegstreamer.http.IMJpegHttpCallback _arg0;
_arg0 = com.app.mjpegstreamer.http.IMJpegHttpCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
this.unregisterCallback();
reply.writeNoException();
return true;
}
case TRANSACTION_sendMedia:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int _arg1;
_arg1 = data.readInt();
this.sendMedia(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_isStreaming:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isStreaming();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isWriteable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isWriteable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.app.mjpegstreamer.http.IMJpegHttpService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void registerCallback(com.app.mjpegstreamer.http.IMJpegHttpCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendMedia(byte[] buffer, int size) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(buffer);
_data.writeInt(size);
mRemote.transact(Stub.TRANSACTION_sendMedia, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isStreaming() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isStreaming, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isWriteable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isWriteable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_sendMedia = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_isStreaming = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isWriteable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void registerCallback(com.app.mjpegstreamer.http.IMJpegHttpCallback callback) throws android.os.RemoteException;
public void unregisterCallback() throws android.os.RemoteException;
public void sendMedia(byte[] buffer, int size) throws android.os.RemoteException;
public boolean isStreaming() throws android.os.RemoteException;
public boolean isWriteable() throws android.os.RemoteException;
}
