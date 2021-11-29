package diffiehellman;

	
	import java.security.GeneralSecurityException;
	import java.security.KeyPair;
	import java.security.KeyPairGenerator;
	import java.security.PrivateKey;
	import java.security.PublicKey;
	import java.security.Security;
	import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
	import org.bouncycastle.jce.provider.BouncyCastleProvider;
	import org.bouncycastle.util.encoders.Hex;
	
	public class DiffieHellman 
	{
	static
	{
	Security.addProvider(new BouncyCastleProvider());
	}
	private static KeyPair generateClassicalDHKeyPair()
	throws GeneralSecurityException 
	{
	final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH",
	"BC");
	keyPairGen.initialize(2048);
	return keyPairGen.generateKeyPair();
	}
	private static byte[] generateClassicalDHSecret(final PrivateKey privKey,
	final PublicKey pubKey) throws GeneralSecurityException 
	{
	final KeyAgreement agreement = KeyAgreement.getInstance("DH", "BC");
	agreement.init(privKey);
	agreement.doPhase(pubKey, true);
	return agreement.generateSecret();
	}
	private static KeyPair generateECCDHKeyPair(final String curveName)
	throws GeneralSecurityException 
	{
	final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC",
	"BC");
	keyPairGen.initialize(new ECGenParameterSpec(curveName));
	return keyPairGen.generateKeyPair();
	}
	private static byte[] generateECCDHSecret(final PrivateKey privKey,
	final PublicKey pubKey) throws GeneralSecurityException 
	{
	final KeyAgreement agreement = KeyAgreement
	.getInstance("ECCDHwithSHA256KDF", "BC");
	agreement.init(privKey);
	agreement.doPhase(pubKey, true);
	return agreement.generateSecret();
	}
	public static void main(final String[] args)
	throws GeneralSecurityException
	{
	// Classical DH
	// 1. Compute a keypair for Alice.
		KeyPair AlicesKey = generateClassicalDHKeyPair();
		PrivateKey alicePrivate = AlicesKey.getPrivate();
		PublicKey alicePublic = AlicesKey.getPublic();
		
	// 2. Compute a keypair for Bob.
		KeyPair BobsKey = generateClassicalDHKeyPair();
		PrivateKey bobsPrivate = BobsKey.getPrivate();
		PublicKey bobsPublic = BobsKey.getPublic();
		
	// 3. Compute the shared secret using the information available	to Alice.
		byte[]  commonsecret = generateClassicalDHSecret(alicePrivate,bobsPublic);
		
	// 4. Compute the shared secret using the information available to Bob.
		byte[]  commonsecretBob = generateClassicalDHSecret(bobsPrivate,alicePublic);
	// 5. Verify that both Alice and Bob have computed the same shared secret.
		System.out.println(Hex.toHexString(commonsecret));
		System.out.println(Hex.toHexString(commonsecretBob));

		
		if (Arrays.equals(commonsecret, commonsecretBob))
		{
			System.out.println("I am correct");
		} else {
			System.out.println("Can I show you the error");
		}

	// ECCDH
	// Repeat the steps 1-5 using Elliptic Curve cryptography.
	}
	}


