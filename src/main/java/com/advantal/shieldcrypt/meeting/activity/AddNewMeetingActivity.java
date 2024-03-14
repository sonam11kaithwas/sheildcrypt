//package com.advantal.shieldcrypt.meeting.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.fragment.app.FragmentActivity;
//
//import com.facebook.react.modules.core.PermissionListener;
//
//import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
//import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
//import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
//import org.jitsi.meet.sdk.JitsiMeetView;
//
//import java.net.MalformedURLException;
//import java.net.Proxy;
//import java.net.URL;
//
//public class AddNewMeetingActivity extends FragmentActivity implements JitsiMeetActivityInterface {
//    private JitsiMeetView view;
//
//    @Override
//    protected void onActivityResult(
//            int requestCode,
//            int resultCode,
//            Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        JitsiMeetActivityDelegate.onActivityResult(
//                this, requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onBackPressed() {
//        JitsiMeetActivityDelegate.onBackPressed();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        view = new JitsiMeetView(this);
//
//        URL serverURL = null;
//        try {
//            serverURL = new URL("https://92.204.128.15:7443/ofmeet");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        JitsiMeetConferenceOptions options = null;
//        try {
//            options = new JitsiMeetConferenceOptions.Builder()
//                    //.setRoom("https://meet.jit.si/test123")
//                    .setServerURL(new URL("https://92.204.128.15:7443"))
//                    .setRoom("ofmeet/test123")
//                    .setToken("eyJraWQiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIvYmQ2YjUyLVNBTVBMRV9BUFAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJqaXRzaSIsImlzcyI6ImNoYXQiLCJpYXQiOjE2NzE2MDkyNTEsImV4cCI6MTY3MTYxNjQ1MSwibmJmIjoxNjcxNjA5MjQ2LCJzdWIiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIiLCJjb250ZXh0Ijp7ImZlYXR1cmVzIjp7ImxpdmVzdHJlYW1pbmciOnRydWUsIm91dGJvdW5kLWNhbGwiOnRydWUsInNpcC1vdXRib3VuZC1jYWxsIjpmYWxzZSwidHJhbnNjcmlwdGlvbiI6dHJ1ZSwicmVjb3JkaW5nIjp0cnVlfSwidXNlciI6eyJoaWRkZW4tZnJvbS1yZWNvcmRlciI6ZmFsc2UsIm1vZGVyYXRvciI6dHJ1ZSwibmFtZSI6ImFydmluZC5tYWxpIiwiaWQiOiJhdXRoMHw2M2EyYmIyMWQ3YWZiM2Q1ZDNjYjJiYjIiLCJhdmF0YXIiOiIiLCJlbWFpbCI6ImFydmluZC5tYWxpQGFkdmFudGFsLm5ldCJ9fSwicm9vbSI6IioifQ.PTe2DResBe_WDgMnNshvLtT6T8SV8fj0BBjTKyBofme_sgRvAgDxPHiWNZYcMn8BMRBiua-Y8vu-gj904zG1aLUUWQVb3Hhx51WrznnI0D9a4FY5KGub1ienIfdORMi4HetyhKB-uhrQarfPQVYOOfqcsB3YfhAKIPgwfP_wIyLiEN6TeVkWEGfzseoyYpdnGg60mkmgvUdO4Z3JikV9HT8GA2oqMFYc082gvXc1tFuBVgOm03pLM5AtqQQO4UTnWQoAGE52Qz6VMYcbaQWRxIxwny4Sbs2PQk8TmoZwiIYSrzW_wDFSDFMkiITffg-Eb2E0KG6GXgborm8sTh6vdg")
//                    .build();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        view.join(options);
//
//        setContentView(view);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        view.dispose();
//        view = null;
//
//        JitsiMeetActivityDelegate.onHostDestroy(this);
//    }
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        JitsiMeetActivityDelegate.onNewIntent(intent);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            final int requestCode,
//            final String[] permissions,
//            final int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        JitsiMeetActivityDelegate.onHostResume(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        JitsiMeetActivityDelegate.onHostPause(this);
//    }
//
//    @Override
//    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
//
//    }
//}
