package main;

import fileio.ActionsInput;
import fileio.Coordinates;
import fileio.Input;

public class Action {
    private String command;
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

    public void operateCommand() {
        Player player;
        switch(command) {
            case "getCardsInHand":
                break;

            case "getPlayerDeck":
                player = gameSet.players[playerIdx - 1];
                System.out.println(player.decks.get(player.deckIndex));
                // se afiseaza deck-ul playerului
                break;

            case "getCardsOnTable":
                break;

            case "getPlayerTurn":
                System.out.println(gameSet.playerTurn);
                break;

            case "getPlayerHero":
                player = gameSet.players[playerIdx - 1];
                System.out.println(player.hero);
                // se afiseaza card-ul eroului
                break;

            case "getCardAtPosition":
                break;

            case "getPlayerMana":
                break;

            case "getEnvironmentCardsInHand":
                break;

            case "getFrozenCardsOnTable":
                break;
        }
    }
}
