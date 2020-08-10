import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import java.util.Base64;

// https://www.devglan.com/java8/rsa-encryption-decryption-java  
public class Cryptography {

    private static final String RSA = "RSA";

    private static KeyPair peerKeyPair;

    // Generating public & private keys
    // using RSA algorithm.
    public void generateRSAKkeyPair() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(2048, secureRandom);
        peerKeyPair = keyPairGenerator.generateKeyPair();
        //peerKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

    public String getPublicKeyAsString() {

        byte [] pubKeyByteArray = peerKeyPair.getPublic().getEncoded();
        String pubKeyString = Base64.getEncoder().encodeToString(pubKeyByteArray);

        return pubKeyString;
    }

    // Encryption function which converts
    // the plainText into a cipherText
    // using private Key.
    public String do_RSAEncryption(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.ENCRYPT_MODE, peerKeyPair.getPrivate());

        //return cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    // Decryption function which converts
    // the ciphertext back to the
    // orginal plaintext.
    public String do_RSADecryption(byte[] cipherText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(cipherText);

        return new String(result);
    }
    
    public String decryptMsg(String peerPublicKeyAsString, String msgText){
        
        PublicKey peerPublicKey = getPublicKey(peerPublicKeyAsString);
        byte[] msgBytes = Base64.getDecoder().decode(msgText);

        String decodedMsg = null;

        try {
            decodedMsg = do_RSADecryption(msgBytes, peerPublicKey);
        } catch (Exception e) {
            System.out.println("Error while decrypting the message");
            e.printStackTrace();
        }

        return decodedMsg;
    }

    public PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
    // Driver code 
/*    public static void main(String args[]) 
        throws Exception 
    { 
  
 
        // System.out.println( 
        //     "The Public Key is: "
        //           + toHexString(keypair.getPublic().getEncoded()));          

        // System.out.println( 
        //     "The Private Key is: "
        //           + Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded()));
  
        
        String pubKeyString = Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
        PublicKey pubKey = Encoding.getPublicKey(pubKeyString);

        System.out.println( 
        "The Original Public Key is: "
                + keypair.getPublic().getEncoded());

        System.out.println("The decoded pubkey is" + pubKey.getEncoded());

        System.out.print("The Encrypted Text is: "); 
  
        System.out.println( 
                cipherText); 
  
        String decryptedText 
                = do_RSADecryption( 
                    cipherText, 
                    pubKey);


        // String decryptedText 
        //     = do_RSADecryption( 
        //         cipherText, 
        //         keypair.getPublic()); 
  
        System.out.println( 
            "The decrypted text is: "
            + decryptedText); 
    } 
    */
} 