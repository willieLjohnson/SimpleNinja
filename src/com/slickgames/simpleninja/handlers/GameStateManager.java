package com.slickgames.simpleninja.handlers;

import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.GameState;
import com.slickgames.simpleninja.states.Pause;
import com.slickgames.simpleninja.states.Pause;
import com.slickgames.simpleninja.states.Play;

import java.util.Stack;

public class GameStateManager {
public int stateStautes ;
    public static final int PLAY = 1;
    public static final int Pause =0;
    private Game game;
    private Stack<GameState> gameStates;
    public static GameState PausePlayState ;
    Play play;
    public GameStateManager(Game game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        play= new Play(this);
        pushState(PLAY);
    }

    public Game game() {
        return game;
    }

    public void update(float dt) {
        gameStates.peek().update(dt);
    }

    public void render() {
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        if (state == PLAY) {
            stateStautes=1;
            return  play;}
        if (state == Pause) {
            stateStautes=0;
            return new Pause(this);}
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    private void pushState(int state) {
        gameStates.push(getState(state));
    }

    public void popState() {
        GameState g = gameStates.pop();
        g.dispose();
    }
}