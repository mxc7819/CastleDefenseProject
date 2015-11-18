package com.igm.hamsterhuey.castledefender;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class describes the actual game that the player plays/
 * The game extends SurfaceView so that it may be used as the parameter in setContentView
 */
public class GameView extends SurfaceView implements Runnable {
    // Member variables of the GameView
    final Context mContext;
    private SurfaceHolder mHolder;
    private Thread mGameThread = null;
    volatile boolean mPlaying;
    private boolean mGameIsOver;
    private boolean mRoundIsOver;
    private int mScreenX;
    private int mScreenY;
    private int mCastleHealth;
    private ArrayList<Attacker> mAttackers;
    private Bitmap mAttackerSprite;

    private Paint paint;
    private Canvas canvas;

    // GameView Constructor
    /*
     * @param {context} The context from which the constructor was invoked
     * @param {x} The resolution width of the device screen
     * @param {y} The resolution height of the device screen
     * @returns {GameView} The actual view to be set as the contentView
     */
    public GameView(Context context) {
        super(context);
        // Set member variables to passed arguments
        mContext = context;
        mScreenX = getWidth();
        mScreenY = getHeight();
        mPlaying = false;

        // Find reference to the SurfaceView's holder
        mHolder = getHolder();
        mAttackers = new ArrayList();
        paint = new Paint();

        // Initialize the game
        initNewGame(context);
    }

    // Run method - required by implementing Runnable
    @Override
    public void run() {
        // The overall steps to running the game take place here
        // They include Updating logic, Drawing the view, and locking the framerate
        // Only run while the game is being played
        while(mPlaying) {
            update();
            draw();
            lockFPS();
        }
    }

    private void update() {
        for (Attacker a: mAttackers) {
            a.update();
        }
    }

    private void draw() {
        if (mHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = mHolder.lockCanvas();
            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 181, 230, 29));

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.argb(255, 0, 0, 0));
            paint.setTextSize(25);

            canvas.drawText("Castle Health:" + mCastleHealth,10, 20, paint);
            for (Attacker a: mAttackers) {
                a.draw(mHolder);

            // Unlock and draw the scene
            mHolder.unlockCanvasAndPost(canvas);
        }


        }
    }

    // An entire new play session starts at round 1 with zero points
    // Starting a new game calls resume to begin the game thread, so init mPlaying to false
    private void initNewGame(Context context) {
        mPlaying = false;
        mCastleHealth = 1000;

        Resources res = getContext().getResources();
        int id = R.drawable.attackersheet;
        Bitmap attackerImage = BitmapFactory.decodeResource(res, id);
        // Make a new sprite for the attackers
        Sprite attackSprite = new Sprite(attackerImage, 2, 8, 64, 64, this);
        // Make a new attacker using this sprite
        Attacker attacker = new Attacker(2, 64, mScreenY - 20, mScreenX - 60, attackSprite);
        // Add the attacker to the member list
        mAttackers.add(attacker);

        Log.d("myLog", mAttackers.size() + "");
        resume();
    }

    // The game execution will be locked to 60 fps via Thread.sleep(17)
    private void lockFPS() {
        try {
            mGameThread.sleep(17);
        }
        catch(InterruptedException e) {
            // We don't care exception, go away
        }
    }

    // Method to pause the game thread
    public void pause() {
        mPlaying = false;
        // Stop the game thread using thread.join, catching the interrupted exception
        try {
            mGameThread.join();
        }
        catch(InterruptedException e) {
            // Do nothing with the exception
        }
    }

    // Method to resume the game by creating a new thread
    public void resume() {
        if (!mPlaying) {
            mPlaying = true;
            mGameThread = new Thread(this);
            mGameThread.start();
        }
    }
}
