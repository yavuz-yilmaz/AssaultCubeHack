package org.example.overlay;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class JHOverlay extends ApplicationAdapter {
    public SpriteBatch mainBatch;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;

    private final ObjectArrayList<Runnable> bodies = new ObjectArrayList<>();

    @Override
    public void create() {
        mainBatch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainBatch.begin();
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).run();
        }
        mainBatch.end();
    }

    @Override
    public void dispose() {
        mainBatch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

    public void addBody(Runnable body) {
        bodies.add(body);
    }
}
