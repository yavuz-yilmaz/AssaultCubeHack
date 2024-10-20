package org.example.entities;

import com.sun.jna.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.*;

import static org.example.util.Geom.*;
import static org.example.util.Util.*;

public class Player {
    private HANDLE hProcess;
    private Pointer entityPtr;
    private Vector3 headPos;
    private Vector3 pos;
    private Vector3 viewAngles;
    private float eyeHeight;
    private int health;
    private int teamId;
    private String name;

    public Player(HANDLE hProcess ,Pointer entityPtr) {
        this.hProcess = hProcess;
        this.entityPtr = FindDMAAddy(hProcess, entityPtr, new int[]{0});
    }

    private Vector3 getVec3(long offset) {
        Vector3 vec = new Vector3(0, 0, 0);
        Pointer ptr = entityPtr.share(offset);
        Memory outputBuffer = new Memory(12);
        boolean isReaded = Kernel32.INSTANCE.ReadProcessMemory(hProcess, ptr, outputBuffer, (int) outputBuffer.size(), null);
        if (isReaded)
        {
            float [] values = outputBuffer.getFloatArray(0, 3);
            vec = new Vector3(values[0], values[1], values[2]);
        }
        outputBuffer.close();
        return vec;
    }

    private int getInt(long offset) {
        int result = 0;
        Pointer ptr = entityPtr.share(offset);
        Memory outputBuffer = new Memory(4);

        boolean isReaded = Kernel32.INSTANCE.ReadProcessMemory(hProcess, ptr, outputBuffer, (int)outputBuffer.size(), null);
        if (isReaded)
            result = outputBuffer.getInt(0);
        outputBuffer.close();

        return result;
    }

    private String getString16(long offset) {
        String str = "";
        Pointer ptr = entityPtr.share(offset);
        Memory outputBuffer = new Memory(16);

        boolean isReaded = Kernel32.INSTANCE.ReadProcessMemory(hProcess, ptr, outputBuffer, (int)outputBuffer.size(), null);
        if (isReaded)
            str = outputBuffer.getString(0);
        outputBuffer.close();
        return  str;
    }

    public Vector3 getHeadPos() {
        headPos = getVec3(0x0004);
        return headPos;
    }

    public Vector3 getPos() {
        pos = getVec3(0x0028);
        return pos;
    }

    public float getEyeHeight() {
        Pointer eyeHeightPtr = entityPtr.share(0x50);
        Memory outputBuffer = new Memory(4);

        Kernel32.INSTANCE.ReadProcessMemory(hProcess, eyeHeightPtr, outputBuffer, (int) outputBuffer.size(), null);

        eyeHeight = outputBuffer.getFloat(0);
        outputBuffer.close();
        return eyeHeight;
    }

    public Vector3 getViewAngles() {
        viewAngles = getVec3(0x0034);
        return viewAngles;
    }

    public void setAngles(float x, float y, float z) {
        Pointer addrToWrite = entityPtr.share(0x0034);
        Memory input = new Memory(12);
        input.setFloat(0, x);
        input.setFloat(4, y);
        input.setFloat(8, z);

        Kernel32.INSTANCE.WriteProcessMemory(hProcess, addrToWrite, input, (int) input.size(), null);
        input.close();
    }

    public int getHealth() {
        health = getInt(0xEC);
        return health;
    }

    public int getTeamId() {
        teamId = getInt(0x30C);
        return teamId;
    }

    public String getName() {
        name = getString16(0x205);
        return name;
    }

}
