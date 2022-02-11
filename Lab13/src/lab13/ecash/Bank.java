package ecash;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

public class Bank {
    private static final RSAKeyPairGenerator generator = new RSAKeyPairGenerator();

    static {
        generator.init(new RSAKeyGenerationParameters(new BigInteger("10001", 16), new SecureRandom(), 2048, 80));
    }

    private final AsymmetricCipherKeyPair keyPair;

    public Bank() {
        // the bank needs a public-private keypair
        keyPair = generator.generateKeyPair();
    }

    public RSAKeyParameters getPublicParams() {
        // the parameters for the bank's public-key are needed to create
        // a blinding factor
        return (RSAKeyParameters) keyPair.getPublic();
    }

    public byte[] sign(final byte[] withdrawalRequest) {
        final var engine = new RSAEngine();
        engine.init(true, keyPair.getPrivate());
        return engine.processBlock(withdrawalRequest, 0,
                                   withdrawalRequest.length);
    }

    public boolean verify(final Coin coin) {
        final var serialNumber = coin.getSerialNumber();
        final var signature = coin.getSignature();
        final var signer = new PSSSigner(new RSAEngine(), new SHA256Digest(),
                                         20);
        signer.init(false, keyPair.getPublic());
        signer.update(serialNumber, 0, serialNumber.length);
        return signer.verifySignature(signature);
    }
}
