package main;

import fileio.CardInput;

public class EnvironmentCard extends Card {
    public EnvironmentCard(CardInput input) {
        super(input);
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
