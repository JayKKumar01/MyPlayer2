package com.github.jaykkumar01.myplayerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.media3.common.util.UnstableApi;

import com.github.jaykkumar01.myplayerlib.models.PlayerProperties;

import java.io.File;

 public class MyPlayer {
    Context context;
    String path;
    String subPath;
    String title = "Untitled";
    public MyPlayer(Context context){
        this.context = context;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setSubPath(String subPath){
        this.subPath = subPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void play(){
        if (path == null || !fileExists(path)){
            Toast.makeText(context, "Not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("UnsafeOptInUsageError")
        Intent intent = new Intent(context,PlayerActivity.class);
        PlayerProperties playerProperties = new PlayerProperties(path);
        playerProperties.setTitle(title);
        playerProperties.setSubPath(subPath);
        intent.putExtra("settings", playerProperties);
        context.startActivity(intent);
    }

    private boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
