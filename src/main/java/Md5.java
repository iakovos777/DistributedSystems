/*
    IAKOVOS EVDAIMON 3130059
    NIKOS KOULOS 3150079
    STEFANOS PAVLOPOULOS 3130168
    GIANNIS IPSILANTIS 3130215
 */


import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {


    public Md5(){}

    //method to create hash of string and return hash as string
    public static String calculateHash(String toHash){
        String hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toHash.getBytes());
            byte[] digest = md.digest();
            hash = DatatypeConverter.printHexBinary(digest).toUpperCase();



        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception to calculate hash");
            e.printStackTrace();
        }
        return hash;
    }

    //method to compare two string hashes
    public static String compareHashes(String h1, String h2){
        int c = h1.compareTo(h2);
        switch(c) {
            case 1:
                return h1;

            case -1:
                return h2;

            default:
                return h1;
        }
    }

    //method to create hash of string and return hash as BigInteger
    public static BigInteger takeHash(String toHash){
        BigInteger no = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toHash.getBytes());
            byte[] digest = md.digest();
            no = new BigInteger(1, digest);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception to calculate hash");
            e.printStackTrace();
        }
        return no;
    }

    //method to compare two BigInteger hashes
    public static BigInteger compareHashes(BigInteger n1, BigInteger n2){
        int c = n1.compareTo(n2);
        if(c < 0){
            return n2;
        }
        else if(c > 0){
            return n1;
        }
        else
            return n1;
    }


    public static BigInteger modulo(BigInteger n1, BigInteger n2){
        //always n2 = maximum hash of brokers
        return n1.mod(n2);
    }

    public static String modulo(String h1, String h2){
        //always h2 = maximum hash of brokers
        BigInteger n1 = new BigInteger(h1,16);
        BigInteger n2 = new BigInteger(h2,16);
        BigInteger result = n1.mod(n2);
        String hash = result.toString(16);
        while (hash.length() < 32) {
            hash = "0" + hash;
        }
        hash = hash.toUpperCase();
        return hash;
    }

}
