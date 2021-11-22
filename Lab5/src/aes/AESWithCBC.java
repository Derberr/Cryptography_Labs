package aes;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class AESWithCBC {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static byte[] encrypt(final Cipher cipher, final Key key,
			final byte[] initialisationVector, final byte[] data)
			throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, key,
				new IvParameterSpec(initialisationVector));
		return cipher.doFinal(data);
	}

	private static byte[] decrypt(final Cipher cipher, final Key key,
			final byte[] initialisationVector, final byte[] data)
			throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, key,
				new IvParameterSpec(initialisationVector));
		return cipher.doFinal(data);
	}

	public static void main(final String[] args)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		final String keytext = "thebestsecretkey";

		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		final Key key = new SecretKeySpec(keytext.getBytes(), "AES");

		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[cipher.getBlockSize()];
		random.nextBytes(iv);

		final String plaintext = "alphanumericallyalphanumerically";
		System.out.println("Plaintext: " + plaintext);

		final byte[] ciphertext = encrypt(cipher, key, iv,
				plaintext.getBytes());
		System.out.println("Ciphertext: " + Hex.toHexString(ciphertext));

		final String plaintext2 = new String(
				decrypt(cipher, key, iv, ciphertext));
		System.out.println("Plaintext (decrypted): " + plaintext2);

		assert (plaintext.equals(plaintext2));
	}
}
