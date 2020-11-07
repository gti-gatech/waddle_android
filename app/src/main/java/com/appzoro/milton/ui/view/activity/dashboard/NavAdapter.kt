package com.appzoro.milton.ui.view.activity.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.NavModel
import com.appzoro.milton.ui.OnClickInterface
import com.google.android.material.textview.MaterialTextView

class NavAdapter(
    val navItemList: ArrayList<NavModel>,
    val mListener: OnClickInterface
) :
    RecyclerView.Adapter<NavAdapter.NavViewHolder>() {
    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleName = itemView.findViewById(R.id.textViewTitle) as MaterialTextView
        val navIcon = itemView.findViewById(R.id.imageViewNav) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nav_item_list, parent, false)
        return NavViewHolder(view)
    }

    override fun getItemCount(): Int {
        return navItemList.size
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.titleName.text = navItemList[position].titleName
        holder.navIcon.setImageResource(navItemList[position].imageDrawable)
        holder.itemView.setOnClickListener {
            mListener.onClickItem(position,false)
        }

    }
}