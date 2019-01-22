//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

import java.io.FileOutputStream;
import java.util.Random;

public class RsaKeyGen {
    //Constants
    private final static LargeInteger ONE = new LargeInteger(new byte[]{0x01});

    public static void main(String[] args) {
        //Necessary vars
        LargeInteger e, d, n, phi, p, q;
        Random r = new Random();
        System.out.println("Now doing LARGE math...");
        long start = System.currentTimeMillis(), end;

        //loop for generating valid pair
        do {
            p = new LargeInteger(256, r);
            q = new LargeInteger(256, r);

            //continually generate new p and q vals until valid
            while (!p.isNegative() && !q.isNegative() && p.equals(q)) {
                p = new LargeInteger(256, r);
                q = new LargeInteger(256, r);
            }

            //now phi of n and e and n
            phi = p.subtract(ONE).multiply(q.subtract(ONE));
            e = new LargeInteger(512, r);
            n = p.multiply(q);

            //contnually create new e if not valid
            while (!e.isNegative() && !phi.XGCD(e)[0].equals(ONE)) {
                e = new LargeInteger(512, r);
            }
            //Lastly d
            d = e.modularExp(ONE.negate(), phi);
        } while (phi.subtract(e).isNegative());
    
        //Display We're done
        end = System.currentTimeMillis();
        System.out.println("DONE!");
        System.out.println("Keys generated in " + ((end-start)/1000.0) + " seconds");

        //Write public key pair to file
        try {
            FileOutputStream pubStream = new FileOutputStream("pubkey.rsa");
            pubStream.write(e.store().getVal());
            pubStream.write(n.store().getVal());
            pubStream.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }

        //write private key pair to file
        try {
            FileOutputStream privStream = new FileOutputStream("privkey.rsa");
            privStream.write(d.store().getVal());
            privStream.write(n.store().getVal());
            privStream.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}