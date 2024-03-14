package com.advantal.shieldcrypt.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.databinding.CreateConferenceDialogBinding;
import com.advantal.shieldcrypt.ui.util.DelayedHintHelper;
import com.advantal.shieldcrypt.utils_pkg.MyApp;

public class CreatePrivateGroupChatDialog extends DialogFragment {

    private static final String ACCOUNTS_LIST_KEY = "activated_accounts_list";
    private CreateConferenceDialogListener mListener;

    public static CreatePrivateGroupChatDialog newInstance(List<String> accounts) {
        CreatePrivateGroupChatDialog dialog = new CreatePrivateGroupChatDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ACCOUNTS_LIST_KEY, (ArrayList<String>) accounts);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_private_group_chat);
        CreateConferenceDialogBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.create_conference_dialog, null, false);
        ArrayList<String> mActivatedAccounts = getArguments().getStringArrayList(ACCOUNTS_LIST_KEY);
//        StartConversationActivity.populateAccountSpinner(getActivity(), mActivatedAccounts, binding.account);
        builder.setView(binding.getRoot());

        builder.setCancelable(false);
        builder.setPositiveButton("Create group", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {// Do not write your logic.If you write it will automatically dismiss the dialog. Instead of here handle onShow() like below.

                if (! binding.groupChatName.getText().toString().trim().equals("")){
                    if (! binding.groupChatDesc.getText().toString().trim().equals("")){
                        mListener.onCreateDialogPositiveClick(binding.account,
                                binding.groupChatName.getText().toString().trim(),binding.groupChatDesc.getText().toString().trim());

                    }else {
                        MyApp.Companion.getAppInstance().showToastMsg("Please enter group description");

                    }

                }else{
                    MyApp.Companion.getAppInstance().showToastMsg("Please enter group name");
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        DelayedHintHelper.setHint(R.string.providing_a_name_is_optional, binding.groupChatName);
        binding.groupChatName.setOnEditorActionListener((v, actionId, event) -> {
            mListener.onCreateDialogPositiveClick(binding.account, binding.groupChatName.getText().toString().trim()
            ,binding.groupChatDesc.getText().toString().trim());
            return true;
        });
        return builder.create();
    }


    public interface CreateConferenceDialogListener {
        void onCreateDialogPositiveClick(Spinner spinner, String subject,String grpDescription);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateConferenceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CreateConferenceDialogListener");
        }
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
