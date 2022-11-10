package minions;

import fileio.CardInput;
import main.Card;

public class TheRipper extends Card {
    final String row = "front";
    final boolean isTank = false;

    public TheRipper(CardInput input) {
        super(input);
    }

    // weak knees = -2 atac pt un minion al adversarului
    public void ability() {

    }
}