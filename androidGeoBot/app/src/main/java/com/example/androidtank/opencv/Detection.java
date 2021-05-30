package com.example.androidtank.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.androidtank.utilities.HandleFiles;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.IOException;
import java.util.List;

public class Detection {

    // Attributes
    private final String TAG = this.getClass().getName();
    private String thingToDraw = "CIRCLE";
    private String cascadeFile = "cascade.xml";

    // Constructor that simply logs the instantiation of this class.
    public Detection() {
        //Log.i(TAG, "Instantiated new " + this.getClass());
    }


    /**
     *
     * @param bitmap This method processes bitmaps and does OpenCV magic
     * @param context We need the context in order to load the cascade file
     * @return We return the input bitmap with shapes drawn on detected objects
     * @throws IOException
     */
    public Bitmap processImage(Bitmap bitmap, Context context) throws IOException {
        // we convert to mat to work with opencv
        ImageProcessor imageProcessor = new ImageProcessor();
        Mat mat = new Mat();
        mat = imageProcessor.bitmapToMat(bitmap);

        // get trained cascade file
        CascadeClassifier carClassifier = new CascadeClassifier();
        HandleFiles handleFiles = new HandleFiles();
        carClassifier.load(handleFiles.getFileFromAssets(cascadeFile, context).toString());

        // Convert Mat to grayscale
        Mat gray = imageProcessor.grayScale(mat);
        // To store detected objects
        MatOfRect detectedObjects = new MatOfRect();
        // Specify smallest detection size
        int minBotSize = Math.round(mat.rows() * 0.1f);

        // Detect Objects
        carClassifier.detectMultiScale(
                mat,
                detectedObjects,
                1.1,
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minBotSize, minBotSize),
                new Size()    // Not sure if this is needed
        );

        // Draw different shapes. For later, when we want to detect more objects
        List<Rect> listOfObjects = detectedObjects.toList();
        if (this.thingToDraw.equals("RECTANGLE")) {
            mat = this.drawRectangles(mat, listOfObjects);
        } else if (this.thingToDraw.equals("CIRCLE")){
            mat = this.drawCircles(mat, listOfObjects);
        } else {
            Toast.makeText(context, "No shape specified", Toast.LENGTH_SHORT).show();
        }

        // and convert back so image can be streamed
        Utils.matToBitmap(mat,bitmap);

        return bitmap;
    }


    /**
     * Types of shapes to draw on images
     * These shapes encircle the found objects
     */
    private Mat drawRectangles (Mat mat, List<Rect> listOfRect) {
        for (Rect rect : listOfRect) {
            Imgproc.rectangle(mat, rect.tl(), rect.br(), new Scalar(255, 0, 0), 2);
        }

        return mat;
    }

    private Mat drawCircles (Mat mat, List<Rect> listOfRect) {
        for (Rect rect : listOfRect) {
            float extraHeight = -7.5F;
            float rectHalfWidth = (float)rect.width / 2;
            float rectHalfHeight = ((float)rect.height / 2);
            Point center = new Point(
                    rect.x + rectHalfWidth,
                    rect.y + rectHalfHeight + extraHeight
            );
            Imgproc.ellipse(mat, center, new Size(rectHalfWidth, rectHalfHeight),
                    0,
                    0,
                    360,
                    new Scalar(255, 0, 0)
            );
        }

        return mat;
    }


    /**
     * Setters and Getters
     */
    public String getThingToDraw() {
        return thingToDraw;
    }

    public void setThingToDraw(String thingToDraw) {
        this.thingToDraw = thingToDraw;
    }
}
