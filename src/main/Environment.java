package main;

import fileio.CardInput;

import java.util.ArrayList;

import static main.Helper.getCardWithMaxHealth;
import static main.Helper.getMirroredRow;

public class Environment extends Card {
    public Environment(CardInput input, GameSet gameSet) {
        super(input, gameSet);
    }

    public void useAbility(int affectedRow) {
        switch (getName()) {
            case "Winterfell" -> useWinterfell(affectedRow);
            case "Firestorm" -> useFirestorm(affectedRow);
            case "Heart Hound" -> useHeartHound(affectedRow);
        }
    }

    private void useWinterfell(int affectedRow) {
        ArrayList<Card> affectedRowList = gameSet.gameBoard.get(affectedRow);
        for (Card card: affectedRowList) {
            //System.out.println("folosesc winterfell pe cartea " + card.getName() + " de pe randul " + affectedRow);
            card.setFrozen(true);
        }
    }

    private void useFirestorm(int affectedRow) {
        ArrayList<Card> cloneAffectedRow = new ArrayList<>(gameSet.gameBoard.get(affectedRow));
        for (Card card: cloneAffectedRow) {
            //System.out.println("folosesc firestorm pe cartea " + card.getName() + " de pe randul " + affectedRow);
            card.setHealth(card.getHealth() - 1);
            if (card.getHealth() <= 0) {
                gameSet.gameBoard.get(affectedRow).remove(card);
            }
        }
    }

    private void useHeartHound(int affectedRow) {
        ArrayList<Card> affectedRowList = gameSet.gameBoard.get(affectedRow);
        ArrayList<Card> mirroredRow = getMirroredRow(affectedRow, gameSet);

        Card cardWithMaxHealth = getCardWithMaxHealth(affectedRowList);
        //System.out.println("folosesc HH pe cartea " + cardWithMaxHealth.getName() + " de pe randul " + affectedRow);

        mirroredRow.add(cardWithMaxHealth);
        affectedRowList.remove(cardWithMaxHealth);
    }
}
