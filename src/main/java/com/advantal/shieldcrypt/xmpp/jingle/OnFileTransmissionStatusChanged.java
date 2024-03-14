package com.advantal.shieldcrypt.xmpp.jingle;

import com.advantal.shieldcrypt.entities.DownloadableFile;

public interface OnFileTransmissionStatusChanged {
	void onFileTransmitted(DownloadableFile file);

	void onFileTransferAborted();
}
