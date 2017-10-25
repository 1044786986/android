package com.example.ljh.wechat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ljh on 2017/10/21.
 */

public class MediaPlayManager {
    static MediaPlayer mediaPlayer;
    private static boolean isStart = false;
    private MediaPlayerListener mediaPlayerListener = null;

    public interface MediaPlayerListener {
        void onStart(String url);

        void onCompletion(String url);

        void onError(String url);
    }

        public static void play(String filePath, Context context) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        mediaPlayer.reset();
                        return false;
                    }
                });
            } else {
                mediaPlayer.reset();
            }

            try {
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //mediaPlayer.setOnCompletionListener(onCompletionListener);
                mediaPlayer.setDataSource(fileDescriptor);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isStart = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void pause() {
            if (isStart) {
                mediaPlayer.stop();
                isStart = false;
            }
        }

        public static void release() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                isStart = false;
            }
        }
    }

