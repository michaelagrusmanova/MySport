import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mysport.ActivityModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    DATABASEVERSION) {
    /**
     * Třída pro práci s databází.
     */

    /**
     * Definice struktury databáze.
     */
    companion object {
        private const val DATABASENAME = "diary.db"
        private const val DATABASEVERSION = 2
        private const val TABLENAME = "table_diary"
        private const val COL_ID = "_id"
        private const val COL_NAME = "Activity"
        private const val COL_DISTANCE = "Distance"
        private const val COL_DURATION = "Duration"
        private const val COL_NOTES = "Notes"
        private const val COL_IMAGE = "Image_data"
    }

    /**
     * Metoda pro vytvoření databáze.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLENAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_DISTANCE + " TEXT," +
                COL_DURATION + " TEXT," +
                COL_NOTES + " TEXT," +
                COL_IMAGE + " BLOB" +
                ")")
        db?.execSQL(createTable)
    }

    /**
     * Volá se, pokud se změní verze databáze (změní se základní struktura tabulky).
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLENAME")
        onCreate(db)
    }

    /**
     * Vložení záznamu do databáze.
     */
    fun insertData(activityModel: ActivityModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COL_NAME, activityModel.name)
        contentValues.put(COL_DISTANCE, activityModel.distance)
        contentValues.put(COL_DURATION, activityModel.duration)
        contentValues.put(COL_NOTES, activityModel.notes)
        contentValues.put(COL_IMAGE, activityModel.image)

        val result = db.insert(TABLENAME, null, contentValues)
        db.close()
        return result
    }

    /**
     * Získá všechny data z databáze a vrátí je.
     */
    @SuppressLint("Range")
    fun getAllData(): ArrayList<ActivityModel> {
        val list: ArrayList<ActivityModel> = ArrayList()
        val query = "SELECT * FROM $TABLENAME"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        var id: Int
        var name: String
        var distance: String
        var duration: String
        var notes: String
        var image: ByteArray

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("_id"))
                name = cursor.getString(cursor.getColumnIndex("Activity"))
                distance = cursor.getString(cursor.getColumnIndex("Distance"))
                duration = cursor.getString(cursor.getColumnIndex("Duration"))
                notes = cursor.getString(cursor.getColumnIndex("Notes"))
                image = cursor.getBlob(cursor.getColumnIndex("Image_data"))

                val act = ActivityModel(
                    id = id,
                    name = name,
                    distance = distance,
                    duration = duration,
                    notes = notes,
                    image = image
                )
                list.add(act)
            } while (cursor.moveToNext())
        }
        return list
    }

    /**
     * Metoda pro smazání položky v databázi.
     */
    fun deleteItem(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, id)
        val success = db.delete(TABLENAME, "$COL_ID=$id", null)
        db.close()
        return success
    }

    /**
     * Metoda pro úpravu položky v databázi.
     */
    fun updateItem(activityModel: ActivityModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_NAME, activityModel.name)
        contentValues.put(COL_DISTANCE, activityModel.distance)
        contentValues.put(COL_DURATION, activityModel.duration)
        contentValues.put(COL_NOTES, activityModel.notes)
        contentValues.put(COL_IMAGE, activityModel.image)

        val success = db.update(TABLENAME, contentValues, "$COL_ID=${activityModel.id}", null)
        db.close()
        return success
    }
}