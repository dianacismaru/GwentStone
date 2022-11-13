package main;

import fileio.CardInput;
import fileio.Coordinates;

public class Minion extends Card {
    Coordinates coordinates;
    String row;
    private boolean tank;

    public Minion(CardInput input, GameSet gameSet) {
        super(input, gameSet);
        this.setProperties();
    }

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
        }
    }

    public void useAbility(Card attacker, Card targetCard) {
//        System.out.println("\nam apelat abilitate intre " + attacker.getName() + " si " + targetCard.getName());
        switch (this.getName()) {
            case "The Ripper" -> weakKnees(targetCard);
            case "Miraj" -> skyJack(attacker, targetCard);
            case "The Cursed One" -> shapeShift(targetCard);
            case "Disciple" -> godsPlan(targetCard);
        }
    }

    private void weakKnees(Card targetCard) {
        // System.out.println("target card avea damage " + targetCard.getAttackDamage());
        if (targetCard.getAttackDamage() < 2) {
            targetCard.setAttackDamage(0);
        } else {
            targetCard.setAttackDamage(targetCard.getAttackDamage() - 2);
        }
    }

    private void skyJack(Card attacker, Card targetCard) {
        int tmpHealth = attacker.getHealth();
        attacker.setHealth(targetCard.getHealth());
        targetCard.setHealth(tmpHealth);
    }

    private void shapeShift(Card targetCard) {
        int tmp = targetCard.getHealth();
        targetCard.setHealth(targetCard.getAttackDamage());
        targetCard.setAttackDamage(tmp);
    }

    private void godsPlan(Card targetCard) {
        targetCard.setHealth(targetCard.getHealth() + 2);
    }

    public boolean isTank() {
        return tank;
    }
}
