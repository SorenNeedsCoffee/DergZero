package com.joesorensen.starbot2.listeners;

public class TwitchEventManager {
    private static Listener listener;

    public static void setListener(Listener listener) {
        TwitchEventManager.listener = listener;
    }

    public static void live() {
        listener.onLive();
    }

    public static void offline() {
        listener.onOffline();
    }
}
