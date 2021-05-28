package com.example.androidtank.utilities;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundEffect {

   private MediaPlayer soundEffect1;
   private  MediaPlayer soundEffect2;


   public SoundEffect (MediaPlayer soundEffect1, MediaPlayer soundEffect2) {
       this.soundEffect1 = soundEffect1;
       this.soundEffect2 = soundEffect2;
   }

    public void setEffect1(Context context,
                           int soundId,
                           float soundVolume,
                           Boolean isThereLoop,
                           int startPoint) {

        if (soundEffect1 != null) {
            soundEffect1.stop();
        }

        soundEffect1 = MediaPlayer.create(context, soundId);
        soundEffect1.setVolume(soundVolume, soundVolume);
        soundEffect1.start();
        if (isThereLoop) {
            repeatEffect1(startPoint);
        }
    }

    public void setEffect2(Context context,
                                    int soundId,
                                    float soundVolume,
                                    Boolean isThereLoop) {


        soundEffect2 = MediaPlayer.create(context, soundId);
        soundEffect2.setVolume(soundVolume, soundVolume);
        soundEffect2.setLooping(isThereLoop);
        soundEffect2.start();


    }

    public void stopEffect1() {
        soundEffect1.stop();
    }

    public void stopEffect2() {
        soundEffect2.stop();
    }

    public void repeatEffect1(int startPoint) {

        soundEffect1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                soundEffect1.seekTo(startPoint);
                soundEffect1.start();
                repeatEffect1(startPoint);
            }
        });
    }

}
