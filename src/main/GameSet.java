package main;

import fileio.ActionsInput;
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

        this.startingPlayer = input.getStartingPlayer();
        this.playerTurn = startingPlayer;
        this.shuffleSeed = input.getShuffleSeed();

        ArrayList<ActionsInput> actionsInputs = inputData.getGames().get(0).getActions();
        for (ActionsInput actionsInput: actionsInputs) {
            Action action = new Action(this, actionsInput);
            actions.add(action);
        }

        this.players[0] = new Player(new Card(input.getPlayerOneHero()),
                inputData.getPlayerOneDecks(), input.getPlayerOneDeckIdx(), shuffleSeed);
        this.players[1] = new Player(new Card(input.getPlayerTwoHero()),
                inputData.getPlayerTwoDecks(), input.getPlayerTwoDeckIdx(), shuffleSeed);

        this.play();
    }

    public void play() {
        // se contorizeaza rundele
    }
}
