package com.hamsterhuey.defender;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class DefenderGame extends ApplicationAdapter {
	// Classes used in rendering game objects to the screen
	private SpriteBatch mBatch;
	private OrthographicCamera mCamera;
    private Texture mGameBackground;

	// Animations used by sprites in game
	private Animation mStandardAttackerWalk;

	// Member variables used in game calculations
	private int mRoundTimeElapsed;
    private int mRoundMaxTime;
	private float mCastleHealth;
    private float mRoundScore;
    private float mTotalScore;
	private int mRoundMaxAttackers;
	private boolean mPaused;
	private int mRoundNumber;

	// Constant variables used in game
    private final float MIN_SPAWN_Y = 65f; // The lowest point an attacker spawns at
    private final float MAX_SPAWN_Y = 95f; // The highest point an attacker spawns at
    private final float MIN_STOP_X = 585f; // The closest point that an attacker can damage the castle

	// Data structures for keeping track of specific objects in game
	private ArrayList<Attacker> mAttackerList;
	private Vector3 mLastTouch;

	
	@Override
	public void create () {
		// Begin by setting up screen rendering classes
		mBatch = new SpriteBatch();
		mCamera = new OrthographicCamera();
		mCamera.setToOrtho(false, 800, 480); // Regardless of resolution, project screen onto 800 x 480 orthographic matrix
        mGameBackground = new Texture("castle_v1.png");

		// Assign value to list so it may be given objects to store
		mAttackerList = new ArrayList<Attacker>();

		// Create the object that tracks the touch coordinates
		mLastTouch = new Vector3();

        // Assign value to miscellaneous game variables
        mRoundTimeElapsed = 0;
        mRoundMaxTime = 50000; // MILLISECONDS! Equal to 50 SECONDS
        mCastleHealth = 10000f; // Will last 10,000 frames if attacked by one single standard attacker constantly
        mRoundScore = 0f;
        mTotalScore = 0f; // Rounds aren't implemented yet
        mRoundNumber = 1;
        mRoundMaxAttackers = 5;
        mPaused = false;

		// Test the sprite class by giving it a walking animation
		Texture walkTexture = new Texture("attackerSheet.png");
		TextureRegion[][] walkTempArray = TextureRegion.split(walkTexture, 64, 64);
		TextureRegion[] walkFrames = new TextureRegion[16];

		int index = 0; // Cut the animation's frames up using this temporary jagged array (8 by 2 matches the image file rows / cols)
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				walkFrames[index++] = walkTempArray[i][j];
			}
		}

		mStandardAttackerWalk = new Animation(0.055f, walkFrames);

		// Test! Make five attackers and draw them in their walking animation
		for(float i = 0; i < 5f; i++) {
			Attacker attacker = new Attacker(1f, 100f, 40f * i, 30 * i);
			attacker.addAnimation(mStandardAttackerWalk, "walk");
			attacker.revive();

			mAttackerList.add(attacker);
		}
	}

	/**
	 * Updating all game variables and drawing is handled once every frame
	 * Since render is called once per frame, update will be called at the start of the method
	 * This allows a separation of concerns for the game code's readability.
	 */
	@Override
	public void render() {
		// Call update before anything gets drawn
		update();

		// Begin drawing operations - set projection matrix, grab frames, etc
		mBatch.setProjectionMatrix(mCamera.combined);

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mBatch.begin();
        // Draw the background first
        mBatch.draw(mGameBackground, 0f, 0f);

		for (Attacker a: mAttackerList) {
			if(a.isAlive()) {
				mBatch.draw(a.getCurrentFrame(), a.getX(), a.getY());
			}
		}
		mBatch.end();
	}

	/**
	 * The update method of the game handles all of the game-specific mechanics and calculations
	 */
	private void update() {
		// Every frame will update these variables, independent of game state
		mCamera.update();
		float deltaTime = Gdx.graphics.getDeltaTime();

		/**
		 * Input Handling
		 * When the screen is touched, kill touched attackers
		 * todo: Fling attackers on flick motions once physics is implemented
		 */
		if(Gdx.input.isTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates

			// Loop through attackers and check (against living only) if they were tapped
			float deltaX, deltaY;
			for (Attacker a: mAttackerList) {
				if(a.isAlive()) {
					deltaX = Math.abs(a.getX() + 32f - mLastTouch.x);
					deltaY = Math.abs(a.getY() + 32f - mLastTouch.y);
					// See if the deltas are both less than the sprite's width
					if(deltaX <= 32f && deltaY <= 32f) {
						// Kill the attacker
						a.kill();
					}
				}
			}
		}

		// Loop through the list of attackers and update each (dead attackers' update does nothing)
		for (Attacker a: mAttackerList) {
			a.update(deltaTime);
		}
	}
}
