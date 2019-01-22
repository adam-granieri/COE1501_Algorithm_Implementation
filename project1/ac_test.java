//Adam Granieri
//COE 1501 Project 1

import java.util.*;
import java.io.*;

public class ac_test {
    public static void main(String[] args) {
        //Variables
        char input;
        int predictionCount = 0;
        DLBTrie dictionary = new DLBTrie();
        DLBTrie userHistory = new DLBTrie();
        double start = 0, end = 0, avg = 0, total = 0;
        String outWord, currWord;
        ArrayList<String> predictions = new ArrayList<String>();
        ArrayList<String> suggestions = new ArrayList<String>();
        ArrayList<String> dictPredictions = new ArrayList<String>();
        ArrayList<String> historyPredictions = new ArrayList<String>();

        //Make Dictionary DLB
        try {
            Scanner dictionaryFile = new Scanner(new File("dictionary.txt"));
            while ( dictionaryFile.hasNextLine() ) {
                dictionary.add(dictionaryFile.nextLine());
            }
            dictionaryFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find dictionary");
        }

        //try-catch to detect if user_dictionary.txt exists, if not, make it.
        try {
            Scanner userHistoryFile = new Scanner( new File("user_dictionary.txt"));
            while ( userHistoryFile.hasNextLine() ) {
                userHistory.add(userHistoryFile.nextLine());
            }
            userHistoryFile.close();
        } catch (FileNotFoundException e) {
            try{
                FileWriter userHistoryCreator = new FileWriter( new File("user_dictionary.txt"));
                userHistoryCreator.close();
            } catch (IOException i) {
                System.out.println("Error creating user history file");
            }
        }

        //Start Loop for user input
        Scanner userIn = new Scanner(System.in);
        System.out.print("\nEnter yout first character: ");
        do {
            currWord = "";
            if ( predictionCount > 0 ) { //If we already had one word predicted
                System.out.print("Enter first character of the next word: ");
            }
            input = userIn.nextLine().charAt(0);

            predictions.clear();
            dictionary.reset();
            userHistory.reset();

            //Check that input is valid
            while ( input >= 'A' ) {
                suggestions.clear();
                currWord += input;
                outWord = "";
                int displayCounter = 1;
                
                start = System.nanoTime();
                dictPredictions = dictionary.makePrediction(input);
                historyPredictions = userHistory.makePrediction(input);

                //Loop to first display previous history letters
                for ( int i = 0; i < historyPredictions.size(); i++ ) {
                    suggestions.add(historyPredictions.get(i));
                    outWord += ("(" + (displayCounter) + ") " + historyPredictions.get(i) + "  ");
                    displayCounter++;
                }

                //Loop to check if same word is in history and dictionary and only display one
                for ( String word : dictPredictions ) {
                    if ( suggestions.size() == 5 ) {
                        break; //text already full
                    } else if ( suggestions.contains(word) ) {
                        continue; //it's already in so we're fine
                    } else {
                        suggestions.add(word);
                        outWord += ("(" + (displayCounter) + ") " + word + "    ");
                        displayCounter++;
                    }
                }
                end = System.nanoTime();

                //Check if nothing matches
                if ( outWord.equals("") ) {
                    outWord = "No predicted words were found. \n Enter '$' once finished to save word.";
                }
                outWord += "\n";
                System.out.print("\n(");

                //calculate time
                System.out.print((end-start)/10000000.0);
                System.out.print(" s) \nPredictions: \n" + outWord);
                predictionCount++;
                total += (end-start)/10000000.0;

                //Prompt next input
                System.out.print("\nEnter the next character: ");
                input = userIn.nextLine().charAt(0);
            }

            //Check if user enters a number
            if ( input < '6' && input > '0' ) {
                System.out.print("\n\nWORD COMPLETED:  " + suggestions.get(input - '1') + "\n");

                //See if history predictions contains the suggestion from user
                if ( !historyPredictions.contains(suggestions.get(input - '1'))) {
                    userHistory.add(suggestions.get(input - '1'));

                    try {
                        FileWriter userHistFileWriter = new FileWriter(new File("user_dictionary.txt"));
                        userHistFileWriter.write(suggestions.get(input - '1') + "\n");
                        userHistFileWriter.close();
                    } catch (IOException e) {
                        System.out.println("Issue with writing to user_history");
                    }
                }
            } else if ( input == '$' ) {
                System.out.print("\n\nWORD COMPLETED:  " + currWord + "\n");

                if (!historyPredictions.contains(currWord)) {
                    userHistory.add(currWord);

                    try {
                        FileWriter userHistFileWriter = new FileWriter(new File("user_dictionary.txt"));
                        userHistFileWriter.write(currWord + "\n");
                        userHistFileWriter.close();
                    } catch (IOException e) {
                        System.out.println("Issue with writing to user_history");
                    }
                }
            }
        } while ( input != '!');
        userIn.close();

        //Final display
        avg = total/predictionCount;
        System.out.println("\n\nAverage Time: " + avg + " s");
        System.out.println("Bye!");
    }

}