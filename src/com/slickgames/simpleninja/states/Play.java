package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.entities.Crystal;
import com.slickgames.simpleninja.entities.HUD;
import com.slickgames.simpleninja.entities.Player;
import com.slickgames.simpleninja.handlers.B2DVars;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyContactListener;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.main.Game;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;

public class Play extends GameState {

    private boolean debug = true;

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private MyContactListener cl;

    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private Player player;
    private Array<Crystal> crystals;

    private HUD hud;
    private int currentAttack;
    private long lastAttack;


    public Play(GameStateManager gsm) {
        super(gsm);

        // set up box2d stuff
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        createPlayer();

        // create tiles
        createTiles();

        // create crystals
        createCrystals();

        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

        // set up hud
        hud = new HUD(player);

    }

    @Override
    public void handleInput() {
        // reset game
        if (MyInput.isPressed(MyInput.RESET)) {
            gsm.setState(GameStateManager.PLAY);
        }
        // player jump
        if (cl.isPlayerOnGround()) {

            if (MyInput.isPressed(MyInput.JUMP)) {

                player.getBody().applyForceToCenter(player.getBody().getLinearVelocity().x > 1f ? 0 : 100, 250, true);
            }
            // player movement
            if (!player.isAttacking())
                if (MyInput.isDown(MyInput.LEFT)) {
                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.MAX_SPEED) {
                        player.getBody().applyForceToCenter(-5f, 0, true);

                    }
                    if (!player.isRunning()) {
                        player.toggleAnimation("run");
                    }
                } else if (MyInput.isDown(MyInput.RIGHT)) {
                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.MAX_SPEED) {

                        player.getBody().applyForceToCenter(5f, 0, true);
                    }
                    if (!player.isRunning()) {
                        player.toggleAnimation("run");
                    }
                } else if (Math.abs(player.getBody().getLinearVelocity().x) > 1) {
                    player.getBody().applyForceToCenter(
                            (player.getBody().getLinearVelocity().x < 0) ? player.MAX_SPEED * 2 : -player.MAX_SPEED * 2,
                            0, true);
                } else {
                    if (!player.isIdle() && !player.isAttacking())
                        player.toggleAnimation("idle");

                }

            // attack
            if (MyInput.isPressed(MyInput.ATTACK)) {

                if (currentAttack >= 16) {
                    currentAttack = 4;

                    System.out.println("MAX");
                }
                if (currentAttack >= 4) {
                    currentAttack += 4;
                    player.getAnimation().setSpeed(1 / (18f + currentAttack));
                }
                if (!player.isAttacking()) {
                    player.toggleAnimation("attack");
                    currentAttack = 4;
                }
                lastAttack = TimeUtils.nanoTime();
            }
            if ((player.getAnimation().getCurrentFrame() == currentAttack) && player.isAttacking()) {
                if (TimeUtils.nanoTime() - lastAttack > 30000000f) {
                    player.setAttacking(false);
                    currentAttack = 0;
                } else {
                    player.getAnimation().setSpeed(0);
                }
            }


        } else {
            if (!player.isJumping())
                player.toggleAnimation("jump");
        }

    }

    @Override
    public void update(float dt) {

        // check input
        handleInput();

        // update box2d
        world.step(dt, 6, 2);

        // remove crystals
        Array<Body> bodies = cl.getBodiesToRemove();
        for (Body b : bodies) {
            crystals.removeValue((Crystal) b.getUserData(), true);
            world.destroyBody(b);
            player.collectCrystal();
        }
        bodies.clear();

        player.udpate(dt);

        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).udpate(dt);
        }
    }

    public void render() {
        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set cam to follow player
        cam.position.set(player.getPosition().x * PPM + Game.V_WIDTH / 4, Game.V_HEIGHT / 2, 0);
        b2dCam.position.set(player.getPosition().x + Game.V_WIDTH / 4 / PPM, Game.V_HEIGHT / 2 / PPM, 0);
        cam.update();
        b2dCam.update();
        // draw tile map
        tmr.setView(cam);
        tmr.render();

        // draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        // draw crytals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        // draw hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        // draw box2d world
        if (debug) {
            b2dr.render(world, b2dCam.combined);
        }

    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // create player
        bdef.position.set(100 / PPM, 200 / PPM);
        bdef.type = BodyType.DynamicBody;
        // bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(6 / PPM, 8 / PPM, new Vector2(0, -10 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CYSTAL;
        body.createFixture(fdef).setUserData("player");

        // creat foot sensor
        shape.setAsBox(6 / PPM, 2 / PPM, new Vector2(0, -20 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        // create player
        player = new Player(body);

        body.setUserData(player);
    }

    private void createTiles() {
        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/dark.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);
        tileSize = (int) tileMap.getProperties().get("tilewidth");
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        MapLayer layer;
        layer = tileMap.getLayers().get("collision");

        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;
            float width = (float) mo.getProperties().get("width") / 2 / PPM;
            float height = (float) mo.getProperties().get("height") / 2 / PPM;

            PolygonShape pshape = new PolygonShape();

            pshape.setAsBox(width, height);
            bdef.position.set(x + width, y + height);
            fdef.shape = pshape;
            fdef.filter.categoryBits = B2DVars.BIT_RED;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;
            // fdef.friction = 1.5f;
            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("ground");

        }
    }

    private void createCrystals() {
        crystals = new Array<>();

        MapLayer layer = tileMap.getLayers().get("crystals");
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;

            bdef.position.set(x, y);
            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("crystal");

            Crystal c = new Crystal(body);
            crystals.add(c);

            body.setUserData(c);
        }
    }

    private void switchBlock() {

        Filter filter = player.getBody().getFixtureList().first().getFilterData();
        short bits = filter.maskBits;

        // switch to next color
        // red > green > blue > red
        if ((bits & B2DVars.BIT_RED) != 0) {
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        } else if ((bits & B2DVars.BIT_GREEN) != 0) {
            bits &= ~B2DVars.BIT_GREEN;
            bits |= B2DVars.BIT_BLUE;
        } else if ((bits & B2DVars.BIT_BLUE) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        // set new mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        // set for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }
}
