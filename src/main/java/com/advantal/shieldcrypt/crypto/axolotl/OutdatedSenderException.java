package com.advantal.shieldcrypt.crypto.axolotl;

public class OutdatedSenderException extends CryptoFailedException {

    public OutdatedSenderException(final String msg) {
        super(msg);
    }
}
