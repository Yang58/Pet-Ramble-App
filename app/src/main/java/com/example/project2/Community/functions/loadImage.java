package com.example.project2.Community.functions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class loadImage extends AsyncTask<Void, Void, Bitmap> {
    private String url;
    private String fileName;
    private String fileType;
    private File cachePath;
    private Bitmap image;

    public loadImage(File cachePath, String url, String fileName, String fileType) {
        this.cachePath = cachePath;
        this.url = url;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL urlConn = new URL(url);
            File file = new File(cachePath, fileName + fileType);
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            if (fileType.equals(".JPG")) {
                image = BitmapFactory.decodeStream(urlConn.openConnection().getInputStream());
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } else if (fileType.equals(".PNG")) {
                image = BitmapFactory.decodeStream(urlConn.openConnection().getInputStream());
                image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } else if (fileType.equals(".GIF")) {
                BufferedInputStream buffInputStream = new BufferedInputStream(urlConn.openConnection().getInputStream());
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                byte img[] = new byte[1024];
                int currentPointer = 0;
                while ((currentPointer = buffInputStream.read()) != -1) {
                    arrayOutputStream.write(currentPointer);
                }
                outputStream.write(arrayOutputStream.toByteArray());
                outputStream.flush();
                buffInputStream.close();
                outputStream.close();
            }
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
