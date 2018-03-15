package io.agora.propeller.gameengine;

import android.view.View;

/**
 * Created by Neo on 14/03/18.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


public class CustomWorldView extends View {
    private MainThread thread;

    ArrayList<CharacterSprite> m_listOfActiveSprites = null;

    void init()
    {
        m_listOfActiveSprites = new ArrayList<>(0);

        //List of activ sprites.
        //thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    public CustomWorldView(Context context) {
        super(context);
    }



    public CustomWorldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWorldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update()
    {


    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Some simple draw on the view...
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FFA800"));


        Path path = new Path();

        path.moveTo(0, 0);
        path.lineTo(getWidth() / 2, 0);
        path.lineTo(getWidth(), getHeight()/2);
        path.lineTo(getWidth() / 2, getHeight());
        path.lineTo( 0, getHeight());
        path.lineTo( 0, 0);

        canvas.drawPath(path, paint);


    }

}
