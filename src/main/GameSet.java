package main;

import fileio.ActionsInput;
import fileio.Coordinates;
import fileio.Input;
import fileio.StartGameInput;

import java.util.ArrayList;

public class GameSet {
    ArrayList<ArrayList<Card>> gameBoard = new ArrayList<>();
    static int gameCount;
    int startingPlayer;
    int shuffleSeed;
    int playerTurn;
    Player[] players = new Player[2];
    ArrayList<Action> actions = new ArrayList<>();
    int roundCount;

    public GameSet() {
        gameCount++;
        this.roundCount = 1;
    }

    public void startGame(Input inputData) {
        StartGameInput input = inputData.getGames().get(0).getStartGame();

        for (int i = 0; i < 4; i++) {
            gameBoard.add(new ArrayList<>(5));
        }

        this.startingPlayer = input.getStartingPlayer();
        this.playerTurn = startingPlayer;
        this.shuffleSeed = input.getShuffleSeed();

        ArrayList<ActionsInput> actionsInputs = inputData.getGames().get(0).getActions();
        for (ActionsInput actionsInput: actionsInputs) {
            Action action = new Action(this, actionsInput);
            actions.add(action);
        }

        this.players[0] = new Player(new Card(input.getPlayerOneHero(), this),
                inputData.getPlayerOneDecks(), input.getPlayerOneDeckIdx(),
                shuffleSeed, this);
        this.players[1] = new Player(new Card(input.getPlayerTwoHero(), this),
                inputData.getPlayerTwoDecks(), input.getPlayerTwoDeckIdx(),
                shuffleSeed, this);
    }

    public Card getCardByCoordinates(Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        return gameBoard.get(x).get(y);
    }
}
