package com.sante.priceindex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sante.priceindex.databinding.ActivityMainBinding
import com.sante.priceindex.ui.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) loadFragment(PriceWatchFragment())
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_watch  -> { loadFragment(PriceWatchFragment()); true }
                R.id.nav_calc   -> { loadFragment(ProfitCalcFragment()); true }
                R.id.nav_slate  -> { loadFragment(DigitalSlateFragment()); true }
                R.id.nav_trends -> { loadFragment(TrendsFragment()); true }
                else -> false
            }
        }
    }

    fun loadFragment(f: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, f).commit()
    }
}
