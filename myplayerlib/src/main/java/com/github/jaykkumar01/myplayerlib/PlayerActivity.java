package com.github.jaykkumar01.myplayerlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jaykkumar01.myplayerlib.models.PlayerProperties;
import com.github.jaykkumar01.myplayerlib.playerutils.TouchGesture;
import com.github.jaykkumar01.myplayerlib.playerutils.TrackDialog;
import com.github.jaykkumar01.myplayerlib.utils.AspectRatio;
import com.github.jaykkumar01.myplayerlib.utils.AutoRotate;
import com.github.jaykkumar01.myplayerlib.playerutils.VideoUI;
import com.google.common.collect.ImmutableList;


@UnstableApi public class PlayerActivity extends AppCompatActivity{

    PlayerView playerView;
    ConstraintLayout rootLayout;
    TextView titleTV;
    ExoPlayer player;
    DefaultTrackSelector trackSelector;
    TrackDialog trackDialog;
    PlayerProperties playerProperties;
    private long startPosition = 0;
    private boolean isPaused,caption;
    private AlertDialog.Builder playbackDialog;
    private int playbackSpeed = 2;
    private boolean pip;
     private boolean expend = true;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_player);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            playerProperties = getIntent().getSerializableExtra("settings",PlayerProperties.class);
        }else {
            playerProperties = (PlayerProperties) getIntent().getSerializableExtra("settings");
        }

        AutoRotate.set(this);
        AspectRatio.set(this);
        initViews();


    }

//    public void test(View view){
//
//        TrackSelectionDialog trackSelectionDialog =
//                TrackSelectionDialog.createForPlayer(
//                        player,
//                        /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
//        trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        playerView = findViewById(R.id.player_view);
        playerView.requestFocus();
        rootLayout = findViewById(R.id.root_exo_layout);
        rootLayout.setOnTouchListener(new OnControlsTouch(rootLayout,playerView));
        titleTV = findViewById(R.id.exo_title);
        titleTV.setText(playerProperties.getTitle());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (pip){
            pip = false;
            return;
        }

        initializePlayer();
        if (isPaused){
            player.pause();
        }
        if (playerView != null) {
            playerView.onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            if (pip){
                pip = false;
                return;
            }

            initializePlayer();
            if (isPaused){
                player.pause();
            }
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void playAndPause(View v){
        ImageView imageView = (ImageView) v;
        if(player.isPlaying()){
            player.pause();
            isPaused = true;
            imageView.setImageResource(R.drawable.exo_play);
        }
        else{
            player.play();
            isPaused = false;
            imageView.setImageResource(R.drawable.exo_pause);
        }
    }

    public void lock(View view){
        findViewById(R.id.ctrlLayout).setVisibility(View.GONE);
        findViewById(R.id.big_lock).setVisibility(View.VISIBLE);
    }
    public void unlock(View view) {
        view.setVisibility(View.GONE);
        findViewById(R.id.ctrlLayout).setVisibility(View.VISIBLE);
    }
    public void muteUnmute(View view){
        if(player == null){
            return;
        }
        ImageView imageView = (ImageView) view;
        if(player.getVolume() == 0f){
            imageView.setImageResource(R.drawable.volume_on);
            player.setVolume(1f);
        }
        else{
            imageView.setImageResource(R.drawable.volume_off);
            player.setVolume(0f);
        }
    }
    public void cc(View view){
        ImageView imageView = (ImageView) view;
        caption = !caption;
        if(caption){
            imageView.setImageResource(R.drawable.cc_off);
            playerView.getSubtitleView().setVisibility(View.GONE);

        }
        else{
            imageView.setImageResource(R.drawable.cc_on);
            playerView.getSubtitleView().setVisibility(View.VISIBLE);
        }
    }

    public void fullScreen(View view){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void getBack(View view){
        finish();
    }
    public void changeSpeed(View view){
        if (player == null){
            return;
        }
        playbackDialog = new AlertDialog.Builder(this);
        playbackDialog.setTitle("Change Playback Speed").setPositiveButton("OK",null);
        String[] items = {"0.25x","0.5x","1x (normal)","1.25x","1.5x","2x"};
        playbackDialog.setSingleChoiceItems(items, playbackSpeed, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playbackSpeed = i;
                switch (i){
                    case 0:
                        player.setPlaybackParameters(new PlaybackParameters(.25f));
                        break;
                    case 1:
                        player.setPlaybackParameters(new PlaybackParameters(.5f));
                        break;
                    case 2:
                        player.setPlaybackParameters(new PlaybackParameters(1f));
                        break;
                    case 3:
                        player.setPlaybackParameters(new PlaybackParameters(1.25f));
                        break;
                    case 4:
                        player.setPlaybackParameters(new PlaybackParameters(1.5f));
                        break;
                    case 5:
                        player.setPlaybackParameters(new PlaybackParameters(2f));
                        break;
                    default:
                        break;
                }
            }
        });
        playbackDialog.show();

    }

     public void changeAudio(View view){
         trackDialog.changeAudio();
     }
     public void changeVideo(View view){
         trackDialog.changeVideo();
     }
     public void changeSub(View view){
         trackDialog.changeSubtitle();
     }
     public void boost(View view) {
     }

     public void enterPIP(View view){
         Display d = getWindowManager().getDefaultDisplay();
         Point p = new Point();
         d.getSize(p);
         Rational ratio = new Rational(16,9);
         //ratio = new Rational(dimension(vidUri)[0],dimension(vidUri)[1]);
         PictureInPictureParams.Builder pipBuilder = null;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             playerView.hideController();
             pipBuilder = new PictureInPictureParams.Builder();
             pipBuilder.setAspectRatio(ratio).build();
             enterPictureInPictureMode(pipBuilder.build());
             pip = true;
         }

     }

     public void setExpend(View view) {
         ImageView track = findViewById(R.id.exo_audioTrack);
         ImageView extend = findViewById(R.id.exo_extendTrack);
         ConstraintLayout layout = findViewById(R.id.trackLayout);
         expend = !expend;
         if(expend){
             ((View)findViewById(R.id.exo_trackBreak)).setVisibility(View.GONE);
             layout.animate().translationX(0).setListener(new AnimatorListenerAdapter() {
                 @Override
                 public void onAnimationEnd(Animator animation) {
                     extend.setImageResource(R.drawable.expend_not);
                 }
             });

         }
         else {
             ((View)findViewById(R.id.exo_trackBreak)).setVisibility(View.VISIBLE);
             float move = track.getX()+ track.getWidth();
             layout.animate().translationX(-move).setListener(new AnimatorListenerAdapter() {
                 @Override
                 public void onAnimationEnd(Animator animation) {
                     extend.setImageResource(R.drawable.expend);
                 }
             });
         }
     }

    protected void initializePlayer() {
        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector == null? new DefaultTrackSelector(this) : trackSelector)
                .build();

        playerView.setPlayer(player);
        MediaItem mediaItem = getMediaItem();
//        player.addListener(new PlayerEventListener());
        player.setMediaItem(mediaItem);
        player.seekTo(startPosition);
        player.prepare();
        player.play();
        VideoUI.setRatio(this,playerProperties.getPath());
        trackDialog = new TrackDialog(this,player);
        playerView.setControllerAutoShow(false);
        playerView.setOnTouchListener(new TouchGesture(this,playerView,player));
    }

    private MediaItem getMediaItem() {
        if(playerProperties.getSubPath() == null){
            return new MediaItem.Builder()
                    .setUri(playerProperties.getPath())
                    .build();
        }
        MediaItem.SubtitleConfiguration sub = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(playerProperties.getSubPath()))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                .setLanguage("en")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build();
        return new MediaItem.Builder()
                .setUri(playerProperties.getPath())
                .setSubtitleConfigurations(ImmutableList.of(sub))
                .build();
    }

    private void releasePlayer() {
        if (player == null){
            return;
        }
        startPosition = Math.max(0,player.getCurrentPosition());
        player.release();
        player = null;
        playerView.setPlayer(null);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView fullscreen = (ImageView) findViewById(R.id.exo_screen);
        VideoUI.setRatio(this,null);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            fullscreen.setImageResource(R.drawable.fullscreen_exit);

//            Menu.changeMenu(this,true);
//            changeDoubleTapBox(true);
        }
        else {
            fullscreen.setImageResource(R.drawable.fullscreen);

//            Menu.changeMenu(this,false);
//            changeDoubleTapBox(false);
        }
    }

}