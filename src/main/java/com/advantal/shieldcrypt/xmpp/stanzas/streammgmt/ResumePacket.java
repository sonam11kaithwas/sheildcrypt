package com.advantal.shieldcrypt.xmpp.stanzas.streammgmt;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class ResumePacket extends AbstractStanza {

	public ResumePacket(final String id, final int sequence) {
		super("resume");
		this.setAttribute("xmlns", Namespace.STREAM_MANAGEMENT);
		this.setAttribute("previd", id);
		this.setAttribute("h", Integer.toString(sequence));
	}

}
