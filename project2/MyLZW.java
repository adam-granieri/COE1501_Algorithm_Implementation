/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

 /*
    Adam Granieri
    COE 1501: Algorithm Implementation
    Dr. Farnan
    Project2 : LZW
    Fall 2018
 */

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static final int L = 65536;       // number of codewords = 2^W, new max of 16bit
    private static int C = 512;              // current codewords
    private static int code = R+1;           // our radix size plus one for codewords
    private static int MIN_WIDTH = 9;        // minimum length for bitstring codeword
    private static int nextCode;             // Stores next codeword in expansion

    public static void compress(String mode) { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);

        //Extra Vars
        double ratio = 0, oldRatio = 0, newRatio = 1; // Ratio vars for compression analysis
        int bitCount = 0, numChars = 0;               // Holds number of bits/chars read in
        boolean monitor = false;                      // flag if we're monitoring
        int modeType = 0;                             // int var to signify which mode we have

        //if structure to assign modeType
        if (mode.equals("m")) {
            modeType = 2;
        } else if (mode.equals("r")) {
            modeType = 1;
        }

        //Write out modetype to read in and codelentgh for expansion later
        BinaryStdOut.write(modeType, MIN_WIDTH);

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), MIN_WIDTH);      // Print s's encoding.
            int t = s.length();
            //added in counts
            bitCount += MIN_WIDTH;
            numChars += t;

            // Ratio math
            if (code >= L && !monitor) {
                monitor = true;
                oldRatio = (double)(numChars*8)/bitCount;
            }

            if (monitor) {
                newRatio = (double)(numChars*8)/bitCount;
                ratio = oldRatio/newRatio;
            }

            //see if codebook is full, then reset code book for r mode
            if (code >= L && mode.equals("r")) {
                st = resetDictionary();
            } else if (mode.equals("m") && ratio > 1.1 && monitor) {
                st = resetDictionary();
                monitor = false;
            }

            if (t < input.length() && code < L) {   // Add s to symbol table.
                if (code == C) { // Additional check to see if max code reached and increase min with and avalible codes avalible
                    MIN_WIDTH++;
                    C *= 2;
                }
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, MIN_WIDTH);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[L];
        //replace i with nextCode

        //Extra Vars same as in compression
        double ratio = 1, oldRatio = 0, newRatio = 1; // Ratio vars for compression analysis
        int bitCount = 0, numChars = 0;               // Holds number of bits/chars read in
        boolean monitor = false;                      // flag if we're monitoring
        int modeType = BinaryStdIn.readInt(MIN_WIDTH);// int var to signify which mode we have
        String mode = "";                             // modeType in String form

        //Conditional for modeType used in compression
        if (modeType == 1) {
            mode = "r";
        } else if (modeType == 2) {
            mode = "m";
        } 

        // initialize symbol table with all 1-character strings
        for (nextCode = 0; nextCode < R; nextCode++)
            st[nextCode] = "" + (char) nextCode;
        st[nextCode++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(MIN_WIDTH);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            //Added in Counts
            bitCount += MIN_WIDTH;
            numChars += val.length();

            //Conditional for ratios
            if (nextCode >= L && !monitor) {
                oldRatio = (double)(numChars*8)/bitCount;
                monitor = true;
            }

            if (monitor) {
                newRatio = (double)(numChars*8)/bitCount;    
                ratio = oldRatio/newRatio;            
            }

            //Mode check for resetting codebok expansion
            if (nextCode >= L && mode.equals("r")) {
                st = resetExpansion();
            }

            if (mode.equals("m") && monitor && ratio > 1.1) {
                st = resetExpansion();
                monitor = false;
            }

            BinaryStdOut.write(val);

            //Check and double avalible code word lengths
            if (nextCode == C && MIN_WIDTH < 16) {
                MIN_WIDTH++;
                C *= 2;
            }

            codeword = BinaryStdIn.readInt(MIN_WIDTH);
            if (codeword == R) break;
            String s = st[codeword];
            if (nextCode == codeword) s = val + val.charAt(0);   // special case hack
            if (nextCode < L) st[nextCode++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    //Added method for reseting dictionary if we encounter case where codebook needs to be undone
    public static TST<Integer> resetDictionary() {
        TST<Integer> st = new TST<>();
        for (int i = 0; i < R; i++) {
            st.put("" + (char)i, i);
        }
        code = R+1;
        C = 512;
        return st;
    }

    //Added method for reseting expansion of compressed file due to resizing ratio
    public static String[] resetExpansion() {
        String[] st = new String[L];
        for (nextCode = 0; nextCode < R; nextCode++) {
            st[nextCode] = "" + (char)nextCode;
        }
        st[nextCode++] = "";
        MIN_WIDTH = 9;
        C = 512;
        return st;
    }

    //Edited to add support for new mode input
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            try{
                compress(args[1]);
            } catch (Exception e) {
                System.out.println("Follow correct command line usage.");
                throw new IllegalArgumentException("Illegal command line argument");
            }
        } else if (args[0].equals("+")) {
            expand();
            /*try{
                expand();
            } catch (Exception e) {
                System.out.println("Follow correct command line usage.");
                throw new IllegalArgumentException("Illegal command line argument");                
            }*/
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    } // End main

} // End Class
