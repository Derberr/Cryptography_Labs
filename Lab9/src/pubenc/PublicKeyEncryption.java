package pubenc;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class PublicKeyEncryption 
{

    static 
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static KeyPair generateKeyPair() throws GeneralSecurityException 
    {
        final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("ElGamal","BC");
        keyPairGen.initialize(2048);
        return keyPairGen.generateKeyPair();
    }

    private static byte[] encrypt(final PublicKey publicKey, final byte[] data)
        throws GeneralSecurityException 
    {
        final Cipher cipher = Cipher.getInstance("ElGamal");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(final PrivateKey privateKey,
        final byte[] data) throws GeneralSecurityException 
    {
        final Cipher cipher = Cipher.getInstance("ElGamal");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static void main(final String[] args)
        throws GeneralSecurityException
    {
        final KeyPair keyPair = generateKeyPair();

        final String plaintext = "Dermot drove to town to get cookies";
        System.out.println("Plaintext: " + plaintext);

        final byte[] ciphertext = encrypt(keyPair.getPublic(),plaintext.getBytes());
        System.out.println("Ciphertext: " + Hex.toHexString(ciphertext));

        final String plaintext2 = new String(decrypt(keyPair.getPrivate(), ciphertext));
        System.out.println("Plaintext (decrypted): " + plaintext2);

        assert (plaintext.equals(plaintext2));
    }
} 