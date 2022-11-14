package main;

import fileio.CardInput;
import fileio.DecksInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static main.Helper.hasEnvironmentCardName;

public final class Player {
    private final int deckIndex;
    private final Hero hero;
    private int mana;
    private final ArrayList<ArrayList<Card>> decks = new ArrayList<>();
    private final ArrayList<Card> cardsInHand = new ArrayList<>();
    private final GameSet gameSet;
    private boolean playedHisTurn;

    public Player(final Hero hero, final DecksInput decksInput, final int deckIndex,
                  final int shuffleSeed, final GameSet gameSet) {
        this.hero = hero;
        this.deckIndex = deckIndex;
        this.gameSet = gameSet;
        this.mana = 1;

        for (int i = 0; i < decksInput.getNrDecks(); i++) {
            ArrayList<Card> deck = new ArrayList<>();
            for (int j = 0; j < decksInput.getNrCardsInDeck(); j++) {
                CardInput cardInput = decksInput.getDecks().get(i).get(j);
                if (hasEnvironmentCardName(cardInput)) {
                    deck.add(new Environment(cardInput, gameSet));
                } else {
                    deck.add(new Minion(cardInput, gameSet));
                }
            }
            this.decks.add(deck);
        }
        Random random = new Random(shuffleSeed);
        Collections.shuffle(decks.get(deckIndex), random);
        cardsInHand.add(decks.get(deckIndex).remove(0));
    }

    public int getDeckIndex() {
        return deckIndex;
    }

    public Hero getHero() {
        return hero;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public ArrayList<ArrayList<Card>> getDecks() {
        return decks;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public boolean hasPlayedHisTurn() {
        return playedHisTurn;
    }

    public GameSet getGameSet() {
        return gameSet;
    }

    public void setPlayedHisTurn(final boolean playedHisTurn) {
        this.playedHisTurn = playedHisTurn;
    }
}
