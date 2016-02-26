package com.example.vinicius.syncstring;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.Random;

public class DrawString extends View {
    private String str;
    private Paint paint;
    private Random rand;

    private int frameWidth;
    private int frameHeight;
    private int sx;
    private int sy;
    private int r;
    private int g;
    private int b;
    private int changeColor;
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    private boolean running;

    public DrawString(Context context, AttributeSet attrs){
        super(context, attrs);
        str = "Android";
        paint = new Paint();

        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(16);

        sx = 0;
        sy = (int)paint.getTextSize();

        rand = new Random(System.currentTimeMillis());
        changeColor = r = g = b = 0;

        up = false;
        down = true;
        left = false;
        right = true;

        running = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(running)
            update();

        paint.setColor(Color.rgb(r, g, b));
        canvas.drawText(str, sx, sy, paint);

        // Delay
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) { }

        invalidate();  // Force a re-draw
    }

    private void update() {
        float stringWidth = paint.measureText(str);

        if (sx >= frameWidth - stringWidth)
        {
            right = false;
            left = true;
        }
        if (sx <= 0)
        {
            right = true;
            left = false;
        }
        if (sy >= frameHeight)
        {
            up = true;
            down = false;
        }
        if (sy <= paint.getTextSize())
        {
            up = false;
            down = true;
        }
        if (up) sy--;
        if (down) sy++;
        if (left) sx--;
        if (right) sx++;
        changeColor++;

        if(changeColor > 100){
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
            changeColor = 0;
        }
    }

    // Called back when the view is first created or its size changes.
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        frameWidth = w;
        frameHeight = h;
    }

    public byte[] getState(){
        ByteBuffer state = ByteBuffer.allocate(28);

        int direction = 0;
        direction = (up) ? direction + 1: direction;
        direction = (right) ? direction + 10: direction;

        state.putInt(sx);
        state.putInt(sy);
        state.putInt(r);
        state.putInt(g);
        state.putInt(b);
        state.putInt(changeColor);
        state.putInt(direction);

        return state.array();
    }

    public boolean updateState(byte[] state){
        if(state.length != 28)
            return false;

        ByteBuffer st = ByteBuffer.wrap(state);

        sx = st.getInt();
        sy = st.getInt();
        r = st.getInt();
        g = st.getInt();
        b = st.getInt();
        changeColor = st.getInt();
        int direction = st.getInt();

        if(direction == 0){
            up = right = false;
            down = left = true;
        }
        else if(direction == 1){
            up = left = true;
            down = right = false;
        }
        else if(direction == 10){
            up = left = false;
            down = right = true;
        }
        else{
            up = right = true;
            down = left = false;
        }

        return true;
    }

    public void startRun(){
        running = true;
    }

    public void stopRun(){
        running = false;
    }
}
