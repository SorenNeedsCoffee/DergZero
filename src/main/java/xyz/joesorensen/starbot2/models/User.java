package com.joesorensen.starbot2.models;

public class User {
    private String id;
    private double xp;
    private int lvl;

    public User(String id) {
        this.id = id;
        this.xp = 0;
        this.lvl = 1;
    }

    public String getId() {
        return id;
    }

    public double getXp() {
        return xp;
    }

    public int getLvl() {
        return lvl;
    }

    public void addXp(double amt) {
        this.xp += amt;
    }

    public void setXp(double val) {
        this.xp = val;
    }

    public void setLvl(int val) {
        this.lvl = val;
    }
}
