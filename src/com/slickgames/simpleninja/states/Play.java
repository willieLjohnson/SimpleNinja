package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
import com.slickgames.simpleninja.handlers.*;
import com.slickgames.simpleninja.main.Game;

import java.util.Random;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;

public class Play extends GameState {


    public Pixmap backgroundForPause;
    public Player player;
    public boolean enemyAi = true;
    ParticleEffect runningDust, bloodSplat;
    private World world;
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;
    private MyContactListener cl;
    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;
    private Array<Crystal> crystals;
    private int currentAttack;
    private long lastAttack;
    private boolean swinging;
    private int jump;
    private float wallRun = 0;
    private int swingSpeed;
    private float tileSize;
    private Enemy enemy;
    private ShapeRenderer sr;
    private boolean ran;
    private boolean attacked;
    private int rotTick;
    private Array<Enemy> enemies;
    private Array<ParticleEffect> bloodParts;
    private boolean pauseOnUpdate;

    public Play(GameStateManager gsm) {
        super(gsm);
        Gdx.input.setInputProcessor(new MyInputProcessor());

        // set up box2d stuff
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        createPlayer();

        // create enemy
        enemies = new Array<Enemy>();
        createEnemy(2);

        // create tiles
        createTiles();

        // create crystals
        createCrystals();

        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

        // set up hud
        sr = new ShapeRenderer();

        // set up particles
        runningDust = new ParticleEffect();
        runningDust.load(Gdx.files.internal("res/particles/running_dust"), Gdx.files.internal("res/particles"));
        runningDust.start();
        bloodSplat = new ParticleEffect();
        bloodSplat.load(Gdx.files.internal("res/particles/blood_splat"), Gdx.files.internal("res/particles"));
        bloodSplat.start();
        bloodParts = new Array<ParticleEffect>();
    }

    @Override
    public void handleInput() {

        // pause game
        if (MyInput.isPressed(MyInput.RESET)) {
            pauseOnUpdate = true;
        }

        // player movement
        if (!swinging)
            if (MyInput.isDown(MyInput.LEFT)) {
                player.setDir(-1);

                if (Math.abs(player.getBody().getLinearVelocity().x) < player.getMaxSpeed()) {
                    player.getBody().applyForceToCenter(cl.isPlayerOnGround() ? -player.getMaxSpeed()*8 : -player.getMaxSpeed(), 0, true);
                }
                if (cl.isPlayerOnGround()) {
                    if (!player.isRunning()) {
                        player.toggleAnimation("run");
                    }
                }
            } else if (MyInput.isDown(MyInput.RIGHT)) {
                player.setDir(1);

                if (Math.abs(player.getBody().getLinearVelocity().x) < player.getMaxSpeed()) {

                    player.getBody().applyForceToCenter(cl.isPlayerOnGround() ? player.getMaxSpeed()*8 : player.getMaxSpeed(), 0, true);
                }
                if (cl.isPlayerOnGround()) {
                    if (!player.isRunning() && cl.isPlayerOnGround()) {
                        player.toggleAnimation("run");
                    }
                }
            } else if (!player.isIdle() && !player.isAttacking())
                player.toggleAnimation("idle");

        // atttack
        if (MyInput.isPressed(MyInput.ATTACK) && !swinging && cl.isPlayerOnGround()) {
            swinging = true;


            if (currentAttack >= 16) {
                currentAttack = 0;
                player.setAttacking(false);
                player.damage(player.health / 2);

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
            player.getBody().applyLinearImpulse(
                    Math.abs(player.getBody().getLinearVelocity().x) > 1 ? 0f : player.getDir() * 6f, 0f, 0f, 0f, true);
            if (cl.isEnemyHit()) {
                attacked = true;
                enemy.damage(currentAttack / 2);
                if (bloodParts.size < 3) {
                    bloodSplat = new ParticleEffect();
                    bloodSplat.load(Gdx.files.internal("res/particles/blood_splat"), Gdx.files.internal("res/particles"));
                    bloodSplat.start();
                    bloodParts.add(bloodSplat);
                }
            }
        }

        if ((player.getAnimation().

                getCurrentFrame()

                == currentAttack) && player.isAttacking())

        {
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
        // player jump
        if (cl.wallRun() && jump >= 1)

        {
            if (MyInput.isDown(MyInput.JUMP) && (player.getBody().getLinearVelocity().y < .1f || wallRun == 0)) {
                player.getBody().applyLinearImpulse(1.5f * player.getDir(), wallRun == 0 ? 4.5f - player.getBody().getLinearVelocity().y : 4.5f - wallRun, 0, 0,
                        true);
                wallRun += (wallRun >= 4.5 ? 0 : .5f);
            }
        } else

        {
            wallRun = 0;
        }

        if (cl.isPlayerOnGround())

        {
            if (player.getBody().getLinearVelocity().y == 0) {
                jump = 0;
            }
            if (Math.abs(player.getBody().getLinearVelocity().x) > 1 && !player.isRunning()) {
                player.getBody().applyForceToCenter(
                        player.getBody().getLinearVelocity().x < 0 ? player.getMaxSpeed() * 8 : -player.getMaxSpeed() * 8, 0,
                        true);
            }
        } else

        {
            swinging = false;
            if (!player.isJumping())
                player.toggleAnimation("jump");
        }

        if (MyInput.isPressed(MyInput.JUMP) && jump < 1)

        {
            player.getBody().applyLinearImpulse(0, 3.5f - player.getBody().getLinearVelocity().y, 0, 0, true);
            jump += 1;
        }

    }

    @Override
    public void update(float dt) {
        if (pauseOnUpdate) {
            gsm.setState(GameStateManager.PAUSE);
            pauseOnUpdate = false;
        }
        // check input
        handleInput();

        // handle enemies
//        for (Enemy e : enemies) {
//            e.update(dt);
//            e.seek(player.getBody(), world, cl);
//        }
        enemy.update(dt);
        if (enemyAi)
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
        Random rand;
        rand = new Random();
        if (cl.isPlayerOnGround())
            runningDust.getEmitters().first().setPosition(player.getPosition().x * PPM - player.getWidth() / 10, player.getPosition().y * PPM - player.getHeight() / 2 + rand.nextInt(5));
        runningDust.update(Gdx.graphics.getDeltaTime());
        for (ParticleEffect p : bloodParts) {
            p.getEmitters().first().setPosition(enemy.getPosition().x * PPM - enemy.getWidth() / 10, enemy.getPosition().y * PPM - enemy.getHeight() / 4);
            p.update(Gdx.graphics.getDeltaTime());
        }
    }

    public void render() {
        if (player.getDir() == -1)
            player.getAnimation().getFrame().flip(!player.getAnimation().getFrame().isFlipX(), false);
        else
            player.getAnimation().getFrame().flip(player.getAnimation().getFrame().isFlipX(), false);

        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Screen shake (probalby going to be used for devastating attacks)
        if (attacked && rotTick < 2) {
            cam.translate(1f, 1.5f);
            rotTick++;
        } else if (rotTick > 0) {
//            cam.translate(2f,0);
            rotTick--;
            attacked = false;
        }

        // set cam to follow player
        cam.position.set((player.getPosition().x * PPM + Game.V_WIDTH / 4) + player.getBody().getLinearVelocity().x, (player.getPosition().y * PPM) + player.getBody().getLinearVelocity().y, 0);
        b2dCam.position.set(cam.position.x / PPM, cam.position.y / PPM, 0);
        cam.update();
        b2dCam.update();

        // draw tile map
        tmr.setView(cam);
        tmr.render();

        // draw entities
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);
//        for (Enemy e : enemies) {
//            e.render(sb);
//        }
        enemy.render(sb);


        // draw crytals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }


        if (gsm.debug) {
            b2dr.render(world, b2dCam.combined);
            sr.setProjectionMatrix(b2dCam.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.line(enemy.getVectors("p1"), enemy.getVectors("p2"));
            sr.line(enemy.getVectors("c"), enemy.getVectors("n"));
            sr.end();
        }

        //particles
        sb.begin();
        if (player.isRunning()) {
            if (runningDust.isComplete() && player.isRunning())
                runningDust.reset();
            runningDust.draw(sb);
            ran = true;
        } else if (ran && Math.abs(player.getBody().getLinearVelocity().x) >= .5) {
            runningDust.draw(sb);
            if (runningDust.isComplete()) {
                ran = false;
            }
        }
        for (ParticleEffect p : bloodParts) {
            if (p.isComplete()) {
                p.dispose();
                bloodParts.removeValue(p, true);
            } else {
                p.draw(sb);
            }
        }
        sb.end();
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
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_GROUND | B2DVars.BIT_WALL | B2DVars.BIT_CYSTAL
                | B2DVars.BIT_ENEMY | B2DVars.BIT_VISIONCONE;
        body.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_GROUND;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        // create hand sensor
        shape.setAsBox(10 / PPM, 4 / PPM, new Vector2(0, -5 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_WALL;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("hand");

        // create attack range
        shape.setAsBox(30 / PPM, 8 / PPM, new Vector2(0, -5 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("attackRange");

        // create player
        player = new Player(body);

        body.setUserData(player);
    }

    private void createEnemy(int numOfEnems) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

//        for (int i = 0; i < numOfEnems; i++) {
        // create enemy
        bdef.position.set(500 / PPM, 800 / PPM);
        bdef.type = BodyType.DynamicBody;
        // bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(6 / PPM, 10 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_WALL | B2DVars.BIT_GROUND | B2DVars.BIT_CYSTAL
                | B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("enemy");

        // creat foot sensor
        shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_EDGE;
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
        body.createFixture(fdef).setUserData("range");

        // create wall/player collision sensor
        shape.setAsBox(7 / PPM, 8 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_WALL | B2DVars.BIT_GROUND | B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("wallcollision");

        // create enemy
        enemy = new Enemy(body);


        body.setUserData(enemy);
        enemies.add(enemy);
//        }
    }

    private void createTiles() {
        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/sliced.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);
        tileSize = (int) tileMap.getProperties().get("tilewidth");
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        MapLayer layer;

        layer = tileMap.getLayers().get("walls");
        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;
            float width = (float) mo.getProperties().get("width") / PPM;
            float height = (float) mo.getProperties().get("height") / PPM;

            ChainShape cshape = new ChainShape();
            Vector2[] v = new Vector2[5];
            v[0] = new Vector2(0, 0);
            v[1] = new Vector2(width, 0);
            v[2] = new Vector2(width, height);
            v[3] = new Vector2(0, height);
            v[4] = new Vector2(0, 0);

            cshape.createChain(v);
            bdef.position.set(x, y);
            fdef.shape = cshape;
            fdef.filter.categoryBits = B2DVars.BIT_WALL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
            // fdef.friction = 1.5f;
            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("wall");

        }

        layer = tileMap.getLayers().get("floors");
        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;
            float width = (float) mo.getProperties().get("width") / PPM;
            float height = (float) mo.getProperties().get("height") / PPM;
            ChainShape cshape = new ChainShape();
            Vector2[] v = new Vector2[5];
            v[0] = new Vector2(0, 0);
            v[1] = new Vector2(width, 0);
            v[2] = new Vector2(width, height);
            v[3] = new Vector2(0, height);
            v[4] = new Vector2(0, 0);

            cshape.createChain(v);
            bdef.position.set(x, y);
            fdef.shape = cshape;
            fdef.filter.categoryBits = B2DVars.BIT_GROUND;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
            // fdef.friction = 1.5f;
            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("ground");

        }

        layer = tileMap.getLayers().get("edges");
        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;
            float width = (float) mo.getProperties().get("width") / PPM;
            float height = (float) mo.getProperties().get("height") / PPM;

            ChainShape cshape = new ChainShape();
            Vector2[] v = new Vector2[5];
            v[0] = new Vector2(0, 0);
            v[1] = new Vector2(width, 0);
            v[2] = new Vector2(width, height);
            v[3] = new Vector2(0, height);
            v[4] = new Vector2(0, 0);

            cshape.createChain(v);
            bdef.position.set(x, y);
            fdef.shape = cshape;
            fdef.filter.categoryBits = B2DVars.BIT_EDGE;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
            // fdef.friction = 1.5f;
            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("edge");

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

}
