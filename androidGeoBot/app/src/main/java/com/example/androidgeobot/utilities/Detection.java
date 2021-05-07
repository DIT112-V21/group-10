package com.example.androidgeobot.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Detection {


    public  Mat convertToMat(Bitmap bm) {
        Mat mat = new Mat();
        Bitmap bmp32 = bm.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;
    }

    public  Mat grayScale(Mat mat){
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(mat, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);
        return grayFrame;
    }

    public Bitmap processImage(Bitmap bitmap, Context context) throws IOException {
        // we convert to mat to work with opencv
        Mat mat = convertToMat(bitmap);
        // get trained cascade file
        CascadeClassifier carClassifier = new CascadeClassifier();
        carClassifier.load(getCascadeCacheFile(context).toString());

        Mat gray = grayScale(mat);
        MatOfRect cars = new MatOfRect();
        // Detect cars
        carClassifier.detectMultiScale(gray, cars);
        List<Rect> listOfCars = cars.toList();
        for (Rect car : listOfCars) {
            Point center = new Point(car.x + car.width / 2, car.y + car.height / 2);
            Imgproc.ellipse(mat, center, new Size(car.width / 2, car.height / 2), 0, 0, 360,
                    new Scalar(255, 0, 255));
        }

        // and convert back so image can be streamed
        Utils.matToBitmap(mat,bitmap);
        return bitmap;
    }

    // need to get cascade file from asset folder
    public File getCascadeCacheFile(Context context) throws IOException {
        File cacheFile = new File(context.getCacheDir(), "cascade.xml");
        try {
            InputStream inputStream = context.getAssets().open("cascade.xml");
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException("Could not open cascade xml", e);
        }
        return cacheFile;
    }


}
