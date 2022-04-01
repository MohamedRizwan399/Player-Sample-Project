package com.example.vplayed_test.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.vplayed_test.R
import com.example.vplayed_test.activity.MainActivity
import com.example.vplayed_test.activity.PlayerActivity
import com.example.vplayed_test.adapter.CircularAdapter
import com.example.vplayed_test.adapter.OnclickListener
import com.example.vplayed_test.adapter.RecyclerAdapter
import com.example.vplayed_test.adapter.SliderAdapter
import com.example.vplayed_test.postApiDataclass.Data
import com.example.vplayed_test.viewmodel.ViewModel
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() ,OnclickListener{
    private lateinit var mainActivity: MainActivity
    private val viewModel=ViewModel()
    private var sliderAdapter: SliderAdapter = SliderAdapter(this)
    private var recyclerAdapter: RecyclerAdapter = RecyclerAdapter()
    private var circularadapter:CircularAdapter= CircularAdapter()

    private lateinit var sliderhandler: Handler
    private lateinit var sliderRun: Runnable
    private lateinit var viewPager2: ViewPager2
    private lateinit var recyclerview: RecyclerView
    private lateinit var recyclerview1: RecyclerView

    private lateinit var navdrawer:ImageView
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview=view.findViewById(R.id.recyclerView)
        recyclerview1=view.findViewById(R.id.recy_view)

        viewPager2 = view.findViewById(R.id.viewPagerImgSlider)
        navdrawer=view.findViewById(R.id.nav_drawer)
        navdrawer.setOnClickListener {
            if(activity is MainActivity) {
                val dashboardView=activity as MainActivity
                dashboardView.visible()
//                dashboardView.openDrawer()
            }
        }


        sliderItems()
        getResult()
    }

    private fun sliderItems() {
//        sliderItemlist= ArrayList()

        layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerview1.layoutManager = layoutManager
        recyclerAdapter = RecyclerAdapter()
        recyclerview1.adapter=recyclerAdapter

        recyclerview.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerview.adapter=circularadapter


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
                    sliderhandler.postDelayed(sliderRun, 3000)

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

    @SuppressLint("NotifyDataSetChanged")
    private fun getResult() {
//        coroutineScope.launch(Dispatchers.Main) {
        val results = viewModel.apiCall()


        Log.i("RESULTS", "$results")
        viewModel.getLiveDataObserver().observe(viewLifecycleOwner) {
            sliderAdapter.setDataList(it as MutableList<Data>)
            recyclerAdapter.setDataList(it as MutableList<Data>)
            circularadapter.setDataList(it)
            recyclerAdapter.notifyDataSetChanged()
            sliderAdapter.notifyDataSetChanged()
            circularadapter.notifyDataSetChanged()


        }
//        val results1 = viewModel.apiCall1()
//        viewModel.getLiveDataObserver1().observe(viewLifecycleOwner) {
//            recyclerAdapter.setDataList(it as MutableList<Data>)
//            recyclerAdapter.notifyDataSetChanged()
//
//        }
    }
    override fun onclick(position: Int){
        activity.let {
            val intent= Intent(it, PlayerActivity::class.java)
            if (it != null) {
                it.startActivity(intent)
            }
        }

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}