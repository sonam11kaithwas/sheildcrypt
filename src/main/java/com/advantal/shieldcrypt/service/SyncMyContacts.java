package com.advantal.shieldcrypt.service;

import android.util.Log;

import com.advantal.shieldcrypt.auth_pkg.fragment_pkg.LoginFragment;
import com.advantal.shieldcrypt.network_pkg.MainViewModel;
import com.advantal.shieldcrypt.network_pkg.RequestApis;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sonam on 29-09-2022 16:44.
 */

public class SyncMyContacts {
    MainViewModel mainViewModel;
    LoginFragment context;

    public SyncMyContacts(MainViewModel mainViewModel, LoginFragment context) {
        this.mainViewModel = mainViewModel;
        this.context = context;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("page", 0);
        hashMap.put("size", 10);
        hashMap.put("search", "");
        mainViewModel.featchDataFromServerWithAuth(
                new Gson().toJson(hashMap), RequestApis.getAllContacts, RequestApis.ALL_CONTACT);


    }


    public void feacthcDataFromServer() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Test", "Test");


//mainViewModel.getResponceCallBack().observe((AuthenticationActivity) context,
//        new Observer<Resource<ResponceModel>>() {
//            @Override
//            public void onChanged(Resource<ResponceModel> responceModelResource) {
//                Log.e("Test", "Test");
//            }
//        }
//        new Observer<Resource<ResponceModel>>() {
//                                    @Override
//                                    public void onChanged(@Nullable Resource<ResponceModel> aBoolean) {
//                                        if (aBoolean.getData().equals("")) {
//                                            Log.e("Test", "Test");
//
//                                        }
//                                    }
//                                }
//);
                            }
                        }
        );
    }
}
