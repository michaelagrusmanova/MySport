package com.example.mysport

import DatabaseHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_update.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class UpdateActivity : AppCompatActivity() {
    /**
     * Třída pro úpravu již existující položky. Volána z třídy MyData.
     */
    private lateinit var databaseHelper: DatabaseHelper
    private val REQUEST_CODE_GALLERY = 999

    /**
     * Nastavení layoutu, zpětné tlačítko, získání dat z MyData, nastavení listenerů na
     * tlačítka - vybrat foto a uložit změny, našeptávač
     */
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("ID").toString()
        val name = intent.getStringExtra("Name").toString()
        val distance = intent.getStringExtra("Distance").toString()
        val duration = intent.getStringExtra("Duration").toString()
        val notes = intent.getStringExtra("Notes").toString()
        val image = intent.getByteArrayExtra("Image")
        databaseHelper = DatabaseHelper(this)
        setItems(name, distance, duration, notes, image)

        btn_select_file2.setOnClickListener { pickImageFromGallery() }

        write_activity_button2.setOnClickListener { updateItem(id.toInt()) }

        val activities = resources.getStringArray(R.array.Activities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, activities)
        set_activity2.setAdapter(adapter)

    }

    /**
     * Doplnění vlastností z vytvořeného detailu do formuláře na úpravu.
     * U obrázku je potřeba z ByteArray vytvořit Bitmap.
     */
    private fun setItems(
        name: String,
        distance: String,
        duration: String,
        notes: String,
        image: ByteArray?
    ) {
        set_activity2.setText(name)
        set_distance2.setText(distance)
        set_duration2.setText(duration)
        set_notes2.setText(notes)

        val bitmap2 = BitmapFactory.decodeByteArray(image, 0, image!!.size)
        imageViewPhoto2.setImageBitmap(bitmap2)
    }

    /**
     * Úprava položky - zavolání dataHelperu.
     * Jednotlivé položky se berou z formuláře pro úpravu.
     * Obrázek se zmenší a jeho typ je změněn na ByteArray.
     */
    private fun updateItem(id: Int) {
        val name = set_activity2.text.toString()
        val distance = set_distance2.text.toString()
        val duration = set_duration2.text.toString()
        val notes = set_notes2.text.toString()

        imageViewPhoto2.isDrawingCacheEnabled = true
        imageViewPhoto2.buildDrawingCache()
        val bitmap: Bitmap = imageViewPhoto2.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        if (name.isEmpty() || distance.isEmpty() || duration.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Prosím vyplňte vše", Toast.LENGTH_LONG).show()
        } else {

            val act = ActivityModel(
                id = id,
                name = name,
                distance = distance,
                duration = duration,
                notes = notes,
                image = image
            )
            val status = databaseHelper.updateItem(act)
            if (status > -1) {
                Toast.makeText(this, "Vaše aktivita byla upravena.",
                    Toast.LENGTH_LONG).show()
                val intent = Intent(this, MyData::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Vaše aktivita nebyla upravenana.",
                    Toast.LENGTH_LONG).show()
            }
        }
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
     * Po vybrání obrázku z galerie. Obrázek se převede z ByteArray na Bitmap a nastaví se
     * do ImageView.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val selectedImage = getResizedBitmap(bitmap, 600)
                imageViewPhoto2!!.setImageBitmap(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Metoda pro zmenšení vybraného obrázku.
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
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}