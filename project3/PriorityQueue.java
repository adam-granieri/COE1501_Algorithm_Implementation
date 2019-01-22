/*
Adam Granieri
COE 1501 ALgorithm Implementation
Fall 2018
*/

import java.util.*;
import java.lang.*;

public class PriorityQueue {

    //Data for PQ
    private int size = 0;
    private HashMap<Integer, Integer> indices = new HashMap<>();
    private HashMap<Integer, Apartment> cities = new HashMap<>();
    private int capacity = 10;
    private Apartment[] pq;
    private boolean isMaxPQ;

    //Cosntructor
    public PriorityQueue(boolean maxOrMin) {
        this.isMaxPQ = maxOrMin; // true for maxPQ, false for minPQ
        this.pq = new Apartment[capacity];
    }

    //Getters
    public int getSize() {
        return size;
    }

    public boolean getPriority() {
        return isMaxPQ;
    }

    public Apartment priorityKey(){
        if(isEmpty()){
            return null;
        }
        return pq[0];
    }

    public Apartment removePriorityKey(){
        if(isEmpty())
            return null;
        Apartment ret = pq[0];

        exchange(0, --size);
        sink(0);

        indices.remove(ret.getHash());

        return ret;
    }

    private int getIndex(int hashCode){
        if(indices.get(hashCode) != null){
            int index = indices.get(hashCode);
            return index;
        } else {
            return -1;
        }
    }

    public boolean removeKey(String address, String aptN, int zip){
        String hashString = address + aptN + zip;
        int hashCode = hashString.hashCode();
        int index = getIndex(hashCode); 

        if(index < 0) {
            return false;
        }
        
        Apartment rem = pq[index];
        int cityHash = rem.getCity().hashCode(); 
        exchange(index, size-1);
        size--;
        sink(index);
        indices.remove(hashCode);

        boolean removeCityHash = false;
        if(cities.get(cityHash) == rem){
            cities.remove(cityHash);
            System.out.println("Removed the priority item.");
            removeCityHash = !findNewCity(rem.getCity());
        }
        return true;
    }

    private boolean findNewCity(String city){
        int cityHash = city.hashCode();
        boolean aCityRemoved = false;
        
        for(int i=0; i<size; i++){
            if(pq[i].getCity().hashCode() == cityHash){
                if(!cities.containsKey(cityHash)){
                    cities.put(cityHash, pq[i]);
                }
                if(swapCities(cityHash, pq[i])){
                    aCityRemoved = true;
                }
            }
        }
        return aCityRemoved;
    }

    public void insert(Apartment item){
        if(size >= capacity){
            growQueue();
        }
        pq[size] = item;
        String hashString = item.getAddress() + item.getNumber() + item.getZIP();
        int hashCode = hashString.hashCode();
        
        indices.put(hashCode, size);

        int cityHash = item.getCity().hashCode();
        if(!cities.containsKey(cityHash)){
            cities.put(cityHash, item);
        } else {
            swapCities(cityHash, item);
        }
        
        swim(size);
        size++;
    }

    private boolean swapCities(int cityHash, Apartment item){ 
        if(isMaxPQ){
            if(item.getSquareFoot() > cities.get(cityHash).getSquareFoot()){
                cities.put(cityHash, item);
                return true;
            }
        } else {
            if(item.getPrice() < cities.get(cityHash).getPrice()){
                cities.put(cityHash, item);
                return true;
            }
        }
        return false;
    }

    public Apartment getPriorityCity(String city){
        int cityHash = city.hashCode();
        if(cities.containsKey(cityHash)){
            return cities.get(cityHash);
        } else {
            return null;
        }
    }

    public boolean updateKey(String address, String aptN, int zip, int update){
        String hashString = address + aptN + zip;
        int hashCode = hashString.hashCode();
        int index = getIndex(hashCode);
        if(index < 0) return false;
        Apartment up = pq[index];
        int cityHash = up.getCity().hashCode();
        int p = pq[index].getPrice();
        pq[index].setPrice(update);
        
        sink(index);
        swim(index);

        if(!isMaxPQ){
            if(cities.get(cityHash).getHash() == up.getHash()){
                if(update > p){
                    cities.remove(cityHash);
                    findNewCity(up.getCity());
                } else {
                    swapCities(cityHash, up);
                }
            } else {
                if(update < cities.get(cityHash).getPrice()){
                    swapCities(cityHash, up);
                }
            }
        }

        return true;
    }

    private void growQueue(){
        int tempN = capacity * 2;
        Apartment[] tmp = new Apartment[tempN];
        for(int i=0; i<size; i++){
            tmp[i] = pq[i];
            pq[i] = null;
        }
        pq = tmp;
        capacity = tempN;
        return;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public boolean contains(String address, String aptNum, int zipCode){
        String a = address + aptNum + zipCode;
        if(indices.containsKey(a.hashCode())){
            return true;
        }
        return false;
    }

    // This will return TRUE if i's price is less than j's price
    private boolean less(int i, int j){
        return pq[i].getPrice() < pq[j].getPrice();
    }

    // This will return TRUE if i's size is greater than j's size
    private boolean greater(int i, int j){
        return pq[i].getSquareFoot() > pq[j].getSquareFoot();
    }

    private void exchange(int i, int j){
        int hash1 = pq[i].getHash();
        int hash2 = pq[j].getHash();
        Apartment t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;

        indices.put(hash1, j);
        indices.put(hash2, i);
    }

    private void swim(int i){
        if(isMaxPQ){ // If the priority is of size
            while(i > 0 && greater(i, (i-1)/2)){
                exchange(i, (i-1)/2);
                i = (i-1)/2;
            }
        } else {
            while(i > 0 && less(i, (i-1)/2)){
                exchange(i, (i-1)/2);
                i = (i-1)/2;
            }
        }
    }

    private void sink(int i){
        if(isMaxPQ){
            while(2*i+1 < size){
                int j = i*2+1;
                if(j < size-1 && !greater(j, j+1)) j++;
                if(greater(i, j)) break;
                exchange(i, j);
                i = j;
            }
        } else {
            while(2*i+1 < size){
                int j = i*2+1;
                if(j < size-1 && !less(j, j+1)) j++;
                if(less(i, j)) break;
                exchange(i, j);
                i = j;
            }
        }
    }
}