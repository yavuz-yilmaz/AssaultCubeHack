package org.example.hacks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.example.entities.Player;
import org.example.offsets.Offsets;
import org.example.overlay.JHOverlay;
import org.example.overlay.Overlay;
import org.example.util.Geom;
import org.example.util.Geom.*;

import java.util.ArrayList;
import java.util.List;

public class Esp {
    private static WinNT.HANDLE hProcess;
    private static Player localPlayer;
    private static Pointer entityList;
    private static Pointer botCountPtr = null;
    private static int botCount;
    private static Pointer viewMatrixPtr;

    static {
        Esp.hProcess = Offsets.hProcess;
        Esp.localPlayer = new Player(hProcess, Offsets.localPlayerPtr);
        Esp.entityList = Offsets.entityList;
        Esp.botCountPtr = Offsets.botCountPtr;
        Esp.viewMatrixPtr = Offsets.viewMatrixPtr;
    }

//    public static void setPointers() {
//        Esp.hProcess = Offsets.hProcess;
//        Esp.entityList = Offsets.entityList;
//        Esp.botCountPtr = Offsets.botCountPtr;
//        Esp.viewMatrixPtr = Offsets.viewMatrixPtr;
//    }

    private static void setBotCount(){
        if (botCountPtr != null)
        {
            IntByReference count = new IntByReference(0);
            Kernel32.INSTANCE.ReadProcessMemory(hProcess, botCountPtr, count.getPointer(), 4, null);
            botCount = count.getValue();
        }
    }
    public static class EspRectangle {
        public Rectangle rectangle;
        public boolean isEnemy;
        public String name;
        public EspRectangle(Rectangle rectangle, boolean isEnemy, String name)
        {
            this.rectangle = rectangle;
            this.isEnemy = isEnemy;
            this.name = name;
        }
    }

    public static void drawEsp() {
        setBotCount();
        int localPlayerTeam = localPlayer.getTeamId();
        List<EspRectangle> rectangles = new ArrayList<>();

        Memory outputBuffer = new Memory(64);
        boolean isRead = Kernel32.INSTANCE.ReadProcessMemory(hProcess, viewMatrixPtr, outputBuffer, (int) outputBuffer.size(), null);
        if (!isRead)
            System.out.println("Okuyamıyom");
        float[] viewMatrix = outputBuffer.getFloatArray(0, 16);
        outputBuffer.close();

        for(int i = 1; i < botCount + 1; i++) {
            Pointer botPtr = entityList.share((long) i * 0x04);
            Player bot = new Player(hProcess, botPtr);
            int botTeam = bot.getTeamId();
            int health = bot.getHealth();
            if (health > 100 || health <= 0)
                continue;

            Vector3 headPos = bot.getHeadPos();
            Vector3 feetPos = bot.getPos();

            Vector3 headScreenPos = Geom.worldToScreen(headPos, viewMatrix);
            Vector3 feetScreenPos = Geom.worldToScreen(feetPos, viewMatrix);

            float height = Math.abs(headScreenPos.y - feetScreenPos.y);
            float width = height / 2;
            Rectangle rectangle = new Rectangle(headScreenPos.x - width / 2, headScreenPos.y - height, width, height);

            rectangles.add(new EspRectangle(rectangle, localPlayerTeam != botTeam, bot.getName()));

        }

            JHOverlay jhOverlay = Overlay.jhOverlay;
            jhOverlay.addBody(() -> {

                jhOverlay.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (EspRectangle rect: rectangles) {
                if(rect.isEnemy)
                    jhOverlay.shapeRenderer.setColor(1, 0, 0, 1);
                else
                    jhOverlay.shapeRenderer.setColor(0, 1, 0, 1);
                jhOverlay.shapeRenderer.rect(rect.rectangle.x, rect.rectangle.y, rect.rectangle.width, rect.rectangle.height);
                SpriteBatch batch = new SpriteBatch();
                batch.begin();
                jhOverlay.font.draw(batch, rect.name, rect.rectangle.x, rect.rectangle.y);
                batch.end();
                batch.dispose();
            }
            jhOverlay.shapeRenderer.end();
            rectangles.clear();
        });

    }
}
