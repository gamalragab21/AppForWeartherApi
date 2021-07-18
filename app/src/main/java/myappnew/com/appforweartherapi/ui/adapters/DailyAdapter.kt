package myappnew.com.conserve.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.daily_item.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.hourly_item.view.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.model.Daily
import myappnew.com.appforweartherapi.model.Hourly
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

class DailyAdapter @Inject constructor(
    private val glide : RequestManager,
    private val context : Context,
  //  private val homeViewModel : HomeViewModel,
    @Named("Daily") private val formatter:SimpleDateFormat
) : RecyclerView.Adapter<DailyAdapter.NoteViewHolder>() {


    var dailies : List<Daily>
        get() = differ.currentList
        set(value) = differ.submitList(value)
    lateinit var tempUnitStatus : String

    private val diffCallback = object : DiffUtil.ItemCallback<Daily>() {
        override fun areContentsTheSame(oldItem : Daily , newItem : Daily) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : Daily , newItem : Daily) : Boolean {
            return oldItem== newItem
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

    class NoteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val tvDailyWeekDay = itemView.tvDailyWeekDay
        val ivDailyIcon = itemView.ivDailyIcon
        val tvDailyDescription = itemView.tvDailyDescription
        val tvDailyContainer = itemView.tvDailyContainer
        val tvDailyMaxMinTemp = itemView.tvDailyMaxMinTemp
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.daily_item ,
                parent ,
                false
            )
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : NoteViewHolder , position : Int) {

        val daily = dailies[position]
        val weekday = formatter.format(daily.dt.toLong() * 1000)
        var max = daily.temp.max
        var min = daily.temp.min
        holder.apply {
            daily?.weather[0]?.icon?.let {icon->
                glide.load("https://openweathermap.org/img/w/$icon.png").into(ivDailyIcon)
            }

//
            tempUnitStatus?.let {
                when (tempUnitStatus) {
                    "Celsius" -> {
                        tvDailyMaxMinTemp.text =
                            "${max.roundToInt()} / ${min.roundToInt()}°C"
                    }
                    "Kelvin" -> {
                        max += 273.15
                        min += 273.15
                        tvDailyMaxMinTemp.text =
                            "${max.roundToInt()} / ${min.roundToInt()}°K"
                    }
                    "Fahrenheit" -> {
                        max = (max * 1.8) + 32
                        min = (min * 1.8) + 32
                        tvDailyMaxMinTemp.text =
                            "${max.roundToInt()} / ${min.roundToInt()}°F"
                    }
                }
            }

            when (position) {
                0 -> {
                    tvDailyWeekDay.text = context.getString(R.string.tomorrow)
                    tvDailyContainer.setBackgroundResource(R.drawable.gradient_square)
                }
                else -> tvDailyWeekDay.text = weekday
            }

           tvDailyDescription.text = daily.weather[0].description.replaceFirstChar {
               if (it.isLowerCase()) it.titlecase(
                   Locale.ROOT
               ) else it.toString()
           }





        }
    }

    override fun getItemCount() : Int = dailies.size


}