package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;

import java.util.ArrayList;

public class Action {
    private final String command;
    private int handIdx;
    private Coordinates cardAttacker;
    private Coordinates cardAttacked;
    private int affectedRow;
    private int playerIdx;
    private int x;
    private int y;
    GameSet gameSet;

    public Action(GameSet gameSet, ActionsInput actionsInput) {
        this.gameSet = gameSet;
        this.command = actionsInput.getCommand();
        this.handIdx = actionsInput.getHandIdx();
        this.cardAttacker = actionsInput.getCardAttacker();
        this.cardAttacked = actionsInput.getCardAttacked();
        this.affectedRow = actionsInput.getAffectedRow();
        this.playerIdx = actionsInput.getPlayerIdx();
        this.x = actionsInput.getX();
        this.y = actionsInput.getY();
    }

    public void operateCommand(ObjectNode actionNode) {
        if (command.startsWith("get"))
            actionNode.put("command", command);

        switch(command) {
            case "getCardsInHand":
                getCardsInHand(actionNode);
                break;

            case "getPlayerDeck":
                getPlayerDeck(actionNode);
                break;

            case "getCardsOnTable":
                break;

            case "getPlayerTurn":
                actionNode.put("output", gameSet.playerTurn);
                break;

            case "getPlayerHero":
                getPlayerHero(actionNode);
                break;

            case "getCardAtPosition":
                getCardAtPosition(actionNode);
                break;

            case "getPlayerMana":
                getPlayerMana(actionNode);
                break;

            case "getEnvironmentCardsInHand":
                break;

            case "getFrozenCardsOnTable":
                break;

            case "cardUsesAttack":
                break;
        }
    }

    private void getCardsInHand(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        ArrayList<Card> cardsInHand = gameSet.players[playerIdx - 1].cardsInHand;

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: cardsInHand) {
            outputArrayNode.add(Helper.createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    private void getPlayerDeck(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        Player player = gameSet.players[playerIdx - 1];
        ArrayList<Card> deck = player.decks.get(player.deckIndex);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: deck) {
            outputArrayNode.add(Helper.createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    private void getPlayerHero(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        Card hero = gameSet.players[playerIdx - 1].hero;
        actionNode.put("output", Helper.createHeroNode(hero));
    }

    private void getCardAtPosition(ObjectNode actionNode) {
        ArrayList<ArrayList<Card>> gameBoard = gameSet.gameBoard;

        if (!gameBoard.isEmpty() && !gameBoard.get(x).isEmpty()) {
            actionNode.put("output", Helper.createCardNode(gameBoard.get(x).get(y)));
        } else {
            actionNode.put("error", "No card at that position.");
        }
    }

    private void getPlayerMana(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        int mana = gameSet.players[playerIdx - 1].getMana();
        actionNode.put("output", mana);
    }

    private void cardUsesAttack(ObjectNode actionNode) {
        Card attacker = gameSet.getCardByCoordinates(cardAttacker);
        Card opponent = gameSet.getCardByCoordinates(cardAttacked);
    }

    public String getCommand() {
        return command;
    }
}
