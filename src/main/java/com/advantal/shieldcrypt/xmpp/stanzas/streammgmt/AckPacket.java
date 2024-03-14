package com.advantal.shieldcrypt.xmpp.stanzas.streammgmt;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class AckPacket extends AbstractStanza {

	public AckPacket(final int sequence) {
		super("a");
		this.setAttribute("xmlns", Namespace.STREAM_MANAGEMENT);
		this.setAttribute("h", Integer.toString(sequence));
	}

}
