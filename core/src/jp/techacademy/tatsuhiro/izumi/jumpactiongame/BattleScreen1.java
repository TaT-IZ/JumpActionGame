package jp.techacademy.tatsuhiro.izumi.jumpactiongame;

/**
 * Created by Tatsuhiro on 2017/11/16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BattleScreen1 extends ScreenAdapter {
    static final float GUI_WIDTH = 960;
    static final float GUI_HEIGHT = 640;

    private JumpActionGame mGame;
    Sprite mBg;
    OrthographicCamera mGuiCamera;
    FitViewport mGuiViewPort;
    Battle mBattle;

    BitmapFont mFont;
    private Music battle;


    public BattleScreen1(JumpActionGame game, int score) {

        battle = Gdx.audio.newMusic(Gdx.files.internal("data/battledark.mp3"));

        mGame = game;
        if (mGame.mRequestHandler != null) { // ←追加する
            mGame.mRequestHandler.showAds(true); // ←追加する
        } // ←追加する


        // 背景の準備

        Texture bgTexture = new Texture("battle.back.jpg");
        mBg = new Sprite(new TextureRegion(bgTexture, 0, 0, 760, 570));
        mBg.setSize(GUI_WIDTH, GUI_HEIGHT);
        mBg.setPosition(0, 0);


        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);

        // フォント
        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);

    }

    @Override
    public void render(float delta) {
        // 描画する
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mGuiCamera.update();
        mGame.batch.setProjectionMatrix(mGuiCamera.combined);


        mGame.batch.begin();
        mBg.draw(mGame.batch);
        mGame.batch.end();
        battle.play();


        if (Gdx.input.justTouched()) {

        }
    }
}