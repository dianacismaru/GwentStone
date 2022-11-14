package main;

import fileio.CardInput;

import java.util.ArrayList;

public class Card {
    private final int mana;
    private int attackDamage;
    private int health;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private boolean frozen;
    private boolean attacked;
    private final GameSet gameSet;

    public Card(final CardInput input, final GameSet gameSet) {
        this.mana = input.getMana();
        this.attackDamage = input.getAttackDamage();
        this.health = input.getHealth();
        this.description = input.getDescription();
        this.colors = input.getColors();
        this.name = input.getName();
        this.gameSet = gameSet;
    }

    public int getMana() {
        return mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public String getName() {
        return name;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    public boolean hasAttacked() {
        return attacked;
    }

    public void setAttacked(final boolean attacked) {
        this.attacked = attacked;
    }

    public GameSet getGameSet() {
        return gameSet;
    }
}
