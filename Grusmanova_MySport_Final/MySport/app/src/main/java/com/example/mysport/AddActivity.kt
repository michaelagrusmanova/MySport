package com.example.mysport

import DatabaseHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.my_drawer_layout
import kotlinx.android.synthetic.main.activity_main.navigation_view
import kotlinx.android.synthetic.main.add_activity_layout.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class AddActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    /**
     * Třída pro přidání aktivity.
     */
    private lateinit var databaseHelper: DatabaseHelper
    private val REQUEST_CODE_GALLERY = 999
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    /**
     * Nastavení layoutu, menu vlevo, listener na položky v menu, databáze, povolení ikony menu
     * v toolbaru, listenery na tlačítka - vybrat soubor a smazat, nastavení našeptávače.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity_layout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, my_drawer_layout, R.string.nav_open, R.string.nav_close)
        my_drawer_layout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        navigation_view.setNavigationItemSelectedListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(this)

        write_activity_button.setOnClickListener { addSportActivity() }

        btn_select_file.setOnClickListener {
            pickImageFromGallery()
        }

        val activities = resources.getStringArray(R.array.Activities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, activities)
        set_activity.setAdapter(adapter)
    }

    /**
     * Otevření galerie.
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    /**
     * Zavolá se po pickImageFromGallery.
     * Nastavení ImageView na viditelný, vytvoření bitmapy z ByteArray
     * a nastavení bitmapy do ImageView.
     * PROBLÉM: když je načten vyfocený obrázek na výšku,
     * je otočen. ŘEŠENÍ: nejspíše bude v EXIF datech?
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            try {
                imageViewPhoto.visibility = View.VISIBLE
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val selectedImage = getResizedBitmap(bitmap, 600)
                imageViewPhoto!!.setImageBitmap(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Změna velikosti obrázku kvůli možnému načtení z galerie.
     */
    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize //pro šířku
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize //pro výšku
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, false)
    }

    /**
     * Metoda pro přidání aktivity. Data se vezmou z prvků - EditText v Layoutu,
     * z bitmapy je vytvořeno ByteArray, pokud je vše vyplněno - kromě obrázku (defaultní obrázek
     * loga aplikace), tak je možné přidat aktivitu do databáze.
     */
    private fun addSportActivity() {
        val name = set_activity.text.toString()
        val distance = set_distance.text.toString()
        val duration = set_duration.text.toString()
        val notes = set_notes.text.toString()

        imageViewPhoto.isDrawingCacheEnabled = true
        imageViewPhoto.buildDrawingCache()
        val bitmap: Bitmap = imageViewPhoto.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        if (name.isEmpty() || distance.isEmpty() || duration.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Prosím vyplňte vše", Toast.LENGTH_LONG).show()
        } else {

            val act = ActivityModel(
                name = name,
                distance = distance,
                duration = duration,
                notes = notes,
                image = image
            )
            val status = databaseHelper.insertData(act)
            if (status > -1) {
                Toast.makeText(this, "Vaše aktivita byla zaznamenána.",
                    Toast.LENGTH_LONG).show()
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this,"Vaše aktivita nebyla zaznamenána.",
                    Toast.LENGTH_LONG).show()
            }
        }
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