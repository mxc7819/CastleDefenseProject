package com.hamsterhuey.defender;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


/**
 * Class for a single attacking enemy in the castle defender game
 *
 * Utilizes a Sprite class also designed for libGDX
 * General behavior involves walking to the right, and attacking the castle
 */
public class Attacker {
    // Member variables unique to each attacker
    private float mHitDamage;
    private float mWalkSpeed;
    private float mStartX;
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    private boolean mAlive;
    private boolean mGrabbed;
    private String mType;
    private Sprite mCharacterSprite;

    /**
     * Attacker constructor that specifies all member variables in order
     * Implies the attacker still starts "dead", where isAlive returns false
     *
     * @param hitDamage: The damage per frame to deal to the castle should the attacker reach the gates
     * @param walkSpeed: The normal movement speed of the attacker as it moves left-to-right
     * @param x: The x coordinate to begin at
     * @param y: The y coordinate to begin at
     * @param width: The width of the attacker's sprite
     * @param height: The height of the attacker's sprite
     * @param type: The string that describes the attacker (knight or regular)
     */
    public Attacker(float hitDamage, float walkSpeed, float x, float y, float width, float height, String type) {
        mHitDamage = hitDamage;
        mWalkSpeed = walkSpeed;
        mX = x;
        mStartX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mAlive = false;
        mGrabbed = false;
        mType = type;
        mCharacterSprite = new Sprite((int)x, (int)y, width, height);
    }

    /**
     * Method that updates the attacker's internal variables (ie. walks if alive, etc)
     *
     * @param deltaTime: The time delta between the current frame and the previous frame, used for movement
     * @return spriteRect: The rectangle enclosing the attacker's sprite
     */
    public Rectangle update(float deltaTime) {
        if(!mAlive) {
            // Return a null value if the attacker isn't alive (no drawing done or updating needed)
            return null;
        }
        else {
            // Update the sprite before grabbing its rectangle
            // Overloaded call to update that adds mWalkSpeed to the sprite's Rectangle
            mCharacterSprite.update(deltaTime, mWalkSpeed, 0f);

            // Grab the (x, y) coordinate from the updated sprite
            Rectangle newBox = mCharacterSprite.getBoundingBox();
            mX = newBox.getX();
            mY = newBox.getY();

            // Return the sprite's bounding box attribute
            return newBox;
        }
    }

    /**
     * Method to revive a specific attacker for reuse. Set alive to true and start animating
     * Assumes animations are already added, otherwise why would it be drawing?
     */
    public void revive() {
        mAlive = true;
        mX = mStartX;
        mCharacterSprite.setPosition(mX, mY);
        mCharacterSprite.restart();
    }

    /**
     * Kills attacker so that it may be later revived without using more memory
     * Sets alive to false after stopping animation
     */
    public void kill() {
        mCharacterSprite.stop();
        mAlive = false;
    }

    /**
     * Method to retrieve the sprite's current frame of animation
     * May return null if the sprite isn't animating and never started
     * @return animationFrame: The current frame of the sprite's animation
     */
    public TextureRegion getCurrentFrame() {
        return mCharacterSprite.getCurrentFrame();
    }

    /**
     * Method to give the Attacker's sprite object a named animation
     * @param animation: The actual Animation object created for the sprite
     * @param name: The string name to save when referencing the specific animation
     */
    public void addAnimation(Animation animation, String name) {
        mCharacterSprite.addAnimation(animation, name);
    }

    /**
     * Method to play a specific animation on the attacker's sprite object
     * @param animation: The string by which the sprite refers to the desired animation itself
     * @param looping: A boolean to specify whether to loop the animation or not
     */
    public void play(String animation, boolean looping) {
        mCharacterSprite.play(animation, looping);
    }

    /**
     * Method to set the position of an attacker, alive or dead is irrelevant
     *
     * @param x: The x coordinate of the attacker's bounding box
     * @param y: The y coordinate of the attacker's bounding box
     */
    public void setPosition(float x, float y) {
        // Numbers not validated - can be moved off screen!
        mX = x;
        mY = y;
        mCharacterSprite.setPosition(x, y);
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public float getHitDamage() {
        return mHitDamage;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public String getType() {
        return mType;
    }
}
