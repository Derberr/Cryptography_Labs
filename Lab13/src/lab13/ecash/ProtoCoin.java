package ecash;

import java.security.SecureRandom;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

public class ProtoCoin {
    private final byte[] serialNumber;
    private final RSABlindingParameters blindingParams;
    private static SecureRandom secureRandom = new SecureRandom();

    public ProtoCoin(final RSAKeyParameters params) {
        // generate a random serial number
        serialNumber = new byte[16];
        secureRandom.nextBytes(serialNumber);

        // generate a blinding factor
        // this requires the bank's public-key parameters
        final var blindingFactorGenerator = new RSABlindingFactorGenerator();
        blindingFactorGenerator.init(params);

        final var blindingFactor = blindingFactorGenerator
            .generateBlindingFactor();

        blindingParams = new RSABlindingParameters(params, blindingFactor);
    }

    public byte[] createWithdrawalRequest() throws CryptoException {
        final var signer = new PSSSigner(new RSABlindingEngine(),
                                         new SHA256Digest(), 20);
        signer.init(true, blindingParams);
        signer.update(serialNumber, 0, serialNumber.length);

        return signer.generateSignature();
    }

    public Coin stripBlindingFactor(final byte[] withdrawalResponse) {
        final var blindingEngine = new RSABlindingEngine();
        blindingEngine.init(false, blindingParams);
        final var signature = blindingEngine.processBlock(withdrawalResponse, 0,
                                                          withdrawalResponse.length);
        return new Coin(serialNumber, signature);
    }
}
