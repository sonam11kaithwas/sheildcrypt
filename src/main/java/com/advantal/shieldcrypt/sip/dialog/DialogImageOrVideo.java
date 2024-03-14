package com.advantal.shieldcrypt.sip.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;

public class DialogImageOrVideo extends BottomSheetDialogFragment {

    RelativeLayout rl_take_photo;
    RelativeLayout rl_tack_cam;
    RelativeLayout rl_cancel;

    TextView tv_take_photo;
    TextView tv_gallery;
    TextView tv_cancel;

    Activity mContext;
    CameraSelectionListener mListener;

  /*  public static DialogImageOrVideo newInstance() {
        DialogImageOrVideo fragment = new DialogImageOrVideo();
        return fragment;
    }*/

    public DialogImageOrVideo(CameraSelectionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_camera_or_video, null);
        dialog.setContentView(contentView);
       // ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        rl_take_photo = contentView.findViewById(R.id.rl_take_photo);
        rl_tack_cam = contentView.findViewById(R.id.rl_gallery);
        rl_cancel = contentView.findViewById(R.id.rl_cancel);

        tv_take_photo = contentView.findViewById(R.id.tv_take_photo);
        tv_gallery = contentView.findViewById(R.id.tv_gallery);
        tv_cancel = contentView.findViewById(R.id.tv_cancel);

        tv_take_photo.setTypeface(tv_take_photo.getTypeface(), Typeface.BOLD);
        tv_gallery.setTypeface(tv_gallery.getTypeface(), Typeface.BOLD);
        tv_cancel.setTypeface(tv_cancel.getTypeface(), Typeface.BOLD);


        rl_tack_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.BottomDialogSelectionListener(AppConstants.STATUS_VIDEO);
            }
        });

        rl_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.BottomDialogSelectionListener(AppConstants.STATUS_IMAGE);
            }
        });


    }

   public interface CameraSelectionListener{

        void BottomDialogSelectionListener(String type);
    }
}
