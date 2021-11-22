package passkey;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class PasswordKey {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(final String[] args)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA256", "BC");
		final byte[] salt = Hex.decode("1234567890"); //

		final Key key = factory.generateSecret(new PBEKeySpec("PasswordPasswordPassword".toCharArray(), salt, 1000, 256));
		System.out.println("Key: " + Hex.toHexString(key.getEncoded()));
		//System.out.print("length is" + );
	}
	
}
