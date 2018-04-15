package com.example.android.musicapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    public static int oneTimeOnly = 0;
    private Button button_pause;
    private Button button_play;
    private MediaPlayer mediaPlayer;
    private double getCurrentTimeSong = 0;
    private double totalDurationSong = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView timePlaying;
    private TextView totalTimeSong;
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            getCurrentTimeSong = mediaPlayer.getCurrentPosition();
            timePlaying.setText(String.format(getString(R.string.time_playing_update),
                    TimeUnit.MILLISECONDS.toMinutes((long) getCurrentTimeSong),
                    TimeUnit.MILLISECONDS.toSeconds((long) getCurrentTimeSong) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) getCurrentTimeSong)))
            );
            seekbar.setProgress((int) getCurrentTimeSong);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the seek bar in the layout
        seekbar = findViewById(R.id.seekBar);
        // Set it non clickable
        seekbar.setClickable(false);
        // Initialize and find the forward button in the layout
        Button buttonForward = findViewById(R.id.button_forward);
        // Find the pause button in the layout
        button_pause = findViewById(R.id.button_pause);
        // Set the pause button as not enabled
        button_pause.setEnabled(false);
        // Find the play button
        button_play = findViewById(R.id.button_play);
        // Initialize and find the rewind button
        Button button_rewind = findViewById(R.id.button_rewind);
        // Find the time playing in the layout
        timePlaying = findViewById(R.id.time_playing);
        // Find the total time of the song in the layout
        totalTimeSong = findViewById(R.id.total_time);
        // Find the song's name in the layout
        TextView songName = findViewById(R.id.song_name);
        // Set the song's name
        songName.setText(R.string.birds_song_title);
        // Create music player
        mediaPlayer = MediaPlayer.create(this, R.raw.kooyoora);

        // Set a click listener on the button Play
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message saying it's playing the sound
                Toast.makeText(getApplicationContext(), R.string.message_playing, Toast.LENGTH_SHORT).show();
                // Start media player
                mediaPlayer.start();
                // Get the total time of the audio file and store it in the {@ totalDurationSong} variable
                totalDurationSong = mediaPlayer.getDuration();
                // Get the current position of the time and store it in the {@ getCurrentTimeSong} variable
                getCurrentTimeSong = mediaPlayer.getCurrentPosition();

                // If the current audio starts
                if (oneTimeOnly == 0) {
                    // set the total size of the seek bar equal to the total duration of the song
                    seekbar.setMax((int) totalDurationSong);
                    // Update the {@ oneTimeOnly} to 1
                    oneTimeOnly = 1;
                }

                // Update the text view of the {@ totalTime} to minutes and seconds of the {@ totalDurationSong} variable
                totalTimeSong.setText(String.format(getString(R.string.song_name_playing),
                        TimeUnit.MILLISECONDS.toMinutes((long) totalDurationSong),
                        TimeUnit.MILLISECONDS.toSeconds((long) totalDurationSong) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        totalDurationSong))
                ));

                // Update the text view of the {@ timePlaying} to minutes and seconds of the {@ getCurrentTimeSong} variable
                timePlaying.setText(String.format(getString(R.string.time_playing_song),
                        TimeUnit.MILLISECONDS.toMinutes((long) getCurrentTimeSong),
                        TimeUnit.MILLISECONDS.toSeconds((long) getCurrentTimeSong) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        getCurrentTimeSong)))
                );

                // Update the seek bar progress to the current time of the playing song
                seekbar.setProgress((int) getCurrentTimeSong);
                // Delay the updating of the song time to 100 milliseconds
                myHandler.postDelayed(UpdateSongTime, 100);
                // Enable the button Pause
                button_pause.setEnabled(true);
                // Disable the button Play
                button_play.setEnabled(false);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(getApplicationContext(), "I'm done!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set a click listener on the button Pause
        button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a toast message
                Toast.makeText(getApplicationContext(), R.string.message_pausing, Toast.LENGTH_SHORT).show();
                // Pause the media player
                mediaPlayer.pause();
                // Disable the button Pause
                button_pause.setEnabled(false);
                // Enable the button Play
                button_play.setEnabled(true);
            }
        });

        // Set a click listener on the button Forward
        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the time the current song has been playing since and store it into an integer
                int temp = (int) getCurrentTimeSong;
                // If the current time of the song plus the 5 sec forward time is less equal to the total duration of the song
                if ((temp + forwardTime) <= totalDurationSong) {
                    // Update the current time of the song to jump 5 sec forward
                    getCurrentTimeSong = getCurrentTimeSong + forwardTime;
                    // Update the current time of the song
                    mediaPlayer.seekTo((int) getCurrentTimeSong);
                    // Display a toast message announcing the 5 sec jump forward
                    Toast.makeText(getApplicationContext(), R.string.message_jumped, Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise display a toast message announcing the non jump
                    Toast.makeText(getApplicationContext(), R.string.message_no_jump, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set a click listener on the button Rewind
        button_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the time of the current song and store it into an integer
                int temp = (int) getCurrentTimeSong;

                // If the current time of the song minus the 5 sec rewind time is more than 0
                if ((temp - backwardTime) > 0) {
                    // Update the current time of the song to jump 5 sec back
                    getCurrentTimeSong = getCurrentTimeSong - backwardTime;
                    // Update the current time of the song
                    mediaPlayer.seekTo((int) getCurrentTimeSong);
                    // Display a toast message announcing the 5 sec jump backwards
                    Toast.makeText(getApplicationContext(), R.string.message_jump_backwards, Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise display a toast message announcing the non jump
                    Toast.makeText(getApplicationContext(), R.string.message_no_jump_back, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
