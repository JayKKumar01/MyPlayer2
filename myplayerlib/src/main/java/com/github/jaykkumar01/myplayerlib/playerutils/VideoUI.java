package com.github.jaykkumar01.myplayerlib.playerutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.Format;
import androidx.media3.exoplayer.ExoPlayer;

import com.github.jaykkumar01.myplayerlib.R;

import java.io.IOException;

@SuppressLint("UnsafeOptInUsageError")
public class VideoUI {
    public static double videoRatio = -1;
    public static double getFFratio(ExoPlayer player) {
        Format videoFormat = player.getVideoFormat();
        return (double) videoFormat.width/videoFormat.height;
    }
    public static int[] dimension(String path){
        MediaMetadataRetriever retriever = null;
        Bitmap bmp;
        int w = -1;
        int h = -1;
        try {
            retriever = new  MediaMetadataRetriever();
            retriever.setDataSource(path);
            bmp = retriever.getFrameAtTime();
            w = bmp.getWidth();
            h = bmp.getHeight();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally{
            if (retriever != null){
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new int[]{w,h};
    }

    public static void setRatio(Context context, String path) {

        Activity activity = (Activity) context;
        boolean isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        ConstraintLayout layout1 = activity.findViewById(R.id.leftBox);
        ConstraintLayout layout2 = activity.findViewById(R.id.rightBox);
        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams)  layout1.getLayoutParams();
        ConstraintLayout.LayoutParams lp2 = (ConstraintLayout.LayoutParams)  layout2.getLayoutParams();
        if (path != null){
            videoRatio = getRatio(dimension(path));
        }
        lp1.dimensionRatio = lp2.dimensionRatio = isLandscape ? "0" : String.valueOf(videoRatio/2);
        layout1.setLayoutParams(lp1);
        layout2.setLayoutParams(lp2);
    }

    private static double getRatio(int[] dimension) {
        return (double) dimension[0]/dimension[1];
    }
}
