package com.example.player_sample_project.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.player_sample_project.R
import com.example.player_sample_project.activity.MainActivity
import com.example.player_sample_project.adapter.MenuAdapter
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.authentication.LoginBaseActivity
import com.example.player_sample_project.data_mvvm.StaticDataForTesting

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var appController: AppController
    private lateinit var profileIcon: ImageView
    private lateinit var profileName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appController = AppController(requireContext())
        profileIcon = view.findViewById(R.id.profile_icon)
        profileName = view.findViewById(R.id.profile_name)
        recyclerView = view.findViewById(R.id.recycler_menu)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Access static data
        val items = StaticDataForTesting.items
        val itemsIcon = StaticDataForTesting.itemImages
        Log.i("menu-", "items are--$items")

        // Set the MenuAdapter
        menuAdapter = MenuAdapter(items, itemsIcon) {
            selectedItem -> navigateToMenuItems(selectedItem)
        }
        recyclerView.adapter = menuAdapter

        val getLoggedInUsername = appController.getLoginStatus("userName", null.toString())
        if (getLoggedInUsername != null) profileName.text = getString(R.string.welcome_message, getLoggedInUsername)
        else profileName.text = getString(R.string.welcome_message, "Guest")
    }

    private fun navigateToMenuItems(selectedItem: String) {
        val mainActivity = activity as MainActivity
        if (selectedItem == "Logout") {
            showLogoutDialog(mainActivity)
        } else {
            Toast.makeText(requireContext(),"Navigate to particular screen", Toast.LENGTH_LONG).show()
        }
    }

    private fun showLogoutDialog(activity: MainActivity) {
        val logoutDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.logout_custom_alert, null)
        logoutDialogView.findViewById<TextView>(R.id.logout_title).text = getString(R.string.logout_title)
        val builder = requireContext().let { it ->
            AlertDialog.Builder(it, R.style.CustomDialog).setView(logoutDialogView).setCancelable(false)
        }
        val alertDialog = builder.show()
        logoutDialogView.findViewById<Button>(R.id.logout_cancel).setOnClickListener {
            alertDialog.dismiss()
        }
        logoutDialogView.findViewById<Button>(R.id.logout_ok).setOnClickListener {
            activity.signOutGoogle()
            val intent = Intent(requireContext(), LoginBaseActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // parent Activity removed
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}