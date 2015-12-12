package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.entities.Player;
import com.slickgames.simpleninja.handlers.B2DVars;
import com.slickgames.simpleninja.handlers.GameStateManager;
import com.slickgames.simpleninja.handlers.MyContactListener;
import com.slickgames.simpleninja.handlers.MyInput;
import com.slickgames.simpleninja.main.Game;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;

public class Play extends GameState {

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private MyContactListener cl;

    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private Player player;
    private Array<Crystal> crystals;

    private int currentAttack;
    private long lastAttack;
    private boolean swinging;
    private int doubleJump = 0;
    private int swingSpeed;
    private Enemy enemy;

    private ShapeRenderer sr;

    public Play(GameStateManager gsm) {
        super(gsm);

        // set up box2d stuff
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        createPlayer();

        // create enemy
        createEnemy();

        // create tiles
        createTiles();

        // create crystals
        createCrystals();

        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

        // set up hud
        sr = new ShapeRenderer();
    }

    @Override
    public void handleInput() {
        // reset gameset
        if (MyInput.isPressed(MyInput.RESET)) {
            gsm.setState(GameStateManager.Pause);
        }
        // player jump

        if (MyInput.isPressed(MyInput.JUMP) && doubleJump != 1) {
            if (player.getBody().getLinearVelocity().y <= 1)
                player.getBody().applyLinearImpulse(0, 3f + doubleJump * 2, 0, 0, true);
            doubleJump++;
        } else if (cl.isPlayerOnGround()) {
            doubleJump = 0;
            if (Math.abs(player.getBody().getLinearVelocity().x) > 1 && !player.isRunning()) {
                player.getBody().applyForceToCenter(
                        (player.getBody().getLinearVelocity().x < 0) ? player.MAX_SPEED * 8 : -player.MAX_SPEED * 8, 0,
                        true);
            }

            // player movement
            if (!swinging)
                if (MyInput.isDown(MyInput.LEFT)) {
                    player.setDir(-1);

                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.MAX_SPEED) {
                        player.getBody().applyForceToCenter(-16f, 0, true);
                    }
                    if (!player.isRunning()) {
                        player.toggleAnimation("run");
                    }
                } else if (MyInput.isDown(MyInput.RIGHT)) {
                    player.setDir(1);
                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.MAX_SPEED) {

                        player.getBody().applyForceToCenter(16f, 0, true);
                    }
                    if (!player.isRunning()) {
                        player.toggleAnimation("run");
                    }
                } else {
                    if (!player.isIdle() && !player.isAttacking())
                        player.toggleAnimation("idle");

                }

            // attack
            if (MyInput.isPressed(MyInput.ATTACK) && !swinging) {
                swinging = true;

                if (currentAttack >= 16) {
                    currentAttack = 0;
                    // System.out.println("MAX");
                    player.setAttacking(false);
                }
                if (currentAttack >= 4) {
                    currentAttack += 4;
                    player.getAnimation().setSpeed(1 / (32f + swingSpeed));

                }
                if (!player.isAttacking()) {
                    player.toggleAnimation("attack");
                    currentAttack = 4;
                }
                if (swingSpeed < 16) {
                    swingSpeed += 4;
                }
                System.out.println(32f + swingSpeed);
                player.getBody().applyLinearImpulse(
                        Math.abs(player.getBody().getLinearVelocity().x) > 1 ? 0f : player.getDir() * 6f, 0f, 0f, 0f,
                        true);

            }
            if ((player.getAnimation().getCurrentFrame() == currentAttack) && player.isAttacking()) {
                player.getAnimation().setSpeed(0f);
                if (swinging) {
                    lastAttack = TimeUtils.nanoTime();
                    swinging = false;
                }
                player.attacked = true;
                if (TimeUtils.nanoTime() - lastAttack > 250000000f) {
                    player.setAttacking(false);
                    currentAttack = 0;
                    swingSpeed = 0;
                }

            }
        } else {
            swinging = false;
            if (!player.isJumping())
                player.toggleAnimation("jump");
        }

    }

    @Override
    public void update(float dt) {

        // check input
        handleInput();

        // handle enemies
        enemy.update(dt);
        enemy.seek(player.getBody(), world, cl);

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

        player.playerUpdate(dt, lastAttack);

        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }
    }

    public void render() {
        if (player.getDir() == -1)
            player.getAnimation().getFrame().flip(!player.getAnimation().getFrame().isFlipX(), false);
        else
            player.getAnimation().getFrame().flip(player.getAnimation().getFrame().isFlipX(), false);
        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set cam to follow player
        cam.position.set(player.getPosition().x * PPM + Game.V_WIDTH / 4, player.getPosition().y * PPM, 0);
        b2dCam.position.set(cam.position.x / PPM, cam.position.y / PPM, 0);
        cam.update();
        b2dCam.update();
        // draw tile map
        tmr.setView(cam);
        tmr.render();

        // draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);
        enemy.render(sb);

        // draw crytals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        if (debug) {
            b2dr.render(world, b2dCam.combined);
            sr.setProjectionMatrix(b2dCam.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.line(enemy.getVectors("p1"), enemy.getVectors("p2"));
            sr.line(enemy.getVectors("c"), enemy.getVectors("n"));
            sr.end();
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
        bdef.position.set(0 / PPM, 600 / PPM);
        bdef.type = BodyType.DynamicBody;
        // bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(6 / PPM, 10 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_CYSTAL | B2DVars.BIT_ENEMY | B2DVars.BIT_VISIONCONE;
        body.createFixture(fdef).setUserData("player");

        // creat foot sensor
        shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        // create player
        player = new Player(body);

        body.setUserData(player);
    }

    private void createEnemy() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // create enemy
        bdef.position.set(1200 / PPM, 800 / PPM);
        bdef.type = BodyType.DynamicBody;
        // bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(6 / PPM, 10 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_CYSTAL | B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("enemy");

        // creat foot sensor
        shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("Efoot");

        // creat vision sensors
        PolygonShape cs = new PolygonShape();
        Vector2[] v = new Vector2[4];
        v[0] = new Vector2(0 / PPM, 0 / PPM);
        v[1] = new Vector2(200 / PPM, 100 / PPM);
        v[2] = new Vector2(0 / PPM, 0 / PPM);
        v[3] = new Vector2(200 / PPM, -100 / PPM);
        cs.set(v);

        fdef.friction = 0;
        fdef.shape = cs;
        fdef.filter.categoryBits = B2DVars.BIT_VISIONCONE;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("visionRight");
        v[0] = new Vector2(0 / PPM, 0 / PPM);
        v[1] = new Vector2(-200 / PPM, 100 / PPM);
        v[2] = new Vector2(0 / PPM, 0 / PPM);
        v[3] = new Vector2(-200 / PPM, -100 / PPM);
        cs.set(v);
        fdef.friction = 0;
        fdef.shape = cs;
        fdef.filter.categoryBits = B2DVars.BIT_VISIONCONE;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("visionLeft");

        // create range of activity
        CircleShape r = new CircleShape();
        r.setRadius(150 / PPM);
        fdef.shape = r;
        fdef.filter.categoryBits = B2DVars.BIT_VISIONCONE;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("enemy");

        // create player
        enemy = new Enemy(body);

        body.setUserData(enemy);
    }

    private void createTiles() {
        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/sliced.tmx");
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
            fdef.filter.categoryBits = B2DVars.BIT_GROUND;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
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

    public MyContactListener getCl() {
        return cl;
    }
}
