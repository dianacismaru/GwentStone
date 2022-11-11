package main;

import fileio.CardInput;
import fileio.DecksInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    Card hero;
    private final int nrCardsInDeck;
    private final int nrDecks;
    ArrayList<ArrayList<Card>> decks;
    ArrayList<Card> cardsInHand;
    int deckIndex;

    public Player(Card hero, DecksInput decksInput, int deckIndex, int shuffleSeed) {
        this.hero = hero;
        this.hero.setHealth(30);
        this.nrCardsInDeck = decksInput.getNrCardsInDeck();
        this.nrDecks = decksInput.getNrDecks();
        this.decks = new ArrayList<>();
        this.cardsInHand = new ArrayList<>();
        this.deckIndex = deckIndex;

        for (int i = 0; i < nrDecks; i++) {
            ArrayList<Card> deck = new ArrayList<>();
            for (int j = 0; j < nrCardsInDeck; j++) {
                CardInput cardInput = decksInput.getDecks().get(i).get(j);
                if (Helper.canBeEnvironmentCard(cardInput)) {
                    deck.add(new EnvironmentCard(cardInput));
                } else {
                    deck.add(new Card(cardInput));
                }
            }
            this.decks.add(deck);
        }
        Random random = new Random(shuffleSeed);
        Collections.shuffle(decks.get(deckIndex), random);
        cardsInHand.add(decks.get(deckIndex).remove(0));
    }
}
