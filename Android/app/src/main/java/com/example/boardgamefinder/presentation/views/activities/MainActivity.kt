package com.example.boardgamefinder.presentation.views.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.boardgamefinder.R
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.databinding.ActivityLogInBinding
import com.example.boardgamefinder.databinding.ActivityMainBinding
import com.example.boardgamefinder.presentation.views.fragments.*

/**
 * Activity for holding all app tabs
 */
internal class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkForAuth()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        // setting fragments for main frame
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.nav_search -> {
                    replaceFragment(SearchFragment())
                }
                R.id.nav_add -> {
                    replaceFragment(NewEventFragment())
                }
                /*R.id.nav_notifications -> {
                    replaceFragment(NotificationsFragment())
                }*/
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                }
            }
            true
        }
    }

    // used for switching between fragments in main frame
    fun replaceFragment(fragment: Fragment) {
        // open fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, fragment)

        if(fragment !is HomeFragment)
            fragmentTransaction.addToBackStack( "tag" )

        fragmentTransaction.commit()
    }

    private fun checkForAuth(){
        mSettings = getSharedPreferences(MySettings.APP_PREFERENCES, MODE_PRIVATE)

        // not logged in
        if (!mSettings.getBoolean(MySettings.APP_PREFERENCES_LOGGED_IN, false) || mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "")?.isEmpty() == true) {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    /**
     * when user agrees the permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SearchFragment.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Need location permission", Toast.LENGTH_SHORT).show()
            else
                (supportFragmentManager.findFragmentById(R.id.main_frame) as SearchFragment).locationGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun openLogIntActivity(){
        val intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}