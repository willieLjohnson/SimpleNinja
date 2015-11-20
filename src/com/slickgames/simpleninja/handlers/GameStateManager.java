package com.slickgames.simpleninja.handlers;

import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.GameState;
import com.slickgames.simpleninja.states.Play;

import java.util.Stack;

public class GameStateManager {

    public static final int PLAY = 1;
    private Game game;
    private Stack<GameState> gameStates;

    public GameStateManager(Game game) {
        this.game = game;
        gameStates = new Stack<GameState>();
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
        if (state == PLAY)
            return new Play(this);
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
