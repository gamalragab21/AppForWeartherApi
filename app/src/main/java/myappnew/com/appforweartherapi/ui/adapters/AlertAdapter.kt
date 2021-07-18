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
import kotlinx.android.synthetic.main.alerts_item.view.*
import kotlinx.android.synthetic.main.daily_item.view.*
import kotlinx.android.synthetic.main.favorites_item.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.hourly_item.view.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.model.Alerts
import myappnew.com.appforweartherapi.model.Daily
import myappnew.com.appforweartherapi.model.Hourly
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

class AlertAdapter @Inject constructor(
    private val glide : RequestManager,
    private val context : Context,
) : RecyclerView.Adapter<AlertAdapter.SavedViewHolder>() {


    var alerts : List<Alerts>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val diffCallback = object : DiffUtil.ItemCallback<Alerts>() {
        override fun areContentsTheSame(oldItem : Alerts , newItem : Alerts) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : Alerts , newItem : Alerts) : Boolean {
            return oldItem== newItem
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

    class SavedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvAlarmFromTime = itemView.tvAlarmFromTime
        val tvAlarmFromDate = itemView.tvAlarmFromDate
        val decripition = itemView.decripition
        val tvAlarmToTime = itemView.tvAlarmToTime
        val tvAlarmToDate = itemView.tvAlarmToDate
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : SavedViewHolder {
        return SavedViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.alerts_item ,
                parent ,
                false
            )
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : SavedViewHolder , position : Int) {
        val alert = alerts[position]

        holder.apply {

            tvAlarmFromTime.text =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(alert.start)
            tvAlarmFromDate.text =
                SimpleDateFormat("dd MMM", Locale.getDefault()).format(alert.start)
            tvAlarmToTime.text =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(alert.end)
            tvAlarmToDate.text =
                SimpleDateFormat("dd MMM", Locale.getDefault()).format(alert.end)
            decripition.text = alert.description
        }
    }

    override fun getItemCount() : Int = alerts.size

    private var onAlertsClickListener : ((Alerts) -> Unit)? = null

    fun setOnLocationClickListener(listener : (Alerts) -> Unit) {
        onAlertsClickListener = listener
    }

    private var onMenuClickListener : ((Alerts,View) -> Unit)? = null

    fun setOnMenuClickListener(listener : (Alerts,View) -> Unit) {
        onMenuClickListener = listener
    }

}