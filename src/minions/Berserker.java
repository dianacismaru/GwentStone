package minions;

import fileio.CardInput;
import main.Card;

public class Berserker extends Card {
    final String row = "back";
    final boolean isTank = false;

    public Berserker(CardInput input) {
        super(input);
    }
}