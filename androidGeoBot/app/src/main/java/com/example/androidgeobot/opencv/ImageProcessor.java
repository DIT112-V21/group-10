package com.example.androidgeobot.opencv;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


/**
 * This Class is a helper class that processes images
 */
public class ImageProcessor {

    // Attributes
    private final String TAG = this.getClass().getName();

    // Constructor that simply logs the instantiation of this class.
    public ImageProcessor() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Handle Image Conversion
     */
    public Mat bitmapToMat(Bitmap bm) {
        Mat mat = new Mat();
        Bitmap bmp32 = bm.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;
    }
/*
    public static Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = new Bitmap().setConfig();
        Bitmap bmp32 = bm.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(mat, bitmap);
        return mat;
    }
 */

    /**
     * Process Mat objects
     */
    public Mat grayScale(Mat mat){
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(mat, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);
        return grayFrame;
    }

}
