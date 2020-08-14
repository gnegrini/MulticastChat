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
/**
 * This class is reponsible for all message encryption and decryption activity
 * Adapted from:
 * https://www.devglan.com/java8/rsa-encryption-decryption-java  
 */
public class Cryptography {

    private static final String RSA = "RSA";
    private static final String signature = "Authenticated";

    private static KeyPair peerKeyPair;

    private String encryptedSignature;

    /**
     * Generating public & private keys using RSA algorithm.
     * */ 
    public void generateRSAKkeyPair() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(2048, secureRandom);
        peerKeyPair = keyPairGenerator.generateKeyPair();
        //peerKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

    public void generateSignature() throws Exception {
        this.encryptedSignature = do_RSAEncryption(signature);
    }

    public String getSignature() {
		return encryptedSignature;
	}

    /**
     * Use this method to get the Public Key already generated in the class
     * as a String
     * @return
     */
    public String getPublicKeyAsString() {

        byte [] pubKeyByteArray = peerKeyPair.getPublic().getEncoded();
        String pubKeyString = Base64.getEncoder().encodeToString(pubKeyByteArray);

        return pubKeyString;
    }

    /**
     * Encryption method which converts
     * the plainText into a cipherText
     * using private Key.
     * @param plainText
     * @return the encrypted message as a Base64 encoded string
     * @throws Exception
     */
    public String do_RSAEncryption(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.ENCRYPT_MODE, peerKeyPair.getPrivate());

        //return cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    /**
     * Decryption method which converts
     * the ciphertext back to the
     * orginal plaintext.
     * @param cipherText the message to be decrypted
     * @param publicKey the public key to decrypt the message
     * @return the decrypted encoded text 
     * @throws Exception
     */
    private byte[] do_RSADecryption(byte[] cipherText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(cipherText);

        return result;
    }
    
    /**
     * Public accessible method to decrypt a message,
     * given the text and the public key as Strings
     * @param peerPublicKeyAsString
     * @param msgText
     * @return  the decrypted message as string
     */
    public String decryptText(String peerPublicKeyAsString, String msgText){
        
        PublicKey peerPublicKey = convertToPublicKeyObject(peerPublicKeyAsString);
        byte[] msgBytes = Base64.getDecoder().decode(msgText);

        byte[] decryptedMsg = null;

        try {
            decryptedMsg = do_RSADecryption(msgBytes, peerPublicKey);
        } catch (Exception e) {
            System.out.println("Error while decrypting the message");
            e.printStackTrace();
        }

        return new String(decryptedMsg);
    }
    /**
     * Generates a PublicKey objet from a string
     * of an public key
     * @param base64PublicKey
     * @return
     */
    private PublicKey convertToPublicKeyObject(String base64PublicKey){
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

	
    
} 