package jp.techacademy.tatsuhiro.izumi.jumpactiongame;

/**
 * Created by Tatsuhiro on 2017/11/29.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Star1 extends Sprite {
    // 横幅、高さ
    public static final float STAR_WIDTH = 1.0f;
    public static final float STAR_HEIGHT = 0.7f;

    // 状態
    public static final int STAR_EXIST = 0;
    public static final int STAR_NONE = 1;

    int mState;

    public Star1(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(STAR_WIDTH, STAR_HEIGHT);
        mState = STAR_EXIST;
    }

    public void get() {
        mState = STAR_NONE;
        setAlpha(0);
    }
}