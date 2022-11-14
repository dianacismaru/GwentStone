package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;

import java.util.ArrayList;

import static main.GameSet.MAX_GAMEBOARD_WIDTH;
import static main.Helper.manageError;
import static main.Helper.hasEnvironmentCardName;
import static main.Helper.createCardNode;
import static main.Helper.enemyHasTank;
import static main.Helper.getCardByCoordinates;
import static main.Helper.getMirroredRow;
import static main.Helper.getTargetRowForMinion;
import static main.Helper.isEnemyRow;
import static main.Helper.removeCardFromTable;
import static main.Helper.createHeroNode;
import static main.Helper.getCardOwnerIndex;


public final class Action {
    private final String command;
    private final int handIdx;
    private final Coordinates cardAttacker;
    private final Coordinates cardAttacked;
    private final int affectedRow;
    private final int playerIdx;
    private final int x;
    private final int y;
    private final GameSet gameSet;
    private ObjectNode actionNode;

    public Action(final GameSet gameSet, final ActionsInput actionsInput) {
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

    public int operateCommand() {
        return switch (command) {
            case "placeCard" -> placeCard();
            case "endPlayerTurn" -> gameSet.endPlayerTurn();
            case "cardUsesAttack" -> cardUsesAttack();
            case "cardUsesAbility" -> cardUsesAbility();
            case "useAttackHero" -> useAttackHero();
            case "useHeroAbility" -> useHeroAbility();
            case "useEnvironmentCard" -> useEnvironmentCard();
            default -> 1;
        };
    }

    public void debugCommand() {
        actionNode.put("command", command);

        switch (command) {
            case "getCardsInHand" -> getCardsInHand();
            case "getPlayerDeck" -> getPlayerDeck();
            case "getCardsOnTable" -> getCardsOnTable();
            case "getPlayerTurn" -> actionNode.put("output", gameSet.getPlayerTurn());
            case "getPlayerHero" -> getPlayerHero();
            case "getCardAtPosition" -> getCardAtPosition();
            case "getPlayerMana" -> getPlayerMana();
            case "getEnvironmentCardsInHand" -> getEnvironmentCardsInHand();
            case "getFrozenCardsOnTable" -> getFrozenCardsOnTable();
            case "getTotalGamesPlayed" -> actionNode.put("output", GameSet.getGameCount());
            case "getPlayerOneWins" -> actionNode.put("output", GameSet.getPlayerOneWins());
            case "getPlayerTwoWins" -> actionNode.put("output", GameSet.getPlayerTwoWins());
            default -> System.err.println("Invalid command.");
        }
    }

    public int placeCard() {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.getPlayers()[activePlayerIndex];
        ArrayList<Card> cardsInHand = player.getCardsInHand();
        Card card = cardsInHand.get(handIdx);

        if (hasEnvironmentCardName(card)) {
            return manageError(this, "Cannot place environment card on table.", actionNode);
        } else if (card.getMana() > player.getMana()) {
            return manageError(this, "Not enough mana to place card on table.", actionNode);
        }

        ArrayList<Card> targetRow = getTargetRowForMinion((Minion) card, activePlayerIndex, player);

        if (targetRow.size() == MAX_GAMEBOARD_WIDTH) {
            return manageError(this, "Cannot place card on table since row is full.", actionNode);
        }

        player.setMana(player.getMana() - card.getMana());
        targetRow.add(cardsInHand.remove(handIdx));

        return 0;
    }

    private int cardUsesAttack() {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker);
        int attackedPlayerIndex = getCardOwnerIndex(cardAttacked);

        if (attackedPlayerIndex == attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the enemy.", actionNode);
        }

        Card attacker = getCardByCoordinates(cardAttacker, gameSet);
        Card opponent = getCardByCoordinates(cardAttacked, gameSet);

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (attacker.isFrozen()) {
            return manageError(this, "Attacker card is frozen.", actionNode);
        }

        if (enemyHasTank(gameSet.getPlayers()[attackedPlayerIndex], gameSet)
                && !((Minion) opponent).isTank()) {
            return manageError(this, "Attacked card is not of type 'Tank'.",
                               actionNode);
        }

        opponent.setHealth(opponent.getHealth() - attacker.getAttackDamage());
        if (opponent.getHealth() <= 0) {
            removeCardFromTable(gameSet, opponent);
        }
        attacker.setAttacked(true);
        return 0;
    }

    private int cardUsesAbility() {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker);
        int attackedPlayerIndex = getCardOwnerIndex(cardAttacked);

        Card attacker = getCardByCoordinates(cardAttacker, gameSet);
        Card opponent = getCardByCoordinates(cardAttacked, gameSet);

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (attacker.getName().equals("Disciple") && attackedPlayerIndex != attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the current player.",
                               actionNode);
        }

        if (!attacker.getName().equals("Disciple") && attackedPlayerIndex == attackerPlayerIndex) {
            return manageError(this, "Attacked card does not belong to the enemy.", actionNode);
        }

        if (enemyHasTank(gameSet.getPlayers()[attackedPlayerIndex], gameSet)
                && !((Minion) opponent).isTank()) {
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

    public int useAttackHero() {
        int attackerPlayerIndex = getCardOwnerIndex(cardAttacker);
        Card attacker = getCardByCoordinates(cardAttacker, gameSet);
        Card hero = gameSet.getPlayers()[attackerPlayerIndex ^ 1].getHero();

        if (attacker.isFrozen()) {
            return manageError(this, "Attacker card is frozen.", actionNode);
        }

        if (attacker.hasAttacked()) {
            return manageError(this, "Attacker card has already attacked this turn.", actionNode);
        }

        if (enemyHasTank(gameSet.getPlayers()[attackerPlayerIndex ^ 1], gameSet)) {
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

    public int useHeroAbility() {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.getPlayers()[activePlayerIndex];
        Hero hero = player.getHero();

        if (player.getMana() < hero.getMana()) {
            return manageError(this, "Not enough mana to use hero's ability.", actionNode);
        }

        if (hero.hasAttacked()) {
            return manageError(this, "Hero has already attacked this turn.", actionNode);
        }

        boolean enemyRow = isEnemyRow(affectedRow, activePlayerIndex);

        if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                && !enemyRow) {
            return manageError(this, "Selected row does not belong to the enemy.", actionNode);
        }

        if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                && enemyRow) {
            return manageError(this, "Selected row does not belong to the current player.",
                               actionNode);
        }

        player.setMana(player.getMana() - hero.getMana());
        hero.useAbility(affectedRow);
        hero.setAttacked(true);
        return 0;
    }

    public int useEnvironmentCard() {
        int activePlayerIndex = gameSet.getPlayerTurn() - 1;
        Player player = gameSet.getPlayers()[activePlayerIndex];
        Card card = player.getCardsInHand().get(handIdx);

        if (!hasEnvironmentCardName(card)) {
            return manageError(this, "Chosen card is not of type environment.", actionNode);
        }

        if (player.getMana() < card.getMana()) {
            return manageError(this, "Not enough mana to use environment card.", actionNode);
        }

        if (!isEnemyRow(affectedRow, activePlayerIndex)) {
            return manageError(this, "Chosen row does not belong to the enemy.", actionNode);
        }

        if (card.getName().equals("Heart Hound")) {
            if (getMirroredRow(affectedRow, gameSet).size() == MAX_GAMEBOARD_WIDTH) {
                return manageError(this, "Cannot steal enemy card since the player's row is full.",
                        actionNode);
            }
        }
        Environment environmentCard = (Environment) card;
        environmentCard.useAbility(affectedRow);
        player.setMana(player.getMana() - environmentCard.getMana());
        player.getCardsInHand().remove(environmentCard);

        return 0;
    }

    private void getCardsInHand() {
        actionNode.put("playerIdx", playerIdx);
        ArrayList<Card> cardsInHand = gameSet.getPlayers()[playerIdx - 1].getCardsInHand();

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: cardsInHand) {
            outputArrayNode.add(createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    private void getPlayerDeck() {
        actionNode.put("playerIdx", playerIdx);
        Player player = gameSet.getPlayers()[playerIdx - 1];
        ArrayList<Card> deck = player.getDecks().get(player.getDeckIndex());

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: deck) {
            outputArrayNode.add(createCardNode(card));
        }

        actionNode.put("output", outputArrayNode);
    }

    private void getCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        ArrayList<ArrayList<Card>> gameBoard = gameSet.getGameBoard();
        for (ArrayList<Card> row : gameBoard) {
            ArrayNode rowArrayNode = objectMapper.createArrayNode();
            for (Card card : row) {
                rowArrayNode.add(createCardNode(card));
            }
            outputArrayNode.add(rowArrayNode);
        }
        actionNode.put("output", outputArrayNode);
    }

    private void getPlayerHero() {
        actionNode.put("playerIdx", playerIdx);
        Card hero = gameSet.getPlayers()[playerIdx - 1].getHero();
        actionNode.put("output", createHeroNode(hero));
    }

    private void getCardAtPosition() {
        ArrayList<ArrayList<Card>> gameBoard = gameSet.getGameBoard();

        actionNode.put("x", x);
        actionNode.put("y", y);

        if (!gameBoard.isEmpty() && gameBoard.get(x).size() > y) {
            actionNode.put("output", createCardNode(gameBoard.get(x).get(y)));
        } else {
            actionNode.put("output", "No card available at that position.");
        }
    }

    private void getPlayerMana() {
        actionNode.put("playerIdx", playerIdx);
        int mana = gameSet.getPlayers()[playerIdx - 1].getMana();
        actionNode.put("output", mana);
    }

    private void getEnvironmentCardsInHand() {
        actionNode.put("playerIdx", playerIdx);
        Player player = gameSet.getPlayers()[playerIdx - 1];

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        for (Card card: player.getCardsInHand()) {
            if (hasEnvironmentCardName(card)) {
                outputArrayNode.add(createCardNode(card));
            }
        }
        actionNode.put("output", outputArrayNode);
    }

    private void getFrozenCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputArrayNode = objectMapper.createArrayNode();

        ArrayList<ArrayList<Card>> gameBoard = gameSet.getGameBoard();
        for (ArrayList<Card> row : gameBoard) {
            for (Card card : row) {
                if (card.isFrozen()) {
                    outputArrayNode.add(createCardNode(card));
                }
            }
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

    public Coordinates getCardAttacker() {
        return cardAttacker;
    }

    public Coordinates getCardAttacked() {
        return cardAttacked;
    }

    public GameSet getGameSet() {
        return gameSet;
    }

    public void setActionNode(final ObjectNode actionNode) {
        this.actionNode = actionNode;
    }
}
