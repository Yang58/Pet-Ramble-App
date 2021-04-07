package com.example.project2.Community.functions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class loadImage extends AsyncTask<Void, Void, Bitmap> {
    private String url;
    private String fileName;
    private File cachePath;
    private Bitmap image;

    public loadImage(File cachePath, String url, String fileName) {
        this.cachePath = cachePath;
        this.url = url;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL urlConn = new URL(url);
            File file = new File(cachePath, fileName+".JPG");
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            image = BitmapFactory.decodeStream(urlConn.openConnection().getInputStream());
            image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }
}
