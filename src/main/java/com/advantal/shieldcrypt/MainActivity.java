package com.advantal.shieldcrypt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.advantal.shieldcrypt.utils_pkg.FloatingView;


public class MainActivity extends AppCompatActivity {


    private static final int PICK_IMAGE = 100;
    //    ImageView mEmoji, mAttachFiles, mSend;
    Uri uri;
    ImageView imageView;
    private LinearLayout mLayoutGallery, mLayoutDocument, mLayoutCamera;
    private ImageView mCameraBlue;
    private EditText mTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate the custom popup layout
        final View inflatedView;

        inflatedView = layoutInflater.inflate(R.layout.popup, null, false);


        mLayoutGallery = inflatedView.findViewById(R.id.layoutGallery);
        mLayoutDocument = inflatedView.findViewById(R.id.layoutDocument);
        mLayoutCamera = inflatedView.findViewById(R.id.layoutCamera);

        mLayoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingView.dismissWindow();
                openGallery();
            }
        });

        mLayoutDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Document.......", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mLayoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
            }
        });

        FloatingView.onShowPopup(this, inflatedView);


    }

    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK && requestCode == PICK_IMAGE) {

//            imageUri=data.getData();
//            imageView.setImageURI(imageUri);
            uri = data.getData();
            imageView.setImageURI(uri);


        }
    }

    public void init() {
//        mEmoji = findViewById(R.id.emojiIcon);
//        mAttachFiles = findViewById(R.id.attechFiles);
//        mSend = findViewById(R.id.send);
//        mCameraBlue = findViewById(R.id.camera);
//        mTV = findViewById(R.id.tv);
    }


}