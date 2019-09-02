package com.inuker.rgbconverter;

import android.content.Context;

/**
 * Created by dingjikerbo on 17/8/21.
 */

public class RgbConverter1 extends RgbConverter {

    public RgbConverter1(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    void onStart() {

    }

    @Override
    void onDrawFrame() {
        readPixels();
        pixelsToBitmap();
    }

    @Override
    void onDestroy() {

    }
}
