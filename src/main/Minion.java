package main;

import fileio.CardInput;
import fileio.Coordinates;

public class Minion extends Card {
    Coordinates coordinates;
    String row;
    private boolean isTank;
    Ability ability;

    public Minion(CardInput input, GameSet gameSet) {
        super(input, gameSet);
        this.setProperties();
    }

    public void setProperties() {
        String name = this.getName();
        switch (name) {
            case "Sentinel", "Berserker", "The Cursed One", "Disciple" -> {
                row = "back";
                isTank = false;
            }
            case "Goliath", "Warden" -> {
                row = "front";
                isTank = true;
            }
            case "The Ripper", "Miraj" -> {
                row = "front";
                isTank = false;
            }
        }
    }

    public boolean isTank() {
        return isTank;
    }

    void attack(Card card) {

    }
}
