package com.advantal.shieldcrypt.crypto.sasl;

import javax.net.ssl.SSLSocket;

import com.advantal.shieldcrypt.entities.Account;

public abstract class ScramPlusMechanism extends ScramMechanism implements ChannelBindingMechanism {

    ScramPlusMechanism(Account account, ChannelBinding channelBinding) {
        super(account, channelBinding);
    }

    @Override
    protected byte[] getChannelBindingData(final SSLSocket sslSocket)
            throws AuthenticationException {
        return ChannelBindingMechanism.getChannelBindingData(sslSocket, this.channelBinding);
    }

    @Override
    public ChannelBinding getChannelBinding() {
        return this.channelBinding;
    }
}
