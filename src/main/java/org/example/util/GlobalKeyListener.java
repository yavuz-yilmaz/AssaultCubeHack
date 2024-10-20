package org.example.util;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
    public static boolean isCtrlPressed = false;

    public void nativeKeyPressed(NativeKeyEvent e) {
        String pressedKey = NativeKeyEvent.getKeyText(e.getKeyCode());
        if (pressedKey.equals("Ctrl"))
            isCtrlPressed = true;
    }
    public void nativeKeyReleased(NativeKeyEvent e) {
        String releasedKey = NativeKeyEvent.getKeyText(e.getKeyCode());
        if (releasedKey.equals("Ctrl"))
            isCtrlPressed = false;
    }
}
