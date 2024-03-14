package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.crypto.axolotl.AxolotlService;

public interface OnKeyStatusUpdated {
	void onKeyStatusUpdated(AxolotlService.FetchStatus report);
}
