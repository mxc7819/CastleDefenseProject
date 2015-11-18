package com.igm.hamsterhuey.castledefender;

import android.view.SurfaceHolder;

/**
 * Class definition of a common enemy that attacks the player castle
 */
public class Attacker {
    // Member variables
    private int mX;
    private int mY;
    private int mSpeed;
    private int mDamage;
    private int mEndZone;
    private boolean mAttacking;
    private boolean mAlive;
    private Sprite mAnim;

    // Constructor for a standard enemy
    /*
     * @param {speed} The movement speed of this unit
     * @param {x} The starting x coordinate of this unit
     * @param {y} The starting y coordinate of this unit
     * @param {endZone} The x coordinate value where the "castle" sits, or, the target of the attacker
     */
    public Attacker(int speed, int x, int y, int endZone, Sprite sprite) {
        mSpeed = speed;
        mX = x;
        mY = y;
        mEndZone = endZone;
        mAnim = sprite;
        mDamage = 1;
        mAlive = false;
        mAttacking = false;
    }

    // Update method to run each frame, move attacker towards castle and update sprite location
    // Stop moving if the endzone has been reached
    public void update() {
        // Return if not alive - can't update a dead guy
        if(!mAlive)
            return;

        // See if the endzone has been reached. If not, continue walking
        if(mX < mEndZone) {
            mX += mSpeed;
            mAnim.setX(mX);
            mAnim.setY(mY);
            mAttacking = false;
        }
        else {
            mAttacking = true;
        }
    }

    // Draw method for the attacker - calls draw in the attacker's sprite
    public void draw(SurfaceHolder holder) {
        if(!mAlive)
            return; // Can't draw a corpse
        mAnim.draw(holder);
    }

    // "Kill" the attacker - set mAlive to false and remove from the scene
    public void die() {
        mAlive = false;
        mX = -100;
    }

    // "Revive" the attacker - set mAlive to true, reuse is better than wasting
    public void revive() {
        mAlive = true;
    }
}
