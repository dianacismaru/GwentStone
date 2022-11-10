package main;

import fileio.DecksInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    int wins;
    Card hero;
    private final int nrCardsInDeck;
    private final int nrDecks;
    ArrayList<ArrayList<Card>> decks;
    int deckIndex;

    public Player(Card hero, DecksInput decksInput, int deckIndex, int shuffleSeed) {
        this.hero = hero;
        this.decks = new ArrayList<>();
        this.nrCardsInDeck = decksInput.getNrCardsInDeck();
        this.nrDecks = decksInput.getNrDecks();
        this.deckIndex = deckIndex;

        // adauga deck-urile din input
        for (int i = 0; i < nrDecks; i++) {
            ArrayList<Card> deck = new ArrayList<>();
            for (int j = 0; j < nrCardsInDeck; j++) {
                deck.add(new Card(decksInput.getDecks().get(i).get(j)));
            }
            this.decks.add(deck);
        }
        Random random = new Random(shuffleSeed);
        Collections.shuffle(decks.get(deckIndex), random);
    }
}
