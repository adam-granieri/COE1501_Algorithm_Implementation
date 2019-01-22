import java.util.ArrayList;

//Adam Granieri
//COE 1501 Project 1

public class DLBTrie {
    private final char END = '^'; //char to denote end of word

    Node head; //Head Node of Tree
    Node curr; //Current Node
    Node itr; //Node for iterating through trie

    public DLBTrie() {

    }

    public void add(String input) {
        input += END;

        //Check for empty trie
        if ( head == null ) {
            this.addBeginning(input);

        } else {
            curr = head;
            //Iterate over input length to add into Trie
            for (int i = 0; i < input.length(); i++) {
                //Check if node character is equal char at user input
                if ( curr.letter == input.charAt(i) || (curr.letter != END && curr.child == null)) {
                    //Check if we need to create new node, and continue
                    if ( curr.child == null ) {
                        curr.child = new Node(input.charAt(i), input);
                        curr = curr.child;
                    } else {
                        curr = curr.child;
                    }

                } else {
                    //Iterate horizontally until something matches or we reach the end
                    while ( curr.sibling != null && curr.letter != input.charAt(i) ) {
                        curr = curr.sibling;
                    }

                    //Found a match, go down a level
                    if ( curr.letter == input.charAt(i) ) {
                        curr = curr.child;
                    } else if ( curr.sibling == null ) { //No sibling match, make a new one
                        curr.sibling = new Node(input.charAt(i), input);
                        curr = curr.sibling;
                    }
                }
            }
        }
    }

    //Method to add first word into Trie
    public void addBeginning(String input) {
        head = new Node(input.charAt(0), input);
        curr = head;

        //load in chars of word into Trie, skipping 0
        for (int i = 1; i < input.length(); i++) {
            curr.child = new Node(input.charAt(i), input);
            curr = curr.child;
        }
    }

    //resets iterating nod efor predictions to head to start again
    public void reset() {
        itr = head;
    }

    //method to provide arraylist of user suggestions
    public ArrayList<String> makePrediction(char input) {
        //Check if tris is empty
        if ( head == null ) {
            return new ArrayList<String>(); //return empty predictions
        }

        //iterate through siblings
        while ( itr.sibling != null && itr.letter != input) {
            itr = itr.sibling;
        }

        //We get a match, go down a level
        if ( itr.letter == input ) {
            itr = itr.child;
        } else if ( itr.letter != input ) { //make a new node for the letter
            itr = new Node();
            return new ArrayList<String>();
        }

        return itr.makePrediction();
    }
}