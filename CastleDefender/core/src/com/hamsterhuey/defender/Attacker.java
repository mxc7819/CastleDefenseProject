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
    private float mX;
    private float mY;
    private boolean mAlive;
    private boolean mGrabbed;
    private Sprite mCharacterSprite;

    // Multiple constructors exist, with a default no-argument constructor available
    public Attacker() {
        new Attacker(1f, 100f, 0f, 0f);
    }

    /**
     * Attacker constructor that specifies all member variables in order
     * Implies the attacker still starts "dead", where isAlive returns false
     *
     * @param hitDamage: The damage per frame to deal to the castle should the attacker reach the gates
     * @param walkSpeed: The normal movement speed of the attacker as it moves left-to-right
     * @param x: The x coordinate to begin at
     * @param y: The y coordinate to begin at
     */
    public Attacker(float hitDamage, float walkSpeed, float x, float y) {
        mHitDamage = hitDamage;
        mWalkSpeed = walkSpeed;
        mX = x;
        mY = y;
        mAlive = false;
        mGrabbed = false;
        mCharacterSprite = new Sprite((int)x, (int)y, 64f, 64f);
    }

    /**
     * Method that updates the attacker's internal variables (ie. walks if alive, etc)
     *
     * @param deltaTime: The time delta between the current frame and the previous frame, used for movement
     * @return spriteRect: The rectangle enclosing the attacker's sprite
     */
    public Rectangle update(float deltaTime) {
        if(!mAlive) {
            // Return a null value if the attacker isn't alive (no drawing done or updating needed
            return null;
        }
        else {
            // Update the sprite before grabbing its rectangle
            // Overloaded call to update that adds mWalkSpeed to the sprite's Rectangle
            mCharacterSprite.update(deltaTime, mWalkSpeed, 0f);

            // Return the sprite's bounding box attribute
            return mCharacterSprite.getBoundingBox();
        }
    }

    /**
     * Method to revive a specific attacker for reuse. Set alive to true and start animating
     * Assumes animations are already added, otherwise why would it be drawing?
     */
    public void revive() {
        mAlive = true;
        mCharacterSprite.play();
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
     * Method to set the position of an attacker, alive or dead is irrelevant
     *
     * @param x: The x coordinate of the attacker's bounding box
     * @param y: The y coordinate of the attacker's bounding box
     */
    public void setPosition(float x, float y) {
        // Numbers not validated - can be moved off screen!
        mCharacterSprite.setPosition(x, y);
    }

    public float getX() {
        return (float)mCharacterSprite.getX();
    }

    public float getY() {
        return (float)mCharacterSprite.getY();
    }

    public float getHitDamage() {
        return mHitDamage;
    }

    public boolean isAlive() {
        return mAlive;
    }
}
