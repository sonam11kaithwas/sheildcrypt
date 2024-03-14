package com.advantal.shieldcrypt.xmpp.stanzas.streammgmt;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class RequestPacket extends AbstractStanza {

	public RequestPacket() {
		super("r");
		this.setAttribute("xmlns", Namespace.STREAM_MANAGEMENT);
	}

}
