package com.advantal.shieldcrypt.xmpp.jingle;

public interface OnPrimaryCandidateFound {
	void onPrimaryCandidateFound(boolean success, JingleCandidate canditate);
}
