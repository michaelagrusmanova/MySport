package com.example.mysport

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class AboutActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    /**
     * Nastavení layoutu, menu vlevo, listener na položky v menu, povolení ikony menu v toolbaru.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_app_layout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, my_drawer_layout, R.string.nav_open, R.string.nav_close)
        my_drawer_layout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        navigation_view.setNavigationItemSelectedListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Pro vybrání položky v menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        } else super.onOptionsItemSelected(item)
    }

    /**
     * Přepínač položek v menu.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_introduction -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_my_data -> {
                val intent = Intent(this, MyData::class.java)
                startActivity(intent)
            }
            R.id.nav_add_activity -> {
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_bmi_activity -> {
                val intent = Intent(this, BMICalculatorActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        my_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}