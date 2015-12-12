package com.slickgames.simpleninja.handlers;

import com.slickgames.simpleninja.main.Game;
import com.slickgames.simpleninja.states.GameState;
import com.slickgames.simpleninja.states.MainMenu;
import com.slickgames.simpleninja.states.Pause;
import com.slickgames.simpleninja.states.Play;

import java.util.Stack;

public class GameStateManager {
    public int stateStautes;
    public static final int PLAY = 1;
    public static final int PAUSE = 0;
    public static final int MAIN_MENU = 2;
    private Game game;
    private Stack<GameState> gameStates;
    private Play play;

    public GameStateManager(Game game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MAIN_MENU);
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
            stateStautes = 1;
            if (play == null) {
                play = new Play(this);
            }
            return play;
        }
        if (state == PAUSE) {
            stateStautes = 0;
            return new Pause(this);
        }
        if (state == MAIN_MENU) {
            stateStautes = 2;
            return new MainMenu(this);
        }

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