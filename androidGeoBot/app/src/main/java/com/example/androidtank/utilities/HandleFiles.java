package com.example.androidtank.utilities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.androidtank.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that handles loading and saving files
 */
public class HandleFiles {

    // Attributes
    private final String TAG = this.getClass().getName();
    private AssetFileDescriptor assetFileDescriptor;

    // Constructor that simply logs the instantiation of this class.
    public HandleFiles() {
        //Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // Get a file from asset folder
    public File getFileFromAssets(String fileName, Context context) throws IOException {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            try (InputStream inputStream = context.getAssets().open(fileName)) {
                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Could not open cascade xml", e);
        }
        return cacheFile;
    }

    // get file from res folder by sending in R.id
    public String getFileFromRes(Context context, int id) throws FileNotFoundException {
        String resPath = "android.resource://" + context.getPackageName() + "/" + id;

        return resPath;
    }

    public int getVideoHeight(Context context) {
        MediaMetadataRetriever metaRetriever = readMetadataFromRaw(context);

        return Integer.parseInt(
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        );
    }

    public int getVideoHWidth(Context context) {
        MediaMetadataRetriever metaRetriever = readMetadataFromRaw(context);

        return Integer.parseInt(
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        );
    }

    // Can read metadata from the raw folder. Might only work with video
    public MediaMetadataRetriever readMetadataFromRaw(Context context) throws RuntimeException{
        assetFileDescriptor = context.getResources().openRawResourceFd(R.raw.mainscreen_video);
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

        metaRetriever.setDataSource(
                assetFileDescriptor.getFileDescriptor(),
                assetFileDescriptor.getStartOffset(),
                assetFileDescriptor.getLength()
        );

        return metaRetriever;
    }
}
