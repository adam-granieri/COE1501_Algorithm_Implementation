/*
Adam Granieri
COE 1501 Algorithm Implementation
Fall 2018
*/
import java.util.*;
import java.lang.*;
import java.io.*;

public class AptTracker {
    public static PriorityQueue priceQueue = new PriorityQueue(false);
    public static PriorityQueue sqFtQueue = new PriorityQueue(true);

    public static void main(String[] args) {
        welcome(); 
        boolean again = true;
        loadTestData();
        
        do {
            mainMenu();
            int input = userIntIn();
            switch(input) {
                case 1: 
                    addMenu();
                    break;
                case 2:
                    updateMenu();
                    break;
                case 3:
                    removeMenu();
                    break;
                case 4:
                    System.out.println("The Lowest Price Apartment is:");
                    System.out.println(priceQueue.priorityKey().toString());
                    break;
                case 5:
                    System.out.println("The Highest Square Footage Apartment is:");
                    System.out.println(sqFtQueue.priorityKey().toString());
                    break;
                case 6:
                    Apartment temp1 = priceQueue.getPriorityCity(cityMenu());
                    if (temp1 == null) {
                        System.out.println("There was no apartment with that city in the queue");
                    } else {
                        System.out.println("The Lowest price apartment in that city is:");
                        System.out.println(temp1.toString());
                    }
                    break;
                case 7:
                    Apartment temp2 = sqFtQueue.getPriorityCity(cityMenu());
                    if (temp2 == null) {
                        System.out.println("There was no apartment with that city in the queue");
                    } else {
                        System.out.println("The highest square footage apartment in that city is:");
                        System.out.println(temp2.toString());
                    }
                    break;
                case 8:
                    again = false;
                    break;
                default:
                    System.out.println("ERROR: Input did not match any cases");
                    break;
            }
        } while (again);

        goodbye();
    }

    //Displaying Menu functions
    public static void welcome() {
       System.out.println("\t ==============================="); 
       System.out.println("\t{ Welcome to Adam's Car Tracker }"); 
       System.out.println("\t ===============================\n"); 
    }

    public static void mainMenu() {
        System.out.println("\n\t\tOperations Menu:");
        System.out.println("\t(1) Add an Apartment");
        System.out.println("\t(2) Update an Apartment");
        System.out.println("\t(3) Remove an Apartment");
        System.out.println("\t(4) Retrieve the Lowest Priced Apartment");
        System.out.println("\t(5) Retrieve the Highest Square Footage Apartment");
        System.out.println("\t(6) Retrieve the Lowest Price Apartment by City");
        System.out.println("\t(7) Retrieve the Highest Square Footage Apartment by City");
        System.out.println("\t(8) Quit\n");
        System.out.println("Only integers between 1-8 will be accepted.  Please enter your selection:");
    }

    public static void addMenu() {
        System.out.println("You chose to add a new apartment!");
        System.out.println("Please enter the required data when prompted:\n");
        Scanner s = new Scanner(System.in);
        try {
            System.out.print("Street Address: ");
            String address = s.nextLine();
            System.out.print("Apartment Number: ");
            String number = s.nextLine();
            System.out.print("City: ");
            String city = s.nextLine();
            System.out.print("ZIP Code: ");
            int zip = s.nextInt();
            System.out.print("Price: ");
            int price = s.nextInt();
            System.out.print("Square Footage: ");
            int sqFeet = s.nextInt();
            Apartment temp = new Apartment(address, number, city, zip, price, sqFeet);
            if (priceQueue.contains(address, number, zip)) {
                System.out.println("Apartment already exists in queue");
                System.out.println("Utilize the update option for the price");
            } else {
                priceQueue.insert(temp);
                sqFtQueue.insert(temp);
            }
        } catch(Exception e) {
            System.out.println(e);
            s.close();
        }
    }

    public static void updateMenu() {
        System.out.println("You chose to update an apartment!");
        System.out.println("Please enter the required data when prompted:\n");
        Scanner s = new Scanner(System.in);
        try {
            System.out.print("Street Address: ");
            String address = s.nextLine();
            System.out.print("Apartment Number: ");
            String number = s.nextLine();
            System.out.print("ZIP Code: ");
            int zip = s.nextInt();
            System.out.print("Updated Price: ");
            int price = s.nextInt();
            boolean inSquareFootage = sqFtQueue.updateKey(address, number, zip, price);
            boolean inPrice = priceQueue.updateKey(address, number, zip, price);
            if (inPrice != inSquareFootage) {
                System.out.println("ERROR: Apartment not in one of 2 queues");
            } else if (!inPrice) {
                System.out.println("That Apartment was not found");
            } else {
                System.out.println("The price has been updated!");
            }
        } catch(Exception e) {
            System.out.println(e);
            s.close();
        }
    }

    public static void removeMenu() {
        System.out.println("You chose to remove an apartment!");
        System.out.println("Please enter the required data when prompted:\n");
        Scanner s = new Scanner(System.in);
        try {
            System.out.print("Street Address: ");
            String address = s.nextLine();
            System.out.print("Apartment Number: ");
            String number = s.nextLine();
            System.out.print("ZIP Code: ");
            int zip = s.nextInt();
            boolean inSquareFootage = sqFtQueue.removeKey(address, number, zip);
            boolean inPrice = priceQueue.removeKey(address, number, zip);
            if (inPrice != inSquareFootage) {
                System.out.println("ERROR: Apartment not in one of 2 queues");
            } else if (!inPrice) {
                System.out.println("That Apartment was not found");
            } else {
                System.out.println("The Apartment has been removed!");
            }
        } catch(Exception e) {
            System.out.println(e);
            s.close();
        }
    }

    public static String cityMenu() {
        Scanner s = new Scanner(System.in);
        try {
            System.out.println("Enter the city: ");
            String city = s.nextLine();
            return city;
        } catch (Exception e) {
            System.out.println(e);
            s.close();
            return null;
        }
    }

    public static void goodbye() {
        System.out.println("\t =========================="); 
        System.out.println("\t{ Goodbye! Have a nice Day! }"); 
        System.out.println("\t ==========================\n");
    }

    //Method to help with input
    private static int userIntIn() {
        Scanner s = new Scanner(System.in);
        int input = 0;
        input = s.nextInt();
        if (input >= 1 && input <= 8) {
            return input;
        } else {
            System.out.print("\nINVALID INPUT\n");
        }
        s.close();
        return 0;
    }

    //Reader function for test
    private static void loadTestData() {
        try {
            BufferedReader buff = new BufferedReader(new FileReader("apartments.txt"));
            String wholeLine;
            buff.readLine();
            String[] aptArr;
            while ((wholeLine = buff.readLine()) != null) {
                aptArr = wholeLine.split(":");
                priceQueue.insert(new Apartment(aptArr[0], aptArr[1], aptArr[2], Integer.parseInt(aptArr[3]), Integer.parseInt(aptArr[4]), Integer.parseInt(aptArr[5])));
                sqFtQueue.insert(new Apartment(aptArr[0], aptArr[1], aptArr[2], Integer.parseInt(aptArr[3]), Integer.parseInt(aptArr[4]), Integer.parseInt(aptArr[5])));
            }
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}