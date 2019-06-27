package com.elhazent.picodiploma.ojekonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elhazent.picodiploma.ojekonline.helper.HeroHelper;
import com.elhazent.picodiploma.ojekonline.helper.MyContants;
import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataDetailDriver;
import com.elhazent.picodiploma.ojekonline.model.ResponseDetailDriver;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.txtReview)
    TextView txtReview;
    @BindView(R.id.ivReviewFoto)
    ImageView ivReviewFoto;
    @BindView(R.id.txtReviewUserNama)
    TextView txtReviewUserNama;
    @BindView(R.id.ratingReview)
    RatingBar ratingReview;
    @BindView(R.id.txtReview2)
    TextView txtReview2;
    @BindView(R.id.edtReviewComment)
    MaterialEditText edtReviewComment;
    @BindView(R.id.txtReview3)
    TextView txtReview3;
    @BindView(R.id.cboReview)
    CheckBox cboReview;
    @BindView(R.id.btnReview)
    Button btnReview;
    private float nilaiRating;
    private SessionManager manager;
    private String idbooking;
    private String iddriver;
    private List<DataDetailDriver> dataRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviwe);
        ButterKnife.bind(this);
        manager = new SessionManager(this);
        idbooking = getIntent().getStringExtra(MyContants.IDBOOKING);
        iddriver = getIntent().getStringExtra(MyContants.IDDRIVER);
        ratingReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                nilaiRating = rating;
            }
        });
    }

    @OnClick({R.id.cboReview, R.id.btnReview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cboReview:
                break;
            case R.id.btnReview:
                setRating();
                break;
        }
    }

    private void setRating() {
        String token = manager.getToken();
        String device = HeroHelper.getDeviceUUID(this);
        String iduser = manager.getIdUser();
        String comment = edtReviewComment.getText().toString();
        InitRetrofit.getInstance().review(token,device,iduser,iddriver,idbooking,String.valueOf(nilaiRating),comment).enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                if (response.isSuccessful()){
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        Toast.makeText(ReviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                        dataRating = response.body().getData();
//                        txtReviewUserNama.setText(dataRating.get(0).getUserNama());
                        Toast.makeText(ReviewActivity.this, "Review Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ReviewActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ReviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
