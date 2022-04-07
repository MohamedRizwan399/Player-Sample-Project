package com.example.vplayed_test.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.vplayed_test.R
import com.example.vplayed_test.fragments.HomeFragment
import com.example.vplayed_test.fragments.PromosFragment
import com.example.vplayed_test.fragments.SearchFragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener{
    //    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val homeFragment=HomeFragment()
    private val searchFragment=SearchFragment()
    private val promosFragment=PromosFragment()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fragmentBackStack:FragmentBackStack
    private lateinit var bundle:Bundle
    private var signin:ImageView?=null
    private lateinit var tv:TextView
    private lateinit var image:ImageView

    private var logoutbutton:MenuItem?=null

    //for Glogin
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var gso: GoogleSignInOptions
    private  lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        mAuth= FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
            .build()

        replacefragment(homeFragment)
        bottomNavigationView=findViewById(R.id.bottomnav)
        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.nav)

        val view:View
        view=navigationView.getHeaderView(0)
        signin=view.findViewById(R.id.iv_edit_profile)
        tv=view.findViewById(R.id.textView)
        image=view.findViewById(R.id.imageView)

        val view1:Menu
        view1=navigationView.menu
        logoutbutton=view1.findItem(R.id.logout)

        if (tv!=null){
            logoutbutton?.setVisible(true)
        }


        signin?.setOnClickListener {
            navigationView.visibility=View.GONE
           Toast.makeText(this, "dhsjhdsjd", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,ProfileBaseActivity::class.java))
        }



        navigationView.setNavigationItemSelectedListener(object :NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.fav->{
                        Toast.makeText(this@MainActivity, "playlists", Toast.LENGTH_SHORT).show()
                    }
                    R.id.playlists->{
                        Toast.makeText(this@MainActivity, "playlists", Toast.LENGTH_SHORT).show()
                    }
                    R.id.savedcards->{
                        Toast.makeText(this@MainActivity, "savedcards", Toast.LENGTH_SHORT).show()
                    }
                    R.id.settings->{
                        Toast.makeText(this@MainActivity, "settings", Toast.LENGTH_SHORT).show()
                    }
                    R.id.rating->{
                        Toast.makeText(this@MainActivity, "rating", Toast.LENGTH_SHORT).show()
                    }
                    R.id.logout->{
                        navigationView.visibility=View.GONE
                        val alert=this.let {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Do you Want to logout?")
                                .setCancelable(true)
                                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                                    Auth.GoogleSignInApi.signOut(googleApiClient!!).setResultCallback(object :ResultCallback<Status>{
                                        override fun onResult(p0: Status) {
                                            if (p0.isSuccess()){
                                                navigationView.visibility=View.GONE
                                                tv.text=""
                                            }
                                            else{
                                                Toast.makeText(this@MainActivity, "id not fetched", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })
                                    dialog.dismiss()
                                })
                                .setNegativeButton("Cancel",DialogInterface.OnClickListener {dialog,id->
                                    dialog.dismiss()
                                })
                                .show()
                        }


                    }
                }

                return true
            }

        })
        



        if (savedInstanceState == null) {
            bundle=Bundle()

            fragmentBackStack = FragmentBackStack.Builder()
                .bottomMaxCount(5)
                .container_id(R.id.container)
                .lastFragmentToStay(HomeFragment::class.java)
                .build(this)


            fragmentBackStack.updateFrag(HomeFragment::class.java,bundle)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(object :BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.home->replacefragment(homeFragment)
                    R.id.search->replacefragment(searchFragment)
                    R.id.promos->replacefragment(promosFragment)
                    R.id.watchlist->replacefragment(searchFragment)
                    R.id.settings->replacefragment(promosFragment)




                }
                return true
            }

        })

    }
    override fun onStart() {
        super.onStart()

        val data = Auth.GoogleSignInApi.silentSignIn(googleApiClient!!)
        if(data.isDone)
        {

            val result = data.get()
            val account = result.signInAccount
            if(account!=null){
                tv.setText(account.email)
                image.setImageURI(account.photoUrl)
                signin?.visibility=View.VISIBLE

            }

        }
        else{
            data.setResultCallback {
                Toast.makeText(applicationContext, "Sign in again", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun visible(){
        navigationView.visibility=View.VISIBLE
    }


    fun openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START)



    }


    private fun replacefragment(fragment: Fragment) {
        if(fragment!=null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
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

    override fun onBackPressed() {
        if(navigationView.isVisible){
            navigationView.visibility=View.GONE
        }
//        if(bottomNavigationView.selectedItemId==R.id.home) {
//            super.onBackPressed()
//            finish()
//        }
//        else {
//            bottomNavigationView.selectedItemId=R.id.home
//        }

        val returnedtag=fragmentBackStack.backHandling()
        if (returnedtag.equals(FragmentBackStack.CHILDTAG)) {
            return
        }
        if (returnedtag.equals(FragmentBackStack.DOBACKSTACK)) {
            super.onBackPressed()
            Toast.makeText(this, "its home", Toast.LENGTH_SHORT).show()

        }
//        else if (returnedtag.equals(HomeFragment::class.java.canonicalName))
//        {
//            bottomNavigationView.selectedItemId=R.id.home
//        }else if (returnedtag.equals(SearchFragment::class.java.canonicalName)){
//            bottomNavigationView.selectedItemId=R.id.search
//        }else if (returnedtag.equals(PromosFragment::class.java.canonicalName)){
//            bottomNavigationView.selectedItemId=R.id.promos
//        }else if (returnedtag.equals(SearchFragment::class.java.canonicalName)){
//            bottomNavigationView.selectedItemId=R.id.watchlist
//        }else if (returnedtag.equals(PromosFragment::class.java.canonicalName)){
//            bottomNavigationView.selectedItemId=R.id.settings
//        }


        }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}







