package com.omelchenkoaleks.cardbwithroom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarsAdapter(
    var context: Context,
    private var cars: ArrayList<Car>,
    var mainActivity: MainActivity
) : RecyclerView.Adapter<CarsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mNameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        var mPriceTextView: TextView = itemView.findViewById(R.id.price_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context).inflate(R.layout.car_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val car: Car = cars[position]

        holder.mNameTextView.text = car.name
        holder.mPriceTextView.text = car.price + " $"
        holder.itemView.setOnClickListener {
            mainActivity.addAndEditCars(true, car, position)
        }
    }
}
