package com.example.androidtank;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

// taken and added makeBackground method from https://github.com/efficientisoceles/JoystickView

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private JoystickListener joystickCallback;
    private MediaPlayer engineSound;
    private Context context;


    private void setupDimensions()
    {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
    }

    public JoystickView(Context context)
    {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoystickView(Context context, AttributeSet attributes, int style)
    {
        super(context, attributes, style);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        makeBackgroundTransparent();
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoystickView (Context context, AttributeSet attributes)
    {
        super(context, attributes);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        makeBackgroundTransparent();
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    // added this method to make bg transparent
    private void makeBackgroundTransparent()
    {
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    private void drawJoystick(float newX, float newY)
    {
        if(getHolder().getSurface().isValid())
        {
            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // Clear the BG

            //Draw the base first before shading
            colors.setARGB(255, 255, 255, 255);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255,224, 88, 58);
            myCanvas.drawCircle(newX, newY,hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas); //Write the new drawing to the SurfaceView
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public boolean onTouch(View v, MotionEvent e)
    {
        if(v.equals(this))
        {

            if(e.getAction() == e.ACTION_DOWN)
            {
                setSoundEffect("engineSound");
                float displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                if(displacement < baseRadius)
                {
                    drawJoystick(e.getX(), e.getY());
                    joystickCallback.onJoystickMoved((e.getX() - centerX)/baseRadius, (e.getY() - centerY)/baseRadius, getId());
                }
                else
                {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX-centerX)/baseRadius, (constrainedY-centerY)/baseRadius, getId());
                }

            }
            else if(e.getAction() == e.ACTION_UP)
            {
                stopSoundEffect("engineSound");
                drawJoystick(centerX, centerY);
            }

            joystickCallback.onJoystickMoved(0,0,getId());
        }
        return true;
    }

    public interface JoystickListener
    {
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }

    public void setSoundEffect(String music) {
        if (music.equals("engineSound")) {
            engineSound = MediaPlayer.create(this.context, R.raw.acceleration);
            engineSound.setLooping(true);
            engineSound.start();
        }
    }
    public void stopSoundEffect(String music) {
        if (music.equals("engineSound")) {
            engineSound.stop();
        }
    }
}
