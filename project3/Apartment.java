import java.util.concurrent.CopyOnWriteArrayList;

/*
Adam Granieri
COE 1501 Algorithm Implementation
Fall 2018
*/
import java.lang.Integer;
import java.lang.String;

public class Apartment {
    //Required info for each Apartment
    private String STREET_ADDRESS;
    private String NUMBER;
    private String CITY;
    private int ZIP;
    private int PRICE;
    private int SQUARE_FEET;
    private int hashVal;

    //Constructor with all info required
    public Apartment(String streetAddress, String num, String city, int zip, int price, int sqFeet) {
        this.STREET_ADDRESS = streetAddress;
        this.NUMBER = num;
        this.CITY = city;
        this.ZIP = zip;
        this.PRICE = price;
        this.SQUARE_FEET = sqFeet;

        String hashString = streetAddress+num+zip;
        hashVal = hashString.hashCode();
    }

    //Empty Constructor
    public Apartment() {
        this.STREET_ADDRESS = "";
        this.NUMBER = "";
        this.CITY = "";
        this.ZIP = 0;
        this.PRICE = 0;
        this.SQUARE_FEET = 0;
    }

    //toString method for apartments
    @Override
    public String toString() {
        return "\nAddress: " + this.STREET_ADDRESS + "\n"
        + "Number: " + this.NUMBER + "\n"
        + "City: " + this.CITY + "\n"
        + "ZIP: " + Integer.toString(this.ZIP) + "\n"
        + "Price: " + Integer.toString(this.PRICE) + "\n"
        + "Square Feet: " + Integer.toString(this.SQUARE_FEET) + "\n";
    }

    //Setters
    public void setAddress(String addr) {
        this.STREET_ADDRESS = addr;
    }

    public void setNumber(String num) {
        this.NUMBER = num;
    }

    public void setCity(String city) {
        this.CITY = city;
    }

    public void setZIP(int zip) {
        this.ZIP = zip;
    }

    public void setPrice(int price) {
        this.PRICE = price;
    }

    public void setSquareFoot(int sqFeet) {
        this.SQUARE_FEET = sqFeet;
    }

    //Getters
    public String getAddress() {
        return this.STREET_ADDRESS;
    }

    public String getNumber() {
        return this.NUMBER;
    }

    public String getCity() {
        return this.CITY;
    }

    public int getZIP() {
        return this.ZIP;
    }
    
    public int getPrice() {
        return this.PRICE;
    }

    public int getSquareFoot() {
        return this.SQUARE_FEET;
    }

    public int getHash() {
        return hashVal;
    }
}