package com.slickgames.simpleninja.handlers;

public class MyInput {

    public static final int NUM_KEYS = 7;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int JUMP = 2;
    public static final int ATTACK = 3;
    public static final int RESET = 4;
    public static int WALLRUN = 5;
    public static final int SHOOT= 6;
    public static boolean[] keys;
    public static boolean[] pkeys;

    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        for (int i = 0; i < NUM_KEYS; i++) {
            pkeys[i] = keys[i];
        }
    }

    public static void setKey(int i, boolean b) {
        keys[i] = b;
    }

    public static boolean isDown(int i) {
        return keys[i];
    }

    public static boolean isPressed(int i) {
        return keys[i] && !pkeys[i];
    }
}
