package com.app.mjpegstreamer;

public class ColorSpaceManager {
	
	public static byte[] NV21toYUY2(byte[] input, byte[] output, int width, int height) {
		int plane = width*height;
		for(int h=0; h < height; h++) {
			for(int w=0; w < width; w+=2) {
				int ypos = h*w*2+w*2;
				int vpos = ypos+1;
				int y2pos = ypos+2;
				int upos= ypos+3;
				int y0 = h*w+w;
				int y1 = h*w+w+1;
				int v = plane + w*(h/2) + w;
				int u = plane + w*(h/2) + w+1;
				output[ypos] = input[y0];
				output[upos] = input[v];
				output[y2pos] = input[y1];
				output[vpos] = input[u];
			}
		}
		
		return output;
	}
	
    public static void NV21toRGB( byte[] input, byte[] output,  int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;
        
        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) input[ci * width + cj]));
                int v = (0xff & ((int) input[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) input[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                output[a++] = (byte)(r&0xff);
                output[a++] = (byte)(g&0xff);
                output[a++] = (byte)(b&0xff);
            }
        }
    }

    public static void NV21toARGB( byte[] input, byte[] output,  int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) input[ci * width + cj]));
                int v = (0xff & ((int) input[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) input[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                
                output[a++] = (byte)(0xff);
                output[a++] = (byte)(r&0xff);
                output[a++] = (byte)(g&0xff);
                output[a++] = (byte)(b&0xff);
           }
        }
    }    
    public static byte[] NV21toYUV420Planar(byte[] input, byte[] output, int width, int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize/4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        byte v, u;

        for (int i = 0; i < qFrameSize; i++) {
            v = input[frameSize + i*2];
            u = input[frameSize + i*2 + 1];

            output[frameSize + i + qFrameSize] = v;
            output[frameSize + i] = u;
        }

        return output;
    }

    @SuppressWarnings("unused")
    public static byte[] NV21toYUV420SemiPlanar(byte[] input, byte[] output, int width, int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize/4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        byte v, u;

        for (int i = 0; i < qFrameSize; i++) {
            v = input[frameSize + i*2];
            u = input[frameSize + i*2 + 1];

            output[frameSize + i*2] = u;
            output[frameSize + i*2 + 1] = v;
        }

        return output;
    }

    @SuppressWarnings("unused")
    public synchronized static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
    /*
     * COLOR_TI_FormatYUV420PackedSemiPlanar is YV12
     * We convert by putting the corresponding U and V bytes together (interleaved).
     */
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;
        for (int i = 0; i < (qFrameSize); i++) {
            output[frameSize + i * 2] = (input[frameSize + qFrameSize + i]);
            output[frameSize + i * 2 + 1] = (input[frameSize + i]);
        }
        return output;
    }

    public static byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width * height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width * height; i < width * height + (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i + (width / 2 * height / 2)];
        for (int i = width * height + (width / 2 * height / 2); i < width * height + 2 * (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i - (width / 2 * height / 2)];
        return i420bytes;
    }

}
