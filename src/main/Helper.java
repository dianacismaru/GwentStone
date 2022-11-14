package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

import static main.GameSet.PLAYER_TWO_FRONT_ROW;
import static main.GameSet.PLAYER_ONE_FRONT_ROW;
import static main.GameSet.PLAYER_ONE_BACK_ROW;
import static main.GameSet.PLAYER_TWO_BACK_ROW;

public final class Helper {
    private Helper() {
    }

    /**
     * Display an error in the json output
     * @param action        the action that is currently running in the game
     * @param errorMessage  the error message that will be displayed
     * @param actionNode    the object node that will be placed in the json output
     */
    public static int manageError(final Action action, final String errorMessage,
                                  final ObjectNode actionNode) {
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
                if (action.getGameSet().gameEnded()) {
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
            default -> System.err.println("Invalid command.");
        }

        actionNode.put("error", errorMessage);
        return 1;
    }

    public static void removeCardFromTable(final GameSet gameSet, final Card deadCard) {
        for (ArrayList<Card> row: gameSet.getGameBoard()) {
            for (Card card: row) {
                if (card.equals(deadCard)) {
                    row.remove(card);
                    return;
                }
            }
        }
    }

    public static void unfreezeCards(final int playerIndex, final GameSet gameSet) {
        ArrayList<Card> frontRow = gameSet.getGameBoard().get(getRowIndex("front", playerIndex));
        ArrayList<Card> backRow = gameSet.getGameBoard().get(getRowIndex("back", playerIndex));

        for (Card card: frontRow) {
            card.setFrozen(false);
        }

        for (Card card: backRow) {
            card.setFrozen(false);
        }
    }

    public static int getRowIndex(final String row, final int playerIndex) {
        if (row.equals("front")) {
            if (playerIndex == 0) {
                return PLAYER_ONE_FRONT_ROW;
            } else {
                return PLAYER_TWO_FRONT_ROW;
            }
        } else {
            if (playerIndex == 0) {
                return PLAYER_ONE_BACK_ROW;
            } else {
                return PLAYER_TWO_BACK_ROW;
            }
        }
    }

    public static boolean enemyHasTank(final Player enemy, final GameSet gameSet) {
        int frontRow;
        if (enemy.equals(gameSet.getPlayers()[0])) {
            frontRow = 2;
        } else {
            frontRow = 1;
        }

        for (Card card: gameSet.getGameBoard().get(frontRow)) {
            if (((Minion) card).isTank()) {
                return true;
            }
        }
        return false;
    }

    public static Card getCardWithMaxHealth(final ArrayList<Card> cardsList) {
        Card cardWithMaxHealth = cardsList.get(0);

        for (Card card: cardsList) {
            if (card.getHealth() > cardWithMaxHealth.getHealth()) {
                cardWithMaxHealth = card;
            }
        }
        return cardWithMaxHealth;
    }

    public static Card getCardWithMaxDamage(final ArrayList<Card> cardsList) {
        Card cardWithMaxDamage = cardsList.get(0);

        for (Card card: cardsList) {
            if (card.getAttackDamage() > cardWithMaxDamage.getAttackDamage()) {
                cardWithMaxDamage = card;
            }
        }
        return cardWithMaxDamage;
    }

    public static Card getCardByCoordinates(final Coordinates coordinates, final GameSet gameSet) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        return gameSet.getGameBoard().get(x).get(y);
    }

    public static ArrayList<Card> getMirroredRow(final int affectedRow, final GameSet gameSet) {
        int targetRow = switch (affectedRow) {
            case PLAYER_TWO_BACK_ROW -> PLAYER_ONE_BACK_ROW;
            case PLAYER_TWO_FRONT_ROW -> PLAYER_ONE_FRONT_ROW;
            case PLAYER_ONE_FRONT_ROW -> PLAYER_TWO_FRONT_ROW;
            case PLAYER_ONE_BACK_ROW -> PLAYER_TWO_BACK_ROW;
            default -> -1;
        };
        return gameSet.getGameBoard().get(targetRow);
    }

    public static int getCardOwnerIndex(final Coordinates coordinates) {
        if (coordinates.getX() == 0 || coordinates.getX() == 1) {
            return 1;
        }
        return 0;
    }

    public static boolean isEnemyRow(final int affectedRow, final int activePlayerIndex) {
        if (activePlayerIndex == 0 && (affectedRow == PLAYER_TWO_BACK_ROW
                || affectedRow == PLAYER_TWO_FRONT_ROW)) {
            return true;
        }
        return activePlayerIndex == 1 && (affectedRow == PLAYER_ONE_FRONT_ROW
                || affectedRow == PLAYER_ONE_BACK_ROW);
    }

    /**
     * Get the target row in the board game for a Minion card
     * @param minion        the Minion card that will be placed on the board
     * @param playerIndex   the index of the player
     * @param player        the reference to the owner of the card
     * @return              the target row in which the minion will be placed
     */
    static ArrayList<Card> getTargetRowForMinion(final Minion minion, final int playerIndex,
                                                 final Player player) {
        int rowIndex = getRowIndex(minion.getRow(), playerIndex);
        return player.getGameSet().getGameBoard().get(rowIndex);
    }

    /**
     * Check if an input card can be an environment card
     * @param  card the card that will be checked
     * @return      true if the card has an environment card name, false otherwise
     */
    public static boolean hasEnvironmentCardName(final CardInput card) {
        String name = card.getName();
        return name.equals("Winterfell") || name.equals("Firestorm") || name.equals("Heart Hound");
    }

    /**
     * Check if an existing card is an environment card
     * @param  card the card that will be checked
     * @return      true if the card has an environment card name, false otherwise
     */
    public static boolean hasEnvironmentCardName(final Card card) {
        String name = card.getName();
        return name.equals("Winterfell") || name.equals("Firestorm") || name.equals("Heart Hound");
    }

    /**
     * Create an ObjectNode from a card
     * @param  card the card that will be parsed
     * @return      the newly-created ObjectNode
     */
    public static ObjectNode createCardNode(final Card card) {
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
    public static ObjectNode createHeroNode(final Card hero) {
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
