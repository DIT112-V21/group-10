package com.example.androidtank.utilities;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundEffect {

   public static MediaPlayer soundEffect;

    public static void startEffect(Context context,
                                   int soundId,
                                   float soundVolume,
                                   Boolean isThereLoop,
                                   int startPoint) {

        if (soundEffect != null) {
            soundEffect.stop();
        }

        soundEffect = MediaPlayer.create(context, soundId);
        soundEffect.setVolume(soundVolume, soundVolume);
        soundEffect.start();
        if (isThereLoop) {
            repeatEffect(startPoint);
        }
    }

    public static void stopEffect() {
        soundEffect.stop();
    }

    public static void repeatEffect(int startPoint) {

        soundEffect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                soundEffect.seekTo(startPoint);
                soundEffect.start();
                repeatEffect(startPoint);
            }
        });
    }
}
