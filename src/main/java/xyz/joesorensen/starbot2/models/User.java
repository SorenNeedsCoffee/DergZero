package xyz.joesorensen.starbot2.models;

public class User {
    private String id;
    private double xp;
    private int lvl;

    public User(String id) {
        this.id = id;
        this.xp = 0;
        this.lvl = 1;
    }

    public User(String id, double xp, int lvl) {
        this.id = id;
        this.xp = xp;
        this.lvl = lvl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.id.equals(user.getId());
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
