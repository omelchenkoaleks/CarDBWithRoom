package com.omelchenkoaleks.cardbwithroom

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var carsAdapter: CarsAdapter
    private val cars: ArrayList<Car> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = DatabaseHandler(this)
        dbHandler.getAllCars()?.let { cars.addAll(it) }
        recyclerView = findViewById(R.id.recycler_view)
        carsAdapter = CarsAdapter(this, cars, this@MainActivity)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = carsAdapter

        fab.setOnClickListener {
            addAndEditCars(false, null, -1)
        }
    }


    fun addAndEditCars(isUpdate: Boolean, car: Car?, position: Int) {
        val layoutInflaterAndroid: LayoutInflater = LayoutInflater.from(applicationContext)
        val view: View = layoutInflaterAndroid.inflate(R.layout.layout_add_car, null)

        val alertDialogBuilderUserInput: AlertDialog.Builder =
            AlertDialog.Builder(this@MainActivity)
        alertDialogBuilderUserInput.setView(view)

        val mNewCarTitle: TextView = view.findViewById(R.id.new_car_title)
        val mNameEditText: EditText = view.findViewById(R.id.name_edit_text)
        val mPriceEditText: EditText = view.findViewById(R.id.price_edit_text)

        mNewCarTitle.text = if (isUpdate) "Add Car" else "Edit Car"

        if (isUpdate && car != null) {
            mNameEditText.setText(car.name)
            mPriceEditText.setText(car.price)
        }

        alertDialogBuilderUserInput.setCancelable(false).setPositiveButton(
            if (isUpdate) "Update" else "Save"
        ) { dialogBox, id -> }.setNegativeButton(
            if (isUpdate) "Delete" else "Cancel"
        ) { dialogBox, id ->
            if (isUpdate) car?.let { deleteCar(it, position) } else {
                dialogBox.cancel()
            }
        }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            when {
                TextUtils.isEmpty(mNameEditText.text.toString()) -> {
                    Toast.makeText(this@MainActivity, "Enter car name!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                TextUtils.isEmpty(mPriceEditText.text.toString()) -> {
                    Toast.makeText(this@MainActivity, "Enter car price!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else -> {
                    alertDialog.dismiss()
                }
            }
            if (isUpdate && car != null) {
                updateCar(
                    mNameEditText.text.toString(),
                    mPriceEditText.text.toString(),
                    position
                )
            } else {
                createCar(
                    mNameEditText.text.toString(),
                    mPriceEditText.text.toString()
                )
            }
        }
    }

    private fun deleteCar(car: Car, position: Int) {
        cars.removeAt(position)
        dbHandler.deleteCar(car)
        carsAdapter.notifyDataSetChanged()
    }

    private fun updateCar(name: String, price: String, position: Int) {
        val car = cars[position]
        car.name = name
        car.price = price
        dbHandler.updateCar(car)
        cars[position] = car
        carsAdapter.notifyDataSetChanged()
    }

    private fun createCar(name: String, price: String) {
        val id: Long = dbHandler.insertCar(name, price)
        val car: Car? = dbHandler.getCar(id)
        if (car != null) {
            cars.add(car)
            carsAdapter.notifyDataSetChanged()
        }
    }
}
