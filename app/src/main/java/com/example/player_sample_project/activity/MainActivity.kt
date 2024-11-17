package com.example.player_sample_project.activity

import android.app.ActivityManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.player_sample_project.R
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.app.FirebaseAuthInstance
import com.example.player_sample_project.app.Utils
import com.example.player_sample_project.authentication.LoginBaseActivity
import com.example.player_sample_project.fragments.DownloadsFragment
import com.example.player_sample_project.fragments.HomeFragment
import com.example.player_sample_project.fragments.MenuFragment
import com.example.player_sample_project.fragments.NewFragment
import com.example.player_sample_project.fragments.SearchFragment
import com.example.player_sample_project.subscription.SubscriptionActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var appController: AppController
    private val homeFragment= HomeFragment()
    private val newFragment=NewFragment()
    private val searchFragment = SearchFragment()
    private val downloadsFragment = DownloadsFragment()
    private val menuFragment = MenuFragment()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var fragmentBackStack:FragmentBackStack
    private lateinit var bundle:Bundle
    private var signin:ImageView?=null
    private lateinit var usernameText:TextView
    private lateinit var image:ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // To change the default to preferred color of app description when goes to recent
        val taskDescription = ActivityManager.TaskDescription(
            getString(R.string.app_name), null, ContextCompat.getColor(this, R.color.dark_white))
        setTaskDescription(taskDescription)

        // Initialize AppController to handle the required utility methods
        appController = AppController(applicationContext)

        if (Utils.checkNetworkAndShowDialog(applicationContext)) {
            replaceFragment(homeFragment)
        } else {
            // If Network unavailable means, data get from Preferences if not null
            replaceFragment(homeFragment)
        }
        bottomNavigationView=findViewById(R.id.bottomNav)
        navigationView=findViewById(R.id.nav)
        drawerLayout=findViewById(R.id.drawerLayout)

        val view:View = navigationView.getHeaderView(0)
        signin=view.findViewById(R.id.iv_edit_profile)
        usernameText=view.findViewById(R.id.textView)
        image=view.findViewById(R.id.imageView)


        auth = FirebaseAuthInstance.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Your web client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val getLoggedInUsername = appController.getLoginStatus("userName", null.toString())
        if (getLoggedInUsername != null) usernameText.text = getString(R.string.welcome_message, getLoggedInUsername)
        else usernameText.text = getString(R.string.welcome_message, "Guest")

        val getProfileUrl = appController.getLoginStatus("photoUrl", "")
        if (getProfileUrl != null) {
            Glide.with(this)
                .load(getProfileUrl)
                .error(R.drawable.union_1)
                .into(image)
        }

        signin?.setOnClickListener {
            closeDrawer()
            Toast.makeText(this, "Update Screen not yet implemented", Toast.LENGTH_SHORT).show()
        }

        /**
         * listeners to handle the NavigationView item
         * Note: If needed, implement drawerListener or setOnMenuItemClickListener to handle states instead of below
        */
        navigationView.setNavigationItemSelectedListener { menuItem ->
            Log.i("connection-", "setNavigationItemSelectedListener")
            when(menuItem.itemId) {
                R.id.fav -> {
                    Toast.makeText(this@MainActivity, "Navigate to Favourite", Toast.LENGTH_SHORT).show()
                }
                R.id.playlists-> {
                    Toast.makeText(this@MainActivity, "Navigate to Playlists", Toast.LENGTH_SHORT).show()
                }
                R.id.savedCards-> {
                    Toast.makeText(this@MainActivity, "Navigate to Saved cards", Toast.LENGTH_SHORT).show()
                }
                R.id.settings-> {
                    Toast.makeText(this@MainActivity, "Navigate to Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.rating-> {
                    Toast.makeText(this@MainActivity, "Navigate to Rating", Toast.LENGTH_SHORT).show()
                }
                R.id.subscription-> {
                    val intent = Intent(this@MainActivity,SubscriptionActivity::class.java)
                    startActivity(intent)
                }
                R.id.logout-> {
                    this.let {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(getString(R.string.logout_title))
                            .setCancelable(true)
                            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                                signOutGoogle() // Sign out from Google
                                val intent = Intent(this, LoginBaseActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            })
                            .setNegativeButton("Cancel",DialogInterface.OnClickListener {dialog,id->
                                dialog.dismiss()
                            })
                            .show()
                    }
                }
            }
            closeDrawer()
            true
        }


        // BackStack handled
        if (savedInstanceState == null) {
            bundle = Bundle()
            fragmentBackStack = FragmentBackStack.Builder()
                .bottomMaxCount(5)
                .container_id(R.id.container)
                .lastFragmentToStay(HomeFragment::class.java)
                .build(this)

            fragmentBackStack.updateFrag(HomeFragment::class.java, bundle)
        }


        // Replace fragment based on bottomNavigation action
        bottomNavigationView.setOnNavigationItemSelectedListener(object :BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    R.id.home->replaceFragment(homeFragment)
                    R.id.search->replaceFragment(searchFragment)
                    R.id.downloads->replaceFragment(downloadsFragment)
                    R.id.watchlist->replaceFragment(newFragment)
                    R.id.menu->replaceFragment(menuFragment)
                }
                return true
            }

        })

    }

    // Handles SignOut action in this function
    fun signOutGoogle() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            Log.i("Login-", "SignOut Done-")
            appController.clearLoginPreferences()
            appController.clearPreferencesApiData()
        }
        // Still the User Exists, We need to delete the user from Firebase using below syntax
        FirebaseAuthInstance.auth.currentUser?.let { user ->
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Login-", "Access revoked successfully")
                } else {
                    Log.i("Login", "Failed to revoke access: ${task.exception}")
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuthInstance.auth.currentUser

        Log.i("Login-", "Main Activity onStart currentUser--${currentUser}")
        if (currentUser == null) {
            Toast.makeText(applicationContext, "Sign in again", Toast.LENGTH_LONG).show()
            appController.clearLoginPreferences() // clear LoggedIn data
            startActivity(Intent(this, LoginBaseActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("connection-", "Main Activity onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i("connection-", "Main Activity onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("connection-", "Main Activity Destroy")
    }


    fun openDrawer() {
        Log.i("connection-","Drawer open in MainActivity")
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        Log.i("connection-","Drawer closed in MainActivity")
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer() // close the navDrawer
            return
        }

        val returnedStack = fragmentBackStack.backHandling()
        if (returnedStack.equals(FragmentBackStack.CHILD_TAG)) {
            Log.i("connection-", "Backstack Child Tag")
            return
        }
        if (returnedStack.equals(FragmentBackStack.DO_BACK_STACK)) {
            Toast.makeText(this, "BackStack handled to navigate previous order", Toast.LENGTH_SHORT).show()
        }
        super.onBackPressed()
    }


}







