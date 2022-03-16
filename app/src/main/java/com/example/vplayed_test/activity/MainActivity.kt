package com.example.vplayed_test.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.vplayed_test.R
import com.example.vplayed_test.adapter.OnclickListener
import com.example.vplayed_test.adapter.RecyclerAdapter
import com.example.vplayed_test.adapter.SliderAdapter
import com.example.vplayed_test.data.DataItem
import com.example.vplayed_test.fragments.HomeFragment
import com.example.vplayed_test.fragments.PromosFragment
import com.example.vplayed_test.fragments.SearchFragment
import com.example.vplayed_test.postApiDataclass.Data
import com.example.vplayed_test.viewmodel.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    //    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val homeFragment=HomeFragment()
    private val searchFragment=SearchFragment()
    private val promosFragment=PromosFragment()

    private lateinit var bottomNavigationView: BottomNavigationView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        replacefragment(homeFragment)
        bottomNavigationView=findViewById(R.id.bottomnav)
        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home->replacefragment(homeFragment)
                R.id.search->replacefragment(searchFragment)
                R.id.promos->replacefragment(promosFragment)
                R.id.watchlist->replacefragment(searchFragment)
                R.id.settings->replacefragment(promosFragment)

            }
            return@setOnItemSelectedListener true
        }

        replacefragment(homeFragment)



    }

    private fun replacefragment(fragment: Fragment) {
        if(fragment!=null){
            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container,fragment)
            transaction.commit()
        }

    }

    //    private fun itemsliderview() {
//        sliderItemlist.add(SliderItem(R.drawable.ic_launcher_background))
//        sliderItemlist.add(SliderItem(R.drawable.ic_launcher_background))
//        sliderItemlist.add(SliderItem(R.drawable.ic_launcher_background))
//        sliderItemlist.add(SliderItem(R.drawable.ic_launcher_background))
//        sliderItemlist.add(SliderItem(R.drawable.ic_launcher_background))
//        sliderItemlist.add(SliderItem(R.drawable.image))
//        sliderItemlist.add(SliderItem(R.drawable.image))
//
//    }
    private fun setUpViewModels() {
//    val service = BaseApi.instance
//    viewModel.repo = Repo(service)
    }

    private fun sliderItems() {
//        sliderItemlist= ArrayList()

//        recyclerview=findViewById(R.id.recyclerView)
//        layoutManager = GridLayoutManager(applicationContext, 2)
//        recyclerview.layoutManager = layoutManager
//        recyclerAdapter = RecyclerAdapter()
//        recyclerview.adapter=recyclerAdapter
//
//        viewPager2 = findViewById(R.id.viewPagerImgSlider)
//        viewPager2.adapter = sliderAdapter
//
//        viewPager2.clipToPadding = false
//        viewPager2.clipChildren = false
//        viewPager2.offscreenPageLimit = 3
//        viewPager2.getChildAt(0).overScrollMode = RecyclerView.SCROLL_AXIS_HORIZONTAL
//        val comppagetrans = CompositePageTransformer()
//        comppagetrans.addTransformer(MarginPageTransformer(40))
//        comppagetrans.addTransformer { page, position ->
//            val r: Float = 1 - abs(position)
//            page.scaleY = 0.85f + r * 0.15f
//        }
//        viewPager2.setPageTransformer(comppagetrans)
//        sliderhandler = Handler()
//        sliderRun = Runnable {
//            viewPager2.currentItem = viewPager2.currentItem + 1
//
//        }
//        viewPager2.registerOnPageChangeCallback(
//            object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                    sliderhandler.removeCallbacks(sliderRun)
//                    sliderhandler.postDelayed(sliderRun, 3000)
//
//                }
//            })
    }

    override fun onPause() {
        super.onPause()
//        sliderhandler.removeCallbacks(sliderRun)


    }

    override fun onResume() {
        super.onResume()
//        sliderhandler.postDelayed(sliderRun, 5000)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getResult() {
//        coroutineScope.launch(Dispatchers.Main) {
//        val results = viewModel.apiCall()
//
//
//        Log.i("RESULTS", "$results")
//        viewModel.getLiveDataObserver().observe(this@MainActivity) {
//            sliderAdapter.setDataList(it as MutableList<Data>)
//            sliderAdapter.notifyDataSetChanged()
//
//        }
//        val results1 = viewModel.apiCall1()
//        viewModel.getLiveDataObserver1().observe(this@MainActivity) {
//            recyclerAdapter.setDataList(it as MutableList<Data>)
//            recyclerAdapter.notifyDataSetChanged()
//
//        }
    }


}