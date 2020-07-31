package com.omelchenkoaleks.cardbwithroom

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(
        context,
        Util.DATABASE_NAME,
        null,
        Util.DATABASE_VERSION
    ) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CARS_TABLE =
            ("CREATE TABLE " + Util.TABLE_NAME + "("
                    + Util.KEY_ID + " INTEGER PRIMARY KEY,"
                    + Util.KEY_NAME + " TEXT,"
                    + Util.KEY_PRICE + " TEXT" + ")")

        db!!.execSQL(CREATE_CARS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        onCreate(db);
    }

    fun insertCar(name: String?, price: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Util.KEY_NAME, name)
        contentValues.put(Util.KEY_PRICE, price)
        val id =
            db.insert(Util.TABLE_NAME, null, contentValues)
        db.close()
        return id
    }

    fun getCar(id: Long): Car? {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            Util.TABLE_NAME,
            arrayOf(
                Util.KEY_ID,
                Util.KEY_NAME,
                Util.KEY_PRICE
            ),
            Util.KEY_ID + "=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        cursor?.moveToFirst()
        val car = cursor?.getString(0)?.let {
            Car(
                it.toInt(),
                cursor?.getString(1), cursor?.getString(2)
            )
        }
        cursor?.close()
        return car
    }

    fun getAllCars(): List<Car>? {
        val db = this.readableDatabase
        val carsList: MutableList<Car> = ArrayList()
        val selectAllCars =
            "SELECT * FROM " + Util.TABLE_NAME
        val cursor = db.rawQuery(selectAllCars, null)
        if (cursor.moveToFirst()) {
            do {
                val car = Car(cursor.getString(0).toInt(), cursor.getString(1), cursor.getString(2))
//                car.setId(cursor.getString(0).toInt())
//                car.name = cursor.getString(1)
//                car.price = cursor.getString(2)
                carsList.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carsList
    }

    fun updateCar(car: Car): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Util.KEY_NAME, car.name)
        contentValues.put(Util.KEY_PRICE, car.price)
        return db.update(
            Util.TABLE_NAME,
            contentValues,
            Util.KEY_ID + "=?",
            arrayOf(car.id.toString())
        )
    }

    fun deleteCar(car: Car) {
        val db = this.writableDatabase
        db.delete(
            Util.TABLE_NAME,
            Util.KEY_ID + "=?",
            arrayOf(car.id.toString())
        )
        db.close()
    }

    fun getCarsCount(): Int {
        val db = this.readableDatabase
        val countQuery =
            "SELECT * FROM " + Util.TABLE_NAME
        val cursor = db.rawQuery(countQuery, null)
        return cursor.count
    }

}