package com.advantal.shieldcrypt.crypto.sasl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import com.advantal.shieldcrypt.entities.Account;

public class ScramSha256 extends ScramMechanism {

    public static final String MECHANISM = "SCRAM-SHA-256";

    public ScramSha256(final Account account) {
        super(account, ChannelBinding.NONE);
    }

    @Override
    protected HashFunction getHMac(final byte[] key) {
        return Hashing.hmacSha256(key);
    }

    @Override
    protected HashFunction getDigest() {
        return Hashing.sha256();
    }
    @Override
    public int getPriority() {
        return 25;
    }

    @Override
    public String getMechanism() {
        return MECHANISM;
    }
}
