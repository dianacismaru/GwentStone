package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;

import java.util.ArrayList;

import static main.Helper.*;

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

    public int operateCommand(ObjectNode actionNode) {
        switch (command) {
            case "placeCard":
                return placeCard(actionNode);
            case "endPlayerTurn":
                gameSet.changePlayerTurn();
                return 0;
            case "cardUsesAttack":
                break;
            case "cardUsesAbility":
                break;
            case "useAttackHero":
                break;
            case "useHeroAbility":
                break;
            case "useEnvironmentCard":
                break;
        }
        return 0;
    }

    public int placeCard(ObjectNode actionNode) {
        int activePlayerIndex = gameSet.playerTurn - 1;
        Player player = gameSet.players[activePlayerIndex];
        ArrayList<Card> cardsInHand = player.cardsInHand;
        Card card = cardsInHand.get(handIdx);

        if (isEnvironmentCard(card)) {
            manageError(this, "Cannot place environment card on table.", actionNode);
            return 1;
        } else if (card.getMana() > player.getMana()) {
            manageError(this, "Not enough mana to place card on table.", actionNode);
            return 1;
        }

        ArrayList<Card> targetRow = getTargetRow((Minion) card, activePlayerIndex, player);

        if (targetRow.size() == 5) {
            manageError(this, "Cannot place card on the table since the row is full.", actionNode);
            return 1;
        }

        //System.out.println("jucatorul avea " + player.getMana() + " mana");
        player.updateMana(-card.getMana());
        targetRow.add(cardsInHand.remove(handIdx));
        //System.out.println("jucatorul mai are " + player.getMana() + " mana");

        return 0;
    }

    public void debugCommand(ObjectNode actionNode) {
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
        }
    }

    private void getCardsInHand(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        ArrayList<Card> cardsInHand = gameSet.players[playerIdx - 1].cardsInHand;

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: cardsInHand) {
            outputArrayNode.add(createCardNode(card));
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
            outputArrayNode.add(createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    private void getPlayerHero(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        Card hero = gameSet.players[playerIdx - 1].hero;
        actionNode.put("output", createHeroNode(hero));
    }

    private void getCardAtPosition(ObjectNode actionNode) {
        ArrayList<ArrayList<Card>> gameBoard = gameSet.gameBoard;

        if (!gameBoard.isEmpty() && !gameBoard.get(x).isEmpty()) {
            actionNode.put("output", createCardNode(gameBoard.get(x).get(y)));
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
       /* Card attacker = gameSet.getCardByCoordinates(cardAttacker);
        Card opponent = gameSet.getCardByCoordinates(cardAttacked);*/
    }

    public String getCommand() {
        return command;
    }

    public int getHandIdx() {
        return handIdx;
    }
}
