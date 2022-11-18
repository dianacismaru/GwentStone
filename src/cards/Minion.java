package cards;

import fileio.CardInput;
import main.GameSet;

public final class Minion extends Card {
    private String row;
    private boolean tank;

    public Minion(final CardInput input, final GameSet gameSet) {
        super(input, gameSet);
        this.setProperties();
    }

    /**
     * Set the proprieties that each type of minion has
     */
    public void setProperties() {
        String name = this.getName();
        switch (name) {
            case "Sentinel", "Berserker", "The Cursed One", "Disciple" -> {
                row = "back";
                tank = false;
            }
            case "Goliath", "Warden" -> {
                row = "front";
                tank = true;
            }
            case "The Ripper", "Miraj" -> {
                row = "front";
                tank = false;
            }
            default -> System.err.println("Invalid Minion name.");
        }
    }

    /**
     * Use the minion card's ability
     * @param attacker      the card that will use the ability
     * @param targetCard    the card that will be targeted
     */
    public void useAbility(final Card attacker, final Card targetCard) {
        switch (this.getName()) {
            case "The Ripper" -> weakKnees(targetCard);
            case "Miraj" -> skyJack(attacker, targetCard);
            case "The Cursed One" -> shapeShift(targetCard);
            case "Disciple" -> godsPlan(targetCard);
            default -> System.err.println("Invalid Minion name.");
        }
    }

    private void weakKnees(final Card targetCard) {
        if (targetCard.getAttackDamage() < 2) {
            targetCard.setAttackDamage(0);
        } else {
            targetCard.setAttackDamage(targetCard.getAttackDamage() - 2);
        }
    }

    private void skyJack(final Card attacker, final Card targetCard) {
        int tmpHealth = attacker.getHealth();
        attacker.setHealth(targetCard.getHealth());
        targetCard.setHealth(tmpHealth);
    }

    private void shapeShift(final Card targetCard) {
        int tmpHealth = targetCard.getHealth();
        targetCard.setHealth(targetCard.getAttackDamage());
        targetCard.setAttackDamage(tmpHealth);
    }

    private void godsPlan(final Card targetCard) {
        targetCard.setHealth(targetCard.getHealth() + 2);
    }

    public String getRow() {
        return row;
    }

    public boolean isTank() {
        return tank;
    }
}
