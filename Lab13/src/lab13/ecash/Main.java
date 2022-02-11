package ecash;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.encoders.Hex;

public class Main {

    public static void main(final String[] args) throws CryptoException {
        // (1) create a bank
        final var bank = new Bank();

        // (2) create a proto-coin
        final var protoCoin = new ProtoCoin(bank.getPublicParams());

        // (3) create a withdrawal request
        final var withdrawalRequest = protoCoin.createWithdrawalRequest();

        printWithdrawalRequest(withdrawalRequest);

        // (4) create a withdrawal response
        final var withdrawalResponse = bank.sign(withdrawalRequest);

        printWithdrawalResponse(withdrawalResponse);

        // (5) strip the blinding factor
        final var coin = protoCoin.stripBlindingFactor(withdrawalResponse);

        printCoin(coin);

        // (6) verify the coin
        final var verification = bank.verify(coin);

        if (verification) {
            System.out.println("The coin is genuine!");
        } else {
            System.out.println("The coin is NOT genuine!");
        }
    }

    private static void printWithdrawalRequest(final byte[] request) {
        System.out.println("\nWithdrawal request received by the bank:");
        System.out.println(Hex.toHexString(request));
    }

    private static void printWithdrawalResponse(final byte[] response) {
        System.out.println("\nWithdrawal response sent by the bank:");
        System.out.println(Hex.toHexString(response));
    }

    private static void printCoin(final Coin coin) {
        System.out.println("\nCoin details:");
        System.out.println(Hex.toHexString(coin.getSerialNumber()));
        System.out.println(Hex.toHexString(coin.getSignature()));
    }
}
