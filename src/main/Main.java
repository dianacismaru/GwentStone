package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();
        GameSet.setGameCount(0);

        while (GameSet.getGameCount() < inputData.getGames().size()) {
            GameSet gameSet = new GameSet();
            gameSet.startGame(inputData);

            //int ct = 1;
            for (Action action : gameSet.getActions()) {
                /*System.out.println("\nComanda nr. " + ct++ + " " + action.getCommand());
                System.out.println("La inceput avem runda " + gameSet.roundCount);
                System.out.println("Si este tura lui " + gameSet.playerTurn);

                for (int i = 1; i <= 2; i++) {
                    System.out.println("Player " + i + " are mana = " + gameSet.players[i-1].getMana());

                }*/
                ObjectNode actionNode = objectMapper.createObjectNode();
                if (action.getCommand().startsWith("get")) {
                    action.debugCommand(actionNode);
                    output.add(actionNode);
                } else {
                    if (action.operateCommand(actionNode) == 1) {
                        output.add(actionNode);
                    }
/*                    if (action.gameSet.gameEnded()) {
                        break;
                    }*/
                }
/*                System.out.println("Urmeaza tura lui " + gameSet.playerTurn);
                System.out.println("La final avem runda " + gameSet.roundCount);*/
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
