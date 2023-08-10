package com.github.jaykkumar01.myplayer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.jaykkumar01.myplayerlib.MyPlayer;
import com.github.jaykkumar01.myplayerlib.utils.FileUtil;

import java.io.File;

 public class MainActivity extends AppCompatActivity {
    private Uri vidUri,subUri;
    TextView selectVideo,selectSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectVideo = findViewById(R.id.selectVideo);
        selectSub = findViewById(R.id.selectSub);
    }


    private String path,subPath;
    private String vidTitle,subTitle;
    ActivityResultLauncher<Intent> activityVideo =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        vidUri = data.getData();
                        path = FileUtil.getVideoPath(MainActivity.this,vidUri);
                        if (path == null){
                            path = FileUtil.uriToString(vidUri);
                        }
                        if (path != null && path.contains("/")) {
                            vidTitle = path.substring(path.lastIndexOf("/") + 1);
                            selectVideo.setText(vidTitle);
                        }

                    }
                }
            }
    );

     ActivityResultLauncher<Intent> activitySub =  registerForActivityResult(
             new ActivityResultContracts.StartActivityForResult(),
             new ActivityResultCallback<ActivityResult>() {
                 @Override
                 public void onActivityResult(ActivityResult result) {
                     if(result.getResultCode() == Activity.RESULT_OK){
                         Intent data = result.getData();
                         subUri = data.getData();
                         subPath = FileUtil.getVideoPath(MainActivity.this,subUri);
                         if (subPath == null){
                             subPath = FileUtil.uriToString(subUri);
                         }
                         if (subPath != null && subPath.contains("/")) {
                             subTitle = subPath.substring(subPath.lastIndexOf("/") + 1);
                             selectSub.setText(subTitle);
                         }

                     }
                 }
             }
     );

    public void selectVid(View view){

        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //data.setType("*/*");
        data.setType("video/*");
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data = Intent.createChooser(data,"Video Picker");
        activityVideo.launch(data);
    }
    public void play(View view){
        if (path == null){
            return;
        }
        MyPlayer myPlayer = new MyPlayer(this);
        myPlayer.setPath(path);
        myPlayer.setTitle(vidTitle);
        myPlayer.setSubPath(subPath);
        myPlayer.play();
    }

     public void selectSub(View view) {
         Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
         //data.setType("*/*");
         data.setType("*/*");
         data.addCategory(Intent.CATEGORY_OPENABLE);
         data = Intent.createChooser(data,"Sub Picker");
         activitySub.launch(data);
     }
 }