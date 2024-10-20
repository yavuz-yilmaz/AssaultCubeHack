package org.example.hacks;

import com.sun.jna.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.*;
import com.sun.jna.ptr.IntByReference;
import org.example.entities.Player;
import org.example.offsets.Offsets;
import org.example.util.GlobalKeyListener;

import static org.example.util.Geom.*;

public class Aimbot {
    private static HANDLE hProcess;
    private static Player localPlayer;
    private static Pointer entityList;
    private static Pointer botCountPtr = null;
    private static int botCount;
    private static final float FOV = 45;

    static {
        Aimbot.hProcess = Offsets.hProcess;
        Aimbot.localPlayer = new Player(hProcess, Offsets.localPlayerPtr);
        Aimbot.entityList = Offsets.entityList;
        Aimbot.botCountPtr = Offsets.botCountPtr;
    }

//    public static void setPointers() {
//        Aimbot.hProcess = Offsets.hProcess;
//        Aimbot.localPlayer = new Player(hProcess, Offsets.localPlayerPtr);
//        Aimbot.entityList = Offsets.entityList;
//        Aimbot.botCountPtr = Offsets.botCountPtr;
//    }

    private static void setBotCount(){
        if (botCountPtr != null)
        {
            IntByReference count = new IntByReference(0);
            Kernel32.INSTANCE.ReadProcessMemory(hProcess, botCountPtr, count.getPointer(), 4, null);
            botCount = count.getValue();
        }
    }

    private static void normalizeAngle(Vector3 angle) {
        angle.x = ((angle.x % 360) + 360) % 360;

        if (angle.y > 90) {
            angle.y = 90;
        } else if (angle.y < -90) {
            angle.y = -90;
        }
    }

    private static void normalizeAngleDiff(Vector3 angleDiff, float Fov){
        if (angleDiff.x > 360 - Fov)
            angleDiff.x = angleDiff.x % Fov;
    }

    private static boolean isInFOV(Player owner, Vector3 target){
        Vector3 angle = calcAngle(owner.getPos(), target);
        Vector3 playerAngle = owner.getViewAngles();
        playerAngle.x += 180;
        normalizeAngle(playerAngle);

        Vector3 angleDiff = playerAngle.subtract(angle);
        //normalizeAngle(angleDiff);
        normalizeAngleDiff(angleDiff, FOV);

        return (Math.abs(angleDiff.x) <= FOV && Math.abs(angleDiff.y) <= FOV);

    }
    private static boolean isValidTarget(Player target) {
        int health = target.getHealth();

        return health > 0 && health <= 100 && isInFOV(localPlayer, target.getPos());
    }

    private static Player getNearestEntityAngle() {
        Player nearestPlayer = null;
        Vector3 playerAngle = localPlayer.getViewAngles();
        playerAngle.x += 180;
        normalizeAngle(playerAngle);
        float smallestAngle = 9999999.0f;
        setBotCount();

        for(int i = 1; i < botCount + 1; i++) {
            Pointer botPtr = entityList.share((long)i * 0x04);
            Player bot = new Player(hProcess, botPtr);
            if (!isValidTarget(bot))
                continue;
            Vector3 targetAngle = calcAngle(localPlayer.getPos(), bot.getPos());
            Vector3 angleDiff = playerAngle.subtract(targetAngle);
            normalizeAngleDiff(angleDiff, FOV);
            float angleMagnitude = angleDiff.length();

            if(angleMagnitude < smallestAngle)
            {
                smallestAngle = angleMagnitude;
                nearestPlayer = bot;
            }
        }
        return nearestPlayer;
    }
//    private static int i;
//    private static Player MevlanaLoop() {
//        setBotCount();
//        System.out.println(i);
//        Player nearestPlayer = null;
//        if ( i > 0 && i < botCount + 1)
//        {
//            System.out.println("op");
//            Pointer botPtr = entityList.share((long)i * 0x04);
//            Player bot = new Player(hProcess, botPtr);
//            nearestPlayer = bot;
//            i++;
//        }
//        else {
//            i = 1;
//        }
//        return nearestPlayer;
//    }

    public static void aimNearest() {
        Player target = getNearestEntityAngle();
        if (target == null)
            return;
        if (!GlobalKeyListener.isCtrlPressed)
            return;
        if (localPlayer.getTeamId() == target.getTeamId())
            return;
        Vector3 angle = calcAngle(localPlayer.getHeadPos(), target.getHeadPos());
        angle.x += 180;
        normalizeAngle(angle);
        localPlayer.setAngles(angle.x, angle.y, angle.z);
    }
}

