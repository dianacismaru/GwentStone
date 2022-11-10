package main;

import fileio.CardInput;

import java.util.ArrayList;

public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    boolean frozen;

    public Card(CardInput input) {
        this.mana = input.getMana();
        this.attackDamage = input.getAttackDamage();
        this.health = input.getHealth();
        this.description = input.getDescription();
        this.colors = input.getColors();
        this.name = input.getName();
        this.frozen = false;
    }

    public int getMana() {
        return mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
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

    @Override
    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana + '\n'
                +  ", attackDamage="
                + attackDamage + '\n'
                + ", health="
                + health + '\n'
                +  ", description='"
                + description + '\n'
                + '\''
                + ", colors="
                + colors + '\n'
                + ", name='"
                +  ""
                + name + '\n'
                + '\''
                + '}' + '\n';
    }
}
