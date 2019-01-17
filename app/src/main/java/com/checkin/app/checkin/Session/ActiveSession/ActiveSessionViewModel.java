package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChatModel;
import com.checkin.app.checkin.Session.SelfPresenceModel;
import com.checkin.app.checkin.Session.SessionCustomerModel;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Bhavik Patel on 05/08/2018.
 */

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SelfPresenceModel>> mPresenceData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ActiveSessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();

    private String mShopPk, mSessionId;

    ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getActiveSessionDetail();
    }

    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
        return mSessionData;
    }

    public LiveData<Resource<ActiveSessionInvoiceModel>> getInvoiceData(){
        return mInvoiceData;
    }

    public void getSessionIdInvoice(String sessionId) {
        mInvoiceData.addSource(mRepository.getSessionInvoiceDetail(sessionId), mInvoiceData::setValue);
    }

    public LiveData<List<OrderedItemModel>> getOrderedItems() {
        return Transformations.map(getActiveSessionDetail(), input -> {
            List<OrderedItemModel> orderedItems = null;
            if (input != null && input.data != null) {
                orderedItems = input.data.getOrderedItems();
            }
            return orderedItems;
        });
    }


    public void cancelOrders(String pk) { }

    public void checkoutSession() {
        Log.e("Session", "Checked out");
    }

    public void addMembers(String ids) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("user_id",ids);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public void sendSelfPresence(boolean is_public) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("is_public",is_public);
        mData.addSource(mRepository.putSelfPresence(data), mData::setValue);
    }

    public void setShopPk(String shopPk) {
        mShopPk = shopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getSessionId() {
        return mSessionId;
    }
}
