package main;

import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

public class Minion extends Card {
    Coordinates coordinates;
    String row;
    boolean isTank;
    Ability ability;

    public Minion(CardInput input, GameSet gameSet) {
        super(input, gameSet);
        this.setProperties();
    }

    public void setProperties() {
        String name = this.getName();
        switch (name) {
            case "Sentinel":
            case "Berserker":
            case "The Cursed One":
            case "Disciple":
                row = "back";
                isTank = false;
                break;

            case "Goliath":
            case "Warden":
                row = "front";
                isTank = true;
                break;

            case "The Ripper":
            case "Miraj":
                row = "front";
                isTank = false;
                break;
        }
    }
    void attack(Card card) {

    }
}
