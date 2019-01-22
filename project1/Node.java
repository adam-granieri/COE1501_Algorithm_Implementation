//Adam Granieri
//COE 1501 Project 1

//using arrayList to store prediction
import java.util.ArrayList;

public class Node {
    char letter;
    Node sibling = null;
    Node child = null;
    String word;

    //null constructor
    public Node() {

    }

    public Node(char ch, String currWord) {
        letter = ch;
        this.word = currWord.substring(0,(currWord.length() - 1));
    }

    public ArrayList<String> makePrediction() {
        ArrayList<String> prediction = new ArrayList<String>();

        //Check if END character has been reached
        if ( letter == '^' ) {
            prediction.add(word);
        }
        //add next letter if child isn't null
        if ( child != null ) {
            prediction.addAll(child.makePrediction());
        }
        //add next letter if sibling isn't null
        if ( sibling != null ) {
            prediction.addAll(sibling.makePrediction());
        }

        return prediction;
    }
}