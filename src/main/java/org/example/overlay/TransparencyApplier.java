package org.example.overlay;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT.HRESULT;

import java.util.Arrays;
import java.util.List;

public class TransparencyApplier {
    public static boolean applyTransparency(WinDef.HWND hwnd) {
        NativeLibrary user32 = NativeLibrary.getInstance("user32");

        AccentPolicy accent = new AccentPolicy();
        accent.AccentState = AccentState.ACCENT_ENABLE_TRANSPARENTGRADIENT;
        accent.AccentFlags = 2; // must be 2 for transparency
        accent.GradientColor = 0; // ARGB color code for gradient
        int accentStructSize = accent.size();
        accent.write();
        Pointer accentPtr = accent.getPointer();

        WindowCompositionAttributeData data = new WindowCompositionAttributeData();
        data.Attribute = WindowCompositionAttribute.WCA_ACCENT_POLICY;
        data.SizeOfData = accentStructSize;
        data.Data = accentPtr;

        com.sun.jna.Function setWindowCompositionAttribute = user32.getFunction("SetWindowCompositionAttribute");
        HRESULT result = (HRESULT) setWindowCompositionAttribute.invoke(HRESULT.class, new Object[]{hwnd, data});

        return result.intValue() == 1;
    }

    public static class AccentPolicy extends Structure {
        public int AccentState;
        public int AccentFlags;
        public int GradientColor;
        public int AnimationId;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("AccentState", "AccentFlags", "GradientColor", "AnimationId");
        }
    }

    // Define the WindowCompositionAttributeData structure
    public static class WindowCompositionAttributeData extends Structure {
        public int Attribute;
        public Pointer Data;
        public int SizeOfData;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Attribute", "Data", "SizeOfData");
        }
    }

    // You need to define these enums or classes if they are not already defined
    public static class AccentState {
        public static final int ACCENT_ENABLE_TRANSPARENTGRADIENT = 2;
    }

    public static class WindowCompositionAttribute {
        public static final int WCA_ACCENT_POLICY = 19;
    }
}
