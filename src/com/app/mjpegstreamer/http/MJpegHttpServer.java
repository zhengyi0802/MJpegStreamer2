package com.app.mjpegstreamer.http;

import java.io.*;
import java.util.*;
import android.content.Context;
import android.util.Log;

public class MJpegHttpServer extends NanoHTTPD
{
    static final String TAG="MJpegHttpServer";
    
    private MJpegStream mStream;
    private boolean isStreaming = false;
    
    public MJpegHttpServer(int port, Context ctx) throws IOException {
        super(port, ctx.getAssets());
    }
    
    public MJpegHttpServer(int port, String wwwroot) throws IOException {
        super(port, new File(wwwroot).getAbsoluteFile() );
    }

    @Override
    public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {
        Log.d(TAG, "httpd request >>" + method + " '" + uri + "' " + "   " + parms);
		Response res = new NanoHTTPD.Response(HTTP_OK, "multipart/x-mixed-replace;boundary=" + MJpegStream.mBoundary,  mStream);
		
		//res.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0");
		//res.addHeader("Cache-Control", "private");
		res.addHeader("Server", "AndroidServer");
		res.addHeader("Cache-Control", "no-cache");
		res.addHeader("Pragma", "no-cache");
		//res.addHeader("Expires", "-1");
		isStreaming = true;
		return res;
      
	}
    
    public void setInputStream(MJpegStream stream) {
    	mStream = stream;
    }
 
    public boolean isStreaming() {
    	return isStreaming;
    }
    
    @Override
    public void serveDone(Response r) {
    	isStreaming = false;
       try{
            if ( r.isStreaming ) { 
                r.data.close();
            }
       } catch(IOException ex) {
       }
    } 

    public static interface CommonGatewayInterface {
        public String run(Properties parms); 
        public InputStream streaming(Properties parms);
    }
    private HashMap<String, CommonGatewayInterface> cgiEntries = new HashMap<String, CommonGatewayInterface>();
    public void registerCGI(String uri, CommonGatewayInterface cgi) {
        if ( cgi != null)
			cgiEntries.put(uri, cgi);
    }

}
