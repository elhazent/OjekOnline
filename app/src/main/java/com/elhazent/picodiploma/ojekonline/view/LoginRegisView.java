package com.elhazent.picodiploma.ojekonline.view;

import android.content.DialogInterface;

import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataLogin;

public interface LoginRegisView {
    void showloading();
    void hideloading();
    void hidedialog();
    void showToast(String msg);
    void showError(String msg);
    void startActivity();
    void session(String token, DataLogin dataLogin);
}
