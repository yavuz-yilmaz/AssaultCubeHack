package org.example.overlay;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class WindowCorrector {

    private static final int SWP_NOSIZE = 0x0001;
    private static final int SWP_NOMOVE = 0x0002;

    private static final int WS_EX_TOOLWINDOW = 0x00000080;
    private static final int WS_EX_APPWINDOW = 0x00040000;

    public static void setupWindow (WinDef.HWND hwnd) {

        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);

        wl = wl & ~WinUser.WS_VISIBLE;

        wl = wl | WS_EX_TOOLWINDOW;
        wl = wl & ~WS_EX_APPWINDOW;

        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_HIDE);
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_STYLE, wl);
        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_SHOW);
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);


        var HWND_TOPPOS = new WinDef.HWND(new Pointer(-1));
        User32.INSTANCE.SetWindowPos(hwnd, HWND_TOPPOS, 0,0, 1920, 1080, SWP_NOMOVE | SWP_NOSIZE);
    }
}
