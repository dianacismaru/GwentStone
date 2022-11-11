package main;

import fileio.CardInput;

public class Environment extends Card {
    public Environment(CardInput input, GameSet gameSet) {
        super(input, gameSet);
    }

    public void useAbility() {
        String name = this.getName();
        switch (name) {
            case "Winterfell":
                // ceva
                break;

            case "Firestorm":
                // ceva
                break;

            case "Heart Hound":
                // ceva
                break;
        }
    }
}
