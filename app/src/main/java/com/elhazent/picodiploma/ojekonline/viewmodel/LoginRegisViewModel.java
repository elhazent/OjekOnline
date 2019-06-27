package com.elhazent.picodiploma.ojekonline.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataLogin;
import com.elhazent.picodiploma.ojekonline.model.ResponseLoginRegis;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;
import com.elhazent.picodiploma.ojekonline.presenter.ImplLoginPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRegisViewModel extends ViewModel implements ImplLoginPresenter {

    MutableLiveData<DataLogin> data = new MutableLiveData<>();
    MutableLiveData<String> pesan = new MutableLiveData<>();
    MutableLiveData<String> error = new MutableLiveData<>();
    MutableLiveData dialogDismis = new MutableLiveData();
    MutableLiveData loadingdismis = new MutableLiveData();
    MutableLiveData loading = new MutableLiveData();
    MutableLiveData pindah = new MutableLiveData();

    public MutableLiveData getPindah() {
        return pindah;
    }

    private Context appContext;

    public MutableLiveData getLoading() {
        return loading;
    }

    public LoginRegisViewModel() {
        super();
        Log.d("TEST", "LoginRegisViewModel: ViewModel dibuat");
    }

    public MutableLiveData<DataLogin> getData() {
        return data;
    }

    public MutableLiveData<String> getPesan() {
        return pesan;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData getDialogDismis() {
        return dialogDismis;
    }

    public MutableLiveData getLoadingdismis() {
        return loadingdismis;
    }

    @Override
    public void ProsessRegister(String name, String phone, String email, String password) {
        loading.setValue(new Object());
        InitRetrofit.getInstance().registerUser(name, phone, email, password).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                loadingdismis.setValue(new Object());
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        pesan.setValue(msg);
                        dialogDismis.setValue(new Object());
                    } else {
                        pesan.setValue(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                error.setValue(t.getLocalizedMessage());
                loadingdismis.setValue(new Object());
            }
        });
    }

    @Override
    public void ProsessLogin(String device, String email, String password) {
        loading.setValue(new Object());
        InitRetrofit.getInstance().loginUser(device, password, email).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                loadingdismis.setValue(new Object());
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        pesan.setValue(msg);
                        DataLogin dataLogin = response.body().getData();
                        data.setValue(dataLogin);
                        String token = response.body().getToken();
                        SessionManager sesion = new SessionManager(appContext);
                        sesion.setIduser(dataLogin.getIdUser());
                        sesion.createLoginSession(token);
                        pindah.setValue(new Object());
                        dialogDismis.setValue(new Object());

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                error.setValue(t.getLocalizedMessage());
                loadingdismis.setValue(new Object());
                dialogDismis.setValue(new Object());
                Log.d("LOGIN", "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

}
