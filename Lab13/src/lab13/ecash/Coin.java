package ecash;

public class Coin {
    private final byte[] serialNumber;
    private final byte[] signature;

    public Coin(final byte[] serialNumber, final byte[] signature) {
        this.serialNumber = serialNumber;
        this.signature = signature;
    }

    public byte[] getSerialNumber() {
        return serialNumber;
    }

    public byte[] getSignature() {
        return signature;
    }
}
