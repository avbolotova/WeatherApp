package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.screen.days.DaysFragment
import com.example.weatherapp.screen.hours.HoursFragment
import com.example.weatherapp.screen.main.MainViewModel
import com.example.weatherapp.screen.main.VpAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "b4f7edf21164438897083657231603"

class MainFragment : Fragment() {
    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf(
        "Hours",
        "Days"
    )
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        updateCurrentCard()


    }

    private fun init() = with(binding) {
        requestWeatherDate("Moscow")
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp){
                tab, pos -> tab.text = tList[pos]
        }.attach()
    }


    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            val maxMin = "${it.maxTemp} ºC / ${it.minTemp} ºC"
            val humidity = "humidity ${it.humidity}"
            val wind = "${it.wind_kph} km/h"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.avgtemp_c
            tvCondition.text = it.conditional
            tvMaxMin.text = maxMin
            tvHumidity.text = humidity
            tvWind.text = wind
            Picasso.get().load("https:" + it.condition_icon).into(imWeather)

        }
    }

    private fun requestWeatherDate(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key="+
                API_KEY +
                "&q="+
                "Moscow" +
                "&days=3&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    result -> parseWeatherDate(result)
            },
            {
                    error -> Log.d("Mylog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherDate(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject) : List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for(i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
                day.getJSONObject("day").getString("maxwind_mph"),
                day.getJSONObject("day").getString("avghumidity")
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }


    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel){
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours,
            mainObject.getJSONObject("current").getString("wind_kph"),
            mainObject.getJSONObject("current").getString("humidity"),
        )
        model.liveDataCurrent.value = item
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}