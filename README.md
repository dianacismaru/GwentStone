*Copyright (C) 2022 Cismaru Diana-Iuliana (321CA - 2022/2023)*

# GwentStone - OOP Homework

### Description of the project:
This project implements the GwentStone card game. It can be played between two
persons, on a 4x5 board game. Each player has a few card decks and a hero card.

### The Gameplay:

During the game, the AI requests **debugging commands** that are individually
implemented in the *Action* class. \
The cards in the game are represented by several types that extend the base
class:

* **Heroes**:
    * legendary cards that can use their ability on an entire row of the
      table;
    * The game ends when one of the heroes dies;

* **Minions**:
    * normal cards that can be placed on the table and can attack other
      cards, including the opponent's hero;
    * some of the minions have special abilities that the player can also use
      either on opponent's cards, in order to kill them, or on his own cards, in
      order to get bonus health;

* **Environment**:
    * cards that can be used only once per game and can not be placed on the
      table;
    * their special abilities can be used on an entire row of the board game



### Resources:
* [Object Mapper Tutorial](https://www.baeldung.com/jackson-object-mapper-tutorial)
