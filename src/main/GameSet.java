package main;

import fileio.ActionsInput;
import fileio.Coordinates;
import fileio.Input;
import fileio.StartGameInput;

import java.util.ArrayList;

import static main.Helper.unfreezeCards;

public class GameSet {
    ArrayList<ArrayList<Card>> gameBoard = new ArrayList<>();
    private int playerTurn;
    Player[] players = new Player[2];
    private final ArrayList<Action> actions = new ArrayList<>();
    private int roundCount;
    private boolean gameEnd;
    private static int gameCount;

    public GameSet() {
        gameCount++;
        this.roundCount = 1;
    }

    public void startGame(Input inputData) {
        StartGameInput input = inputData.getGames().get(0).getStartGame();

        for (int i = 0; i < 4; i++) {
            gameBoard.add(new ArrayList<>(5));
        }

        this.playerTurn = input.getStartingPlayer();

        ArrayList<ActionsInput> actionsInputs = inputData.getGames().get(0).getActions();
        for (ActionsInput actionsInput: actionsInputs) {
            Action action = new Action(this, actionsInput);
            actions.add(action);
        }

        this.players[0] = new Player(new Card(input.getPlayerOneHero(), this),
                inputData.getPlayerOneDecks(), input.getPlayerOneDeckIdx(),
                input.getShuffleSeed(), this);
        this.players[1] = new Player(new Card(input.getPlayerTwoHero(), this),
                inputData.getPlayerTwoDecks(), input.getPlayerTwoDeckIdx(),
                input.getShuffleSeed(), this);
    }

    void endPlayerTurn() {
        // mark that the current player has finished his turn
        players[playerTurn - 1].setPlayedHisTurn(true);
        unfreezeCards(playerTurn - 1, this);

        // change to the next player
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }

        // check if the other player has also played his turn
        // start a new round
        if (players[playerTurn - 1].hasPlayedHisTurn()) {
            if (roundCount < 10)
                roundCount++;

            for (Player player: players) {
                player.setMana(player.getMana() + roundCount);
                player.setPlayedHisTurn(false);
                if (!player.decks.get(player.deckIndex).isEmpty()) {
                    Card firstDeckCard = player.decks.get(player.deckIndex).get(0);
                    player.decks.get(player.deckIndex).remove(0);
                    player.cardsInHand.add(firstDeckCard);
                }
            }

            for (ArrayList<Card> deck: gameBoard) {
                for (Card card: deck) {
                    card.setAttacked(false);
                }
            }
        }
    }

    public Card getCardByCoordinates(Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        return gameBoard.get(x).get(y);
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public static int getGameCount() {
        return gameCount;
    }

    public static void setGameCount(int gameCount) {
        GameSet.gameCount = gameCount;
    }

    public boolean gameEnded() {
        return gameEnd;
    }

    public void setGameEnd(boolean gameEnd) {
        this.gameEnd = gameEnd;
    }
}
