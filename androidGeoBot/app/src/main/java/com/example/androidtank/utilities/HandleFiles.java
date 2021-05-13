package com.example.androidtank.utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that handles loading and saving files
 */
public class HandleFiles {

    // Attributes
    private final String TAG = this.getClass().getName();

    // Constructor that simply logs the instantiation of this class.
    public HandleFiles() {
        Log.i(TAG, "Instantiated new " + this.getClass());
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
}
