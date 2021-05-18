package com.example.androidtank;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.example.androidtank.utilities.HandleFiles;

/**
 * Custom VideoView for displaying MainActivity background video
 * TODO; Refactor setLayoutLook. Use instance variables in a more efficient way.
 */
public class MainVideoView extends VideoView {
    final AssetFileDescriptor assetFileDescriptor =
            getResources().openRawResourceFd(R.raw.mainscreen_video);
    private int videoWidth;
    private int videoHeight;
    private String videoPath;

    public MainVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MainVideoView(Context context) {
        super(context);
    }

    // Makes it possibly for video to be correctly scaled and centered in view
    public void setLayoutLook(VideoView videoView) {
        float videoAspect = 0;
        HandleFiles handleFiles = new HandleFiles();

        // Set video dimensions and video aspect ratio by reading file metadata
        try {
            videoHeight = handleFiles.getVideoHeight(getContext());
            videoWidth = handleFiles.getVideoHWidth(getContext());
            //getAndSetVideoDimensions();
            videoAspect = (float) videoHeight / (float) videoWidth;
        } catch (RuntimeException e){
            e.printStackTrace();
            Log.i("Metadata", "Could not get and set video dimensions");

            // if it fails, use a pre-set aspect ratio
            videoAspect = getVideoAspect();
        }

        // Variables for storing screen dimensions and screen aspect ratio
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenAspect = (float) screenHeight / (float) screenWidth;
        android.view.ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();

        // Change the VideoView parameters depending on previously set variables
        if (videoAspect < screenAspect) {
            layoutParams.height = screenHeight;
            layoutParams.width = (int) ((float) screenHeight / videoAspect);
        } else {
            layoutParams.width = screenWidth;
            layoutParams.height = (int) ((float) screenWidth * videoAspect);
        }
        videoView.setLayoutParams(layoutParams);
    }

    // Returns a default aspect ratio. Based on 16:9
    private float getVideoAspect(){
        return 0.54f;
    }

    // Enable restarting video
    public void enableRestartVideo() {
        this.setOnCompletionListener(mp -> {
            mp.seekTo(0); // Go to start of video
            mp.start();
        });
    }

    @Override
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

}