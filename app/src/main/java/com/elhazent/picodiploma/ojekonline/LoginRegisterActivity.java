package com.elhazent.picodiploma.ojekonline;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elhazent.picodiploma.ojekonline.databinding.ActivityLoginregisterBinding;
import com.elhazent.picodiploma.ojekonline.helper.HeroHelper;
import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataLogin;
import com.elhazent.picodiploma.ojekonline.model.ResponseLoginRegis;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;
import com.elhazent.picodiploma.ojekonline.presenter.LoginRegisPresenter;
import com.elhazent.picodiploma.ojekonline.view.LoginRegisView;
import com.elhazent.picodiploma.ojekonline.viewmodel.LoginRegisViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRegisterActivity extends AppCompatActivity implements LoginRegisView {
    private SessionManager session;
//    LoginRegisPresenter presenter;
    ProgressDialog loading;
    ActivityLoginregisterBinding binding;
    Observer<String> pesanObserver = pesan -> showToast(pesan);
    Observer<String> errorObserver = pesanError -> showToast(pesanError);
    Observer loadinghideObserver = hideloading -> hideloading();
    Observer loadingshowObserver = showloading -> showloading();
    Observer hidedialogObserver = hidedialog -> hidedialog();
    Observer pindahActivity = pindah -> startActivity();
    private LoginRegisViewModel regisLoViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        permission();
        loading = new ProgressDialog(this);
//        presenter = new LoginRegisPresenter(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loginregister);
        regisLoViewModel = ViewModelProviders.of(this).get(LoginRegisViewModel.class);
        regisLoViewModel.setAppContext(getApplicationContext());
        regisLoViewModel.getPesan().observe(this, pesanObserver);
        regisLoViewModel.getError().observe(this, errorObserver);
        regisLoViewModel.getDialogDismis().observe(this,hidedialogObserver);
        regisLoViewModel.getLoadingdismis().observe(this, loadinghideObserver);
        regisLoViewModel.getLoading().observe(this, loadingshowObserver);
        regisLoViewModel.getPindah().observe(this, pindahActivity);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        100);

            }
            return;
        }
    }



    private void register() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage(getString(R.string.messageregister));
        LayoutInflater inflater = getLayoutInflater();
        View viewReg = inflater.inflate(R.layout.layout_register, null, false);
        final ViewHolderRegister holderRegister = new ViewHolderRegister(viewReg);
        builder.setView(viewReg);
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cek validasi
                String email = holderRegister.edtEmail.getText().toString().trim();
                String password = holderRegister.edtPassword.getText().toString().trim();
                String name = holderRegister.edtName.getText().toString().trim();
                String phone = holderRegister.edtPhone.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    holderRegister.edtEmail.setError(getString(R.string.requireemail));
                } else if (TextUtils.isEmpty(password)) {
                    holderRegister.edtPassword.setError(getString(R.string.requirepassword));
                } else if (TextUtils.isEmpty(name)) {
                    holderRegister.edtName.setError(getString(R.string.requirename));
                } else if (TextUtils.isEmpty(phone)) {
                    holderRegister.edtPhone.setError(getString(R.string.requirephone));
                } else {
                    regisLoViewModel.ProsessRegister(name,phone,email,password);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void login() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setMessage(getString(R.string.messageregister));
        LayoutInflater inflater = getLayoutInflater();
        View viewLog = inflater.inflate(R.layout.layout_login, null, false);
        final ViewHolderLogin holderLogin = new ViewHolderLogin(viewLog);
        builder.setView(viewLog);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cek validasi
                String email = holderLogin.edtEmail.getText().toString().trim();
                String password = holderLogin.edtPassword.getText().toString().trim();
                String device = HeroHelper.getDeviceUUID(LoginRegisterActivity.this);
                if (TextUtils.isEmpty(email)) {
                    holderLogin.edtEmail.setError(getString(R.string.requireemail));
                } else if (TextUtils.isEmpty(password)) {
                    holderLogin.edtPassword.setError(getString(R.string.requirepassword));
                } else {
//                    presenter.ProsessLogin(device, email, password, dialog);
                    regisLoViewModel.ProsessLogin(device,email,password);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }


    @Override
    public void showloading() {
        loading.setMessage("Loading...");
        loading.show();
    }

    @Override
    public void hideloading() {
        loading.dismiss();
    }


    @Override
    public void hidedialog() {
//        dialogInterface.dismiss();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startActivity() {
        startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
    }


    @Override
    public void session(String token, DataLogin dataLogin) {

    }

    static class ViewHolderRegister {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;
        @BindView(R.id.edtName)
        MaterialEditText edtName;
        @BindView(R.id.edtPhone)
        MaterialEditText edtPhone;

        ViewHolderRegister(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static
    class ViewHolderLogin {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;

        ViewHolderLogin(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
