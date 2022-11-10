package minions;

import fileio.CardInput;
import main.Card;

public class Miraj extends Card {
    final String row = "front";
    final boolean isTank = false;

    public Miraj(CardInput input) {
        super(input);
    }

    public void ability() {
        //Skyjack (swap intre viata lui si viata unui minion al advers.)
    }
}