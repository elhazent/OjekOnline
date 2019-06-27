package com.elhazent.picodiploma.ojekonline;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elhazent.picodiploma.ojekonline.helper.HeroHelper;
import com.elhazent.picodiploma.ojekonline.helper.SessionManager;
import com.elhazent.picodiploma.ojekonline.model.DataItem;
import com.elhazent.picodiploma.ojekonline.model.ResponseHistoryReq;
import com.elhazent.picodiploma.ojekonline.network.InitRetrofit;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Unbinder unbinder;
    int i;
    private SessionManager session;
    public static List<DataItem> dataHistory;
    public static List<DataItem> dataHistory2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryFragment(int i) {
        this.i = i;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proses, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDatahistory();
        return view;

    }

    private void getDatahistory() {
        session = new SessionManager(getActivity());
        String token = session.getToken();
        String idUser = session.getIdUser();
        String device = HeroHelper.getDeviceUUID(getActivity());
        if (i == 2) {
            InitRetrofit.getInstance().getDataHistory(token, device, String.valueOf(i), idUser).enqueue(new Callback<ResponseHistoryReq>() {
                @Override
                public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        dataHistory = response.body().getData();
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(dataHistory, getActivity(), i);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {
                    Toast.makeText(getContext(), "GAGAL"+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FAILURE", "onFailure: " + t.getLocalizedMessage() );
                }
            });
        }  else if (i == 4){
            InitRetrofit.getInstance().getDataHistory(token,device, String.valueOf(i), idUser).enqueue(new Callback<ResponseHistoryReq>() {
                @Override
                public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        dataHistory2 = response.body().getData();
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(dataHistory2, getActivity(), i);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {

                }
            });
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
