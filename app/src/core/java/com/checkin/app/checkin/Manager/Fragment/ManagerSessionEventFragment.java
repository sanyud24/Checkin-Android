package com.checkin.app.checkin.Manager.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Manager.Adapter.ManagerSessionEventAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionInvoiceActivity;
import com.checkin.app.checkin.Manager.ManagerSessionViewModel;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ManagerSessionEventFragment extends Fragment implements ManagerSessionEventAdapter.SessionEventInteraction {
    private Unbinder unbinder;
    @BindView(R.id.rv_ms_events)
    RecyclerView rvMSEvent;
    @BindView(R.id.tv_ms_customer_count)
    TextView tvCustomerCount;
    @BindView(R.id.tv_ms_event_session_time)
    TextView tvSessionTime;
    @BindView(R.id.swipe_refresh_ms_event)
    SwipeRefreshLayout swipeRefreshLayout;

    private ManagerSessionViewModel mViewModel;
    private ManagerSessionEventAdapter mAdapter;
    private String mTable;

    public static ManagerSessionEventFragment newInstance() {
        return new ManagerSessionEventFragment();
    }

    public ManagerSessionEventFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manager_session_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
    }

    private void setupUi() {
        rvMSEvent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ManagerSessionEventAdapter(this);
        rvMSEvent.setAdapter(mAdapter);
        rvMSEvent.setNestedScrollingEnabled(false);

        swipeRefreshLayout.setOnRefreshListener(() -> mViewModel.updateResults());
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerSessionViewModel.class);
        mViewModel.fetchSessionEvents();
        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    setupSessionData(data);
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

        mViewModel.getSessionEventData().observe(this, listResource -> {
            if (listResource == null || listResource.data == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    mAdapter.setData(listResource.data);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case LOADING:
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                }
            }
        });

        mViewModel.getDetailData().observe(this, resource -> {
            if (resource == null || resource.data == null)
                return;
            switch (resource.status) {
                case SUCCESS:
                   mViewModel.updateUiEventStatus(Long.parseLong(resource.data.getPk()));
                    break;
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), resource.message);
                }
            }
        });
    }

    private void setupSessionData(SessionBriefModel data) {
        mTable = data.getTable();
        tvCustomerCount.setText(data.formatCustomerCount());
        tvSessionTime.setText(String.format(Locale.ENGLISH, "Session Time: %s" , data.formatTimeDuration()));
    }

    @OnClick(R.id.btn_ms_event_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(getContext(), mViewModel.getShopPk(), mViewModel.getSessionPk());
        mViewModel.updateResults();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onEventMarkDone(ManagerSessionEventModel eventModel) {
        mViewModel.markEventDone(eventModel.getPk());
    }

    @Override
    public void onBillApprove() {
        Intent intent = new Intent(requireContext(), ManagerSessionInvoiceActivity.class);
        intent.putExtra(ManagerSessionInvoiceActivity.TABLE_NAME, mTable)
                .putExtra(ManagerSessionInvoiceActivity.KEY_SESSION, mViewModel.getSessionPk());
        startActivity(intent);
    }
}
