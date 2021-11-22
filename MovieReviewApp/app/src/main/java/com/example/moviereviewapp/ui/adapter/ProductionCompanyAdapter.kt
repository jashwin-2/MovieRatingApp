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
import com.example.moviereviewapp.model.ProductionCompany
import com.example.moviereviewapp.ui.view.AppTextView
import com.example.moviereviewapp.utils.Constants

class ProductionCompanyAdapter(val context: Context, val companyList: List<ProductionCompany>) :
    RecyclerView.Adapter<ProductionCompanyAdapter.CompanyHolder>() {

    inner class CompanyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster = view.findViewById<ImageView>(R.id.iv_company_poster)
        val comapanyName = view.findViewById<AppTextView>(R.id.tv_company_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.production_company_holder, parent, false)
        return CompanyHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyHolder, position: Int) {
        val url = Constants.IMAGE_BASE_URL + companyList[position].logo_path
        (context as AppCompatActivity).loadImage(url, holder.poster, R.drawable.ic_default_profile)
        holder.comapanyName.text = companyList[position].name
    }

    override fun getItemCount(): Int {
        return companyList.size
    }
}