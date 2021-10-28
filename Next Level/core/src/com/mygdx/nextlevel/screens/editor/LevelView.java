package com.mygdx.nextlevel.screens.editor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.mygdx.nextlevel.screens.EditLevelScreen;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Widget to display a preview of the level
 */
public class LevelView extends Widget {
    protected ShapeRenderer shapeRenderer;
    protected float scale;              //The number of pixels per tile
    protected float originX, originY;   //Where the origin of the tilemap is in screen space
    protected Vector2 panStart;
    protected EditorLevel editorLevel;
    private InputListener inputListener;
    private ArrayList<Texture> tiles;
    private ArrayList<Texture> actorTextures;
    private int dragButton;

    private EditLevelScreen screen;

    protected Color gridColor = Color.WHITE;

    /**
     * Initialize the view widget
     * @param screen The EditLevelScreen that owns this
     * @param editorLevel The loaded level to display
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     */
    public LevelView(EditLevelScreen screen, EditorLevel editorLevel, int screenWidth, int screenHeight) {
        this(screenWidth, screenHeight);

        this.screen = screen;
        this.tiles = screen.getTiles();
        this.actorTextures = screen.getActorTextures();

        this.editorLevel = editorLevel;

        editorLevel.map[0][0] = 0;
        editorLevel.map[editorLevel.width - 1][editorLevel.height - 1] = 0;

        panStart = new Vector2(0.0f,0.0f);

        scale = 32.0f;
        centerLevel();
    }

    /**
     * Initialize the widget
     * @param screenWidth   Width of the Screen
     * @param screenHeight  Height of the Screen
     */
    public LevelView(int screenWidth, int screenHeight) {
        super();

        setWidth(screenWidth);
        setHeight(screenHeight);

        shapeRenderer = new ShapeRenderer(1000);

        setPosition(0,0);
        setBounds(0, 0, screenWidth, screenHeight);

        inputListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragButton = button;

                if(button == Input.Buttons.RIGHT) {
                    // We need to keep track of where the mouse started so we can keep track of how far
                    // the user has panned
                    panStart.set(x, y);
                    return true;
                }
                else if (button == Input.Buttons.LEFT) {
                    Vector2 tilePos = screenToWorld(x, y);
                    placeCurrentSelection(tilePos.x, tilePos.y);

                    return true;
                }

                return false;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(button == Input.Buttons.RIGHT) {
                    //move the origin by the amount that mouse has moved since the last update
                    originX += x - panStart.x;
                    originY += y - panStart.y;

                    //update the start point
                    panStart.set(x, y);
                }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(dragButton == Input.Buttons.RIGHT) {
                    //move the origin by the amount that mouse has moved since the last update
                    originX += x - panStart.x;
                    originY += y - panStart.y;

                    //update the start point
                    panStart.set(x, y);
                }
                else if(dragButton == Input.Buttons.LEFT) {
                    Vector2 tilePos = screenToWorld(x, y);
                    placeCurrentSelection((int) tilePos.x, (int) tilePos.y);
                }
            }

            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                Vector2 start = screenToWorld(x, y);

                scale -= (amountY / 10.0f * scale);

                // clamp the scale to reasonable bounds.
                // If the scale goes to 1 pixel per tile, the user will just see the grid
                scale = Math.max(scale, 16.0f);
                scale = Math.min(scale, 100.0f);

                //update the origin point so that we zoom into the mouse position
                Vector2 end = screenToWorld(x, y);

                Vector2 worldDelta = start.sub(end); // I miss c++ operator overloading ;_;

                // Can't use the worldToScreen() method here because delta is a vector, not a point.
                // The method adds the origin coordinates to it, which we don't want.
                originX -= worldDelta.x * scale;
                originY -= worldDelta.y * scale;

                return true;
            }
        };

        addListener(inputListener);
    }

    public void centerLevel() {
        Vector2 screenMiddle = new Vector2(getWidth()/ 2, getHeight() / 2);
        Vector2 middleTileInScreen = worldToScreen(editorLevel.width / 2, editorLevel.height / 2);

        originX = screenMiddle.x - middleTileInScreen.x;
        originY = screenMiddle.y - middleTileInScreen.y;
    }

    // These methods are used to perform coordinate conversions. Screen coordinates originate from the bottom
    // left corner of the screen, up is +y, right is +x. World coordinates originate from the bottom left
    // corner of the the tilemap, up is +y, right is +x.
    // world coordinates must be converted to screen for rendering, and then mouse input must be converted
    // from screen coordinates to world coordinates.
    public Vector2 screenToWorld(Vector2 screenCoord) {
        return screenToWorld(screenCoord.x, screenCoord.y);
    }

    public Vector2 screenToWorld(float x, float y) {
        return new Vector2((x - originX) / scale, (y - originY) / scale);
    }

    public Vector2 worldToScreen(Vector2 worldCoord) {
        return worldToScreen(worldCoord.x, worldCoord.y);
    }

    public Vector2 worldToScreen(float x, float y) {
        return new Vector2(x * scale + originX, y * scale + originY);
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch,parentAlpha);

        drawTiles(batch);
        drawActors(batch);

        //The batch must be shut off before rendering the grid because batches don't
        //play well with shape renderers
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        drawGrid();

        //Reactivate the batch.
        batch.begin();
    }

    protected float clamp(float val, float low, float high) {
        return Math.max(Math.min(val, low), high);
    }

    protected void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(gridColor);

        //Convert the edges of the tilemap into screen space coordinates
        float leftX   = originX;
        float rightX  = originX + editorLevel.width * scale + 1;
        float bottomY = originY;
        float topY    = originY + editorLevel.height * scale + 1;

        for(float x = leftX; x <= rightX; x += scale) {
            shapeRenderer.line(x, bottomY, x, topY);
        }

        for(float y = bottomY; y <= topY; y += scale) {
            shapeRenderer.line(leftX, y, rightX, y);
        }

        shapeRenderer.end();
    }

    /**
     * Draws the tiles of the level attached to the widget
     * @param batch The sprite batch to use for rendering
     */
    protected void drawTiles(Batch batch) {
        //Calculate what tile position the bottom-left and top-right corners
        //of the screen are in. This is so that only tiles in view are drawn
        Vector2 bottomLeft = screenToWorld(0.0f, 0.0f);
        Vector2 topRight = screenToWorld(getWidth(), getHeight());

        //If the two positions are outside the level's tile map, clamp them
        bottomLeft.x = clamp(bottomLeft.x, 0, editorLevel.width);
        bottomLeft.y = clamp(bottomLeft.y, 0, editorLevel.height);
        topRight.x = clamp(topRight.x, 0, editorLevel.width);
        topRight.y = clamp(topRight.y, 0, editorLevel.height);

        for(int yi = (int)bottomLeft.y; yi < (int)topRight.y; yi++) {
            for(int xi = (int)bottomLeft.x; xi < (int)topRight.x; xi++) {
                int tileNumber = editorLevel.map[xi][yi];

                if(tileNumber != EditorLevel.NONE) {
                    Vector2 tileScreenPos = worldToScreen(xi, yi);

                    Texture tex = tiles.get(tileNumber);

                    batch.draw(tex, tileScreenPos.x, tileScreenPos.y, scale, scale);
                }
            }
        }
    }

    /**
     * Draws all of the actors listed in the attached level
     * @param batch Rendering Batch to use
     */
    protected void drawActors(Batch batch) {
        for(EditorActor a : editorLevel.actors) {
            Vector2 actorScreenPos = worldToScreen(a.x, a.y);

            batch.draw(actorTextures.get(a.actorId), actorScreenPos.x, actorScreenPos.y, scale, scale);
        }
    }

    /**
     * Placed screen's current selection at the specified location
     * @param x X coordinate
     * @param y Y coordinate
     */
    protected void placeCurrentSelection(float x, float y) {
        if (!coordinatesInMap(x, y))
            return;

        AssetSelectorWindow selWin = screen.getSelectorWindow();

        //Placement rules are different depending on whether the object being placed
        //is an actor or a tile
        if (selWin.getCurrentTabTitle().equals("Tiles")) {
            int index = selWin.getSelectionIndex();
            editorLevel.map[(int)x][(int)y] = index;
        }
        else if (selWin.getCurrentTabTitle().equals("Actors")) {
            int index = selWin.getSelectionIndex();
            editorLevel.placeActor(x, y, index);
        }
    }

    /**
     * returns whether a coordinate is inside the level's playable area
     * @param x X Coordinate
     * @param y Y Coordinate
     * @return Whether the coordinates are in the level
     */
    public boolean coordinatesInMap(float x, float y) {
        return x >= 0 && x < editorLevel.width && y >= 0 && y < editorLevel.height;
    }
}

