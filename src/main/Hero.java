package main;

import fileio.CardInput;

import java.util.ArrayList;

import static main.Helper.getCardWithMaxDamage;
import static main.Helper.getCardWithMaxHealth;

public final class Hero extends Card {
    public static final int HERO_HEALTH = 30;

    public Hero(final CardInput input, final GameSet gameSet) {
        super(input, gameSet);
        this.setHealth(HERO_HEALTH);
    }

    public void useAbility(final int affectedRow) {
        ArrayList<Card> affectedRowList = getGameSet().getGameBoard().get(affectedRow);
        switch (this.getName()) {
            case "Lord Royce" -> subZero(affectedRowList);
            case "Empress Thorina" -> lowBlow(affectedRowList);
            case "King Mudface" -> earthBorn(affectedRowList);
            case "General Kocioraw" -> bloodThirst(affectedRowList);
            default -> System.err.println("Invalid Hero name.");
        }
    }

    /**
     * Freeze the card with maximum attack damage on the row
     * @param affectedRowList the row that contains the target card
     */
    private void subZero(final ArrayList<Card> affectedRowList) {
        Card targetCard = getCardWithMaxDamage(affectedRowList);
        targetCard.setFrozen(true);
    }

    /**
     * Destroy the card with maximum health on the row
     * @param affectedRowList the row that contains the target card
     */
    private void lowBlow(final ArrayList<Card> affectedRowList) {
        Card targetCard = getCardWithMaxHealth(affectedRowList);
        affectedRowList.remove(targetCard);
    }

    /**
     * Give +1 health for all the cards in the given row
     * @param affectedRowList the row that contains the target cards
     */
    private void earthBorn(final ArrayList<Card> affectedRowList) {
        for (Card card: affectedRowList) {
            card.setHealth(card.getHealth() + 1);
        }
    }

    /**
     * Give +1 attack damage for all the cards in the given row
     * @param affectedRowList the row that contains the target cards
     */
    private void bloodThirst(final ArrayList<Card> affectedRowList) {
        for (Card card: affectedRowList) {
            card.setAttackDamage(card.getAttackDamage() + 1);
        }
    }
}
