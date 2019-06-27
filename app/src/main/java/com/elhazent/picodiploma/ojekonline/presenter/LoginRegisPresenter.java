package com.elhazent.picodiploma.ojekonline.presenter;

import android.content.DialogInterface;
import android.util.Log;

import com.elhazent.picodiploma.ojekonline.model.DataLogin;
import com.elhazent.picodiploma.ojekonline.model.ResponseLoginRegis;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;
import com.elhazent.picodiploma.ojekonline.view.LoginRegisView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRegisPresenter implements ImplLoginPresenter {

    LoginRegisView loginRegisView;
    DataLogin dataLogin;

    public LoginRegisPresenter(LoginRegisView loginRegisView) {
        this.loginRegisView = loginRegisView;
    }

    @Override
    public void ProsessRegister(String name, String phone, String email, String password) {
        loginRegisView.showloading();
        InitRetrofit.getInstance().registerUser(name, phone, email, password).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                loginRegisView.hideloading();
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        loginRegisView.showToast(msg);
                        loginRegisView.hidedialog();
                    } else {
                        loginRegisView.showToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                loginRegisView.showError("gagal format" + t.getLocalizedMessage());
                loginRegisView.hideloading();
            }
        });
    }

    @Override
    public void ProsessLogin(String device, String email, String password) {
        loginRegisView.showloading();
        InitRetrofit.getInstance().loginUser(device, password, email).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                loginRegisView.hideloading();
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        loginRegisView.showToast(msg);
                        dataLogin = response.body().getData();
                        String token = response.body().getToken();
                        loginRegisView.session(token, dataLogin);
                        loginRegisView.startActivity();
                        loginRegisView.hidedialog();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                loginRegisView.showError("Failure " + t.getLocalizedMessage());
                Log.d("LOGIN", "onFailure: " + t.getLocalizedMessage());
                loginRegisView.hidedialog();
                loginRegisView.hideloading();
            }
        });
    }
}
