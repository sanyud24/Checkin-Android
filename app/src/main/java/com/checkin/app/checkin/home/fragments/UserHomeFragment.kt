package com.checkin.app.checkin.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.home.holders.LiveSessionTrackerAdapter
import com.checkin.app.checkin.home.holders.LiveSessionTrackerInteraction
import com.checkin.app.checkin.home.holders.NearbyRestaurantAdapter
import com.checkin.app.checkin.home.holders.PopularDishesAdapter
import com.checkin.app.checkin.home.model.LiveSessionDetailModel
import com.checkin.app.checkin.home.model.SessionType
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.LiveSessionViewModel
import com.checkin.app.checkin.menu.activities.ActiveSessionMenuActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.checkin.app.checkin.session.scheduled.activities.PreorderSessionDetailActivity
import com.checkin.app.checkin.session.scheduled.activities.QSRSessionDetailActivity

class UserHomeFragment : BaseFragment(), LiveSessionTrackerInteraction {
    override val rootLayout = R.layout.fragment_user_home

    @BindView(R.id.rv_home_suggested_dishes)
    internal lateinit var rvSuggestedDishes: RecyclerView
    @BindView(R.id.rv_home_nearby_restaurants)
    internal lateinit var rvNearbyRestaurants: RecyclerView
    @BindView(R.id.container_home_live_session)
    internal lateinit var containerLiveSession: ViewGroup
    @BindView(R.id.vp_home_session_live)
    internal lateinit var vpLiveSession: ViewPager2

    private lateinit var mPopularDishAdapter: PopularDishesAdapter
    private lateinit var mRestAdapter: NearbyRestaurantAdapter
    private lateinit var mLiveSessionAdapter: LiveSessionTrackerAdapter

    private val mViewModel: HomeViewModel by activityViewModels()
    private val mLiveSessionViewModel: LiveSessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshScreen(R.id.sr_home)
        enableDisableSwipeRefresh(true)

        mRestAdapter = NearbyRestaurantAdapter()
        mPopularDishAdapter = PopularDishesAdapter()
        mLiveSessionAdapter = LiveSessionTrackerAdapter(this)

        vpLiveSession.adapter = mLiveSessionAdapter

        rvNearbyRestaurants.layoutManager = LinearLayoutManager(context)
        rvNearbyRestaurants.adapter = mRestAdapter

        rvSuggestedDishes.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rvSuggestedDishes.adapter = mPopularDishAdapter
        rvNearbyRestaurants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                enableDisableSwipeRefresh(newState == RecyclerView.SCROLL_STATE_IDLE)
            }
        })

        mViewModel.nearbyRestaurants.observe(this, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.SUCCESS -> listResource.data?.let(mRestAdapter::updateData)
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.liveSessionData.observe(this, Observer {
            it?.let {
                handleLoadingRefresh(it)
                when (it.status) {
                    Resource.Status.SUCCESS -> it.data?.let {
                        containerLiveSession.visibility = View.VISIBLE
                        mLiveSessionAdapter.updateData(it)
                    }
                    Resource.Status.LOADING -> startRefreshing()
                    Resource.Status.ERROR_NOT_FOUND -> {
                        containerLiveSession.visibility = View.GONE
                    }
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.fetchScheduledSessions()
        mLiveSessionViewModel.fetchLiveActiveSession()
    }

    override fun updateScreen() {
        super.updateScreen()
        mViewModel.fetchMissing()
        mViewModel.updateResults()
        mLiveSessionViewModel.updateResults()
    }

    override fun onOpenSessionDetails(session: LiveSessionDetailModel) {
        when (session.sessionType) {
            SessionType.DINING -> startActivity(Intent(requireContext(), ActiveSessionActivity::class.java))
            SessionType.PREDINING -> PreorderSessionDetailActivity.startScheduledSessionDetailActivity(requireContext(), session.pk)
            SessionType.QSR -> QSRSessionDetailActivity.startScheduledSessionDetailActivity(requireContext(), session.pk)
        }
    }

    override fun onOpenRestaurantProfile(restaurant: RestaurantLocationModel) {
        requireContext().openPublicRestaurantProfile(restaurant.pk)
    }

    override fun onOpenRestaurantMenu(session: LiveSessionDetailModel) {
        when (session.sessionType) {
            SessionType.DINING -> ActiveSessionMenuActivity.openMenu(requireContext(), session.restaurant.pk)
            else -> onOpenRestaurantProfile(session.restaurant)
        }
    }

    companion object {
        fun newInstance() = UserHomeFragment()
    }
}
