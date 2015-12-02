package com.hamsterhuey.defender;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Random;

public class DefenderGame extends ApplicationAdapter {
	// Classes used in rendering game objects to the screen
	private SpriteBatch mBatch;
    private BitmapFont mFont;
	private OrthographicCamera mCamera;
    private Texture mGameBackground;

	// Animations used by sprites in game
	private Animation mStandardAttackerWalk;

	// Member variables used in game calculations
    private int mRoundNumber;
	private int mRoundTimeElapsed;
    private int mRoundMaxTime;
    private int mRoundMaxAttackers;
	private float mCastleHealth;
    private float mRoundScore;
    private float mTotalScore;
    private String mHealthText;
    private String mScoreText;
	private boolean mPaused;
	private int score;

	// Monster House Variables
	private int monsterHouseSpawn;
	private boolean monsterHouseMode;

	// Spawn variables
	int spawnTime;
	private final int SPAWN_TIME_MIN = 85;
	private final int SPAWN_TIME_MAX = 120;
	private final int SPAWN_LOCATION_MIN = 1;
	private final int SPAWN_LOCATION_MAX = 4;
	private final int SPAWN_SPEED_MAX = 150;
	private final int SPAWN_SPEED_MIN = 60;

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
        mFont = new BitmapFont();
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
        mCastleHealth = 1000f; // Will last 1,000 frames if attacked by one single standard attacker constantly
        mRoundScore = 0f;
        mTotalScore = 0f; // Rounds aren't implemented yet
        mRoundNumber = 1;
        mRoundMaxAttackers = 5;
        mPaused = false;
        mHealthText = "Castle Strength: " + mCastleHealth;
        mScoreText = "Score: " + mRoundScore;
		//TEMPORARY! Will change the logic later
        spawnTime = 100;
        score = 0;
		monsterHouseSpawn = 10;
		monsterHouseMode = false;

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
        if(!mPaused)
		    update();

		// Begin drawing operations - set projection matrix, grab frames, etc
		mBatch.setProjectionMatrix(mCamera.combined);

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mBatch.begin();
        // Draw the background first
        mBatch.draw(mGameBackground, 0f, 0f);

        // Draw text on top of the background in the sky
        mFont.draw(mBatch, mHealthText, 600, 440);
        mFont.draw(mBatch, mScoreText, 50, 440);

        // Draw attackers if the game isn't paused
        for (Attacker a : mAttackerList) {
            if (a.isAlive()) {
                mBatch.draw(a.getCurrentFrame(), a.getX(), a.getY());
            }
        }
        if(mPaused)
            mFont.draw(mBatch, "Game Over", 380, 440);

		mBatch.end();
	}

	/**
	 * The update method of the game handles all of the game-specific mechanics and calculations
	 */
	private void update() {
		// Every frame will update these variables, independent of game state
		mCamera.update();
		float deltaTime = Gdx.graphics.getDeltaTime();
        mHealthText = "Castle Strength: " + mCastleHealth;
        mScoreText = "Score: " + mRoundScore;

		spawnTime--;

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

						// Update the score
						mRoundScore += 100f;

						//update the Monster House spawner
						if(!monsterHouseMode)
							monsterHouseSpawn--;
					}
				}
			}
		}

		// Spawns a new enemy once the respawn timer hits less than 0
		if(spawnTime<0) {
			// Randomly selects a lane to spawn an enemy
			Random rng = new Random();
			float laneLocation = rng.nextInt(SPAWN_LOCATION_MAX - SPAWN_LOCATION_MIN + 1) + SPAWN_LOCATION_MIN;
			float walkSpeed = rng.nextInt(SPAWN_SPEED_MAX - SPAWN_SPEED_MIN +1) + SPAWN_SPEED_MIN;
			Attacker attacker = new Attacker(1f, walkSpeed, 40f * laneLocation, 30 * laneLocation);
			attacker.addAnimation(mStandardAttackerWalk, "walk");
			attacker.revive();
			mAttackerList.add(attacker);

			// Randomly selects a new respawn timer
			spawnTime = rng.nextInt(SPAWN_TIME_MAX - SPAWN_TIME_MIN + 1) + SPAWN_TIME_MIN;
		}

		//spawns a row of enemy units if player is doing well
		if(monsterHouseSpawn<0) {
			monsterHouseMode = true;
			int monsterHouseRush = 3;
			while(monsterHouseRush > 0) {
				for (float i = 0; i < 5f; i++) {
					Attacker attacker = new Attacker(1f, 150f, 40f * i, 30 * i);
					attacker.addAnimation(mStandardAttackerWalk, "walk");
					attacker.revive();

					mAttackerList.add(attacker);
				}
				monsterHouseRush--;
			}
			monsterHouseSpawn = 10;
			monsterHouseMode = false;
		}

		// Loop through the list of attackers and update each (dead attackers' update does nothing)
		for (Attacker a: mAttackerList) {
			a.update(deltaTime);

            if(a.getX() > MIN_STOP_X) {
                // The attacker stops at the castle and the castle begins losing health
                a.setPosition(MIN_STOP_X, a.getY());
                mCastleHealth -= a.getHitDamage();
            }
		}

        // Check if the game is over, where mCastleHealth <= 0
        if(mCastleHealth <= 0) {
            // The castle has fallen to the attackers
            // Currently, the game will pause and add text that states the game is over
            mCastleHealth = 0f;
            pause();
        }
	}

    @Override
    public void pause() {
        mPaused = true;
    }

    @Override
    public void resume() {
        mPaused = false;
    }

    @Override
    public void dispose() {
        mBatch.dispose();
        mFont.dispose();
    }
}
