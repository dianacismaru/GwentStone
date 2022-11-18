package cards;

import fileio.CardInput;
import main.GameSet;

import java.util.ArrayList;

import static main.GameSet.MAX_GAMEBOARD_HEIGHT;
import static main.Helper.getCardWithMaxHealth;

public final class Environment extends Card {
    public Environment(final CardInput input, final GameSet gameSet) {
        super(input, gameSet);
    }

    /**
     * Use the environment card's ability
     * @param affectedRow   the index of the row that will be targeted
     */
    public void useAbility(final int affectedRow) {
        switch (getName()) {
            case "Winterfell" -> useWinterfell(affectedRow);
            case "Firestorm" -> useFirestorm(affectedRow);
            case "Heart Hound" -> useHeartHound(affectedRow);
            default -> System.err.println("Invalid Environment Card.");
        }
    }

    private void useWinterfell(final int affectedRow) {
        ArrayList<Card> affectedRowList = getGameSet().getGameBoard().get(affectedRow);
        for (Card card: affectedRowList) {
            card.setFrozen(true);
        }
    }

    private void useFirestorm(final int affectedRow) {
        ArrayList<Card> originalAffectedRow = getGameSet().getGameBoard().get(affectedRow);
        ArrayList<Card> cloneAffectedRow = new ArrayList<>(originalAffectedRow);

        for (Card card: cloneAffectedRow) {
            card.setHealth(card.getHealth() - 1);
            if (card.getHealth() <= 0) {
                getGameSet().getGameBoard().get(affectedRow).remove(card);
            }
        }
    }

    private void useHeartHound(final int affectedRow) {
        int mirroredRowIndex = MAX_GAMEBOARD_HEIGHT - 1 - affectedRow;
        ArrayList<Card> mirroredRow = getGameSet().getGameBoard().get(mirroredRowIndex);
        ArrayList<Card> affectedRowList = getGameSet().getGameBoard().get(affectedRow);

        Card cardWithMaxHealth = getCardWithMaxHealth(affectedRowList);

        mirroredRow.add(cardWithMaxHealth);
        affectedRowList.remove(cardWithMaxHealth);
    }
}
