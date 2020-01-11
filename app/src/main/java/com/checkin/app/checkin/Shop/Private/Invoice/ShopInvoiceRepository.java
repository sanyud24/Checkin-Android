package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.data.network.ApiClient;
import com.checkin.app.checkin.data.network.ApiResponse;
import com.checkin.app.checkin.data.resource.NetworkBoundResource;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.data.network.RetrofitLiveData;
import com.checkin.app.checkin.data.network.WebApiService;

import java.util.List;

public class ShopInvoiceRepository {
    private static ShopInvoiceRepository INSTANCE;
    private final WebApiService mWebService;

    public ShopInvoiceRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static ShopInvoiceRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopInvoiceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShopInvoiceRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessions(long restaurantId, String fromDate, String toDate) {
        return new NetworkBoundResource<List<RestaurantSessionModel>, List<RestaurantSessionModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantSessionModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantSessionsById(restaurantId, fromDate, toDate));
            }

            @Override
            protected void saveCallResult(List<RestaurantSessionModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ShopSessionDetailModel>> getShopSessionDetail(long sessionId) {
        return new NetworkBoundResource<ShopSessionDetailModel, ShopSessionDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopSessionDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopSessionDetailById(sessionId));
            }

            @Override
            protected void saveCallResult(ShopSessionDetailModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessions(long restaurantId) {
        return getRestaurantSessions(restaurantId, null, null);
    }

    public LiveData<Resource<RestaurantAdminStatsModel>> getRestaurantAdminStats(long restaurantId, String fromDate, String toDate) {
        return new NetworkBoundResource<RestaurantAdminStatsModel, RestaurantAdminStatsModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantAdminStatsModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantAdminStats(restaurantId, fromDate, toDate));
            }

            @Override
            protected void saveCallResult(RestaurantAdminStatsModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RestaurantAdminStatsModel>> getRestaurantAdminStats(long restaurantId) {
        return getRestaurantAdminStats(restaurantId, null, null);
    }

    public LiveData<Resource<List<ShopSessionFeedbackModel>>> getRestaurantSessionReviews(final long sessionId) {
        return new NetworkBoundResource<List<ShopSessionFeedbackModel>, List<ShopSessionFeedbackModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopSessionFeedbackModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantSessionReviews(sessionId));
            }

            @Override
            protected void saveCallResult(List<ShopSessionFeedbackModel> data) {

            }
        }.getAsLiveData();
    }
}
