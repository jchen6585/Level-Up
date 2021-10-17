package com.mygdx.nextlevel.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.nextlevel.screens.GameScreen;

public class Player extends Actor {
    Vector2 spawnpoint;
    int lives;
    boolean powerUp;
    boolean invulnerable = false;

    public Player(Texture texture, World world, Vector2 position, float density, float restitution) {
        this.world = world;
        this.sprite = new Sprite(texture);
        this.sprite.setSize(64.0F, 64.0F);
        this.lives = 3;
        this.powerUp = false;
        this.invulnerable = false;
        super.setPosition(position.x, position.y);
        this.spawnpoint = this.worldSpawn;
        super.setBody(BodyDef.BodyType.DynamicBody);
        setShape();
        setFixture(density, restitution);
    }

    private void setShape() {
        this.shape = new PolygonShape();
        this.shape.setAsBox(this.sprite.getWidth()/2.0F/PIXELS_TO_METERS, this.sprite.getHeight()/2.0F/PIXELS_TO_METERS);

        super.setEdgeShape(); //Only needed if want collisions
//        System.out.println("Player Vertices");
//        Array<Vector2> verts = new Array<Vector2>();
//        Vector2 tmp = new Vector2();
//        for (int i = 0; i < this.shape.getVertexCount(); i++) {
//            // fill tmp with the vertex
//            this.shape.getVertex(i, tmp);
//            verts.add(new Vector2(tmp));
//            System.out.println(tmp.toString());
//        }
    }

    private void setFixture(float density, float restitution) {
        this.fixtureDef = new FixtureDef();
        this.fixtureDef.density = density;
        this.fixtureDef.restitution = restitution;
        this.fixtureDef.filter.categoryBits = PHYSICS_ENTITY;
        this.fixtureDef.filter.maskBits = WORLD_ENTITY | PHYSICS_ENTITY | BLOCK_ENTITY;

//        EdgeShape feet = new EdgeShape();
//        feet.set(-this.sprite.getWidth()/2.0F/PIXELS_TO_METERS, -this.sprite.getHeight()/2.0F/PIXELS_TO_METERS, this.sprite.getWidth()/2.0F/PIXELS_TO_METERS, -this.sprite.getHeight()/2.0F/PIXELS_TO_METERS);
//        this.fixtureDef.filter.categoryBits = PHYSICS_ENTITY;
//        this.fixtureDef.shape = feet;
//        this.body.createFixture(this.fixtureDef);
//        EdgeShape head = new EdgeShape();
//        head.set(-this.sprite.getWidth()/2.0F/PIXELS_TO_METERS, this.sprite.getHeight()/2.0F/PIXELS_TO_METERS, this.sprite.getWidth()/2.0F/PIXELS_TO_METERS, this.sprite.getHeight()/2.0F/PIXELS_TO_METERS);
//        this.fixtureDef.shape = head;

//        this.body.createFixture(this.fixtureDef);
        this.fixtureDef.shape = this.shape;
        this.fixtureDef.isSensor = false;
        this.body.createFixture(this.fixtureDef);

        this.fixtureDef.shape.dispose();
        this.edgeShape.set( (-w / 2.0F + tolerance/2), -h / 2.0F -  2*tolerance, (w / 2.0F - tolerance/2), -h / 2.0F - 2*tolerance); //Bottom
//        this.edgeShape.set( -w / 2.0F, -h / 2.0F, w / 2.0F, -h / 2.0F ); //Bottom
        super.setContactSide(this.bottom);

        this.edgeShape.set(-w / 2.0F - tolerance, (-h / 2.0F + tolerance/2)+0.1f, -w / 2.0F - tolerance,(h / 2.0F - tolerance/2)-0.1f); //Left
//        this.edgeShape.set(-w / 2.0F, -h / 2.0F, -w / 2.0F ,h / 2.0F ); //Left
        super.setContactSide(this.leftSide);

        this.edgeShape.set( (-w / 2.0F + tolerance/2)+0.1f, (h / 2.0F + 2*tolerance), (w / 2.0F - tolerance/2)-0.1f, (h / 2.0F + 2*tolerance)); //Head
//        this.edgeShape.set( -w / 2.0F , h / 2.0F , w / 2.0F , h / 2.0F); //Head
        super.setContactSide(this.head);

        this.edgeShape.set(w / 2.0F + tolerance, (-h / 2.0F + tolerance/2)+0.1f, w / 2.0F + tolerance, (h / 2.0F - tolerance/2)-0.1f); //Right Side
//        this.edgeShape.set(w / 2.0F, -h / 2.0F, w / 2.0F, h / 2.0F); //Right Side
        super.setContactSide(this.rightSide);
        this.edgeShape.dispose();
    }

    public void setSpawnpoint(Vector2 position) {
        this.spawnpoint = position;
    }

    public Vector2 getSpawnpoint() {
        return this.spawnpoint;
    }

    public int getLives() { return this.lives; }

    public void addLife() {
        this.lives++;
    }

    public void subLife() { this.lives--; }

    public Vector2 getWorldSpawn() { return this.worldSpawn; }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }

    public boolean hasPowerUp() {
        return powerUp;
    }

    public void setPowerUp(boolean set) {
        this.powerUp = set;
    }

    public boolean getsInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}
