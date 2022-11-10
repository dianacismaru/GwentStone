package minions;

import fileio.CardInput;
import main.Card;

public class Sentinel extends Card {
    final String row = "back";
    final boolean isTank = false;

    public Sentinel(CardInput input) {
        super(input);
    }
}
