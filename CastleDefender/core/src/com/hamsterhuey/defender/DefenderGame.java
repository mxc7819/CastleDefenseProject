package com.hamsterhuey.defender;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class DefenderGame extends ApplicationAdapter {
	// Classes used in rendering game objects to the screen
	private SpriteBatch mBatch;
    private BitmapFont mFont;
	private OrthographicCamera mCamera;
	private Texture[] mGameBackgrounds;
	private DefenderButtons[] mButtons;

	// Animations used by sprites in game
	private Animation mStandardAttackerWalk;
	private Animation mKnightAttackerWalk;
	private Animation mKnightAttackerAttack;

	// Member variables used in game calculations
    private int mRoundNumber;
    private int mRoundMaxAttackers;
	private int mLivingAttackerCount;
	private int mCastleDamageStage;
	private int mGameState;
	private int mAttackerDelay;
	private float mRoundTimeElapsed;
	private float mCastleMaxHealth;
	private float mCastleHealth;
    private float mRoundScore;
    private float mTotalScore;
	private float mKnightSpawnChance;
	private float mRoundMaxTime;
	private float mRepairCost;
    private String mHealthText;
	private String mTotalScoreText;
    private String mRoundScoreText;
	private String mGameTitle;
	private String mGameSavedText;
	private String mRoundText;
	private boolean mPaused;
	private boolean mRoundSaved;

	// Monster House Variables
	private int mMonsterHouseSpawn;

	// Spawn variables
	int mSpawnTime;
	private Random mRandom;
	private int mSpawnTimeMin = 85;
	private int mSpawnTimeMax = 120;

	// Constant variables used in game
	private final float MIN_SPEED = 150f;
	private final float MAX_SPEED_VARIANCE = 120f; // Can be UP TO an additional 120 "units" of speed, based on rng
    private final float MIN_SPAWN_Y = 65f; // The lowest point an attacker spawns at
    private final float MAX_SPAWN_Y_VARIANCE = 30f; // The greatest that a spawning y-coordinate varies (adds to MIN_SPAWN_Y at creation)
    private final float MIN_STOP_X = 585f; // The closest point that an attacker can damage the castle
	private final int ATTACKER_ARRAY_SIZE = 50; // The number of attackers to make on creating the scene
	private final int ATTACKER_HALF_SIZE = ATTACKER_ARRAY_SIZE / 2; // Used so half of the array are knights, half are normal
	private final int STATE_PRE_GAME = 0;
	private final int STATE_ROUND_START = 1;
	private final int STATE_ROUND_PLAY = 2;
	private final int STATE_ROUND_OVER = 3;
	private final int STATE_GAME_OVER = 4;

	// Constants used for saving a game
	private final String PREFS_NAME = "Defender_Data";
	private final String KEY_HEALTH = "castleHealth";
	private final String KEY_ROUND = "roundNumber";
	private final String KEY_SCORE = "totalScore";

	// Data structures for keeping track of specific objects in game
	private Attacker[] mAttackers;
	private Vector3 mLastTouch;

	
	@Override
	public void create () {
		// Begin by setting up screen rendering classes
		mBatch = new SpriteBatch();
        mFont = new BitmapFont();
		mCamera = new OrthographicCamera();
		mCamera.setToOrtho(false, 800, 480); // Regardless of resolution, project screen onto 800 x 480 orthographic matrix

		mGameBackgrounds = new Texture[6]; // Six frames describing six states of damage to the castle, from pristine to broken
		mGameBackgrounds[0] = new Texture("castle_v1.png");
		mGameBackgrounds[1] = new Texture("castle_v1_dam1.png");
		mGameBackgrounds[2] = new Texture("castle_v1_dam2.png");
		mGameBackgrounds[3] = new Texture("castle_v1_dam3.png");
		mGameBackgrounds[4] = new Texture("castle_v1_dam4.png");
		mGameBackgrounds[5] = new Texture("castle_v1_dam5.png");

		// Assign value to list so it may be given objects to store
		mAttackers = new Attacker[ATTACKER_ARRAY_SIZE];
		mButtons = new DefenderButtons[7];
		mButtons[0] = new DefenderButtons(DefenderButtons.TYPE_NEW_GAME);
		mButtons[1] = new DefenderButtons(DefenderButtons.TYPE_RESUME_SAVED_GAME);
		mButtons[2] = new DefenderButtons(DefenderButtons.TYPE_PAUSE_GAME);
		mButtons[3] = new DefenderButtons(DefenderButtons.TYPE_RESUME_PAUSED_GAME);
		mButtons[4] = new DefenderButtons(DefenderButtons.TYPE_REPAIR_CASTLE);
		mButtons[5] = new DefenderButtons(DefenderButtons.TYPE_CONTINUE_NEXT_ROUND);
		mButtons[6] = new DefenderButtons(DefenderButtons.TYPE_SAVE_GAME);

		mButtons[0].setTexture(new Texture("button1.png"));
		mButtons[1].setTexture(new Texture("button1.png"));
		mButtons[2].setTexture(new Texture("button2.png"));
		mButtons[3].setTexture(new Texture("button2.png"));
		mButtons[4].setTexture(new Texture("button3.png"));
		mButtons[5].setTexture(new Texture("button3.png"));
		mButtons[6].setTexture(new Texture("button3.png"));

		// Create the object that tracks the touch coordinates
		mLastTouch = new Vector3();

        // Assign value to miscellaneous game variables
        mRoundTimeElapsed = 0;
        mRoundMaxTime = 25f; // Counted in SECONDS
		mCastleMaxHealth = 1000f;
        mCastleHealth = mCastleMaxHealth; // Will last 1,000 frames if attacked by one single standard attacker constantly
		mCastleDamageStage = 0;
        mRoundScore = 0f;
        mTotalScore = 0f;
        mRoundNumber = 1;
		mGameState = STATE_PRE_GAME;
		mAttackerDelay = 0;
        mRoundMaxAttackers = 5;
		mRepairCost = 1000f;
		mLivingAttackerCount = 0;
		mKnightSpawnChance = 0.35f; // 35% chance to spawn a knight instead of normal under regular circumstances
        mPaused = false;
		mRoundSaved = false;
        mHealthText = "Castle Strength: " + mCastleHealth;
        mRoundScoreText = "This Round Score: " + mRoundScore;
		mTotalScoreText = "Total Game Score: " + mTotalScore;
		mGameTitle = "Castle Defender";
		mGameSavedText = "Game Saved!";
		mRoundText = "Round :" + mRoundNumber;
		mRandom = new Random();
		//TEMPORARY! Will change the logic later
        mSpawnTime = 100;
		mMonsterHouseSpawn = 10;

		// Pull out the normal attacker's frames through temporary objects
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

		// Do the same process for the knight animations
		Texture knightWalk = new Texture("knight_sheet_walk.png");
		Texture knightAttack = new Texture("knight_sheet_attack.png");

		TextureRegion[][] knightWalkTemp = TextureRegion.split(knightWalk, 64, 128);
		TextureRegion[][] knightAtkTemp = TextureRegion.split(knightAttack, 64, 128);

		TextureRegion[] knightWalkFrames = new TextureRegion[8];
		TextureRegion[] knightAtkFrames = new TextureRegion[4];

		index = 0;
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				knightWalkFrames[index++] = knightWalkTemp[i][j];
			}
		}

		index = 0;
		for(int i = 0; i < 4; i++) {
			knightAtkFrames[index++] = knightAtkTemp[0][i];
		}

		mKnightAttackerAttack = new Animation(0.25f, knightAtkFrames);
		mKnightAttackerWalk = new Animation(0.125f, knightWalkFrames);

		// Populate the mAttackers array with 50 total attackers, 25 of each
		for(int i = 0; i < ATTACKER_ARRAY_SIZE; i++){
			// Grab a walk speed between constant minimum and maximum
			float walkSpeed = mRandom.nextFloat() * MAX_SPEED_VARIANCE;
			walkSpeed += MIN_SPEED; // Ensures the speed is AT LEAST 60 "units"
			// Spawn at a random y coordinate starting at MIN_SPAWN_Y, varying by MAX_SPAWN_Y_VARIANCE
			float spawnY = mRandom.nextFloat() * MAX_SPAWN_Y_VARIANCE;
			spawnY += MIN_SPAWN_Y;

			if(i < ATTACKER_HALF_SIZE) {
				// The first half of the array are normal attackers
				mAttackers[i] = new Attacker(1f, walkSpeed, -100f, spawnY, 64f, 64f, "normal");
				mAttackers[i].addAnimation(mStandardAttackerWalk, "walk");
			} else {
				// The second half of the array are "knight" attackers
				mAttackers[i] = new Attacker(2f, walkSpeed * 0.75f, -100f, spawnY, 64f, 128f, "knight");
				mAttackers[i].addAnimation(mKnightAttackerWalk, "walk");
				mAttackers[i].addAnimation(mKnightAttackerAttack, "attack");
			}
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
        // Draw the background first, regardless of state
        mBatch.draw(mGameBackgrounds[mCastleDamageStage], 0f, 0f);

		switch (mGameState) {
			case STATE_PRE_GAME:
				mFont.draw(mBatch, mGameTitle, 125, 375);
				mButtons[0].draw(mFont, mBatch);
				mButtons[1].draw(mFont, mBatch);
				break;
			case STATE_ROUND_START:
				mFont.draw(mBatch, mRoundText, 360, 440);
				mButtons[2].draw(mFont, mBatch);
				mButtons[3].draw(mFont, mBatch);
				break;
			case STATE_ROUND_PLAY:
				// Draw text on top of the background in the sky
				mFont.draw(mBatch, mHealthText, 600, 440);
				mFont.draw(mBatch, mRoundScoreText, 50, 440);
				mFont.draw(mBatch, mRoundText, 360, 440);
				// Draw attackers if the game isn't paused
				for (int i = 0; i < ATTACKER_ARRAY_SIZE; i++) {
					Attacker a = mAttackers[i];
					if (a.isAlive()) {
						mBatch.draw(a.getCurrentFrame(), a.getX(), a.getY());
					}
				}
				mButtons[2].draw(mFont, mBatch);
				mButtons[3].draw(mFont, mBatch);
				break;
			case STATE_ROUND_OVER:
				mFont.draw(mBatch, mHealthText, 600, 440);
				mFont.draw(mBatch, mRoundScoreText, 50, 440);
				mFont.draw(mBatch, mTotalScoreText, 50, 400);
				mButtons[4].draw(mFont, mBatch);
				mButtons[5].draw(mFont, mBatch);
				mButtons[6].draw(mFont, mBatch);
				if(mRoundSaved) {
					mFont.draw(mBatch, mGameSavedText, 350, 125);
				}
				break;
			case STATE_GAME_OVER:
				mButtons[0].draw(mFont, mBatch);

				break;
		}

        if(mPaused)
            mFont.draw(mBatch, "Paused", 380, 400);

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
        mRoundScoreText = "This Round Score: " + mRoundScore;
		mTotalScoreText = "Total Game Score: " + mTotalScore;

		// Switch statement to handle game state
		switch (mGameState) {
			case STATE_PRE_GAME:
				// Before the game starts, the first two buttons (new game and resume) are visible
				mButtons[0].setVisible(true);
				mButtons[1].setVisible(true);
				checkStartNewGameButton();
				checkLoadGameButton();
				break;
			case STATE_ROUND_START:
				mButtons[2].setVisible(true);
				handleRoundStart();
				checkPauseGameButton();
				break;
			case STATE_ROUND_PLAY:
				mButtons[2].setVisible(true);
				if(!mPaused) {
					handleStandardGameplay(deltaTime);
					checkPauseGameButton();
				}
				else {
					mButtons[3].setVisible(true);
					checkResumeGameButton();
				}
				break;
			case STATE_ROUND_OVER:
				mButtons[4].setVisible(true);
				mButtons[5].setVisible(true);
				mButtons[6].setVisible(true);
				checkRepairCastleButton();
				checkSaveGameButton();
				checkNextRoundButton();
				break;
			case STATE_GAME_OVER:
				mButtons[0].setVisible(true);
				checkStartNewGameButton();
				break;
		}


	}

	/**
	 * Method that calculates the standard logic for gameplay - STATE_ROUND_PLAY matches game state
	 * @param deltaTime: The time delta passed by the caller
	 */
	private void handleStandardGameplay(float deltaTime) {
		// Decrease time to next revived enemy
		mSpawnTime--;

		// Accumulate time elapsed while playing
		mRoundTimeElapsed += deltaTime;

		/**
		 * Input Handling
		 * When the screen is touched, kill touched attackers
		 */
		if(Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates

			// Find the first attacker located at the touch position, if any
			int indexOfAttacker = getFirstAttackerAt(mLastTouch.x, mLastTouch.y);
			// If the index is greater than negative one, kill the attacker and add points
			if(indexOfAttacker > -1){
				mAttackers[indexOfAttacker].kill();
				mLivingAttackerCount--;
				mMonsterHouseSpawn--;
				mRoundScore += 100f;
				if(indexOfAttacker > ATTACKER_HALF_SIZE - 1)
					mRoundScore += 150f; // 150 Bonus points for killing a knight
			}
		}

		// Spawns a new enemy once the respawn timer hits less than 0
		if(mSpawnTime <0) {
			// Revive an attacker
			reviveAttacker();

			// Randomly select a new respawn timer within min and max bounds
			mSpawnTime = mRandom.nextInt(mSpawnTimeMax - mSpawnTimeMin + 1) + mSpawnTimeMin;
		}

		//spawns a row of enemy units if player is doing well
		if(mMonsterHouseSpawn <0) {
			int monsterHouseRush = 3;
			while(monsterHouseRush > 0) {
				for (float i = 0; i < 5f; i++) {
					reviveAttacker();
				}
				monsterHouseRush--;
			}
			mMonsterHouseSpawn = 10;
		}

		// Loop through the array of attackers and update each (dead attackers' update does nothing)
		for (int i = 0; i < ATTACKER_ARRAY_SIZE; i++) {
			Attacker a = mAttackers[i];
			a.update(deltaTime);

			if(a.getX() > MIN_STOP_X) {
				// Play the attacker's "attack" animation
				a.play("attack", true);
				// The attacker stops at the castle and the castle begins losing health
				a.setPosition(MIN_STOP_X, a.getY());
				mCastleHealth -= a.getHitDamage();
				// Only bother checking castle damage stage here - only when damage is being dealt
				float percentDamage = (mCastleMaxHealth - mCastleHealth) / mCastleMaxHealth;
				if(percentDamage < 0.5f) {
					mCastleDamageStage = 0;
				}
				else {
					mCastleDamageStage = 1;
				}
				if(percentDamage > 0.15f) {
					mCastleDamageStage = 2;
				}
				if(percentDamage > 0.35f) {
					mCastleDamageStage = 3;
				}
				if(percentDamage > 0.65f) {
					mCastleDamageStage = 4;
				}
				if(percentDamage > 0.85f) {
					mCastleDamageStage = 5;
				}
			}
		}

		// Check if the game is over, where mCastleHealth <= 0
		if(mCastleHealth <= 0) {
			// The castle has fallen to the attackers
			// Currently, the game will pause and add text that states the game is over
			mCastleHealth = 0f;
			mHealthText = "Castle Strength: " + mCastleHealth;
			mGameState = STATE_GAME_OVER;
			hideAllButtons();
		}

		// If the round time is up, end the round and go to a new state
		if(mRoundTimeElapsed > mRoundMaxTime) {
			mGameState = STATE_ROUND_OVER;
			mTotalScore += mRoundScore;
			mRoundNumber++;
			hideAllButtons();
		}
	}

	/**
	 * Method that begins a new round - delays the attackers for 15 frames
	 */
	private void handleRoundStart() {
		// Increment attackerDelay
		mAttackerDelay++;
		mRoundText = "Starting Round " + mRoundNumber + "...";

		// If it is over the threshold of 15 frames, start the standard round
		if(mAttackerDelay > 15) {
			mGameState = STATE_ROUND_PLAY;
			mRoundText = "Round: " + mRoundNumber;
			hideAllButtons();
		}
	}

	/**
	 * Method to revive a single enemy of a specified type
	 * Two types exist, "normal" and "knight". Knights to double damage but move at 75% speed of "normal"
	 * May or may not spawn desired enemy, depending on mRoundMaxAttackers
	 *
	 * @param type: A string to determine which enemy to spawn
	 */
	private void reviveAttacker(String type) {
		// Check to see if the game will allow another enemy on screen
		if(mLivingAttackerCount >= mRoundMaxAttackers)
			return;

		// Put the string into lowercase just "in case"
		type = type.toLowerCase();
		// Check against the two types to revive
		if(type.equals("normal")) {
			for(int i = 0; i < ATTACKER_HALF_SIZE; i++) {
				if(!mAttackers[i].isAlive()){
					// Found an attacker indexed as a "normal" attacker
					mAttackers[i].revive();
					mLivingAttackerCount++;
					return; // Just need one!
				}
			}
		} else if(type.equals("knight")) {
			for(int i = ATTACKER_HALF_SIZE; i < ATTACKER_ARRAY_SIZE; i++) {
				if(!mAttackers[i].isAlive()) {
					// Found an attacker indexed as a "knight" attacker
					mAttackers[i].revive();
					mAttackers[i].play("walk", true);
					mLivingAttackerCount++;
					return; // Just need one!
				}
			}
		}
	}

	/**
	 * Find the index of the first attacker that point (x, y) intersects
	 * @param x: The x coordinate to check against the array of living attackers
	 * @param y: The y coordinate to check against the array of living attackers
	 * @return index: The index of the first attacker found to be intersected by (x, y)
	 */
	private int getFirstAttackerAt(float x, float y) {
		int index = -1; // Start assuming no attacker intersects the point
		Rectangle temp = new Rectangle();
		for(int i = 0; i < ATTACKER_ARRAY_SIZE; i++) {
			if(mAttackers[i].isAlive()){
				Attacker a = mAttackers[i];
				// Populate the temporary rectangle with the attacker's parameters
				temp.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());

				// See if (x, y) lies within the rectangle described by a.x, a.y, a.width, a.height
				if(temp.contains(x, y)) {
					// Within this block, the point should lie within the rectangle -- Attacker has been tapped
					index = i;
					return index; // Loop exits here with index value OR never arrives here at all (-1)
				}
			}
		}
		return index; // This return value is -1, since execution should only arrive here when testing fails all attackers
	}
	/**
	 * The following methods check if the hard-coded buttons have been pressed
	 * Each button has a specific behavior so each method has custom code
	 */
	private void checkStartNewGameButton() {
		if(mButtons[0].isVisible() && Gdx.input.justTouched()){
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if(mButtons[0].getRect().contains(mLastTouch.x, mLastTouch.y)){
				// The "New Game" button was pressed, start a new game!
				mRoundNumber = 1;
				resetRoundVars();
				mGameState = STATE_ROUND_START;
				hideAllButtons();
			}
		}
	}

	private void checkLoadGameButton() {
		if(mButtons[1].isVisible() && Gdx.input.justTouched()){
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if(mButtons[1].getRect().contains(mLastTouch.x, mLastTouch.y)){
				// The "Continue Game" button was pressed, load the saved game, or default to new anyway
				Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
				mRoundNumber = prefs.getInteger(KEY_ROUND, 1);
				mCastleHealth = prefs.getFloat(KEY_HEALTH, 1000f);
				mTotalScore = prefs.getFloat(KEY_SCORE, 0f);
				resetRoundVars();
				mGameState = STATE_ROUND_START;
				hideAllButtons();
			}
		}
	}

	private void checkPauseGameButton() {
		if(mButtons[2].isVisible() && Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if (mButtons[2].getRect().contains(mLastTouch.x, mLastTouch.y)) {
				// The pause button was tapped, set paused to true
				pause();
				hideAllButtons();
			}
		}
	}

	private void checkResumeGameButton() {
		if(mButtons[3].isVisible() && Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if (mButtons[3].getRect().contains(mLastTouch.x, mLastTouch.y)) {
				// The resume button was tapped, set paused to false
				resume();
				hideAllButtons();
			}
		}
	}

	private void checkRepairCastleButton() {
		if(mButtons[4].isVisible() && Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if (mButtons[4].getRect().contains(mLastTouch.x, mLastTouch.y)) {
				// The repair button was tapped, see if the player has enough total points to repair once
				if(mTotalScore > mRepairCost) {
					// Deduct the repair cost from total score and add 100 to castle health
					mTotalScore -= mRepairCost;
					mCastleHealth += 100f;

					// Update the visual of the castle if it is repaired
					float percentDamage = (mCastleMaxHealth - mCastleHealth) / mCastleMaxHealth;
					if(percentDamage < 0.5f) {
						mCastleDamageStage = 0;
					}
					else {
						mCastleDamageStage = 1;
					}
					if(percentDamage > 0.15f) {
						mCastleDamageStage = 2;
					}
					if(percentDamage > 0.35f) {
						mCastleDamageStage = 3;
					}
					if(percentDamage > 0.65f) {
						mCastleDamageStage = 4;
					}
					if(percentDamage > 0.85f) {
						mCastleDamageStage = 5;
					}
				}
			}
		}
	}

	private void checkNextRoundButton() {
		if(mButtons[5].isVisible() && Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if (mButtons[5].getRect().contains(mLastTouch.x, mLastTouch.y)) {
				// The "advance round" button was pressed, round is already incremented, start new
				resetRoundVars();
				hideAllButtons();
				mGameState = STATE_ROUND_START;
			}
		}
	}

	private void checkSaveGameButton() {
		if(mButtons[6].isVisible() && Gdx.input.justTouched()) {
			// Grab the touch position, saved as a Vector3
			mLastTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(mLastTouch); // Translates to game world coordinates
			// The button is visible and the screen was just touched
			// See if the touch was in the button's outer rectangle
			if (mButtons[6].getRect().contains(mLastTouch.x, mLastTouch.y)) {
				// The "save game" button has been pressed, save game varables
				Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
				// Three variables saved - Total score, castle health, round number
				prefs.putInteger(KEY_ROUND, mRoundNumber);
				prefs.putFloat(KEY_SCORE, mTotalScore);
				prefs.putFloat(KEY_HEALTH, mCastleHealth);
				// "Flush" the changes
				prefs.flush();
				// Show the user the save went through
				mRoundSaved = true;
			}
		}
	}

	/**
	 * Method to reset common round-dependent game variables used between rounds
	 * Variables reset:
	 * mAttackerDelay
	 * mRoundTimeElapsed
	 * mRoundScore
	 * mKnightChance
	 * mRoundMaxAttackers
	 * mRoundMaxTime
	 * mRoundSaved
	 * mSpawnTimeMin
	 * mSpawnTimeMax
	 * Kills all living attackers
	 */
	private void resetRoundVars() {
		mAttackerDelay = 0;
		mRoundTimeElapsed = 0;
		mRoundScore = 0f;
		mKnightSpawnChance = 0.35f + (0.1f * (mRoundNumber - 1)); // +10% chance per round, starting at 35%
		if(mKnightSpawnChance > 0.65f) mKnightSpawnChance = 0.65f; // Hard cap at 65%
		mRoundMaxAttackers = 5 + mRoundNumber * 2;
		mRoundMaxTime = 50 + (mRoundNumber * 1.5f);
		mRoundSaved = false;
		mRoundText = "Round: " + mRoundNumber;
		mSpawnTimeMin = mSpawnTimeMin - (mRoundNumber * 5);
		mSpawnTimeMax = mSpawnTimeMax - (mRoundNumber * 5);

		// Kill all living attackers
		for(int i = 0; i < ATTACKER_ARRAY_SIZE; i++) {
			mAttackers[i].kill();
		}
	}

	/**
	 * Method that chooses to revive a knight or normal enemy, depending on probability of mKnightSpawnChance
	 */
	private void reviveAttacker() {
		String type = (mRandom.nextFloat() > mKnightSpawnChance) ? "normal" : "knight";
		reviveAttacker(type);
	}

	/**
	 * Small method that hides all buttons so update only shows state-dependent ones
	 */
	private void hideAllButtons() {
		for(int i = 0; i < 7; i++) {
			mButtons[i].setVisible(false);
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
