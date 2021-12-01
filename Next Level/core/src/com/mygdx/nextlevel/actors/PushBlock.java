package com.mygdx.nextlevel.actors;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.nextlevel.BoxCollider;
import com.mygdx.nextlevel.CollisionGroups;
import com.mygdx.nextlevel.TileMap;
import com.mygdx.nextlevel.screens.GameScreen2;

import javax.swing.*;

public class PushBlock extends Actor2 {
    protected BoxCollider collider;
    protected TileMap tm;

    public PushBlock(GameScreen2 screen, TileMap tm) {
        super(screen, 0f, tm.getMapHeight()/2f, 1, tm.getMapHeight());
        this.tm = tm;

        collider = new BoxCollider(
                this,
                new Vector2(0f, tm.getMapHeight()/2f),
                new Vector2(1, tm.getMapHeight()),
                tm.getAutoScroll(),
                (short) (CollisionGroups.ACTOR | CollisionGroups.WORLD | CollisionGroups.FIRE | CollisionGroups.ITEM),
                CollisionGroups.WORLD
        );

        collider.setKinematic();
    }

    public void update(float delta) {
        if (tm.getMapWidth() - collider.getPosition().x <= tm.getScreenWidth()) {
            collider.setVelocity(new Vector2(0, 0));
            collider.setStatic();
        } else if (tm.getAutoScroll()) {
            collider.setVelocity(new Vector2(0.97f, 0f)); //Velocity to move with the screen rate
        }
    }

    public void updatePosition(Vector2 vec) {
////        Vector2 v = new Vector2();
////        v.x = vec.x-1f;
//        collider.setPosition(vec);
////        collider.setPosition(v);
        setPosition(collider.getPosition());
    }

    public BoxCollider getCollider() { return collider; }

    public void createBoxCollider(float x) {
        collider = new BoxCollider(
                this,
                new Vector2(x, tm.getMapHeight()/2f),
                new Vector2(1, tm.getMapHeight()),
                true,
                (short) (CollisionGroups.ACTOR | CollisionGroups.WORLD | CollisionGroups.FIRE | CollisionGroups.ITEM),
                CollisionGroups.WORLD
        );
        collider.setKinematic();
    }
}
