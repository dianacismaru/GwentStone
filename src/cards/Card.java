package cards;

import fileio.CardInput;
import main.GameSet;

import java.util.ArrayList;

public class Card {
    private final int mana;
    private int attackDamage;
    private int health;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private boolean frozen;
    private boolean attacked;
    private final GameSet gameSet;

    public Card(final CardInput input, final GameSet gameSet) {
        this.mana = input.getMana();
        this.attackDamage = input.getAttackDamage();
        this.health = input.getHealth();
        this.description = input.getDescription();
        this.colors = input.getColors();
        this.name = input.getName();
        this.gameSet = gameSet;
    }

    /**
     * @return the card's mana value
     */
    public int getMana() {
        return mana;
    }

    /**
     * @return the card's attack damage
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * @param attackDamage the card's attack damage to be set
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * @return the card's health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health the card's health to be set
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * @return the card's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the card's colors
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * @return the card's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the card's frozen status
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * @param frozen the card's frozen status to be set
     */
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    /**
     * @return the card's attack status for the current turn
     */
    public boolean hasAttacked() {
        return attacked;
    }

    /**
     * @param attacked the card's attack status to be set
     */
    public void setAttacked(final boolean attacked) {
        this.attacked = attacked;
    }

    /**
     * @return the game the card belongs to
     */
    public GameSet getGameSet() {
        return gameSet;
    }
}
