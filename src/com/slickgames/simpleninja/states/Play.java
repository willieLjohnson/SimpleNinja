package com.slickgames.simpleninja.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.slickgames.simpleninja.entities.Crystal;
import com.slickgames.simpleninja.entities.Enemy;
import com.slickgames.simpleninja.entities.Player;
import com.slickgames.simpleninja.entities.Projectile;
import com.slickgames.simpleninja.handlers.*;
import com.slickgames.simpleninja.handlers.postprocessing.PostProcessor;
import com.slickgames.simpleninja.handlers.postprocessing.ShaderLoader;
import com.slickgames.simpleninja.handlers.postprocessing.effects.Bloom;
import com.slickgames.simpleninja.main.Game;

import java.util.Random;

import static com.slickgames.simpleninja.handlers.B2DVars.PPM;

public class Play extends GameState {
    public final PostProcessor postProcessor;
    public Player player;
    public boolean enemyAi = true;
    public World world;
    public MyContactListener cl;
    public Bloom bloom;
    public Bloom sword;
    public Array<Enemy> enemies;
    public boolean ignorePlayer = false;
    public Array<Projectile> projectiles;
    private ParticleEffect runningDust, bloodSplat;
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;
    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;
    private Array<Crystal> crystals;
    private int currentAttack;
    private float lastAttack;
    private boolean swinging;
    private int jump;
    private float wallRun = 0;
    private int swingSpeed;
    private float tileSize;
    private ShapeRenderer sr;
    private boolean ran;
    private boolean attacked;
    private int rotTick;
    public Array<ParticleEffect> bloodParts;
    private boolean pauseOnUpdate;
    private Sound hita = game.getAssetManager().get("res/sfx/hit/hit3.wav");
    private Sound step = game.getAssetManager().get("res/sfx/simple_step1.wav");

    public float currentTime = TimeUtils.nanoTime();
    private int airAttack;
    private float lastStep;

    public Play(GameStateManager gsm) {
        super(gsm);
        Gdx.input.setInputProcessor(new MyInputProcessor());

        // set up box2d stuff

        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener(this);
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        createPlayer();

        // create enemy
        enemies = new Array<Enemy>();
        createEnemy(1);

        // create tiles
        createTiles();

        // create crystals
        createCrystals();

        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
        // cam.zoom +=.25f;
        // set up hud & debug
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);

        // set up particles
        runningDust = new ParticleEffect();
        runningDust.load(Gdx.files.internal("res/particles/running_dust"), Gdx.files.internal("res/particles"));
        runningDust.start();
        bloodParts = new Array<ParticleEffect>();

        // shaders
        ShaderLoader.BasePath = "res/shaders/";
        postProcessor = new PostProcessor(false, false, true);
        postProcessor.setClearColor(40 / 255f, 38 / 255f, 33 / 255f, 1f);
        bloom = new Bloom((int) (Gdx.graphics.getWidth() * .05f), (int) (Gdx.graphics.getHeight() * .05f));
        sword = new Bloom((int) (Gdx.graphics.getWidth() * .5f), (int) (Gdx.graphics.getHeight() * .5f));

        // makes the swords glow their actual color(ie. not white)
        bloom.setBloomIntesity(2f);
        bloom.setThreshold(.85f);
        sword.setThreshold(.85f);

        postProcessor.addEffect(bloom);
        postProcessor.addEffect(sword);

        // projectiles
        projectiles = new Array<>();
    }

    @Override
    public void handleInput() {

        // pause game
        if (MyInput.isPressed(MyInput.RESET)) {
            pauseOnUpdate = true;
        }

        if (MyInput.isPressed(MyInput.SHOOT) && !player.throwing && player.ammo >= 0) {
            player.toggleAnimation("throw");
        }

        if ((player.throwing && player.getAnimation().getCurrentFrame() == 2)) {
            player.throwing = false;
            createProjectile();
            player.ammo--;
        }

        if (!MyInput.isDown(MyInput.BLOCK)) {
            player.blocking = false;
            // movement
            if (!attacked)
                if (MyInput.isDown(MyInput.LEFT)) {
                    player.setDir(-1);
                    if ((currentTime - (player.getBody().getLinearVelocity().x) * 20000000) - lastStep > 270000000 && cl.isPlayerOnGround()) {
                        lastStep = currentTime;
                        step.play(.1f, ((float) Math.random() * 2) + 2, 1);
                    }
                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.getMaxSpeed()) {
                        player.getBody().applyForceToCenter(
                                cl.isPlayerOnGround() ? -player.getMaxSpeed() * 8 : -player.getMaxSpeed(), 0, true);
                    }
                    if (cl.isPlayerOnGround()) {
                        if (!player.isRunning() && !player.attacking) {
                            player.toggleAnimation("run");
                        }
                    }
                } else if (MyInput.isDown(MyInput.RIGHT)) {
                    player.setDir(1);
                    if ((currentTime + (player.getBody().getLinearVelocity().x) * 20000000) - lastStep > 270000000 && cl.isPlayerOnGround()) {
                        lastStep = currentTime;
                        step.play(.1f, ((float) Math.random() * 2) + 2, 1);
                    }
                    if (Math.abs(player.getBody().getLinearVelocity().x) < player.getMaxSpeed()) {

                        player.getBody().applyForceToCenter(
                                cl.isPlayerOnGround() ? player.getMaxSpeed() * 8 : player.getMaxSpeed(), 0, true);
                    }
                    if (cl.isPlayerOnGround()) {
                        if (!player.isRunning() && !player.attacking) {
                            player.toggleAnimation("run");
                        }
                    }
                } else if (!player.isIdle() && !player.isAttacking() && !player.throwing) {
                    player.toggleAnimation("idle");
                    swinging = false;
                }

            // atttack
            if (MyInput.isPressed(MyInput.ATTACK) && !swinging) {
                if (cl.isPlayerOnGround()) {
                    hita.play();
                    swinging = true;
                    if (player.stamina > 0)
                        player.stamina -= 25;
                    if (currentAttack >= 16) {
                        currentAttack = 0;
                        player.setAttacking(false);
                        if (player.stamina <= 0 && player.health > player.getMaxHealth() / 5)
                            player.damage(player.getMaxHealth() / 5);
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
                    if ((MyInput.isDown(MyInput.LEFT) || MyInput.isDown(MyInput.RIGHT)) && player.stamina > 40) {
                        player.getBody().applyLinearImpulse(
                                Math.abs(player.getBody().getLinearVelocity().x) > 3.5f ? 0f : player.getDir() * 4f, 0f, 0f,
                                0f, true);
                        player.stamina -= 40;
                    }
                    for (Enemy e : cl.enemiesHit) {
                        attacked = true;
                        e.damage((currentAttack / 2));
                        e.charge += 100000000f;
                    }
                } else {
                    if (player.stamina > 75) {
                        hita.play();
                        swinging = true;
                        if (player.stamina > 0)
                            player.stamina -= 75;
                        if (!player.isAttacking()) {
                            player.toggleAnimation("attack");
                            currentAttack = 8;
                        }
                    }
                }
            }
        } else {
            if (!player.blocking) {
                player.toggleAnimation("block");
            }
        }
        if ((player.getAnimation().getCurrentFrame() == currentAttack) && player.isAttacking()) {
            player.getAnimation().setSpeed(0f);
            if (swinging) {
                lastAttack = currentTime;
                swinging = false;

                if (player.stamina < 0)
                    player.stamina = 0;
            }
            player.attacked = true;
            if (currentTime - lastAttack > 250000000f) {
                player.setAttacking(false);
                currentAttack = 0;
                swingSpeed = 0;
            }
            if (!cl.isPlayerOnGround()) player.setAttacking(false);
        }
        // player jump
        if (cl.wallRun() && jump >= 1) {
            if (MyInput.isDown(MyInput.JUMP) && (player.getBody().getLinearVelocity().y < .1f || wallRun == 0)) {
                player.getBody().applyLinearImpulse(1.5f * player.getDir(),
                        wallRun == 0 ? 4.5f - player.getBody().getLinearVelocity().y : 4.5f - wallRun, 0, 0, true);
                wallRun += (wallRun >= 4.5 ? 0 : .5f);
            }
        } else {
            wallRun = 0;
        }

        if (cl.isPlayerOnGround()) {
            if (player.getBody().getLinearVelocity().y == 0) {
                jump = 0;
            }
            if (Math.abs(player.getBody().getLinearVelocity().x) > 1 && !player.isRunning()) {
                player.getBody().applyForceToCenter(player.getBody().getLinearVelocity().x < 0
                        ? player.getMaxSpeed() * 8 : -player.getMaxSpeed() * 8, 0, true);
            }
        } else {
            if (!player.isJumping() && !player.throwing && !player.isAttacking())
                player.toggleAnimation("jump");
        }

        if (MyInput.isPressed(MyInput.JUMP) && jump < 1) {
            player.getBody().applyLinearImpulse(0, 3.5f - player.getBody().getLinearVelocity().y, 0, 0, true);
            jump += 1;
            player.attacking = false;
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
        for (Enemy e : enemies) {
            e.update(dt);
            if (enemyAi)
                e.seek(player.getBody(), world);
        }

        // update box2d
        world.step(dt, 6, 2);

        for (Enemy e : cl.enemiesShot) {
            e.damage(5);
        }
        // remove bodies
        Array<Body> bodies = cl.getBodiesToRemove();

        for (Body b : bodies) {
            // crystals
            try {
                if (crystals.contains((Crystal) b.getUserData(), true)) {
                    crystals.removeValue((Crystal) b.getUserData(), true);
                    player.collectCrystal();

                }
            } catch (java.lang.ClassCastException e) {
            }

            // projectiles
            try {
                if (projectiles.contains((Projectile) b.getUserData(), true))
                    projectiles.removeValue((Projectile) b.getUserData(), true);
            } catch (java.lang.ClassCastException e) {
            }

            // enemies
            try {
                if (enemies.contains((Enemy) b.getUserData(), true)) {
                    enemies.removeValue((Enemy) b.getUserData(), true);
                    if (cl.enemiesHit.contains((Enemy) b.getUserData(), true)) {
                        cl.enemiesHit.removeValue((Enemy) b.getUserData(), true);
                    }
//					cl.enemiesShot.clear(); Comment out if you notice problems with "ghost" enemies
                }
            } catch (java.lang.ClassCastException e) {
            }
            world.destroyBody(b);
        }
        bodies.clear();

        player.playerUpdate(dt, lastAttack);

        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }

        // projectile creation

        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).update(dt);
        }

        Random rand;
        rand = new Random();
        if (cl.isPlayerOnGround())
            runningDust.getEmitters().first().setPosition(player.getPosition().x * PPM - player.getWidth() / 10,
                    player.getPosition().y * PPM - player.getHeight() / 2 + rand.nextInt(5));
        runningDust.update(Gdx.graphics.getDeltaTime());
        for (ParticleEffect p : bloodParts) {
            p.update(Gdx.graphics.getDeltaTime());
        }

        for (Enemy e : cl.getAttackers()) {
            if (e.attacked && e.playerAttackable) {
                player.damage(player.blocking ? 1 : (int)e.getDamage());
            }
        }
        currentTime = TimeUtils.nanoTime();
    }

    public void render() {
        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(40 / 255f, 38 / 255f, 33 / 255f, 1f);

        // flip player if dir is changed
        if (player.getDir() == -1)
            player.getAnimation().getFrame().flip(!player.getAnimation().getFrame().isFlipX(), false);
        else
            player.getAnimation().getFrame().flip(player.getAnimation().getFrame().isFlipX(), false);

		/* Screen shake */
        if (attacked && rotTick < 2) {
            cam.translate(1f, 1.5f);
            rotTick++;
        } else if (rotTick > 0) {
            // cam.translate(2f,0);
            rotTick--;
            attacked = false;
        }

		/* set cam and debug cam to follow player */
        float lerp = 10f;
        Vector3 position = cam.position;
        position.x += Math.floor((player.getPosition().x * PPM - position.x) * lerp / 2 * Gdx.graphics.getDeltaTime())
                + player.getBody().getLinearVelocity().x * 2;
        position.y += Math.floor((player.getPosition().y * PPM - position.y) * lerp * Gdx.graphics.getDeltaTime());

		/* set cam and debug cam to follow player */
        cam.position.set(position.x, position.y, 0);
        b2dCam.position.set(cam.position.x / PPM, cam.position.y / PPM, 0);

        cam.update();
        b2dCam.update();

		/* render */
        postProcessor.capture(); // begin postProcessor
        // draw tile map
        tmr.setView(cam);
        tmr.render();

        // draw entities
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);
        for (Enemy e : enemies) {
            e.render(sb);
        }

        // draw crytals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).render(sb);

        }

        // particles
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
        postProcessor.render(); // end postProcessor

		/* Hud & debug */
        sr.setProjectionMatrix(b2dCam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // gray health
        sr.setColor(.09f, .09f, .09f, .2f);
        sr.box(b2dCam.position.x - 1.66f, b2dCam.position.y + .83f, 0, .05f * player.getMaxHealth(), .025f, 0);

        // player health
        sr.setColor(1f, .2f, 0f, 1f);
        sr.box(b2dCam.position.x - 1.66f, b2dCam.position.y + .83f, 0, .05f * player.health, .025f, 0);

        // player stamina
        sr.setColor(.7f, 1f, 0f, 1f);
        sr.box(b2dCam.position.x - 1.66f, b2dCam.position.y + .80f, 0, (float) (.005f * player.stamina), .025f, 0);

        // enemy health
        sr.setColor(1f, 0f, 0f, 1f);
        for (Enemy e : enemies) {
            if (e.health < e.getMaxHealth())
                sr.box(e.getPosition().x - .08f, e.getPosition().y - .25f, 0, .01f * e.health, .015f, 0);
        }

        sr.set(ShapeRenderer.ShapeType.Line);
        if (gsm.debug) {
            b2dr.render(world, b2dCam.combined);
            for (Enemy e : enemies) {
                sr.line(e.getVectors("p1"), e.getVectors("p2"));
                sr.line(e.getVectors("c"), e.getVectors("n"));
            }

        }
        sr.end();
    }

    @Override
    public void dispose() {
        postProcessor.dispose();
    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // create player
        bdef.position.set(0 / PPM, 700 / PPM);
        bdef.type = BodyType.DynamicBody;
        // bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(6 / PPM, 10 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_GROUND | B2DVars.BIT_WALL | B2DVars.BIT_CYSTAL
                | B2DVars.BIT_ENEMY | B2DVars.BIT_VISIONCONE;
        body.createFixture(fdef).setUserData("player");

        // create player Hitboxs
        shape.setAsBox(12 / PPM, 20 / PPM, new Vector2(0, -9 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY_ATTACK_RANGE;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("playerHitBox");

        // create foot sensor
        shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_GROUND | B2DVars.BIT_ENEMY;
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
        shape.setAsBox(20 / PPM, 8 / PPM, new Vector2(0, -5 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ATTACK_RANGE;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("attackRange");

        // create player
        player = new Player(body, this);

        body.setUserData(player);
    }

    public void createEnemy(int numOfEnems) {

        for (int i = 0; i < numOfEnems; i++) {
            BodyDef bdef = new BodyDef();
            FixtureDef fdef = new FixtureDef();
            PolygonShape shape = new PolygonShape();

            // create enemy
            bdef.position.set((80 + (i * 10)) / PPM, 500 / PPM);
            bdef.type = BodyType.DynamicBody;
            Body body = world.createBody(bdef);

            shape.setAsBox(6 / PPM, 10 / PPM, new Vector2(0, -9 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
            fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_WALL | B2DVars.BIT_GROUND | B2DVars.BIT_CYSTAL
                    | B2DVars.BIT_PLAYER | B2DVars.BIT_Projectile;
            body.createFixture(fdef).setUserData("enemy" + i);

            // stack prevention
            shape.setAsBox(2f / PPM, 3f / PPM, new Vector2(0, -9 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
            fdef.filter.maskBits = B2DVars.BIT_ENEMY;
            fdef.restitution = 1;
            body.createFixture(fdef).setUserData("stack" + i);

            // create enemy Hitboxs
            shape.setAsBox(12 / PPM, 20 / PPM, new Vector2(0, -9 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
            fdef.filter.maskBits = B2DVars.BIT_ATTACK_RANGE | B2DVars.BIT_Projectile;
            fdef.isSensor = true;
            body.createFixture(fdef).setUserData("enemyHitBox" + i);

            // create foot sensor
            shape.setAsBox(12 / PPM, 4 / PPM, new Vector2(0, -19 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
            fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_EDGE | B2DVars.BIT_WALL;
            fdef.isSensor = true;
            body.createFixture(fdef).setUserData("Efoot" + i);

            // create vision sensors
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
            body.createFixture(fdef).setUserData("visionRight" + i);
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
            body.createFixture(fdef).setUserData("visionLeft" + i);

            // create range of activity
            CircleShape r = new CircleShape();
            r.setRadius(150 / PPM);
            fdef.shape = r;
            fdef.filter.categoryBits = B2DVars.BIT_VISIONCONE;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;
            fdef.isSensor = true;
            body.createFixture(fdef).setUserData("range" + i);

            // create wall/player collision sensor
            shape.setAsBox(7 / PPM, 8 / PPM, new Vector2(0, -9 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
            fdef.isSensor = true;
            fdef.filter.maskBits = B2DVars.BIT_EDGE | B2DVars.BIT_WALL | B2DVars.BIT_GROUND | B2DVars.BIT_PLAYER;
            body.createFixture(fdef).setUserData("wallcollision" + i);

            // create attack range
            shape.setAsBox(35 / PPM, 8 / PPM, new Vector2(0, -5 / PPM), 0);
            fdef.shape = shape;
            fdef.filter.categoryBits = B2DVars.BIT_ENEMY_ATTACK_RANGE;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;
            fdef.isSensor = true;
            body.createFixture(fdef).setUserData("enemyAttackRange" + i);

            new Enemy(body, this, i);
        }
    }

    private void createTiles() {
        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/slicedNEW.tmx");
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
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY | B2DVars.BIT_Projectile;
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
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY | B2DVars.BIT_Projectile;
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
            fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY | B2DVars.BIT_Projectile;
            // fdef.friction = 1.5f;
            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("edge");

        }
    }

    private void createProjectile() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();

        bdef.position.set(player.getPosition().x, player.getPosition().y - .1f);
        bdef.type = BodyType.DynamicBody;
        bdef.angularDamping = 1f;

        Body body = world.createBody(bdef);

        shape.setAsBox(3 / PPM, 1 / PPM);
        fdef.shape = shape;
        // fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_Projectile;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY | B2DVars.BIT_GROUND | B2DVars.BIT_WALL | B2DVars.BIT_EDGE;

        body.createFixture(fdef).setUserData("project");

        new Projectile(body, this, 7 * player.getDir());

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

            Crystal c = new Crystal(body, this);
            crystals.add(c);

            body.setUserData(c);
        }
    }
}
