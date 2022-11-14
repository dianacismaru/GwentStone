package main;

import fileio.CardInput;
import fileio.DecksInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static main.Helper.canBeEnvironmentCard;

public class Player {
    private Hero hero;
    ArrayList<ArrayList<Card>> decks = new ArrayList<>();
    ArrayList<Card> cardsInHand = new ArrayList<>();
    int deckIndex;
    GameSet gameSet;
    private int mana;
    private boolean playedHisTurn;

    public Player(Hero hero, DecksInput decksInput, int deckIndex, int shuffleSeed, GameSet gameSet) {
        this.hero = hero;
        this.hero.setHealth(30);
        this.deckIndex = deckIndex;
        this.gameSet = gameSet;
        this.mana = 1;

        for (int i = 0; i < decksInput.getNrDecks(); i++) {
            ArrayList<Card> deck = new ArrayList<>();
            for (int j = 0; j < decksInput.getNrCardsInDeck(); j++) {
                CardInput cardInput = decksInput.getDecks().get(i).get(j);
                if (canBeEnvironmentCard(cardInput)) {
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

    public Hero getHero() {
        return hero;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public boolean hasPlayedHisTurn() {
        return playedHisTurn;
    }

    public void setPlayedHisTurn(boolean playedHisTurn) {
        this.playedHisTurn = playedHisTurn;
    }
}