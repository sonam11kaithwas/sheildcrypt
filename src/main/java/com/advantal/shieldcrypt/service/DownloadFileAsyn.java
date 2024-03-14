package com.advantal.shieldcrypt.service;//package com.advantal.shieldcrypt.service;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Environment;
//
////import com.ithub.http.MySSLSocketFactory;
////import com.ithub.model.ChatModel;
////import com.ithub.room_db.AppDataBase;
////import com.ithub.utility.AppConstants;
////import com.ithub.xmpp.RoosterConnectionService;
////
////import org.apache.commons.io.FilenameUtils;
//import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//public class DownloadFileAsyn{}
// extends AsyncTask<String, String, String> {
//
////    AppDataBase appDataBase;
//    String responseString = "";
//    Context ctx;
////    ChatModel chatModel;
//    String filePath = "";
//    int count = 0;
//    int lenghtOfFile = 0;
////    String mediaDirectory = AppConstants.rootDirectory + File.separator + AppConstants.subRootMediaDirectory + File.separator;
//
//
//    public DownloadFileAsyn() {
//
//    }
//
//    public DownloadFileAsyn(Context context, ChatModel chatModel) {
//
//        appDataBase = AppDataBase.getAppDatabase(context);
//        this.ctx = context;
//        this.chatModel = chatModel;
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//System.out.println("Download file status = doinback " + chatModel.getMessageId() );
//        responseString = Download(chatModel);
//        return responseString;
//    }
//
//    public String Download(ChatModel chatModel) {
//
//        try {
//
//            int type = chatModel.getContentType();
//            String downloadUrl = chatModel.getContent();
//
//            if (type == AppConstants.CODE_IMAGE || type == AppConstants.GROUP_CODE_IMAGE) {
//                filePath = getFilename();
//            } else if (type == AppConstants.CODE_VIDEO || type == AppConstants.GROUP_CODE_VIDEO) {
//                filePath = getVideoFilename();
//            } else if (type == AppConstants.CODE_AUDIO || type == AppConstants.GROUP_CODE_AUDIO) {
//                filePath = getAudioFilename();
//            } else if (type == AppConstants.CODE_DOCUMENT || type == AppConstants.GROUP_CODE_DOCUMENT) {
//                filePath = getFileDocname(chatModel);
//            }
//
//            System.out.println("Download file status = download uploading " + chatModel.getMessageId() );
//            chatModel.setFileTransferStatus(AppConstants.filedownloading);
//            updateFileInfoAndSendBroadcast(chatModel);
//
//            URL u = new URL(downloadUrl);
//
//
//            FileOutputStream f = new FileOutputStream(new File(filePath));
//            InputStream in;
//
//            if (downloadUrl.contains("https:")) {
//                System.out.println("Download file status = inside https " + chatModel.getMessageId() );
//                HttpsURLConnection c = MySSLSocketFactory.getHttpUrlConnection(u);
//
//                javax.net.ssl.SSLSocketFactory sslSocketFactory = createSslSocketFactory();
//                c.setSSLSocketFactory(sslSocketFactory);
//                c.setHostnameVerifier(new AllowAllHostnameVerifier());
//                c.setUseCaches(false);
//
//                c.setUseCaches(false);
//                c.setRequestMethod("GET");
//                c.setDoOutput(true);
//                c.connect();
//
//                lenghtOfFile = c.getContentLength();
//
//                in = c.getInputStream();
//
//            } else {
//                System.out.println("Download file status = inside http " + chatModel.getMessageId() );
//                HttpURLConnection c = (HttpURLConnection) u.openConnection();
//
//                c.setUseCaches(false);
//                c.setRequestMethod("GET");
//                c.setDoOutput(true);
//                c.connect();
//
//                lenghtOfFile = c.getContentLength();
//
//                in = c.getInputStream();
//            }
//
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//
////            chatModel.setFileStatus(AppConstants.filedownloading);
////            updateFileInfoAndSendBroadcast(chatModel);
//
//            while ((len1 = in.read(buffer)) > 0) {
//                count += len1;
//                f.write(buffer, 0, len1);
//
//            }
//
//            try {
//                System.out.println("Download file status = file downloaded" + chatModel.getMessageId() );
//                chatModel.setFileTransferStatus(AppConstants.filedownloaded);
//
//                chatModel.setLocalFilePath(filePath);
//                updateFileInfoAndSendBroadcast(chatModel);
////                chatModel.setProgress("100");
//
//               /*db.updateFileInfo(chatModel);
//
//                db.updateFileInfoFileUploaded(chatModel);
//*/
//                // chatWindow.fileDownloaded(chatModel);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Download file status = exception " );
//                uploadingFailed();
//
//            }
//
//
//            f.flush();
//            f.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Download file status = exception " );
//            // Log.e("downloadfile", "", e);
//            uploadingFailed();
//
//        }
//
//        return responseString;
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//
////        Intent intent = new Intent(RoosterConnectionService.FILE_DOWNLOAD_RECEIVER);
////        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, chatModel.getFriendJID());
////        intent.putExtra("CHAT", chatModel);
////
////        ctx.sendBroadcast(intent);
//
//
//    }
//
//    public void uploadingFailed() {
////        chatModel.setProgress("0");
////        chatModel.setUrl(chatModel.getUrl());
//        chatModel.setLocalFilePath("");
//        chatModel.setFileTransferStatus(AppConstants.filedownloadfailed);
//        updateFileInfoAndSendBroadcast(chatModel);
//    }
//
//
//   /* public void uploadingFile() {
//        chatModel.setProgress("0");
//        chatModel.setUrl("");
//        chatModel.setLocalUri(chatFile.getAbsolutePath());
//        chatModel.setStatus(PreferenceConstants.fileUploding);
//        db.updateFileInfo(chatModel);
//    }
//
//    public void uploadingFailed() {
//        chatModel.setProgress("0");
//        chatModel.setUrl("");
//        chatModel.setLocalUri(chatFile.getAbsolutePath());
//        chatModel.setStatus(PreferenceConstants.fileFail);
//        db.updateFileInfo(chatModel);
//    }*/
//
//
//    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), mediaDirectory + AppConstants.dirImages);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".jpg");
//        return uriSting;
//    }
//
//    public String getVideoFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), mediaDirectory + AppConstants.dirVideo);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".mp4");
//        return uriSting;
//    }
//
//    public String getAudioFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), mediaDirectory + AppConstants.dirAudio);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + ".mp3");
//        return uriSting;
//    }
//
//    private String getFileDocname(ChatModel chatbody) {
//        String extension = "";
//        try {
//            extension = FilenameUtils.getExtension(chatModel.getContent());
//        } catch (Exception e) {
//            System.out.println("Download file status = exception " );
//            e.printStackTrace();
//        }
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), mediaDirectory + AppConstants.dirfile);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//
////        String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + "." + chatbody.getExtension());
//        String uriSting = (file.getPath() + "/" + System.currentTimeMillis() + "." + extension);
//        return uriSting;
//    }
//
//    public void updateFileInfoAndSendBroadcast(ChatModel chatModel) {
//        System.out.println("Download file status = update chat " + chatModel.getMessageId() + chatModel.getFileTransferStatus() );
//        appDataBase.chatDao().updateChat(chatModel);
//
//        Intent intent = new Intent(RoosterConnectionService.FILE_DOWNLOAD_RECEIVER);
//        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, chatModel.getThreadBareJid());
//        intent.putExtra("chat", chatModel);
//        ctx.sendBroadcast(intent);
//    }
//
//    private javax.net.ssl.SSLSocketFactory createSslSocketFactory()
//            throws Exception {
//        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
//            public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[0];
//            }
//
//            public void checkClientTrusted(X509Certificate[] chain,
//                                           String authType) {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain,
//                                           String authType) {
//            }
//        }};
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, byPassTrustManagers, new SecureRandom());
//        return sslContext.getSocketFactory();
//    }
//}
//
//
//
