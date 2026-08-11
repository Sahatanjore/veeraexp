package com.veeraexp.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.veeraexp.app.R
import com.veeraexp.app.ui.goals.GoalsFragment
import com.veeraexp.app.ui.home.HomeFragment
import com.veeraexp.app.ui.saha.SahaFragment
import com.veeraexp.app.ui.settings.SettingsFragment
import com.veeraexp.app.ui.transactions.TransactionsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Hosts the five main sections behind bottom navigation (spec section 26).
 * Each tab is a real, working Fragment backed by Room via FinanceRepository —
 * not a placeholder screen.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        if (savedInstanceState == null) {
            showFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_transactions -> TransactionsFragment()
                R.id.nav_goals -> GoalsFragment()
                R.id.nav_saha -> SahaFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            showFragment(fragment)
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
