package main;

public class Ability {
    Card attacker;

    public Ability(Card attacker) {
        this.attacker = attacker;
    }

    void useAbility(Card opponent) {
        if (attacker.getFrozen()) {
            System.out.println("Attacker card is frozen.");
        } else {
            switch (attacker.getName()) {
                case "The Ripper" -> weakKnees(opponent);
                case "Miraj" -> skyJack(opponent);
                case "The Cursed One" -> shapeShift(opponent);
                case "Disciple" -> godsPlan(opponent);
            }
        }
    }

    private void weakKnees(Card opponent) {
        int newAttackDamage = opponent.getAttackDamage() - 2;
        opponent.setAttackDamage(newAttackDamage);
    }

    private void skyJack(Card opponent) {
        int tmpHealth = attacker.getHealth();
        attacker.setHealth(opponent.getHealth());
        opponent.setHealth(tmpHealth);
    }

    private void shapeShift(Card opponent) {

    }

    private void godsPlan(Card opponent) {
        // cum verific daca cartea oponentului apartine unui player
        opponent.addHealth(2);
    }
}
