import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Chat-Client-Server Project
 *
 * This class represents a chat filter for messages sent to the server.
 * A chatfilter can be passed as an argument for the ChatServer executable
 * This would then enable censoring of words specified in the respective
 * text file that would constitute said ChatFilter
 *
 * @author Neso Udora, pudora@purdue.edu
 * @author Zach Skiles, skilesz@purdue.edu
 *
 * @version 2018-11-14
 */

public class ChatFilter {

    //public variables

    ArrayList<String> badWords;



    //constructor

    public ChatFilter(String badWordsFileName) {
        File file = new File(badWordsFileName);
        this.badWords = new ArrayList<>();

        if (file.exists()) {
            try {
                Scanner in = new Scanner(file);

                while (in.hasNextLine()) {
                    String[] currentLine = in.nextLine().split(" ");

                    this.badWords.addAll(Arrays.asList(currentLine));
                }

            } catch (IOException e) {
                System.out.println("File ERROR! Please check file.");
                e.printStackTrace();
            }
        }

    }



    public String filter(String msg) {
        String[] message = msg.split(" ");
        String finalResult = "";

        for (int i = 0; i < message.length; i++) {
            if (badWords.contains(message[i])) {
                String result = "";
                for (int j = 0; j < message[i].length(); j++) {
                    result += "*";
                }

                message[i] = result;
            }
        }

        finalResult += message[0];

        for (int i = 1; i < message.length; i++) {
            finalResult += " " + message[i];
        }


        return finalResult;
    }
}
