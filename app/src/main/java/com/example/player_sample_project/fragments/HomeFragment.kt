package com.example.player_sample_project.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.player_sample_project.R
import com.example.player_sample_project.activity.MainActivity
import com.example.player_sample_project.activity.PlayerActivity
import com.example.player_sample_project.adapter.CircularAdapter
import com.example.player_sample_project.adapter.OnclickListener
import com.example.player_sample_project.adapter.RecyclerAdapter
import com.example.player_sample_project.adapter.SliderAdapter
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.app.Utils
import com.example.player_sample_project.seeallpage.view.SeeAllActivity
import com.example.player_sample_project.viewmodel.ViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() ,OnclickListener, NetworkObserveReceiver.NetworkConnected {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var networkReceiver: NetworkObserveReceiver
    private lateinit var appController: AppController
    private lateinit var viewModel: ViewModel
    private var isApiCalled: Boolean = false // used this to avoid api re-hit from connectionReceiver
    private var sliderAdapter: SliderAdapter = SliderAdapter(this)
    private var recyclerAdapter: RecyclerAdapter = RecyclerAdapter(this)
    private var circularAdapter:CircularAdapter= CircularAdapter(this)

    private lateinit var sliderhandler: Handler
    private lateinit var sliderRun: Runnable
    private lateinit var viewPager2: ViewPager2
    private lateinit var recyclerview: RecyclerView
    private lateinit var recyclerview1: RecyclerView
    private lateinit var seeAll: ImageView
    private lateinit var seeAll1: ImageView
    private lateinit var title: TextView
    private lateinit var title1: TextView

    private lateinit var navdrawer:ImageView
    private var layoutManager: RecyclerView.LayoutManager? = null

    private lateinit var mShimmer:ShimmerFrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adManagerAdView: AdManagerAdView
    //private var storedData: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        sliderAdapter.clearDataList() // clear the Data class to avoid UI looks stuck, when use SharedPrefs to retain the response data

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    // Using this implement function to get status of internet
    override fun connectionReceived(isConnect: Boolean) {
        val isFetchedApi = appController.getPreferenceApiData("homePageResponse", "") ?: false
        if (isConnect) {
            if ((isFetchedApi == "" || isFetchedApi == false) && !isApiCalled) {
                coroutineScope.launch {
                    getResult(false)
                    showAds()
                    Thread.sleep(2000)
                }
            }
            adManagerAdView.visibility = View.VISIBLE
        } else {
            Utils.showLongMessage(activity, "Please check your network") // shows toast
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview = view.findViewById(R.id.recyclerView)
        recyclerview1 = view.findViewById(R.id.recy_view)
        mShimmer = view.findViewById(R.id.shimmer1)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh_home)
        seeAll = view.findViewById(R.id.see_all)
        seeAll1 = view.findViewById(R.id.see_all1)
        title=view.findViewById(R.id.title)
        title1=view.findViewById(R.id.title1)
        viewPager2 = view.findViewById(R.id.viewPagerImgSlider)
        navdrawer=view.findViewById(R.id.nav_drawer_button)
        adManagerAdView=view.findViewById(R.id.adManagerAdView)

        // Initialize
        appController = AppController(requireContext())
        val mainActivity = activity as MainActivity

        swipeRefreshLayout.setOnRefreshListener {
            appController.clearPreferencesApiData()
            swipeRefreshLayout.isRefreshing = true
            sliderAdapter.clearDataList()
            title.visibility = View.GONE
            seeAll.visibility = View.GONE
            title1.visibility = View.GONE
            seeAll1.visibility = View.GONE
            adManagerAdView.visibility = View.GONE
            fetchApiData()
        }
        navdrawer.setOnClickListener {
            if(activity is MainActivity) {
                mainActivity.openDrawer()
            }
        }
        seeAll.setOnClickListener {
            val intent = Intent(activity, SeeAllActivity::class.java)
            intent.putExtra("title", getString(R.string.trending_musics))
            startActivity(intent)
        }
        seeAll1.setOnClickListener {
            val intent = Intent(activity, SeeAllActivity::class.java)
            intent.putExtra("title", getString(R.string.latest_movies))
            startActivity(intent)
        }

        // Restore adapter data if available
        /*val savedDataSlider: ArrayList<Data>? = storedData?.getParcelableArrayList("adapter1_data")
        savedDataSlider?.let { data ->
            Log.i("connection-","savedData inside let--$data")
            sliderAdapter.setDataList(data)
            sliderAdapter.notifyDataSetChanged()
        }*/

        activity?.let {
            fetchApiData() // fetch ApiData
        }
    }

    // Api fetch and Ad load to setup Home userInterface
    private fun fetchApiData() {
        enableShimmer(true) // initially shimmer will show until get results
        sliderItems() //for set all the adapters
        val isFetchedApi = appController.getPreferenceApiData("homePageResponse", "") ?: false
        var isPrefsExists: Boolean = false;

        if (isFetchedApi != "" && isFetchedApi != false) {
            isPrefsExists = true // means prefs data exists
        }
        coroutineScope.launch {
            getResult(isPrefsExists)
            if (Utils.checkNetConnection(activity)) { showAds() }
            else adManagerAdView.visibility = View.GONE

            Thread.sleep(2000)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("connection-", "onStart")

        // Initialize the NetworkReceiver
        networkReceiver = NetworkObserveReceiver(this)

        // Register the receiver to observer networks
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireActivity().registerReceiver(networkReceiver, intentFilter)
    }


    override fun onResume() {
        super.onResume()
        Log.i("connection-", "onResume")

        sliderhandler.postDelayed(sliderRun, 5000)
    }

    override fun onPause() {
        super.onPause()
        Log.i("connection-", "onPause")
        // saved the response in bundle to load data when fragment changes and comeback, if needed
        /*val bundle = Bundle()
        val sliderData = sliderAdapter.getDataList()
        bundle.putParcelableArrayList("adapter1_data", ArrayList(sliderData))
        storedData = bundle*/

        sliderhandler.removeCallbacks(sliderRun)
        requireActivity().unregisterReceiver(networkReceiver)
    }

    override fun onStop() {
        super.onStop()
        Log.i("connection-", "onStop")
        sliderhandler.removeCallbacks(sliderRun)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("connection-", "onDestroyView")
    }

    private suspend fun showAds() {
        var adRequest = AdManagerAdRequest.Builder().build()

        /*val customAdSize = AdSize(80, 250)
        val adView = context?.let { AdManagerAdView(it) }
        adView?.setAdSizes(AdSize.BANNER, AdSize(120, 20), AdSize(250, 250))*/

        adManagerAdView.loadAd(adRequest)
        adManagerAdView.adListener= object:AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("loaded","Ad loaded success")
                adManagerAdView.visibility = View.VISIBLE
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.i("opened","success")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                adManagerAdView.visibility=View.GONE
                Toast.makeText(context, "Ad Closed", Toast.LENGTH_LONG).show()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                adManagerAdView.visibility=View.GONE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Toast.makeText(context, "Ad Loaded has issue...Check", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sliderItems() {
        layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerview1.layoutManager = layoutManager
        recyclerview1.adapter = recyclerAdapter

        recyclerview.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerview.adapter = circularAdapter

        viewPager2.adapter = sliderAdapter
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.SCROLL_AXIS_HORIZONTAL

        val compPageTrans = CompositePageTransformer()
        compPageTrans.addTransformer(MarginPageTransformer(40))
        compPageTrans.addTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager2.setPageTransformer(compPageTrans)
        sliderhandler = Handler()
        sliderRun = Runnable {
            if (viewPager2.currentItem == viewPager2.adapter?.itemCount?.minus(1)) { // items met lastItem, start again
                Thread.sleep(3000)
                viewPager2.setCurrentItem(0, true)
            } else viewPager2.currentItem += 1
        }
        viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderhandler.removeCallbacks(sliderRun)
                    sliderhandler.postDelayed(sliderRun, 3000)
                }
            }
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    suspend fun getResult(isPrefsExists: Boolean) {
        Log.i("connection-", " IF api called yes")

        // Call the api
        viewModel.apiCallHomeScreen(requireContext(), isPrefsExists)

        viewModel.getLiveDataObserver().observe(viewLifecycleOwner) { // response data is observed here using livedata
            // Note: Livedata observes the data change

            if (it != null) {
                Log.i("connection-","UI data 200")
                isApiCalled = true // needs only when comes to app with network connection
                sliderAdapter.setDataList(it)
                recyclerAdapter.setDataList(it)
                circularAdapter.setDataList(it)
                recyclerAdapter.notifyDataSetChanged()
                sliderAdapter.notifyDataSetChanged()
                circularAdapter.notifyDataSetChanged()
                enableShimmer(false)
                swipeRefreshLayout.isRefreshing = false
                title.visibility = View.VISIBLE
                seeAll.visibility = View.VISIBLE
                title1.visibility = View.VISIBLE
                seeAll1.visibility = View.VISIBLE
            } else {
                enableShimmer(true)
                Log.i("connection-","UI something is null")
            }

        }
    }

    /**
     * To click the banner means, navigate to player activity
     */
    override fun onclick(position: Int){
        activity.let {
            val intent= Intent(it, PlayerActivity::class.java)
            if (it != null) {
                it.startActivity(intent)
            }
        }
    }

    /**
    *To handle the shimmer functionality is to start/stop
    */
    private fun enableShimmer(isEnabled: Boolean) {
        if (isEnabled) {
            mShimmer.visibility = View.VISIBLE
            mShimmer.startShimmer()
        } else {
            mShimmer.stopShimmer()
            mShimmer.visibility = View.GONE
        }
    }



}