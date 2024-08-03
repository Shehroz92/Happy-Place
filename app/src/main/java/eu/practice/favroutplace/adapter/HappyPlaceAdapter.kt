package eu.practice.favroutplace.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.practice.favroutplace.R
import eu.practice.favroutplace.models.HappyPlaceModel

open class HappyPlaceAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlaceAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlaceImage: ImageView = view.findViewById(R.id.iv_place_image_recycler)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDescription: TextView = view.findViewById(R.id.tv_description)
        val tv_location :TextView = view.findViewById(R.id.tv_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.item_happy_place, parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]




        holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
        holder.tvTitle.text = model.title
        holder.tvDescription.text = model.description
        holder.tv_location.text =  model.location
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnClickListener{
        fun onClick(position: Int , model: HappyPlaceModel)

    }

}
