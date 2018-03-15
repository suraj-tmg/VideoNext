package io.agora.propeller.gameengine;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Neo on 13/03/18.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;
    private CharacterSprite characterSprite;

    ArrayList<CharacterSprite> m_listOfActiveSprites = null;
    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    void init()
    {
        m_listOfActiveSprites = new ArrayList<>(0);
        getHolder().addCallback(this);
        //List of activ sprites.
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    public void addSprite(CharacterSprite sprite)
    {
        m_listOfActiveSprites.add(sprite);
    }

    public void processSynchronizedJob(final Runnable runnable) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch(InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        for(int i=0;i<m_listOfActiveSprites.size();i++)
        {
           m_listOfActiveSprites.get(i).update();
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        if(canvas!=null) {
            for(int i=0;i<m_listOfActiveSprites.size();i++)
            {
                m_listOfActiveSprites.get(i).draw(canvas);
            }
        }
    }





}
