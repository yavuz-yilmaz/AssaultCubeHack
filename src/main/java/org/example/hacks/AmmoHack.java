package org.example.hacks;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.*;
import com.sun.jna.ptr.*;
import org.example.offsets.Offsets;

import static org.example.util.Util.*;

public class AmmoHack {

    private static HANDLE hProcess;
    private static Pointer localPlayerPtr;
    private final static int[] ammoOffsets = {0x140};
    private static Pointer ammoAddr;
    private static IntByReference newAmmo;

    static {
        AmmoHack.hProcess = Offsets.hProcess;
        AmmoHack.localPlayerPtr = Offsets.localPlayerPtr;

        AmmoHack.ammoAddr = FindDMAAddy(hProcess, localPlayerPtr, AmmoHack.ammoOffsets);
    }
//    public static void setPointers() {
//        AmmoHack.hProcess = Offsets.hProcess;
//        AmmoHack.localPlayerPtr = Offsets.localPlayerPtr;
//
//        AmmoHack.ammoAddr = FindDMAAddy(hProcess, localPlayerPtr, AmmoHack.ammoOffsets);
//    }

    public static void setAmmo(int ammoCount) {
        AmmoHack.newAmmo = new IntByReference(ammoCount);

        Kernel32.INSTANCE.WriteProcessMemory(AmmoHack.hProcess, AmmoHack.ammoAddr, AmmoHack.newAmmo.getPointer(), 4, null);
    }

}
