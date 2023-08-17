package com.example.weatherapp.screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListitemBinding
import com.example.weatherapp.model.WeatherModel
import com.squareup.picasso.Picasso

class WeatherAdapter() : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ListitemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding){
            tvDate.text = item.time
            tvCondition.text = item.conditional
            tvTemp.text = item.avgtemp_c.ifEmpty { "${item.maxTemp}ºC" }
            tvWind.text = item.wind_kph
            tvHumidity.text = item.humidity
            Picasso.get().load("https:" + item.condition_icon).into(im)
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}