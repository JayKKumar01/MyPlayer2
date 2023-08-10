package com.github.jaykkumar01.myplayerlib;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.ui.PlayerView;

import com.github.jaykkumar01.myplayerlib.playerutils.MyHandler;

public class OnControlsTouch implements View.OnTouchListener {
    private PlayerView playerView;

    public OnControlsTouch(ConstraintLayout rootLayout,PlayerView playerView) {
        this.playerView = playerView;
        setTouchAll(rootLayout);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            MyHandler.hideControls(playerView);
        }
        return false;
    }

    void setTouchAll(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for(int i=0; i<count; i++){
            View view = viewGroup.getChildAt(i);
            if(view instanceof ViewGroup){
                setTouchAll((ViewGroup) view);
            }
            else{
                view.setOnTouchListener(this);
            }
        }
    }
}
