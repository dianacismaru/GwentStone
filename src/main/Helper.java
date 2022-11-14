package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

public class Helper {
    private Helper() {
    }

    /**
     * Display an error in the json output
     * @param action        the action that is currently running in the game
     * @param errorMessage  the error message that will be displayed
     * @param actionNode    the object node that will be placed in the json output
     */
    public static int manageError(Action action, String errorMessage, ObjectNode actionNode) {
        actionNode.put("command", action.getCommand());
        ObjectMapper objectMapper = new ObjectMapper();

        switch (action.getCommand()) {
            case "placeCard" -> actionNode.put("handIdx", action.getHandIdx());
            case "useEnvironmentCard" -> {
                actionNode.put("handIdx", action.getHandIdx());
                actionNode.put("affectedRow", action.getAffectedRow());
            }
            case "cardUsesAttack", "cardUsesAbility" -> {
                ObjectNode attackerNode = objectMapper.createObjectNode();
                attackerNode.put("x", action.getCardAttacker().getX());
                attackerNode.put("y", action.getCardAttacker().getY());
                actionNode.put("cardAttacker", attackerNode);
                ObjectNode attackedNode = objectMapper.createObjectNode();
                attackedNode.put("x", action.getCardAttacked().getX());
                attackedNode.put("y", action.getCardAttacked().getY());
                actionNode.put("cardAttacked", attackedNode);
            }
            case "useAttackHero" -> {
                if (action.gameSet.gameEnded()) {
                    actionNode.remove("command");
                    actionNode.put("gameEnded", errorMessage);
                    return 1;
                }
                ObjectNode attackerNode = objectMapper.createObjectNode();
                attackerNode.put("x", action.getCardAttacker().getX());
                attackerNode.put("y", action.getCardAttacker().getY());
                actionNode.put("cardAttacker", attackerNode);
            }
            case "useHeroAbility" -> {
                actionNode.put("affectedRow", action.getAffectedRow());
            }
        }

        actionNode.put("error", errorMessage);
        return 1;
    }

    public static void removeCardFromTable(GameSet gameSet, Card deadCard) {
        for (ArrayList<Card> row: gameSet.gameBoard) {
            for (Card card: row) {
                if (card.equals(deadCard)) {
                    row.remove(card);
                    return;
                }
            }
        }
    }

    public static void unfreezeCards(int playerIndex, GameSet gameSet) {
        ArrayList<Card> frontRow;
        ArrayList<Card> backRow;
        if (playerIndex == 0) {
            frontRow = gameSet.gameBoard.get(2);
            backRow = gameSet.gameBoard.get(3);
        } else {
            frontRow = gameSet.gameBoard.get(1);
            backRow = gameSet.gameBoard.get(0);
        }

        for (Card card: frontRow) {
            card.setFrozen(false);
        }

        for (Card card: backRow) {
            card.setFrozen(false);
        }
    }

    public static boolean enemyHasTank(Player enemy, GameSet gameSet) {
        int frontRow;
        if (enemy.equals(gameSet.players[0])) {
            frontRow = 2;
        } else {
            frontRow = 1;
        }

        for (Card card: gameSet.gameBoard.get(frontRow)) {
            if (((Minion) card).isTank()) {
                return true;
            }
        }
        return false;
    }

    public static Card getCardWithMaxHealth(ArrayList<Card> cardsList) {
        Card cardWithMaxHealth = cardsList.get(0);

        for (Card card: cardsList) {
            if (card.getHealth() > cardWithMaxHealth.getHealth()) {
                cardWithMaxHealth = card;
            }
        }
        return cardWithMaxHealth;
    }

    public static Card getCardWithMaxDamage(ArrayList<Card> cardsList) {
        Card cardWithMaxDamage = cardsList.get(0);

        for (Card card: cardsList) {
            if (card.getAttackDamage() > cardWithMaxDamage.getAttackDamage()) {
                cardWithMaxDamage = card;
            }
        }
        return cardWithMaxDamage;
    }

    public static ArrayList<Card> getMirroredRow(int affectedRow, GameSet gameSet) {
        int targetRow = switch (affectedRow) {
            case 0 -> 3;
            case 1 -> 2;
            case 2 -> 1;
            default -> 0;
        };
        return gameSet.gameBoard.get(targetRow);
    }

    public static int getCardOwnerIndex(Coordinates coordinates, Player[] players) {
        if (coordinates.getX() == 0 || coordinates.getX() == 1)
            return 1;
        return 0;
    }

    public static boolean isEnemyRow(int affectedRow, int activePlayerIndex) {
        if (activePlayerIndex == 0 && (affectedRow == 0 || affectedRow == 1)) {
            return true;
        }
        return activePlayerIndex == 1 && (affectedRow == 2 || affectedRow == 3);
    }

    /**
     * Get the target row in the board game for a Minion card
     * @param minion        the Minion card that will be placed on the board
     * @param playerIndex   the index of the player
     * @param player        the reference to the owner of the card
     * @return              the target row in which the minion will be placed
     */
    static ArrayList<Card> getTargetRowForMinion(Minion minion, int playerIndex, Player player) {
        int rowIndex;

        if (minion.getRow().equals("front")) {
            if (playerIndex == 0) {
                rowIndex = 2;
            } else {
                rowIndex = 1;
            }
        } else {
            if (playerIndex == 0) {
                rowIndex = 3;
            } else {
                rowIndex = 0;
            }
        }
        return player.gameSet.gameBoard.get(rowIndex);
    }

    /**
     * Check if an input card can be an environment card
     * @param  card the card that will be checked
     * @return      true if the card has an environment card name, false otherwise
     */
    public static boolean canBeEnvironmentCard(CardInput card) {
        String name = card.getName();
        return name.equals("Winterfell") || name.equals("Firestorm") || name.equals("Heart Hound");
    }

    /**
     * Check if an existing card is an environment card
     * @param  card the card that will be checked
     * @return      true if the card has an environment card name, false otherwise
     */
    public static boolean isEnvironmentCard(Card card) {
        String name = card.getName();
        return name.equals("Winterfell") || name.equals("Firestorm") || name.equals("Heart Hound");
    }

    /**
     * Create an ObjectNode from a card
     * @param  card the card that will be parsed
     * @return      the newly-created ObjectNode
     */
    public static ObjectNode createCardNode(Card card) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardNode = objectMapper.createObjectNode();

        cardNode.put("mana", card.getMana());

        if (!(card instanceof Environment)) {
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealth());
        }
        cardNode.put("description", card.getDescription());

        ArrayNode colorsArrayNode = cardNode.putArray("colors");
        for (String color: card.getColors()) {
            colorsArrayNode.add(color);
        }
        cardNode.put("name", card.getName());

        return cardNode;
    }

    /**
     * Create an ObjectNode from a hero card
     * @param  hero the hero card that will be parsed
     * @return      the newly-created ObjectNode
     */
    public static ObjectNode createHeroNode(Card hero) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode heroNode = objectMapper.createObjectNode();

        heroNode.put("mana", hero.getMana());
        heroNode.put("description", hero.getDescription());

        ArrayNode colorsArrayNode = heroNode.putArray("colors");
        for (String color: hero.getColors()) {
            colorsArrayNode.add(color);
        }
        heroNode.put("name", hero.getName());
        heroNode.put("health", hero.getHealth());

        return heroNode;
    }
}
