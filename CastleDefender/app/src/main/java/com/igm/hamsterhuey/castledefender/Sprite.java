package com.igm.hamsterhuey.castledefender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Class to handle instanced drawable objects on-screen
 * used to animate characters and scenery
 */
public class Sprite {
    // Member variables of each sprite
    private int mX;
    private int mY;
    private int mFrameRow;
    private int mFrameCol;
    private int mWidth;
    private int mHeight;
    private int mRows;
    private int mCols;
    private int mNoDrawLeft;
    private int mNoDrawTop;
    private int mScreenWidth;
    private int mScreenHeight;
    private Bitmap mBitmap;

    // Constructor for Sprite
    /*
     * @param {Bitmap map} The bitmap resource to use as a sprite
     * @param {int numRows} The number of rows in the spritesheet
     * @param {int numCols} The number of columns in the spritesheet
     * @param {int width} The width of the intended sprite to draw
     * @param {int height} The height of the intended sprite to draw
     * @return {Sprite} The constructed sprite object
     */
    public Sprite (Bitmap map, int numRows, int numCols, int width, int height, View parent) {
        mBitmap = map;
        mRows = numRows;
        mCols = numCols;
        mWidth = width;
        mHeight = height;
        mX = -2 * width;
        mY = -2 * height;
        mNoDrawLeft = -1 * width;
        mNoDrawTop = -1 * height;
        mScreenWidth = parent.getWidth();
        mScreenHeight = parent.getHeight();
        mFrameRow = 0;
        mFrameCol = 0;
    }

    // Draw method that draws the bitmap to a specific canvas
    /*
     * @param {Canvas} The canvas object to which the sprite is drawn
     */
    public void draw(SurfaceHolder holder) {

        if(holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();

            // Only draw if the sprite is within the noDraw bounds
            if (mX > mNoDrawLeft && mX < mScreenWidth + mWidth
                    && mY > mNoDrawTop && mY < mScreenHeight + mHeight) {
                // Within this block, the sprite's x and y are within visible screen bounds
                // Create a source rectangle within the spritesheet and a destination rectangle on the canvas
                int x = mFrameCol * mWidth;
                int y = mFrameRow * mHeight;
                Rect src = new Rect(x, y, x + mWidth, y + mHeight);
                Rect dst = new Rect(mX, mY, mX + mWidth, mY + mHeight);
                // Draw the sprite to the canvas
                canvas.drawBitmap(mBitmap, src, dst, null);
                // Update the animation that runs on this sprite
                updateAnim();
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    /*
     * Method that updates the animation on the spritesheet
     */
    private void updateAnim() {
        // Start by advancing the columns, staying on the current row
        mFrameCol++;

        // Move down a row and return to column 0 if moved past the last column
        if(mFrameCol > mCols) {
            mFrameRow++;
            mFrameCol = 0;
        }

        // Reset the row to the top once the bottom has completed
        if(mFrameRow > mRows) {
            mFrameRow = 0;
        }
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }
}
