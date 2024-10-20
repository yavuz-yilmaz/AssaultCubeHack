package org.example.offsets;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;

import static org.example.util.Util.FindDMAAddy;

public class Offsets {
    public static WinNT.HANDLE hProcess;
    public static Pointer moduleBase;
    public static Pointer localPlayerPtr;
    public static Pointer entityListPtr;
    public static Pointer entityList;
    public static Pointer botCountPtr;
    public static Pointer viewMatrixPtr;

    public static void setPointers(WinNT.HANDLE hProcess, Pointer moduleBase) {
        Offsets.hProcess = hProcess;
        Offsets.moduleBase = moduleBase;

        localPlayerPtr = moduleBase.share(0x18AC00);
        entityListPtr = moduleBase.share(0x18AC04);
        botCountPtr = moduleBase.share(0x191FD4);
        viewMatrixPtr = moduleBase.share(0x17DFFC- 0x6C + 0x4 *16);
    }

    public static void getEntityList() {
        entityList = FindDMAAddy(hProcess, entityListPtr, new int[] {0});
    }
}
