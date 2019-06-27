package com.elhazent.picodiploma.ojekonline.presenter;

import android.content.DialogInterface;

public interface ImplLoginPresenter {
    void ProsessRegister(final String name, final String phone, String email, final String password);
    void ProsessLogin(String device, String email, String password);
}
