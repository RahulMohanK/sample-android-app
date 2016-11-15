package com.vdocipher.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vdocipher.aegis.player.VdoPlayer;
import com.vdocipher.aegis.player.VdoPlayerFragment;

public class OfflinePlayerActivity extends AppCompatActivity implements VdoPlayer.OnInitializationListener {

    private final String TAG = "OfflinePlayerActivity";

    private VdoPlayer player;
    private VdoPlayerFragment playerFragment;
    private Button playButton, pauseButton;
    private TextView seekStart, seekEnd;
    private SeekBar seekBar;
    private ProgressBar bufferingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_player);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        seekBar = (SeekBar)findViewById(R.id.offline_activity_seek);
        seekBar.setEnabled(false);
        seekStart = (TextView)findViewById(R.id.offline_activity_seek_indicator);
        seekEnd = (TextView)findViewById(R.id.offline_activity_seek_end);
        playerFragment = (VdoPlayerFragment)getFragmentManager().findFragmentById(R.id.offline_vdo_player_fragment);
        playButton = (Button)findViewById(R.id.offline_play_button);
        playButton.setEnabled(false);
        pauseButton = (Button)findViewById(R.id.offline_pause_button);
        pauseButton.setEnabled(false);
        bufferingIcon = (ProgressBar) findViewById(R.id.loading_icon);

        startPlayer();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop called");
        super.onStop();
    }

    private void startPlayer() {
        final String videoId = "********";
        String localFolder = getExternalFilesDir(null).getPath();
        // create vdoInitParams
        VdoPlayer.VdoInitParams vdoParams = new VdoPlayer.VdoInitParams(null, true, localFolder, videoId);
        // initialize vdoPlayerFragment with otp(null) and a VdoPlayer.OnInitializationListener
        playerFragment.initialize(vdoParams, OfflinePlayerActivity.this);
    }

    View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            player.play();
        }
    };

    View.OnClickListener pauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            player.pause();
        }
    };

    @Override
    public void onInitializationSuccess(VdoPlayer player, boolean wasRestored) {
        Log.v(TAG, "onInitializationSuccess");
        this.player = player;
        player.setOnPlaybackEventListener(playbackListener);
        Log.v(TAG, "player duration = " + player.getDuration());
        seekEnd.setText(Utils.digitalClockTime(player.getDuration()));
        seekBar.setMax(player.getDuration());
        seekBar.setEnabled(true);
        seekBar.setOnSeekBarChangeListener(seekbarChangeListener);
        playButton.setOnClickListener(playListener);
        pauseButton.setOnClickListener(pauseListener);
        playButton.setEnabled(true);
        pauseButton.setEnabled(true);
    }

    @Override
    public void onInitializationFailure(VdoPlayer.InitializationResult result) {
        Log.v(TAG, "onInitializationFailure: " + result.name());
    }

    private VdoPlayer.OnPlaybackEventListener playbackListener = new VdoPlayer.OnPlaybackEventListener() {
        @Override
        public void onPlaying() {
            Log.v(TAG, "onPlaying");
        }

        @Override
        public void onPaused() {
            Log.v(TAG, "onPaused");
        }

        @Override
        public void onStopped() {
            Log.v(TAG, "onStopped");
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            Log.v(TAG, isBuffering ? "buffering started" : "buffering stopped");
            showLoadingIcon(isBuffering);
        }

        @Override
        public void onBufferUpdate(int bufferTime) {
        }

        @Override
        public void onSeekTo(int millis) {
            Log.v(TAG, "onSeekTo " + String.valueOf(millis));
        }

        @Override
        public void onProgress(int millis) {
            seekBar.setProgress(millis);
            seekStart.setText(Utils.digitalClockTime(millis));
        }

        @Override
        public void onError(VdoPlayer.PlaybackErrorReason playbackErrorReason) {
            Log.e(TAG, playbackErrorReason.name());
        }
    };

    private void showLoadingIcon(final boolean showIcon) {
        if (showIcon) {
            bufferingIcon.setVisibility(View.VISIBLE);
            bufferingIcon.bringToFront();
        } else {
            bufferingIcon.setVisibility(View.INVISIBLE);
            bufferingIcon.requestLayout();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekbarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
            // nothing much to do here
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // nothing much to do here
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.seekTo(seekBar.getProgress());
        }
    };
}
