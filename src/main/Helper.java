package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

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
    static void manageError(Action action, String errorMessage, ObjectNode actionNode) {
        actionNode.put("command", action.getCommand());
        actionNode.put("handIdx", action.getHandIdx());
        actionNode.put("error", errorMessage);
    }

    /**
     * Get the target row in the board game for a Minion card
     * @param minion        the Minion card that will be placed on the board
     * @param playerIndex   the index of the player
     * @param player        the reference to the owner of the card
     * @return              the target row in which the minion will be placed
     */
    static ArrayList<Card> getTargetRow(Minion minion, int playerIndex, Player player) {
        int rowIndex;

        if (minion.row.equals("front")) {
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
