package com.example.community_t1.functions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("deprecation")
public class DrawUrlImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView ivSample;

    public DrawUrlImageTask(ImageView ivSample) {
        this.ivSample = ivSample;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        ivSample.setImageBitmap(bitmap);
    }
}