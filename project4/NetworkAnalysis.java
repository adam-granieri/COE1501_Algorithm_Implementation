import java.util.Scanner;

//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

public class NetworkAnalysis {
    public static void main(String[] args) {
        //Check user input
        verifyInput(args);
        welcome();
        Graph graph = new Graph(args[0]);
        Scanner input = new Scanner(System.in);
        boolean again = true;
        int val;

        //Loop for function menu
        do {
            MainMenu();
            val = input.nextInt();
            switch (val) {
                case 1:
                    firstMenu(graph);
                    break;
                case 2:
                    secondMenu(graph);
                    break;
                case 3:
                    thirdMenu(graph);
                    break;
                case 4:
                    graph.connectedVertexFailure();
                    break;
                case 5:
                    again = false;
                    break;
                default:
                    p("ERROR: Invalid input value");
                    break;
            }
        } while (again);

        goodbye();
        input.close();
    }

    //Menu Functions
    private static void MainMenu() {
        p("\n\t\tOperations Menu:");
        p("\t(1) Find Lowest Latency Path");
        p("\t(2) Determine if Graph is Copper-Only Connected");
        p("\t(3) Determine Maximum Data Transfer between 2 verticies");
        p("\t(4) Determine if any 2 verticies can make the Graph lose connectivity");
        p("\t(5) Quit\n");
        p("Only integers between 1-5 will be accepted.  Please enter your selection:");
    }

    public static void welcome() {
        p("\n\t ===================================="); 
        p("\t{ Welcome to Adam's Network Analyzer }"); 
        p("\t ====================================\n"); 
     }

     public static void goodbye() {
        p("\t =========================="); 
        p("\t{ Goodbye! Have a nice Day! }"); 
        p("\t ==========================\n");
    }

     public static void firstMenu(Graph g) {
        Scanner s = new Scanner(System.in);
        try{
            p("Enter first vertex ID: ");
            int id1 = Integer.parseInt(s.next());
            p("Enter second vertex ID: ");
            int id2 = Integer.parseInt(s.next());
            if (id1 > g.vertexCount-1 || id2 > g.vertexCount-1 || id1 == id2) {
                p("ERROR: Vertex ID is invalid");
                s.close();
                return;
            }
            int minBandwidth = g.lowestLatencyPath(id1, id2);
            System.out.println("BANDWIDTH ALONG PATH: " + minBandwidth + " Mb/s");
        } catch (Exception e) {
            System.out.println(e);
            s.close();
        }
     }

     public static void secondMenu(Graph g) {
        boolean isConnected = g.cooperConnected();
        if (isConnected) {
            p("THE GRAPH IS COPPER-ONLY CONNECTED");
        } else {
            p("THE GRAPH IS NOT COPPER-ONLY CONNECTED");
        }
    }

     public static void thirdMenu(Graph g) {
        Scanner s = new Scanner(System.in);
        try {
            p("Enter first vertex ID: ");
            int id1 = Integer.parseInt(s.next());
            p("Enter second vertex ID: ");
            int id2 = Integer.parseInt(s.next());
            if (id1 > g.vertexCount-1 || id2 > g.vertexCount-1 || id1 == id2) {
                p("ERROR: Vertex ID is invalid");
                s.close();
                return;
            }
            int maxBandwidth = g.maxDataPath(id1, id2);
            System.out.println("MAXIMUM DATA TRANSFER: " + maxBandwidth + " Mb/s");
        } catch (Exception e) {
            System.out.println(e);
            s.close();
        }
     }
 
    //Helper Functions
    private static void p(String s) {
        System.out.println(s);
    }

    private static void verifyInput(String[] a) {
        if (a.length != 1) {
            p("ERROR: Incorrect amount of input arguements");
            System.exit(0);
        } else {
            return;
        }
    }

}