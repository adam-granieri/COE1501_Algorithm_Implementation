//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.nio.file.Path;

public class RsaSign {
    public static void main(String[] args) {
        LargeInteger key = null;
        //get file names from args we do this for both options v or s
        //try block from example
        try {
			Path path = Paths.get(args[1]);
			byte[] data = Files.readAllBytes(path);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			key = new LargeInteger(md.digest());
		} catch(Exception e) {
			System.out.println("ERROR: " + e);
			return;
        }
        System.out.println("Now doing LARGE math...");
        long start = System.currentTimeMillis(), end;

        //Parse the mode of execution for verifying for creating
		if (args[0].equals("s")) {
			try {
				FileInputStream privIn = new FileInputStream("privkey.rsa");
                byte[] tempD = new byte[65], tempN = new byte[65];
                
                //read into temp buffers
				privIn.read(tempD, 1, 64);
                privIn.read(tempN, 1, 64);
                
                //create d and n
				LargeInteger d = new LargeInteger(tempD);
                LargeInteger n = new LargeInteger(tempN);
                
                //close our buffer
                privIn.close();
                
                //generate and write signature to file
                LargeInteger signature = key.modularExp(d, n);
                
                //show we're done
                end = System.currentTimeMillis();
                System.out.println("DONE!");
                System.out.println("Signature generated in " + ((end-start)/1000.0) + " seconds");  

				FileOutputStream outFile = new FileOutputStream(args[1].concat(".sig"));
				outFile.write(signature.getVal());
				outFile.close();
			} catch(Exception e) {
				System.out.println("ERROR: " + e);
            }
            
		} else if (args[0].equals("v")) {
			try {
				Path sigFile = Paths.get(args[1].concat(".sig"));
				byte[] temp = Files.readAllBytes(sigFile);
				LargeInteger signedkey = new LargeInteger(temp);
				FileInputStream pubIn = new FileInputStream("pubkey.rsa");
                byte[] tempE = new byte[65], tempN = new byte[65];
                
                //read in RSA keypair
				pubIn.read(tempE, 1, 64);
                pubIn.read(tempN, 1, 64);
				LargeInteger e = new LargeInteger(tempE);
				LargeInteger n = new LargeInteger(tempN);
                pubIn.close();
                
                //decrypt value and compare
                signedkey = signedkey.modularExp(e, n);
                
                //Display end messages
                end = System.currentTimeMillis();
                System.out.println("DONE!");
                System.out.println("Signature decrypted in " + ((end-start)/1000.0) + " seconds");

				if (key.equals(signedkey)) {
					System.out.println("Signature is valid");
				} else {
					System.out.println("Signature is NOT valid");
				}
			} catch(Exception e) {
				System.out.println("ERROR: " + e);
			}
		}
	}
}

