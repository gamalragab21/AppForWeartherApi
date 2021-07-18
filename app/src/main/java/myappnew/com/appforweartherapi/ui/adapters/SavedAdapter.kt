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
import kotlinx.android.synthetic.main.favorites_item.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.hourly_item.view.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.model.Daily
import myappnew.com.appforweartherapi.model.Hourly
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

class SavedAdapter @Inject constructor(
    private val glide : RequestManager,
    private val context : Context,
) : RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {


    var locions : List<Location>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val diffCallback = object : DiffUtil.ItemCallback<Location>() {
        override fun areContentsTheSame(oldItem : Location , newItem : Location) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : Location , newItem : Location) : Boolean {
            return oldItem.id== newItem.id
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

    class SavedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvFavoritesName = itemView.tvFavoritesName
        val btnMenu = itemView.btnMenu
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : SavedViewHolder {
        return SavedViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favorites_item ,
                parent ,
                false
            )
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : SavedViewHolder , position : Int) {
        val location = locions[position]
        holder.apply {
            tvFavoritesName.text = location.name

            itemView.setOnClickListener {
                   onLocationClickListener?.let { click->
                       click(location)
                   }
            }
            btnMenu.setOnClickListener {
                   onMenuClickListener?.let { click->
                       click(location,btnMenu)
                   }
            }

        }
    }

    override fun getItemCount() : Int = locions.size

    private var onLocationClickListener : ((Location) -> Unit)? = null

    fun setOnLocationClickListener(listener : (Location) -> Unit) {
        onLocationClickListener = listener
    }

    private var onMenuClickListener : ((Location,View) -> Unit)? = null

    fun setOnMenuClickListener(listener : (Location,View) -> Unit) {
        onMenuClickListener = listener
    }
}