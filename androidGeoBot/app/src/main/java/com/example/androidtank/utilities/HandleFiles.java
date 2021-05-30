package com.example.androidtank.utilities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.androidtank.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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

    /**
     * Write and read modified from here: https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
     * These oinly work with "broker.txt" at the moment but this could be easily changed.
     * @param data in String format
     * File is saved in /data/data/<package_name>/files/broker.txt in the apps directory
     */
    public void write(ArrayList<String> data, Context context) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput("broker.txt", Context.MODE_PRIVATE));
            for (String line: data) {
                osw.write(line + "\n");
            }
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a list of the read data. Each line becomes a String object in the ArrayList
     */
    public ArrayList<String> read(Context context) throws NullPointerException{
        ArrayList<String> readData = new ArrayList<>();

        try {
            InputStream is = context.openFileInput("broker.txt");

            if ( is != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String currentLine;

                while ( (currentLine = bufferedReader.readLine()) != null ) {
                    readData.add(currentLine);
                }

                is.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readData;
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
