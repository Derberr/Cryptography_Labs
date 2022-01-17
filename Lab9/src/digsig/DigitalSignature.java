package digsig;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class DigitalSignature {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static KeyPair generateKeyPair() throws GeneralSecurityException {
        final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC","BC");
        keyPairGen.initialize(384);
        return keyPairGen.generateKeyPair();
    }

    private static byte[] sign(final PrivateKey privateKey, final byte[] data)
        throws GeneralSecurityException {
        final Signature signer = Signature.getInstance("SHA256withECDSA","BC");
        signer.initSign(privateKey);
        signer.update(data);
        return signer.sign();
    }

    private static boolean verify(final PublicKey publicKey, final byte[] data,
    	final byte[] signature) throws GeneralSecurityException {
        final Signature verifier = Signature.getInstance("SHA256withECDSA","BC");
        verifier.initVerify(publicKey);
        verifier.update(data);
        return verifier.verify(signature);
    }

    public static void main(final String[] args)
        throws GeneralSecurityException {
        final KeyPair keyPair = generateKeyPair();

        final String message = "Hello World!";
        final byte[] signature = sign(keyPair.getPrivate(), message.getBytes());

        System.out.println("Signature: " + Hex.toHexString(signature));

        final boolean verification = verify(keyPair.getPublic(),
                                            message.getBytes(), signature);

        System.out.println("Verification: " + verification);
    }
}
