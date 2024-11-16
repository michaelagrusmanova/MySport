package com.example.mysport

import DatabaseHelper
import android.content.Intent
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.my_drawer_layout
import kotlinx.android.synthetic.main.activity_main.navigation_view
import kotlinx.android.synthetic.main.my_data_layout.recyclerView

class MyData : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    /**
     * Třída pro zobrazení dat - seznam vytvořených aktivit.
     */
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var databaseHelper: DatabaseHelper
    private var adapter: ActivityAdapter? = null
    private val itemsList = ArrayList<ActivityModel>()

    /**
     * Nastavení layoutu, menu vlevo, listener na položky v menu, povolení ikony menu v toolbaru,
     * databáze, inicializace RecyclerView, devideru, adapteru pro smazání a editaci položky
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_data_layout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, my_drawer_layout, R.string.nav_open, R.string.nav_close)
        my_drawer_layout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        navigation_view.setNavigationItemSelectedListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(this)

        initRecyclerView()

        val itemDecor = DividerItemDecoration(this, HORIZONTAL)
        recyclerView.addItemDecoration(itemDecor)

        adapter?.setOnClickDeleteItem {
            deleteItem(it.id)
        }

        adapter?.setOnClickUpdateItem {
            updateItem(it.id, it.name, it.distance, it.duration, it.notes, it.image)
        }
    }

    /**
     * Metoda pro smazání položky.
     */
    private fun deleteItem(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Jste si jistí?")
        builder.setCancelable(true)
        builder.setPositiveButton("Ano") { dialog, _ ->
            databaseHelper.deleteItem(id)
            prepareItems()
            dialog.dismiss()
        }
        builder.setNegativeButton("Ne") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    /**
     * Metoda pro úpravu položky.
     */
    private fun updateItem(
        id: Int,
        name: String,
        distance: String,
        duration: String,
        notes: String,
        image: ByteArray?
    ) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("ID", id.toString())
        intent.putExtra("Name", name)
        intent.putExtra("Distance", distance)
        intent.putExtra("Duration", duration)
        intent.putExtra("Notes", notes)
        intent.putExtra("Image", image)
        startActivity(intent)
    }

    /**
     * Metoda pro inicializaci RecyclerView, nastavení adapteru.
     */
    private fun initRecyclerView() {
        adapter = ActivityAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        prepareItems()
    }

    /**
     * Metoda pro získání dat z databáze, vytvoření jednotlivých položek - model ActivityModel,
     * přidání každé do ArrayListu a zkontrolování adapterem změny v arrayListu.
     */
    private fun prepareItems() {
        itemsList.clear()
        val list = databaseHelper.getAllData()
        for (i in 0 until list.size) {
            val example = ActivityModel(
                list[i].id,
                list[i].name,
                list[i].distance,
                list[i].duration,
                list[i].notes,
                list[i].image
            )
            itemsList.add(example)
        }
        adapter?.notifyDataSetChanged()

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
        when (item.itemId) {
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