package myappnew.com.conserve.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.hourly_item.view.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.model.Hourly
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

class HourlyAdapter @Inject constructor(
    private val glide : RequestManager,
  //  private val homeViewModel : HomeViewModel,
    @Named("Hourly") private val formatter:SimpleDateFormat
) : RecyclerView.Adapter<HourlyAdapter.NoteViewHolder>() {


    var hourlies : List<Hourly>
        get() = differ.currentList
        set(value) = differ.submitList(value)
    lateinit var tempUnitStatus : String

    private val diffCallback = object : DiffUtil.ItemCallback<Hourly>() {
        override fun areContentsTheSame(oldItem : Hourly , newItem : Hourly) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : Hourly , newItem : Hourly) : Boolean {
            return oldItem== newItem
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

    class NoteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvHourlyTempUnit = itemView.tvHourlyTempUnit
        val tvHourlyTemp = itemView.tvHourlyTemp
        val ivHourlyIcon = itemView.ivHourlyIcon
        val tvHourlyTime = itemView.tvHourlyTime
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.hourly_item ,
                parent ,
                false
            )
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : NoteViewHolder , position : Int) {
        val hourly = hourlies[position]
        var temp = hourly.temp
        holder.apply {
            glide.load("https://openweathermap.org/img/w/${hourly.weather[0].icon}.png").into(ivHourlyIcon)

//
            tempUnitStatus?.let {
                when (tempUnitStatus) {
                    "Celsius" -> {
                        tvHourlyTempUnit.text = "°C"
                    }
                    "Kelvin" -> {
                        temp += 273.15
                        tvHourlyTempUnit.text = "°K"
                    }
                    "Fahrenheit" -> {
                        temp = (temp * 1.8) + 32
                        tvHourlyTempUnit.text = "°F"
                    }
                }
            }


            val time = formatter.format(hourly.dt.toLong() * 1000)

            tvHourlyTemp.text = temp.roundToInt().toString()
             tvHourlyTime.text = time
//            if (hourly.dt < tomorrowTime - 72000) {
//                sunrise = todaySunrise
//                sunset = todaySunset
//            } else {
//                sunrise = tomorrowSunrise
//                sunset = tomorrowSunset
//            }



        }
    }

    override fun getItemCount() : Int = hourlies.size


}