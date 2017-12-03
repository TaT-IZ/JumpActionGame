package jp.techacademy.tatsuhiro.izumi.jumpactiongame;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Tatsuhiro on 2017/11/15.
 */

public class Battle extends GameObject {
    // 横幅、高さ
    public static final float MON1_WIDTH = 2.0f;
    public static final float MON1_HEIGHT = 1.3f;

    public Battle(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(MON1_WIDTH, MON1_HEIGHT);
    }
}

