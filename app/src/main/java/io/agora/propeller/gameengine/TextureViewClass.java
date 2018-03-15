package io.agora.propeller.gameengine;

/**
 * Created by Neo on 14/03/18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.agora.openvcall.R;

//This is added to for rendering the view with an overlay.
public class TextureViewClass extends TextureView implements TextureView.SurfaceTextureListener {

    ArrayList<CharacterSprite> m_listOfActiveSprites = null;


    private final static float SIZE = 32;
    public static final long FRAME_INTERVAL = 20;
    private static final float BOUNCE_FACTOR = 0.25f;
    private Vector2 ballLocation = new Vector2();
    private float holeX,holeY;
    private float scale;
    private int points;
    private int totalPoints;
    private BitmapDrawable ball;
    private Map<String,Bitmap> bitmaps = new HashMap<>();
    private Paint paintBitmap = new Paint();
    private Paint paintHole = new Paint();
    private Paint paintText = new Paint();
    private RectF drawRect = new RectF();
    private Rect ballRect = new Rect();
    private Rect rect = new Rect();
    private ScheduledExecutorService executorService;
    private long t;
    private long frames;
    private RectF playfield;
    private boolean ballInHole;
    private boolean playing;
    private Matrix2 bounceMatrix = new Matrix2();

    // this constructor is needed if the view shall show up in an layout xml
    /*public TextureViewClass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }*/

    public TextureViewClass(Context context) {
        super(context);
        init();
    }

    public TextureViewClass(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    void init()
    {
        scale = getResources().getDisplayMetrics().density;
        ball = (BitmapDrawable) getResources().getDrawable(R.drawable.one);
        paintBitmap.setAntiAlias(true);
        paintHole.setColor(Color.BLACK);
        paintHole.setAntiAlias(true);
        paintHole.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        paintText.setColor(Color.argb(200, 255, 255, 255));
        paintText.setTextSize(scale * 30);
        paintText.setStyle(Paint.Style.FILL);
        ballRect.set(0, 0, ball.getBitmap().getWidth(), ball.getBitmap().getHeight());

        setOpaque(false);
        setSurfaceTextureListener(this);
        setFocusable(false);
        setWillNotDraw(false);

        m_listOfActiveSprites = new ArrayList<>(0);
        //List of activ sprites.

        CharacterSprite sprite = new CharacterSprite(BitmapFactory.decodeResource(getResources(),R.drawable.two));
        addSprite(sprite);
        setFocusable(true);
    }

    public void setBallPosition(Vector2 pos) {
        ballLocation.x = pos.x * getHorizontalBaseDimension();
        ballLocation.y = pos.y * getVerticalBaseDimension();
    }

    public void setHolePosition(float x, float y) {
        holeX = x;
        holeY = y;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setTypeface(Typeface typeface) {
        paintText.setTypeface(typeface);
    }

    protected void doDraw(Canvas canvas) {
        // clear background
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // hole
        canvas.drawCircle(holeX, holeY, calcRadius(), paintHole);
        ballInHole = Math.sqrt((ballLocation.x-holeX)*(ballLocation.x-holeX) + (ballLocation.y-holeY)*(ballLocation.y-holeY)) < calcRadius();
        for(int i=0;i<m_listOfActiveSprites.size();i++)
        {
            m_listOfActiveSprites.get(i).draw(canvas);
        }
        // traps
        /*hitTrap = null;
        for(Trap t : level.getTraps()) {
            Bitmap bitmap = findCachedBitmap(t.getTexture());
            rect.set(0,0,bitmap.getWidth()-1, bitmap.getHeight()-1);
            drawRect.set(t.getX()*getHorizontalBaseDimension(), t.getY()*getVerticalBaseDimension(),
                    (t.getX()+t.getW())*getHorizontalBaseDimension()-1,
                    (t.getY()+t.getH())*getVerticalBaseDimension()-1);
            if(drawRect.contains(ballLocation.x,ballLocation.y)) hitTrap = t;
            canvas.drawBitmap(bitmap, rect, drawRect, paintBitmap);
        }

        // draw ball only when round is active (points>0)
        if(playing) {
            drawRect.set(ballLocation.x - calcRadius(), ballLocation.y - calcRadius(), ballLocation.x + calcRadius(), ballLocation.y + calcRadius());
            canvas.drawBitmap(ball.getBitmap(), ballRect, drawRect, paintBitmap);
        }
        */
        // score
        canvas.drawText(Integer.toString(totalPoints),10*scale, 40*scale, paintText);
        canvas.drawText(Integer.toString(points),canvas.getWidth()-100*scale,40*scale, paintText);
        //canvas.drawText("Level " + level.getNumber(), 10*scale,canvas.getHeight()-10*scale, paintText);
        //canvas.drawText(Integer.toString(countdown),canvas.getWidth()-30*scale,canvas.getHeight()-30*scale, paintText);
        frames++;
    }

    private float calcRadius() {
        return SIZE * scale / 2;
    }

    private Bitmap findCachedBitmap(String texture) {
        Bitmap b = bitmaps.get(texture);
        if(b!=null) return  b;
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(getResources().getIdentifier(texture, "drawable", getContext().getPackageName()));
        if(bd!=null) {
            bitmaps.put(texture,bd.getBitmap());
            return bd.getBitmap();
        }
        else Log.e("GameView", "texture not found: " + texture);
        return null;
    }


    public int getFps() {
        long delta = System.currentTimeMillis() - t;
        if(delta<1000) return 0;
        return (int) (frames/(delta /1000));
    }

    private float getHorizontalBaseDimension() {
        return getWidth()/16;
    }

    private float getVerticalBaseDimension() {
        return getHeight()/9;
    }

    //Addes sprite in the active list.
    public void addSprite(CharacterSprite sprite)
    {
        m_listOfActiveSprites.add(sprite);
    }

    public void updateCycleForGame()
    {
        for(int i=0;i<m_listOfActiveSprites.size();i++)
        {
            m_listOfActiveSprites.get(i).update();
        }
    }

    //Render the frame.
    private void render() {
        updateCycleForGame();
        Canvas canvas=null;
        try {
            canvas = lockCanvas();
            doDraw(canvas);
        }
        finally {
            unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        t= System.currentTimeMillis();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                render();
            }
        }, FRAME_INTERVAL, FRAME_INTERVAL, TimeUnit.MILLISECONDS);
        Log.d(getClass().getSimpleName(), "onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        executorService.shutdown();
        Log.d(getClass().getSimpleName(), "onSurfaceTextureDestroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public RectF getPlayfield() {
        return playfield;
    }

    public boolean isBallInHole() {
        return ballInHole;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Matrix2 getBounceMatrix() {
        return bounceMatrix;
    }

    public boolean checkBounce() {
        boolean res=false;
        bounceMatrix.unity();
        // check playfield bounds and bounce
        if (ballLocation.x < getPlayfield().left) {
            ballLocation.x = getPlayfield().left;
            bounceMatrix.xx=-BOUNCE_FACTOR;
            res=true;
        }
        if (ballLocation.y < getPlayfield().top) {
            ballLocation.y = getPlayfield().top;
            bounceMatrix.yy=-BOUNCE_FACTOR;
            res=true;
        }
        if (ballLocation.x > getPlayfield().right) {
            ballLocation.x = getPlayfield().right;
            bounceMatrix.xx=-BOUNCE_FACTOR;
            res=true;
        }
        if (ballLocation.y > getPlayfield().bottom) {
            ballLocation.y = getPlayfield().bottom;
            bounceMatrix.yy=-BOUNCE_FACTOR;
            res=true;
        }
        return res;
    }
}

