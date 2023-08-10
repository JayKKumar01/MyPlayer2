package com.github.jaykkumar01.myplayerlib.playerutils;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.TrackSelectionDialogBuilder;

@UnstableApi public class TrackDialog {

    private Dialog audDialog,vidDialog,subDialog;
    private final Context context;
    private final Player player;

    public TrackDialog(Context context,Player player) {
        this.context = context;
        this.player = player;
    }
    public void changeAudio(){
        if(audDialog == null){
            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "AUDIO TRACKS", player,
                    C.TRACK_TYPE_AUDIO);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(true);

            audDialog = trackSelectionDialogBuilder.build();
            audDialog.show();
        }else {
            audDialog.show();
        }
    }

    public void changeVideo(){
        if(vidDialog == null){
            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "VIDEO TRACKS", player,
                    C.TRACK_TYPE_VIDEO);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(true);
            vidDialog = trackSelectionDialogBuilder.build();
            vidDialog.show();
        }else {
            vidDialog.show();
        }
    }
    public void changeSubtitle(){
        if(subDialog == null){
            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "SUBTITLE TRACKS", player,
                    C.TRACK_TYPE_TEXT);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(false);
            subDialog = trackSelectionDialogBuilder.build();
            subDialog.show();
        }else {
            subDialog.show();
        }
    }
}
