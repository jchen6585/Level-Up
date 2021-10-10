package com.mygdx.nextlevel;


import com.badlogic.gdx.graphics.Texture;


public class Tile {
    private Texture texture;
    private int     flags;

    final short SOLID_BIT = 1 << 0;
    final short BREAK_BIT = 1 << 1;

    public Tile(String fileName, boolean solid) {
        texture = new Texture(fileName);

        if(solid) {
            flags |= SOLID_BIT;
        }
    }

    boolean isSolid() { return (flags & SOLID_BIT) == SOLID_BIT; }

}
