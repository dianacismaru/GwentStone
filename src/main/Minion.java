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
    }

    public void setProperties() {
        // ArrayList<Card> deck = player.decks.get(player.deckIndex);
        String name = this.getName();
        switch (name) {
            case "Sentinel":
            case "Berserker":
                row = "back";
                isTank = false;
                break;

            case "Goliath":
            case "Warden":
                row = "front";
                isTank = true;
                break;

            case "The Ripper":
                row = "front";
                ability = new Ability(this);
                break;

            case "Miraj":
                break;

            case "The Cursed One":
                break;

            case "Disciple":
                break;
        }
    }
    void attack(Card card) {

    }
}
