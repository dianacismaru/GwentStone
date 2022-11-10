package main;

import fileio.ActionsInput;
import fileio.Input;
import fileio.StartGameInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameSet {
    static int gameCount;
    public int[][] game = new int[4][5];
    /*int firstPlayerDeckIndex;
    int secondPlayerDeckIndex;*/
    int startingPlayer;
    int shuffleSeed;
    int playerTurn;
    Player[] players = new Player[2];
    ArrayList<Action> actions = new ArrayList<>();

    public GameSet() {
        gameCount++;
    }

    // se apeleaza cand se incepe jocul nou
    public void startGame(Input inputData) {
        StartGameInput input = inputData.getGames().get(0).getStartGame();
       /* this.firstPlayerDeckIndex = input.getPlayerOneDeckIdx();
        this.secondPlayerDeckIndex = input.getPlayerTwoDeckIdx();*/
        this.startingPlayer = input.getStartingPlayer();
        this.shuffleSeed = input.getShuffleSeed();

        ArrayList<ActionsInput> actionsInputs = inputData.getGames().get(0).getActions();
        for (ActionsInput actionsInput: actionsInputs) {
            Action action = new Action(this, actionsInput);
            actions.add(action);
        }

        /*this.firstPlayerHero = new Card(input.getPlayerOneHero());
        this.secondPlayerHero = new Card(input.getPlayerTwoHero());*/
        this.players[0] = new Player(new Card(input.getPlayerOneHero()),
                inputData.getPlayerOneDecks(), input.getPlayerOneDeckIdx(), shuffleSeed);
        this.players[1] = new Player(new Card(input.getPlayerTwoHero()),
                inputData.getPlayerTwoDecks(), input.getPlayerTwoDeckIdx(), shuffleSeed);
    }
}
