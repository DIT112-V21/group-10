package com.example.androidtank.utilities;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundEffect {
   public static MediaPlayer soundEffect;


    public static void startEffect(Context context,
                                   int soundId,
                                   float soundVolume,
                                   Boolean isThereLoop) {
        soundEffect = MediaPlayer.create(context, soundId);
        soundEffect.setVolume(soundVolume, soundVolume);
        soundEffect.setLooping(isThereLoop);
        soundEffect.start();
    }

    public static void stopEffect() {
        soundEffect.stop();
    }

}
