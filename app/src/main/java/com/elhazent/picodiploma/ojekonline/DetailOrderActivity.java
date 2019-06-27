package com.elhazent.picodiploma.ojekonline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elhazent.picodiploma.ojekonline.helper.DirectionMapsV2;
import com.elhazent.picodiploma.ojekonline.helper.HeroHelper;
import com.elhazent.picodiploma.ojekonline.helper.MyContants;
import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataItem;
import com.elhazent.picodiploma.ojekonline.model.ResponseHistoryReq;
import com.elhazent.picodiploma.ojekonline.model.ResponseWaypoint;
import com.elhazent.picodiploma.ojekonline.model.RoutesItem;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;
import com.elhazent.picodiploma.ojekonline.network.RestApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.txtidbooking)
    TextView txtidbooking;
    @BindView(R.id.requestFrom)
    TextView requestFrom;
    @BindView(R.id.requestTo)
    TextView requestTo;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.requestWaktu)
    TextView requestWaktu;
    @BindView(R.id.requestTarif)
    TextView requestTarif;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.requestNama)
    TextView requestNama;
    @BindView(R.id.requestEmail)
    TextView requestEmail;
    @BindView(R.id.requestID)
    TextView requestID;

    @BindView(R.id.CompleteBooking)
    Button CompleteBooking;
    private GoogleMap mMap;
    int index;
    int status;
    DataItem dataItem;
    SessionManager session;
    String iddriver;
    String token;
    String device;
    String idbooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);
        index = getIntent().getIntExtra(MyContants.INDEX, 0);
        status = getIntent().getIntExtra(MyContants.STATUS, 0);
        if (status == 2) {
            CompleteBooking.setVisibility(View.VISIBLE);
            dataItem = HistoryFragment.dataHistory.get(index);
        } else if (status == 4){
            CompleteBooking.setVisibility(View.GONE);
            dataItem = HistoryFragment.dataHistory2.get(index);
        }
        detailRequest();
        session = new SessionManager(this);
        iddriver = session.getIdUser();
        token = session.getToken();
        device = HeroHelper.getDeviceUUID(this);
        idbooking = dataItem.getIdBooking();
    }

    private void detailRequest() {
        requestFrom.setText("dari : " + dataItem.getBookingFrom());
        requestTo.setText("tujuan : " + dataItem.getBookingTujuan());
        requestTarif.setText("dari : " + dataItem.getBookingBiayaUser());
        requestWaktu.setText("dari : " + dataItem.getBookingJarak());
        requestNama.setText("dari : " + dataItem.getUserNama());
        requestEmail.setText("dari : " + dataItem.getUserEmail());
        txtidbooking.setText("dari : " + dataItem.getIdBooking());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        detailMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + dataItem.getBookingTujuanLat()
                                + "," + dataItem.getBookingTujuanLng()));
                startActivity(i);
            }
        });
    }

    private void detailMap() {
        //get koordinat
        String origin = String.valueOf(dataItem.getBookingFromLat()) + "," + String.valueOf(dataItem.getBookingFromLng());
        String desti = String.valueOf(dataItem.getBookingTujuanLat()) + "," + String.valueOf(dataItem.getBookingTujuanLng());

        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(Double.parseDouble(dataItem.getBookingFromLat()), Double.parseDouble(dataItem.getBookingFromLng())));
        bound.include(new LatLng(Double.parseDouble(dataItem.getBookingTujuanLat()), Double.parseDouble(dataItem.getBookingTujuanLng())));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));
        LatLngBounds bounds = bound.build();
// begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
// end of new code

        mMap.animateCamera(cu);

        RestApi service = InitRetrofit.getInstanceGoogle();
        String api = getString(R.string.google_maps_key);
        Call<ResponseWaypoint> call = service.setRute(origin, desti, api);
        call.enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
                List<RoutesItem> routes = response.body().getRoutes();

                DirectionMapsV2 direction = new DirectionMapsV2(DetailOrderActivity.this);
                try {
                    String points = routes.get(0).getOverviewPolyline().getPoints();
                    direction.gambarRoute(mMap, points);

                } catch (Exception e) {
                    Toast.makeText(DetailOrderActivity.this, "lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.CompleteBooking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.CompleteBooking:
                completeBooking();
                break;
        }
    }

    private void completeBooking() {
        InitRetrofit.getInstance().completeBooking(iddriver, idbooking, device, token).enqueue(new Callback<ResponseHistoryReq>() {
            @Override
            public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(DetailOrderActivity.this, "selamat" + msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailOrderActivity.this, ReviewActivity.class);
                        intent.putExtra(MyContants.IDBOOKING,dataItem.getIdBooking());
                        intent.putExtra(MyContants.IDDRIVER,dataItem.getBookingDriver());
                        startActivity(intent);
                    } else {
                        Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {
                Toast.makeText(DetailOrderActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}
