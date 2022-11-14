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
        return switch (command) {
            case "placeCard" -> placeCard(actionNode);
            case "endPlayerTurn" -> gameSet.endPlayerTurn();
            case "cardUsesAttack" -> cardUsesAttack(actionNode);
            case "cardUsesAbility" -> cardUsesAbility(actionNode);
            case "useAttackHero" -> useAttackHero(actionNode);
            case "useHeroAbility" -> useHeroAbility(actionNode);
            case "useEnvironmentCard" -> useEnvironmentCard(actionNode);
            default -> 0;
        };
    }

    public int placeCard(ObjectNode actionNode) {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.players[activePlayerIndex];
        ArrayList<Card> cardsInHand = player.cardsInHand;
        Card card = cardsInHand.get(handIdx);

        if (isEnvironmentCard(card)) {
            return manageError(this, "Cannot place environment card on table.", actionNode);
        } else if (card.getMana() > player.getMana()) {
            return manageError(this, "Not enough mana to place card on table.", actionNode);
        }

        ArrayList<Card> targetRow = getTargetRowForMinion((Minion) card, activePlayerIndex, player);

        if (targetRow.size() == 5) {
            return manageError(this, "Cannot place card on table since row is full.", actionNode);
        }

        player.setMana(player.getMana() - card.getMana());
        targetRow.add(cardsInHand.remove(handIdx));

        return 0;
    }

    public int useEnvironmentCard(ObjectNode actionNode) {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.players[activePlayerIndex];
        Card card = player.cardsInHand.get(handIdx);

        if (!isEnvironmentCard(card)) {
            return manageError(this, "Chosen card is not of type environment.", actionNode);
        }

        if (player.getMana() < card.getMana()) {
            return manageError(this, "Not enough mana to use environment card.", actionNode);
        }

        if (!isEnemyRow(affectedRow, activePlayerIndex)) {
            return manageError(this, "Chosen row does not belong to the enemy.", actionNode);
        }

        if (card.getName().equals("Heart Hound")) {
            if (getMirroredRow(affectedRow, gameSet).size() == 5) {
                return manageError(this, "Cannot steal enemy card since the player's row is full.", actionNode);
            }
        }
        Environment environmentCard = (Environment) card;
        environmentCard.useAbility(affectedRow);
        player.setMana(player.getMana() - environmentCard.getMana());
        player.cardsInHand.remove(environmentCard);

        return 0;
    }

    private int cardUsesAttack(ObjectNode actionNode) {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker, gameSet.players);
        int attackedPlayerIndex = getCardOwnerIndex(cardAttacked, gameSet.players);

        if (attackedPlayerIndex == attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the enemy.", actionNode);
        }

        Card attacker = gameSet.getCardByCoordinates(cardAttacker);
        Card opponent = gameSet.getCardByCoordinates(cardAttacked);

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (attacker.isFrozen()) {
            return manageError(this, "Attacker card is frozen.", actionNode);
        }

        if (enemyHasTank(gameSet.players[attackedPlayerIndex], gameSet) && !((Minion) opponent).isTank()) {
            return manageError(this, "Attacked card is not of type 'Tank'.", actionNode);
        }

        opponent.setHealth(opponent.getHealth() - attacker.getAttackDamage());
        if (opponent.getHealth() <= 0) {
            removeCardFromTable(gameSet, opponent);
        }
        attacker.setAttacked(true);
        return 0;
    }

    private int cardUsesAbility(ObjectNode actionNode) {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker, gameSet.players);
        int attackedPlayerIndex = getCardOwnerIndex(cardAttacked, gameSet.players);

        Card attacker = gameSet.getCardByCoordinates(cardAttacker);
        Card opponent = gameSet.getCardByCoordinates(cardAttacked);

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (attacker.getName().equals("Disciple") && attackedPlayerIndex != attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the current player.", actionNode);
        }

        if (!attacker.getName().equals("Disciple") && attackedPlayerIndex == attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the enemy.", actionNode);
        }

        if (enemyHasTank(gameSet.players[attackedPlayerIndex], gameSet) && !((Minion) opponent).isTank()) {
            return manageError(this, "Attacked card is not of type 'Tank'.", actionNode);
        }

        if (attacker.isFrozen()) {
            return manageError(this, "Attacker card is frozen.", actionNode);
        }

        ((Minion) attacker).useAbility(attacker, opponent);
        attacker.setAttacked(true);

        if (opponent.getHealth() <= 0) {
            removeCardFromTable(gameSet, opponent);
        }
        return 0;
    }

    public int useAttackHero(ObjectNode actionNode) {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker, gameSet.players);
        Card attacker = gameSet.getCardByCoordinates(cardAttacker);
        Card hero = gameSet.players[attackerPlayerIndex ^ 1].getHero();

        if (attacker.isFrozen()) {
            return manageError(this, "Attacker card is frozen.", actionNode);
        }

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (enemyHasTank(gameSet.players[attackerPlayerIndex ^ 1], gameSet)) {
            return manageError(this, "Attacked card is not of type 'Tank'.", actionNode);
        }

        hero.setHealth(hero.getHealth() - attacker.getAttackDamage());
        if (hero.getHealth() <= 0) {
            gameSet.setGameEnd(true);
            if (attackerPlayerIndex == 0) {
                GameSet.setPlayerOneWins(GameSet.getPlayerOneWins() + 1);
                return manageError(this, "Player one killed the enemy hero.", actionNode);
            }
            GameSet.setPlayerTwoWins(GameSet.getPlayerTwoWins() + 1);
            return manageError(this, "Player two killed the enemy hero.", actionNode);
        }
        attacker.setAttacked(true);
        return 0;
    }

    public int useHeroAbility(ObjectNode actionNode) {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.players[activePlayerIndex];
        Hero hero = player.getHero();

        if (player.getMana() < hero.getMana()) {
            return manageError(this, "Not enough mana to use hero's ability.", actionNode);
        }

        if (hero.hasAttacked()) {
            return manageError(this, "Hero has already attacked this turn.", actionNode);
        }

        boolean enemyRow = isEnemyRow(affectedRow, activePlayerIndex);

        if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) && !enemyRow) {
            return manageError(this, "Selected row does not belong to the enemy.", actionNode);
        }

        if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) && enemyRow) {
            return manageError(this, "Selected row does not belong to the current player.", actionNode);
        }

        player.setMana(player.getMana() - hero.getMana());
        hero.useAbility(affectedRow);
        hero.setAttacked(true);
        return 0;
    }

    public void debugCommand(ObjectNode actionNode) {
        actionNode.put("command", command);

        switch (command) {
            case "getCardsInHand" -> getCardsInHand(actionNode);
            case "getPlayerDeck" -> getPlayerDeck(actionNode);
            case "getCardsOnTable" -> getCardsOnTable(actionNode);
            case "getPlayerTurn" -> actionNode.put("output", gameSet.getPlayerTurn());
            case "getPlayerHero" -> getPlayerHero(actionNode);
            case "getCardAtPosition" -> getCardAtPosition(actionNode);
            case "getPlayerMana" -> getPlayerMana(actionNode);
            case "getEnvironmentCardsInHand" -> getEnvironmentCardsInHand(actionNode);
            case "getFrozenCardsOnTable" -> getFrozenCardsOnTable(actionNode);
            case "getTotalGamesPlayed" -> actionNode.put("output", GameSet.getGameCount());
            case "getPlayerOneWins" -> actionNode.put("output", GameSet.getPlayerOneWins());
            case "getPlayerTwoWins" -> actionNode.put("output", GameSet.getPlayerTwoWins());
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

    private void getCardsOnTable(ObjectNode actionNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        ArrayList<ArrayList<Card>> gameBoard = gameSet.gameBoard;
        for (ArrayList<Card> row : gameBoard) {
            ArrayNode rowArrayNode = objectMapper.createArrayNode();
            for (Card card : row) {
                rowArrayNode.add(createCardNode(card));
            }
            outputArrayNode.add(rowArrayNode);
        }
        actionNode.put("output", outputArrayNode);
    }

    private void getFrozenCardsOnTable(ObjectNode actionNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        ArrayList<ArrayList<Card>> gameBoard = gameSet.gameBoard;
        for (ArrayList<Card> row : gameBoard) {
            for (Card card : row) {
                if (card.isFrozen())
                    outputArrayNode.add(createCardNode(card));
            }
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
        Card hero = gameSet.players[playerIdx - 1].getHero();
        actionNode.put("output", createHeroNode(hero));
    }

    private void getCardAtPosition(ObjectNode actionNode) {
        ArrayList<ArrayList<Card>> gameBoard = gameSet.gameBoard;

        actionNode.put("x", x);
        actionNode.put("y", y);

        if (!gameBoard.isEmpty() && gameBoard.get(x).size() > y) {
            actionNode.put("output", createCardNode(gameBoard.get(x).get(y)));
        } else {
            actionNode.put("output", "No card available at that position.");
        }
    }

    private void getPlayerMana(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        int mana = gameSet.players[playerIdx - 1].getMana();
        actionNode.put("output", mana);
    }

    private void getEnvironmentCardsInHand(ObjectNode actionNode) {
        actionNode.put("playerIdx", playerIdx);
        Player player = gameSet.players[playerIdx - 1];

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: player.cardsInHand) {
            if (isEnvironmentCard(card))
                outputArrayNode.add(createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    public String getCommand() {
        return command;
    }

    public int getHandIdx() {
        return handIdx;
    }

    public int getAffectedRow() {
        return affectedRow;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinates getCardAttacker() {
        return cardAttacker;
    }

    public Coordinates getCardAttacked() {
        return cardAttacked;
    }
}
