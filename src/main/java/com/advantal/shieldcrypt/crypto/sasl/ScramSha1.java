package com.advantal.shieldcrypt.crypto.sasl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import com.advantal.shieldcrypt.entities.Account;

public class ScramSha1 extends ScramMechanism {

    public static final String MECHANISM = "SCRAM-SHA-1";

    public ScramSha1(final Account account) {
        super(account, ChannelBinding.NONE);
    }

    @Override
    protected HashFunction getHMac(final byte[] key) {
        return Hashing.hmacSha1(key);
    }

    @Override
    protected HashFunction getDigest() {
        return Hashing.sha1();
    }

    @Override
    public int getPriority() {
        return 20;
    }

    @Override
    public String getMechanism() {
        return MECHANISM;
    }
}
