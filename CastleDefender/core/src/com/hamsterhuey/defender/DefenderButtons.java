package com.hamsterhuey.defender;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * This class defines a quick-and-dirty implementation for simple buttons to be drawn in the defender game
 * Only a few buttons currently are needed: New Game, Resume, Pause, Play, Repair, and Continue
 * These six different buttons are hard-coded to fit this specific game as needed, constructed with a single argument ("type")
 */
public class DefenderButtons {

    // Public constants for type matching
    public static final int TYPE_NEW_GAME = 0;
    public static final int TYPE_RESUME_SAVED_GAME = 1;
    public static final int TYPE_PAUSE_GAME = 2;
    public static final int TYPE_RESUME_PAUSED_GAME = 3;
    public static final int TYPE_REPAIR_CASTLE = 4;
    public static final int TYPE_CONTINUE_NEXT_ROUND = 5;
    public static final int TYPE_SAVE_GAME = 6;

    // Member variables for the class
    private Rectangle mRect;
    private Texture mTexture;
    private boolean mVisible;
    private String mText;
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    private int mType;

    /**
     * Constructor for a defender button, with type specified
     * @param type: The integer that denotes which button is being created
     */
    public DefenderButtons(int type) {
        // Creating a button doesn't draw it immediately - visible = false
        mVisible = false;

        switch(type) {
            case TYPE_NEW_GAME:
                mType = type;
                mText = "New Game";
                mRect = new Rectangle(125f, 280f, 115f, 50f);
                break;
            case TYPE_RESUME_SAVED_GAME:
                mType = type;
                mText = "Resume Game";
                mRect = new Rectangle(125f, 205f, 115f, 50f);
                break;
            case TYPE_PAUSE_GAME:
                mType = type;
                mText = "Pause";
                mRect = new Rectangle(650f, 375f, 100f, 35f);
                break;
            case TYPE_RESUME_PAUSED_GAME:
                mType = type;
                mText = "Resume";
                mRect = new Rectangle(350f, 345f, 100f, 35f);
                break;
            case TYPE_REPAIR_CASTLE:
                mType = type;
                mText = "Repair: -1000pts";
                mRect = new Rectangle(420f, 200f, 150f, 50f);
                break;
            case TYPE_CONTINUE_NEXT_ROUND:
                mType = type;
                mText = "Start Next Round";
                mRect = new Rectangle(420f, 50f, 150f, 50f);
                break;
            case TYPE_SAVE_GAME:
                mType = type;
                mText = "Save Current Round";
                mRect = new Rectangle(420f, 125f, 150f, 50f);
                break;
            default:
                mType = -1; // Shouldn't ever arrive here
                mText = "bad";
                mRect = new Rectangle(0f, 0f, 10f, 10f);
                break;
        }
        // Grab value from the rectangle
        mX = mRect.getX();
        mY = mRect.getY();
        mWidth = mRect.getWidth();
        mHeight = mRect.getHeight();
    }

    public void draw(BitmapFont font, SpriteBatch batch) {
        // Only draw if visible
        if(mVisible) {
            // Draw the texture (all are set), then draw the text over top
            batch.draw(mTexture, mX, mY, mWidth, mHeight);
            font.draw(batch, mText, mX + 10, mY + 25);
        }
    }

    public boolean isVisible() {
        return mVisible;
    }

    public Rectangle getRect() {
        return mRect;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public String getText() {
        return mText;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setTexture(Texture texture) {
        mTexture = texture;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        mHeight = height;
    }
}
