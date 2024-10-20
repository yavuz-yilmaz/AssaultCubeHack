package org.example;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.sun.jna.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinNT.*;

import static org.example.util.Util.*;

import org.example.hacks.Aimbot;
import org.example.hacks.AmmoHack;
import org.example.hacks.Esp;
import org.example.offsets.Offsets;
import org.example.overlay.Overlay;
import org.example.util.GlobalKeyListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final long PROCESS_ALL_ACCESS = ((0x000F0000L) | (0x00100000L) | 0xFFFF);

    public static void runGameLoop() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable gameTask = () -> {
            try {
                AmmoHack.setAmmo(9999);
                Aimbot.aimNearest();
                Esp.drawEsp();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Schedule the task to run every 50 milliseconds
        executor.scheduleAtFixedRate(gameTask, 30, 10, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {

        DWORD processId = GetProcessId("ac_client.exe");
        Pointer moduleBase = GetModuleBaseAddress(processId, "ac_client.exe");

        HANDLE hProcess = new HANDLE(null);
        hProcess = Kernel32.INSTANCE.OpenProcess((int) PROCESS_ALL_ACCESS, false, processId.intValue());

        Offsets.setPointers(hProcess, moduleBase);
        Offsets.getEntityList();

        //Esp.setPointers();
        //Aimbot.setPointers();
        //AmmoHack.setPointers();

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());

        Overlay.open();
//        HWND hwnd = User32.INSTANCE.FindWindow(null, "AssaultCube");
//        RECT rectangle = new RECT();
//        User32.INSTANCE.GetWindowRect(hwnd, rectangle);
//        int width = rectangle.right - rectangle.left;
//        int height = rectangle.bottom - rectangle.top;
//        System.out.println(rectangle.top);
//        System.out.println(rectangle.left);
//        while (true)
//        {
//            AmmoHack.setAmmo(9999);
//            Aimbot.aimNearest();
//            Esp.drawEsp();
//            try {
//                Thread.sleep(50);
//            } catch(InterruptedException e) {
//                System.out.println("got interrupted!");
//            }
//        }

        runGameLoop();
    }
}