package minions;

import fileio.CardInput;
import main.Card;

public class Warden extends Card {
    final String row = "front";
    final boolean isTank = true;

    public Warden(CardInput input) {
        super(input);
    }
}
