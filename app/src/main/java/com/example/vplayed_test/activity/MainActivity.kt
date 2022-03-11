package com.example.vplayed_test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.apivplayed.viewmodel.ViewModel
import com.example.vplayed_test.adapter.SliderAdapter
import com.example.vplayed_test.R
import com.example.vplayed_test.data.DataItem
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    //    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<ViewModel>()
    private var sliderAdapter: SliderAdapter = SliderAdapter()
    private lateinit var sliderhandler: Handler
    private lateinit var sliderRun: Runnable
    private lateinit var viewPager2: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sliderItems()
//        setUpViewModels()
        getResult()
//        itemsliderview()


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
        viewPager2 = findViewById(R.id.viewPagerImgSlider)
        viewPager2.adapter = sliderAdapter
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.SCROLL_AXIS_HORIZONTAL
        val comppagetrans = CompositePageTransformer()
        comppagetrans.addTransformer(MarginPageTransformer(40))
        comppagetrans.addTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager2.setPageTransformer(comppagetrans)
        sliderhandler = Handler()
        sliderRun = Runnable {
            viewPager2.currentItem = viewPager2.currentItem + 1
        }
        viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderhandler.removeCallbacks(sliderRun)
                    sliderhandler.postDelayed(sliderRun, 5000)

                }
            })
    }

    override fun onPause() {
        super.onPause()
        sliderhandler.removeCallbacks(sliderRun)


    }

    override fun onResume() {
        super.onResume()
        sliderhandler.postDelayed(sliderRun, 5000)


    }

    private fun getResult() {
//        coroutineScope.launch(Dispatchers.Main) {
        val results = viewModel.apiCall()
        Log.i("RESULTS", "$results")
        viewModel.getLiveDataObserver().observe(this@MainActivity) {
            sliderAdapter.setDataList(it as MutableList<DataItem>)
            sliderAdapter.notifyDataSetChanged()

        }
    }


}