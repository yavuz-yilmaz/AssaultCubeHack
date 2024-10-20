package org.example.overlay;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;

import java.util.ArrayList;
import java.util.List;

public class Overlay {

    private static HWND hwnd;

    public static JHOverlay jhOverlay;

    public static void open() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Sj";
        config.width = 1920;
        config.height = 1080;
        config.resizable = false;

        jhOverlay = new JHOverlay();
        LwjglApplication application = new LwjglApplication(jhOverlay, config);


        do {
            HWND hwnd = User32.INSTANCE.FindWindow(null, "Sj");
            if (hwnd != null)
            {
                Overlay.hwnd = hwnd;
                break;
            }
            try {
                Thread.sleep(64);
            } catch (InterruptedException e) {
                System.out.println("got interrupted(overlay)!");
            }
        } while (!Thread.interrupted());

        WindowCorrector.setupWindow(hwnd);

        TransparencyApplier.applyTransparency(hwnd);
    }

}
