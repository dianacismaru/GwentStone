package main;

import fileio.ActionsInput;
import fileio.GameInput;
import fileio.Input;
import fileio.StartGameInput;

import java.util.ArrayList;

import static main.Helper.unfreezeCards;

public final class GameSet {
    private final ArrayList<ArrayList<Card>> gameBoard = new ArrayList<>();
    private int playerTurn;
    private final Player[] players = new Player[2];
    private final ArrayList<Action> actions = new ArrayList<>();
    private int roundCount;
    private boolean gameEnd;
    private static int gameCount;
    private static int playerOneWins;
    private static int playerTwoWins;
    public static final int MAX_GAMEBOARD_WIDTH = 5;
    public static final int MAX_GAMEBOARD_HEIGHT = 4;
    public static final int PLAYER_ONE_FRONT_ROW = 2;
    public static final int PLAYER_ONE_BACK_ROW = 3;
    public static final int PLAYER_TWO_FRONT_ROW = 1;
    public static final int PLAYER_TWO_BACK_ROW = 0;
    public static final int MAXIMUM_MANA_PER_ROUND = 10;

    public GameSet() {
        gameCount++;
        this.roundCount = 1;
    }

    public void startGame(final Input inputData) {
        StartGameInput input = inputData.getGames().get(gameCount - 1).getStartGame();

        for (int i = 0; i < MAX_GAMEBOARD_HEIGHT; i++) {
            gameBoard.add(new ArrayList<>(MAX_GAMEBOARD_WIDTH));
        }

        this.playerTurn = input.getStartingPlayer();

        GameInput gameInput = inputData.getGames().get(gameCount - 1);
        for (ActionsInput actionsInput: gameInput.getActions()) {
            Action action = new Action(this, actionsInput);
            actions.add(action);
        }

        this.players[0] = new Player(new Hero(input.getPlayerOneHero(), this),
                inputData.getPlayerOneDecks(), input.getPlayerOneDeckIdx(),
                input.getShuffleSeed(), this);
        this.players[1] = new Player(new Hero(input.getPlayerTwoHero(), this),
                inputData.getPlayerTwoDecks(), input.getPlayerTwoDeckIdx(),
                input.getShuffleSeed(), this);
    }

    public int endPlayerTurn() {
        players[playerTurn - 1].setPlayedHisTurn(true);
        unfreezeCards(playerTurn - 1, this);

        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }

        // Start a new round
        if (players[playerTurn - 1].hasPlayedHisTurn()) {
            if (roundCount < MAXIMUM_MANA_PER_ROUND) {
                roundCount++;
            }

            for (Player player: players) {
                player.setMana(player.getMana() + roundCount);
                player.setPlayedHisTurn(false);
                player.getHero().setAttacked(false);
                if (!player.getDecks().get(player.getDeckIndex()).isEmpty()) {
                    Card firstDeckCard = player.getDecks().get(player.getDeckIndex()).get(0);
                    player.getDecks().get(player.getDeckIndex()).remove(0);
                    player.getCardsInHand().add(firstDeckCard);
                }
            }

            for (ArrayList<Card> deck: gameBoard) {
                for (Card card: deck) {
                    card.setAttacked(false);
                }
            }
        }
        return 0;
    }

    public ArrayList<ArrayList<Card>> getGameBoard() {
        return gameBoard;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public Player[] getPlayers() {
        return players;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public boolean gameEnded() {
        return gameEnd;
    }

    public void setGameEnd(final boolean gameEnd) {
        this.gameEnd = gameEnd;
    }

    public static int getGameCount() {
        return gameCount;
    }

    public static void setGameCount(final int gameCount) {
        GameSet.gameCount = gameCount;
    }

    public static int getPlayerOneWins() {
        return playerOneWins;
    }

    public static void setPlayerOneWins(final int playerOneWins) {
        GameSet.playerOneWins = playerOneWins;
    }

    public static int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public static void setPlayerTwoWins(final int playerTwoWins) {
        GameSet.playerTwoWins = playerTwoWins;
    }
}
