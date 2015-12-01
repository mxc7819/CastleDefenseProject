package com.hamsterhuey.defender;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Class definition of a single animated "actor" in a game
 * Written requiring the libGDX game engine for android development
 *
 * This implementation assumes a sprite has a fixed width and height
 */
public class Sprite {
    // Member variables related to location
    private int mX;
    private int mY;

    // Member variables related to size
    private float mWidth;
    private float mHeight;
    private Rectangle mBoundingBox;

    // Member variables related to animations
    private boolean mAnimating;
    private boolean mLooping;
    private float mStateTime;
    private String mCurrentAnimation;
    private TextureRegion mCurrentFrame;
    private Map<String, Animation> mAnimations;
    private ArrayList<String> mAnimNames;
    private ArrayList<Animation> mAnimList;

    /**
     * Constructor for a new sprite object
     *
     * @param x: The starting x coordinate on the GDX projection
     * @param y: The starting y coordinate on the GDX projection
     * @param width: The width of the Rectangle of the sprite
     * @param height: The height of the Rectangle of the sprite
     */
    public Sprite(int x, int y, float width, float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mBoundingBox = new Rectangle();
        mBoundingBox.setX(mX);
        mBoundingBox.setY(mY);
        mBoundingBox.setWidth(mWidth);
        mBoundingBox.setHeight(mHeight);
        mStateTime = 0f;
        mCurrentFrame = null;
        mAnimating = false;
        mCurrentAnimation = "";
        mLooping = false;
        // Animations are saved as key-value pairs so they can be switched between by name
        // The list of names is mapped to the list of actual animations as pairs
        mAnimNames = new ArrayList<String>();
        mAnimList = new ArrayList<Animation>();
        defineMap();
    }

    /**
     * Method that updates the sprite's current frame, based on a time delta passed as a parameter
     * Does nothing if the sprite isn't animating
     *
     * @param deltaTime: The time delta between this frame and the previous
     * @param deltaX: The change in the x-axis to add to the bounding box
     * @param deltaY: The change in the y-axis to add to the bounding box
     */
    public void update (float deltaTime, float deltaX, float deltaY) {
        if(!mAnimating) {
            return;
        }
        mStateTime += deltaTime;
        mCurrentFrame = mAnimations.get(mCurrentAnimation).getKeyFrame(mStateTime, mLooping);
        mX += deltaX * deltaTime;
        mY += deltaY * deltaTime;
        setPosition(mX, mY);
    }

    /**
     * Overload constructor for update without needing to change location (static object)
     *
     * @param deltaTime: The time delta between this frame and the previous
     */
    public void update(float deltaTime) {
        update(deltaTime, 0f, 0f);
    }

    /**
     * Method to play an animation, additionally specifying whether looping is enabled
     *
     * @param animationName: String that retrieves the animation from the map
     * @param looping: If true, the animation will repeat until otherwise specified
     */
    public void play(String animationName, boolean looping) {
        if(!mAnimations.containsKey(animationName))
            return;

        // Code here only runs if the desired animation exists on this sprite
        mCurrentAnimation = animationName;
        mAnimating = true;
        mLooping = looping;
        mStateTime = 0f;
    }

    /**
     * Overload method to play an animation without specifying any particular animation
     * Defaults to the first animation in the list and assumes looping is true
     */
    public void play() {
        if(mAnimations.isEmpty()) {
            return;
        }
        else
            play(mAnimNames.get(0), true);
    }

    /**
     * Method to stop animation of the sprite
     */
    public void stop() {
        mAnimating = false;
        mLooping = false;
        mStateTime = 0f;
    }

    /**
     * Method to add an animation with a string name
     *
     * @param animation: The animation to add to the sprite's map
     * @param name: The string to save as the key in the key value pair
     */
    public void addAnimation(Animation animation, String name) {
        if(!mAnimations.containsKey(name)) {
            // Add the animation to the map
            mAnimations.put(name, animation);
        }
    }

    // Moved the definition of the mAnimations map to a separate method for cleanliness
    private void defineMap() {
        mAnimations = new Map<String, Animation>() {
            @Override
            public int size() {
                return mAnimList.size();
            }

            @Override
            public boolean isEmpty() {
                return mAnimList.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                String sKey = (String) key;
                return mAnimNames.contains(sKey);
            }

            @Override
            public boolean containsValue(Object value) {
                Animation aValue = (Animation) value;
                return mAnimList.contains(aValue);
            }

            @Override
            public Animation get(Object key) {
                String sKey = (String) key;
                if(mAnimNames.contains(sKey)) {
                    return mAnimList.get(mAnimNames.indexOf(sKey));
                }
                else
                    return null;
            }

            @Override
            public Animation put(String key, Animation value) {
                // Don't allow duplicate strings
                if (!mAnimNames.contains(key)) {
                    // The key is unique - add the value and key to their array lists
                    mAnimList.add(value);
                    mAnimNames.add(key);
                    return value;
                }
                else
                    return null;
            }

            @Override
            public Animation remove(Object key) {
                String sKey = (String) key;
                if (mAnimNames.contains(sKey)) {
                    return mAnimList.remove(mAnimNames.indexOf(sKey));
                }
                else
                    return null;
            }

            @Override
            public void putAll(Map<? extends String, ? extends Animation> m) {

            }

            @Override
            public void clear() {
                mAnimNames.clear();
                mAnimList.clear();
            }

            @Override
            public Set<String> keySet() {
                // Don't call this method, please. Just don't.
                return null;
            }

            @Override
            public Collection<Animation> values() {
                return mAnimList;
            }

            @Override
            public Set<Entry<String, Animation>> entrySet() {
                // Yeah don't call this either
                return null;
            }
        };
    }

    /**
     * Method to set the sprite's position to a specified coordinate location, updating it's bounding box
     *
     * @param x: The x coordinate to move the sprite to
     * @param y: The y coordinate to move the sprite to
     */
    public void setPosition(float x, float y) {
        mBoundingBox.setPosition(x, y);
    }

    // Generated Getters and Setters below
    public TextureRegion getCurrentFrame() {
        return mCurrentFrame;
    }

    public boolean isAnimating() {
        return mAnimating;
    }

    public Rectangle getBoundingBox() {
        return mBoundingBox;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }
}
