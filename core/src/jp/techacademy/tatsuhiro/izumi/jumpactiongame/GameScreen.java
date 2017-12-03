package jp.techacademy.tatsuhiro.izumi.jumpactiongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen extends ScreenAdapter {

    static final float CAMERA_WIDTH = 15;
    static final float CAMERA_HEIGHT = 10;
    static final float WORLD_WIDTH = 15;
    static final float WORLD_HEIGHT = 10 * 20; // 20画面分登れば終了
    static final float GUI_WIDTH = 960;
    static final float GUI_HEIGHT = 640;

    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;
    static final int BATTLE_SCENE = 3;

    // 重力
    static final float GRAVITY = -12;

    private JumpActionGame mGame;

    Sprite mBg;
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewPort;

    Random mRandom;
    List<Step> mSteps;
    List<Star> mStars;
    List<Star1> mStars1;
    Ufo mUfo;
    Player mPlayer;
    List<Enemy> mEnemys;
    List<Enemy2> mEnemys2;
    List<Enemy3> mEnemys3;
    List<Enemy4> mEnemys4;
    List<Enemy5> mEnemys5;
    List<Enemy6> mEnemys6;
    List<Enemy7> mEnemys7;
    List<Enemy8> mEnemys8;
    List<Enemy9> mEnemys9;
    List<Enemy10> mEnemys10;
    List<Enemy11> mEnemys11;
    List<Enemy12> mEnemys12;
    List<Enemy13> mEnemys13;
    List<Enemy14> mEnemys14;
    List<Enemy15> mEnemys15;
    List<Enemy16> mEnemys16;


    float mHeightSoFar;
    int mGameState;

    Vector3 mTouchPoint;
    BitmapFont mFont;
    int mScore;
    int mHighScore;
    Preferences mPrefs; // ←追加する

    private Sound over;
    private Sound hoshi;
    private Sound jump;
    private Sound dorodoro;

    private Music valley;
    private Music battle;

    public GameScreen(JumpActionGame game) {
        mGame = game;

        over = Gdx.audio.newSound(Gdx.files.internal("data/se_maoudamashii_onepoint30.mp3"));//音の再生には時間がかかるので、先にコンストラクタ内で呼び込みを行っておく
        hoshi = Gdx.audio.newSound(Gdx.files.internal("data/se_maoudamashii_system49.mp3"));
        jump = Gdx.audio.newSound(Gdx.files.internal("data/jump.mp3"));
        dorodoro = Gdx.audio.newSound(Gdx.files.internal("data/dorodoro.mp3"));
        valley = Gdx.audio.newMusic(Gdx.files.internal("valley.mp3"));

        // 背景の準備
        Texture bgTexture = new Texture("back1.jpg");
        // TextureRegionで切り出す時の原点は左上
        mBg = new Sprite(new TextureRegion(bgTexture, 0, 0, 640,480));
        mBg.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        mBg.setPosition(0, 0);

        // カメラ、ViewPortを生成、設定する
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera);

        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);

        // メンバ変数の初期化
        mRandom = new Random();
        mSteps = new ArrayList<Step>();
        mStars = new ArrayList<Star>();
        mStars1 = new ArrayList<Star1>();

        mEnemys = new ArrayList<Enemy>();
        mEnemys2 = new ArrayList<Enemy2>();
        mEnemys3 = new ArrayList<Enemy3>();
        mEnemys4 = new ArrayList<Enemy4>();
        mEnemys5 = new ArrayList<Enemy5>();
        mEnemys6 = new ArrayList<Enemy6>();
        mEnemys7 = new ArrayList<Enemy7>();
        mEnemys8 = new ArrayList<Enemy8>();
        mEnemys9 = new ArrayList<Enemy9>();
        mEnemys10 = new ArrayList<Enemy10>();
        mEnemys11 = new ArrayList<Enemy11>();
        mEnemys12 = new ArrayList<Enemy12>();
        mEnemys13 = new ArrayList<Enemy13>();
        mEnemys14 = new ArrayList<Enemy14>();
        mEnemys15 = new ArrayList<Enemy15>();
        mEnemys16 = new ArrayList<Enemy16>();


        mGameState = GAME_STATE_READY;
        mTouchPoint = new Vector3();
        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
        mFont.getData().setScale(0.8f);
        mScore = 0;

        // ハイスコアをPreferencesから取得する
        mPrefs = Gdx.app.getPreferences("p.techacademy.tatsuhiro.izumi.jumpactiongame"); // ←追加する
        mHighScore = mPrefs.getInteger("HIGHSCORE", 0); // ←追加する

        createStage();
    }

    @Override
    public void render(float delta) {
        // 状態を更新する
        update(delta);

        // 描画する
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // カメラの中心を超えたらカメラを上に移動させる つまりキャラが画面の上半分には絶対に行かない
        if (mPlayer.getY() > mCamera.position.y) {
            mCamera.position.y = mPlayer.getY();
        }

        mGame.batch.begin();


        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);

        // 背景
        // 原点は左下
        mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2);
        mBg.draw(mGame.batch);

        // Step
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).draw(mGame.batch);
        }

        // Star
        for (int i = 0; i < mStars.size(); i++) {
            mStars.get(i).draw(mGame.batch);
        }

        // Star1
        for (int i = 0; i < mStars1.size(); i++) {
            mStars1.get(i).draw(mGame.batch);
        }


        // enemy
        for (int i = 0; i < mEnemys.size(); i++) {
            mEnemys.get(i).draw(mGame.batch);
        }

        // enemy2
        for (int i = 0; i < mEnemys2.size(); i++) {
            mEnemys2.get(i).draw(mGame.batch);
        }

        // enemy3
        for (int i = 0; i < mEnemys3.size(); i++) {
            mEnemys3.get(i).draw(mGame.batch);
        }

        // enemy4
        for (int i = 0; i < mEnemys4.size(); i++) {
            mEnemys4.get(i).draw(mGame.batch);
        }

        // enemy5
        for (int i = 0; i < mEnemys5.size(); i++) {
            mEnemys5.get(i).draw(mGame.batch);
        }

        // enemy6
        for (int i = 0; i < mEnemys6.size(); i++) {
            mEnemys6.get(i).draw(mGame.batch);
        }

        // enemy7
        for (int i = 0; i < mEnemys7.size(); i++) {
            mEnemys7.get(i).draw(mGame.batch);
        }

        // enemy8
        for (int i = 0; i < mEnemys8.size(); i++) {
            mEnemys8.get(i).draw(mGame.batch);
        }

        // enemy9
        for (int i = 0; i < mEnemys9.size(); i++) {
            mEnemys9.get(i).draw(mGame.batch);
        }

        // enemy10
        for (int i = 0; i < mEnemys10.size(); i++) {
            mEnemys10.get(i).draw(mGame.batch);
        }

        // enemy11
        for (int i = 0; i < mEnemys11.size(); i++) {
            mEnemys11.get(i).draw(mGame.batch);
        }

        // enemy12
        for (int i = 0; i < mEnemys12.size(); i++) {
            mEnemys12.get(i).draw(mGame.batch);
        }

        // enemy13
        for (int i = 0; i < mEnemys13.size(); i++) {
            mEnemys13.get(i).draw(mGame.batch);
        }

        // enemy14
        for (int i = 0; i < mEnemys14.size(); i++) {
            mEnemys14.get(i).draw(mGame.batch);
        }

        // enemy15
        for (int i = 0; i < mEnemys15.size(); i++) {
            mEnemys15.get(i).draw(mGame.batch);
        }

        // enemy16
        for (int i = 0; i < mEnemys16.size(); i++) {
            mEnemys16.get(i).draw(mGame.batch);
        }

        // UFO
        mUfo.draw(mGame.batch);


        //Player
        mPlayer.draw(mGame.batch);


        // スコア表示
        mGuiCamera.update(); // ←追加する
        mGame.batch.setProjectionMatrix(mGuiCamera.combined); // ←追加する
       // mGame.batch.begin(); // ←追加する
        mFont.draw(mGame.batch, "HighScore: " + mHighScore, 16, GUI_HEIGHT - 15); // ←追加する
        mFont.draw(mGame.batch, "Score: " + mScore, 16, GUI_HEIGHT - 35); // ←追加する
        mGame.batch.end(); // ←追加する
    }

    @Override
    public void resize(int width, int height) {
        mViewPort.update(width, height);
        mGuiViewPort.update(width, height);
    }

    // ステージを作成する
    private void createStage() {


        // テクスチャの準備
        Texture stepTexture = new Texture("step.png");
        Texture starTexture = new Texture("hono.png");
        Texture starTexture1= new Texture("hono1.png");
        Texture playerTexture = new Texture("3-9fc_boss.png");
        Texture ufoTexture = new Texture("ufo.png");

        Texture enemyTexture = new Texture("6-13_boss.png");
        Texture enemy2Texture = new Texture("skelton.gif");
        Texture enemy3Texture = new Texture("sido.png");
        Texture enemy4Texture = new Texture("metal.gif");
        Texture enemy5Texture = new Texture("11.png");
        Texture enemy6Texture = new Texture("12.png");
        Texture enemy7Texture = new Texture("13.png");
        Texture enemy8Texture = new Texture("14.png");
        Texture enemy9Texture = new Texture("15.png");
        Texture enemy10Texture = new Texture("16.png");
        Texture enemy11Texture = new Texture("17.png");
        Texture enemy12Texture = new Texture("18.png");
        Texture enemy13Texture = new Texture("19.png");
        Texture enemy14Texture = new Texture("20.png");
        Texture enemy15Texture = new Texture("21.png");
        Texture enemy16Texture = new Texture("22.png");


        // StepとStarをゴールの高さまで配置していく
        float y = 0;

        float maxJumpHeight = Player.PLAYER_JUMP_VELOCITY * Player.PLAYER_JUMP_VELOCITY / (2 * -GRAVITY);
        while (y < WORLD_HEIGHT - 5) {
            int type = mRandom.nextFloat() > 0.8f ? Step.STEP_TYPE_MOVING : Step.STEP_TYPE_STATIC;
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Step.STEP_WIDTH);

            Step step = new Step(type, stepTexture, 0, 0, 144, 36);
            step.setPosition(x, y);
            mSteps.add(step);

            if (mRandom.nextFloat() > 0.6f) {
                Star star = new Star(starTexture, 0, 0, 220,137);
                star.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Star.STAR_HEIGHT + mRandom.nextFloat() * 3);
                mStars.add(star);
            }
            if (mRandom.nextFloat() > 0.6f) {
                Star1 star1 = new Star1(starTexture1, 0, 0, 220,137);
                star1.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Star1.STAR_HEIGHT + mRandom.nextFloat() * 3);
                mStars1.add(star1);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy enemy = new Enemy(enemyTexture, 0, 0, 300,140);
                enemy.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys.add(enemy);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy2 enemy2 = new Enemy2(enemy2Texture, 0, 0, 76,96);
                enemy2.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy2.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys2.add(enemy2);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy3 enemy3 = new Enemy3(enemy3Texture, 0, 0, 184,186);
                enemy3.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy3.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys3.add(enemy3);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy4 enemy4 = new Enemy4(enemy4Texture, 0, 0, 30,30);
                enemy4.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy4.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys4.add(enemy4);
            }


            if (mRandom.nextFloat() > 0.6f) {
                Enemy6 enemy6 = new Enemy6(enemy6Texture, 0, 0, 144,126);
                enemy6.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy6.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys6.add(enemy6);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy7 enemy7 = new Enemy7(enemy7Texture, 0, 0, 126,152);
                enemy7.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy7.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys7.add(enemy7);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy8 enemy8 = new Enemy8(enemy8Texture, 0, 0, 172,198);
                enemy8.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy8.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys8.add(enemy8);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy9 enemy9 = new Enemy9(enemy9Texture, 0, 0, 142,190);
                enemy9.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy9.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys9.add(enemy9);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy10 enemy10 = new Enemy10(enemy10Texture, 0, 0, 238,148);
                enemy10.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy10.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys10.add(enemy10);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy11 enemy11 = new Enemy11(enemy11Texture, 0, 0, 148,180);
                enemy11.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy11.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys11.add(enemy11);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy12 enemy12 = new Enemy12(enemy12Texture, 0, 0, 128,164);
                enemy12.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy12.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys12.add(enemy12);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy13 enemy13 = new Enemy13(enemy13Texture, 0, 0, 112,128);
                enemy13.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy13.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys13.add(enemy13);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy14 enemy14 = new Enemy14(enemy14Texture, 0, 0, 142,144);
                enemy14.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy14.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys14.add(enemy14);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy15 enemy15 = new Enemy15(enemy15Texture, 0, 0, 186,134);
                enemy15.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy15.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys15.add(enemy15);
            }

            if (mRandom.nextFloat() > 0.6f) {
                Enemy16 enemy16 = new Enemy16(enemy16Texture, 0, 0, 142,120);
                enemy16.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy16.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemys16.add(enemy16);
            }


            y += (maxJumpHeight - 0.5f);
            y -= mRandom.nextFloat() * (maxJumpHeight / 3);
        }

        // Playerを配置
        mPlayer = new Player(playerTexture, 0, 0,152 ,162);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, Step.STEP_HEIGHT);


        // ゴールのUFOを配置
        mUfo = new Ufo(ufoTexture, 0, 0, 120, 74);
        mUfo.setPosition(WORLD_WIDTH / 2 - Ufo.UFO_WIDTH / 2, y);
    }

    // それぞれのオブジェクトの状態をアップデートする
    private void update(float delta) {
        switch (mGameState) {

            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                updateGameOver();
                break;
            case BATTLE_SCENE:
                battleStart();
                break;
        }
    }

    private void updateReady() {
        if (Gdx.input.justTouched()) {
            valley.play();
            mGameState = GAME_STATE_PLAYING;
        }
    }


    private void updatePlaying(float delta) {
        float accel = 0;
        if (Gdx.input.isTouched()) {
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle left = new Rectangle(0, 0, GUI_WIDTH / 2, GUI_HEIGHT);
            Rectangle right = new Rectangle(GUI_WIDTH / 2, 0, GUI_WIDTH / 2, GUI_HEIGHT);
            if (left.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = 5.0f;
            }
            if (right.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = -5.0f;
            }
        }

        // Step
        for (int i = 0; i < mSteps.size(); i++) {
            mSteps.get(i).update(delta);
        }

        // Player
        if (mPlayer.getY() <= Player.PLAYER_HEIGHT / 2) {
            mPlayer.hitStep();
        }
        mPlayer.update(delta, accel);
        mHeightSoFar = Math.max(mPlayer.getY(), mHeightSoFar);

        // 当たり判定を行う
        checkCollision();

        // ゲームオーバーか判断する
        checkGameOver();
    }

    private void checkGameOver() {
        if (mHeightSoFar - CAMERA_HEIGHT / 2 > mPlayer.getY()) {
            valley.stop();
            dorodoro.play();
            Gdx.app.log("JampActionGame", "GAMEOVER");
            mGameState = GAME_STATE_GAMEOVER;
        }
    }

    private void updateGameOver() {
        if (Gdx.input.justTouched()) {
            mGame.setScreen(new ResultScreen(mGame, mScore));//あたしくクラスを作って、戦闘シーン画面を作る
        }
    }

    private void battleStart() {
        if (Gdx.input.justTouched()) {
            mGame.setScreen(new BattleScreen1(mGame, mScore));//あたしくクラスを作って、戦闘シーン画面を作る

        }
    }


    private void checkCollision() {
        // UFO(ゴールとの当たり判定)
        if (mPlayer.getBoundingRectangle().overlaps(mUfo.getBoundingRectangle())) {
            Gdx.app.log("JampActionGame", "CLEAR");
            mGameState = GAME_STATE_GAMEOVER;
            return;
        }

            // ｝Enemyとの当たり判定
            for (int i = 0; i < mEnemys.size(); i++) {
                Enemy enemy = mEnemys.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();

                 }
            }

        // ｝Enemy2との当たり判定
        for (int i = 0; i < mEnemys2.size(); i++) {
            Enemy2 enemy2 = mEnemys2.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy2.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy3との当たり判定
        for (int i = 0; i < mEnemys3.size(); i++) {
            Enemy3 enemy3 = mEnemys3.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy3.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy4との当たり判定
        for (int i = 0; i < mEnemys4.size(); i++) {
            Enemy4 enemy4 = mEnemys4.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy4.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy5との当たり判定
        for (int i = 0; i < mEnemys5.size(); i++) {
            Enemy5 enemy5 = mEnemys5.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy5.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy6との当たり判定
        for (int i = 0; i < mEnemys6.size(); i++) {
            Enemy6 enemy6 = mEnemys6.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy6.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy7との当たり判定
        for (int i = 0; i < mEnemys7.size(); i++) {
            Enemy7 enemy7 = mEnemys7.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy7.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enem8との当たり判定
        for (int i = 0; i < mEnemys8.size(); i++) {
            Enemy8 enemy8 = mEnemys8.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy8.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy9との当たり判定
        for (int i = 0; i < mEnemys9.size(); i++) {
            Enemy9 enemy9 = mEnemys9.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy9.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy10との当たり判定
        for (int i = 0; i < mEnemys10.size(); i++) {
            Enemy10 enemy10 = mEnemys10.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy10.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy11との当たり判定
        for (int i = 0; i < mEnemys11.size(); i++) {
            Enemy11 enemy11 = mEnemys11.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy11.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy12との当たり判定
        for (int i = 0; i < mEnemys12.size(); i++) {
            Enemy12 enemy12 = mEnemys12.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy12.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy13との当たり判定
        for (int i = 0; i < mEnemys13.size(); i++) {
            Enemy13 enemy13 = mEnemys13.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy13.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy14との当たり判定
        for (int i = 0; i < mEnemys14.size(); i++) {
            Enemy14 enemy14 = mEnemys14.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy14.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy15との当たり判定
        for (int i = 0; i < mEnemys15.size(); i++) {
            Enemy15 enemy15 = mEnemys15.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy15.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }

        // ｝Enemy16との当たり判定
        for (int i = 0; i < mEnemys16.size(); i++) {
            Enemy16 enemy16 = mEnemys16.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy16.getBoundingRectangle())) {
                valley.stop();
                over.play();
                Gdx.app.log("JampActionGame", "BATTLE");
                mGameState = BATTLE_SCENE;//mGame(Screen)
                battleStart();
            }
        }


        // Starとの当たり判定
        for (int i = 0; i < mStars.size(); i++) {
            Star star = mStars.get(i);

            if (star.mState == Star.STAR_NONE) {
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(star.getBoundingRectangle())) {
                hoshi.play();
                star.get();
                mScore++; // ←追加する
                if (mScore > mHighScore) { // ←追加する
                    mHighScore = mScore; // ←追加する
                    //ハイスコアをPreferenceに保存する
                    mPrefs.putInteger("HIGHSCORE", mHighScore); // ←追加する
                    mPrefs.flush(); // ←追加する
                }
                break;
            }
        }

        // Star1との当たり判定
        for (int i = 0; i < mStars1.size(); i++) {
            Star1 star1 = mStars1.get(i);

            if (star1.mState == Star1.STAR_NONE) {
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(star1.getBoundingRectangle())) {
                hoshi.play();
                star1.get();
                mScore++; // ←追加する
                if (mScore > mHighScore) { // ←追加する
                    mHighScore = mScore; // ←追加する
                    //ハイスコアをPreferenceに保存する
                    mPrefs.putInteger("HIGHSCORE", mHighScore); // ←追加する
                    mPrefs.flush(); // ←追加する
                }
                break;
            }
        }

        // Stepとの当たり判定
        // 上昇中はStepとの当たり判定を確認しない
        if (mPlayer.velocity.y > 0) {
            return;
        }

        for (int i = 0; i < mSteps.size(); i++) {
            Step step = mSteps.get(i);

            if (step.mState == Step.STEP_STATE_VANISH) {
                continue;
            }

            if (mPlayer.getY() > step.getY()) {
                if (mPlayer.getBoundingRectangle().overlaps(step.getBoundingRectangle())) {
                    jump.play();
                    mPlayer.hitStep();
                    if (mRandom.nextFloat() > 0.5f) {
                        step.vanish();
                    }
                    break;
                }
            }
        }
    }
}