package main;

import fileio.CardInput;

import java.util.ArrayList;

import static main.Helper.getCardWithMaxHealth;
import static main.Helper.getMirroredRow;

public final class Environment extends Card {
    public Environment(final CardInput input, final GameSet gameSet) {
        super(input, gameSet);
    }

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
        ArrayList<Card> affectedRowList = getGameSet().getGameBoard().get(affectedRow);
        ArrayList<Card> mirroredRow = getMirroredRow(affectedRow, getGameSet());

        Card cardWithMaxHealth = getCardWithMaxHealth(affectedRowList);

        mirroredRow.add(cardWithMaxHealth);
        affectedRowList.remove(cardWithMaxHealth);
    }
}
