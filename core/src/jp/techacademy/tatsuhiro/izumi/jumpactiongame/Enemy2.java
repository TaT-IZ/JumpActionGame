package jp.techacademy.tatsuhiro.izumi.jumpactiongame;

/**
 * Created by Tatsuhiro on 2017/12/02.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Tatsuhiro on 2017/11/15.
 */

public class Enemy2 extends Sprite {
    // 横幅、高さ
    public static final float ENEMY_WIDTH = 0.8f;
    public static final float ENEMY_HEIGHT = 0.8f;

    // 状態
    public static final int ENEMY_EXIST = 0;
    public static final int ENEMY_NONE = 1;

    int mState;

    public Enemy2 (Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT);
        mState = ENEMY_EXIST;
    }

    public void get() {
        mState = ENEMY_NONE;
        setAlpha(0);
    }
}
