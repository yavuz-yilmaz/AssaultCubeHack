package org.example.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinNT.*;
import com.sun.jna.ptr.*;

public class Util {
    private static final int TH32CS_SNAPPROCESS = 0x00000002;
    private static final int TH32CS_SNAPMODULE = 0x00000008;
    private static final int TH32CS_SNAPMODULE32 = 0x00000010;


    public static DWORD GetProcessId(String processName) {
        DWORD processId = new DWORD(0);

        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(new DWORD(TH32CS_SNAPPROCESS), new DWORD(0));
        if (snapshot != null) {
            PROCESSENTRY32.ByReference processEntry = new PROCESSENTRY32.ByReference();
            processEntry.dwSize = new DWORD(processEntry.size());

            if (Kernel32.INSTANCE.Process32First(snapshot, processEntry)) {
                do {
                    if (Native.toString(processEntry.szExeFile).equals(processName))
                    {
                        processId = processEntry.th32ProcessID;
                        break;
                    }
                } while (Kernel32.INSTANCE.Process32Next(snapshot, processEntry));
            }
        }

        Kernel32.INSTANCE.CloseHandle(snapshot);
        return processId;
    }

    public static Pointer GetModuleBaseAddress(DWORD processId, String modName) {
        Pointer modBaseAddr = new Pointer(0);

        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(new DWORD(TH32CS_SNAPMODULE | TH32CS_SNAPMODULE32), processId);
        if (snapshot != null) {
            MODULEENTRY32W.ByReference modEntry = new MODULEENTRY32W.ByReference();
            modEntry.dwSize = new DWORD(modEntry.size());

            if(Kernel32.INSTANCE.Module32FirstW(snapshot, modEntry)) {
                do {
                    if (Native.toString(modEntry.szModule).equals(modName))
                    {
                        modBaseAddr = modEntry.modBaseAddr;
                    }
                } while (Kernel32.INSTANCE.Module32NextW(snapshot, modEntry));
            }
        }

        Kernel32.INSTANCE.CloseHandle(snapshot);
        return modBaseAddr;
    }

    public static Pointer FindDMAAddy(HANDLE hProc, Pointer ptr, int[] offsets) {
        Pointer addr = ptr;

        for (int offset : offsets) {
            PointerByReference pointerRef = new PointerByReference();
            Kernel32.INSTANCE.ReadProcessMemory(hProc, addr, pointerRef.getPointer(), 4, null);
            addr = pointerRef.getValue();
            addr = addr.share(offset);
        }

        return addr;
    }
}
