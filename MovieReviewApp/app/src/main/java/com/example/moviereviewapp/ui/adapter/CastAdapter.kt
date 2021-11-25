package com.example.moviereviewapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.loadImage
import com.example.moviereviewapp.model.Cast
import com.example.moviereviewapp.ui.view.AppTextView
import com.example.moviereviewapp.utils.Constants

class CastAdapter(val context: Context, private val castList: List<Cast>) :
    RecyclerView.Adapter<CastAdapter.CastHolder>() {

    inner class CastHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photo = view.findViewById<ImageView>(R.id.iv_photo)
        val realName = view.findViewById<AppTextView>(R.id.tv_real_name)
        val inMovieName = view.findViewById<AppTextView>(R.id.tv_in_movie_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cast_view_holder, parent, false)
        return CastHolder(view)
    }

    override fun onBindViewHolder(holder: CastHolder, position: Int) {
        val url = Constants.IMAGE_BASE_URL + castList[position].profile_path
        (context as AppCompatActivity).loadImage(url, holder.photo, R.drawable.ic_default_profile)
        holder.realName.text = castList[position].name
        holder.inMovieName.text = castList[position].character
    }

    override fun getItemCount(): Int {
        return castList.size
    }
}