package com.inuker.rgbconverter;

import android.content.Context;
import android.opengl.GLES30;

import com.inuker.library.program.YUVProgram;
import com.inuker.library.utils.GlUtil;

/**
 * Created by dingjikerbo on 17/8/22.
 */

public class RgbConverter3 extends SingleRgbConverter {

    private int mFrameBuffer;

    private int mOffscreenTexture;

    private YUVProgram mYUVProgram;

    public RgbConverter3(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    void onSurfaceCreated() {
        super.onSurfaceCreated();
        mYUVProgram = new YUVProgram(mContext, mWidth, mHeight);
        prepareFramebuffer();
    }

    private void prepareFramebuffer() {
        int[] values = new int[1];

        // Create a texture object and bind it.  This will be the color buffer.
        GLES30.glGenTextures(1, values, 0);
        mOffscreenTexture = values[0];   // expected > 0
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mOffscreenTexture);

        // Create texture storage.
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mWidth, mHeight, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

        // Set parameters.  We're probably using non-power-of-two dimensions, so
        // some values may not be available for use.
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,
                GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,
                GLES30.GL_CLAMP_TO_EDGE);

        // Create framebuffer object and bind it.
        GLES30.glGenFramebuffers(1, values, 0);
        mFrameBuffer = values[0];    // expected > 0
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffer);

        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, mOffscreenTexture, 0);

        // See if GLES is happy with all this.
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        GlUtil.checkGlError("prepareFramebuffer done");
    }

    @Override
    void onDrawSurface() {
        super.onDrawSurface();

        synchronized (mYUVBuffer) {
            mYUVProgram.useProgram();
            mYUVProgram.draw(mYUVBuffer.array());
        }

        readPixels();
        pixelsToBitmap();
    }

    @Override
    void onSurfaceDestroy() {
        mYUVProgram.release();
        GLES30.glDeleteFramebuffers(1, new int[] {mFrameBuffer}, 0);
        GLES30.glDeleteTextures(1, new int[] {mOffscreenTexture}, 0);
        super.onSurfaceDestroy();
    }
}
