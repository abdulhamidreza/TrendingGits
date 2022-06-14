package com.lenskart.trendinggits.data

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.widget.AdapterView.OnItemClickListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.lenskart.trendinggits.R
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.Glide
import androidx.cardview.widget.CardView
import java.util.*
import kotlin.collections.ArrayList

class RepoAdapter(
    mUserList: ArrayList<Repo>,
    onUserItemClickedListener: OnUserItemClickedListener
) : RecyclerView.Adapter<RepoAdapter.ViewHolder>(), OnItemClickListener, Filterable {
    private val dataSet = ArrayList<Repo>()
    private var repoOriginalFullList: ArrayList<Repo> = ArrayList()
    private val mOnUserItemClickedListener: OnUserItemClickedListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repo_list, parent, false)
        return ViewHolder(view, mOnUserItemClickedListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(16))
        Glide.with(holder.view.context)
            .load("https://github.com/" + dataSet[position].ownerAvatar + ".png")
            .centerCrop()
            .apply(requestOptions)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.repoAvatarImg)
        holder.repoNameTxt.text = dataSet[position].repoName
        holder.repoLinkTxt.text = dataSet[position].repoLink
        holder.repoLangTxt.text = dataSet[position].language
        holder.repoForkCountTxt.text = dataSet[position].totalForks
        holder.repoStarTxt.text = dataSet[position].totalStars
        holder.todayRepoStarTxt.text = dataSet[position].todayStars
        if (dataSet[position].isSelected) {
            holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
        } else {
            holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {}
    override fun getFilter(): Filter {
        return searchedFilter
    }

    inner class ViewHolder(var view: View, onUserItemClickedListener: OnUserItemClickedListener) :
        RecyclerView.ViewHolder(
            view
        ), View.OnClickListener {
        var repoNameTxt: TextView
        var cardViewItem: CardView
        var repoAvatarImg: ImageView
        var repoLinkTxt: TextView
        var repoLangTxt: TextView
        var repoForkCountTxt: TextView
        var repoStarTxt: TextView
        var todayRepoStarTxt: TextView
        var onUserItemClickedListener: OnUserItemClickedListener
        override fun onClick(view: View) {
            if (dataSet[absoluteAdapterPosition].isSelected) {
                cardViewItem.setCardBackgroundColor(Color.WHITE)
                dataSet[absoluteAdapterPosition].isSelected = false
            } else {
                cardViewItem.setCardBackgroundColor(Color.GRAY)
                dataSet[absoluteAdapterPosition].isSelected = true
            }
            onUserItemClickedListener.onUserItemClicked(repoOriginalFullList.indexOf(dataSet[absoluteAdapterPosition]))
        }

        init {
            cardViewItem = view.findViewById(R.id.cardViewItem)
            repoNameTxt = view.findViewById(R.id.repoNameTxt)
            repoAvatarImg = view.findViewById(R.id.repoAvatar)
            repoLinkTxt = view.findViewById(R.id.repoLinkTxt)
            repoLangTxt = view.findViewById(R.id.repoLangTxt)
            repoForkCountTxt = view.findViewById(R.id.repoForkCountTxt)
            repoStarTxt = view.findViewById(R.id.repoStarTxt)
            todayRepoStarTxt = view.findViewById(R.id.todayRepoStarTxt)
            this.onUserItemClickedListener = onUserItemClickedListener
            itemView.setOnClickListener(this)
        }
    }

    interface OnUserItemClickedListener {
        fun onUserItemClicked(itemId: Int)
    }

    private val searchedFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Repo>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(repoOriginalFullList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in repoOriginalFullList) {
                    if (item.repoName.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            dataSet.clear()
            dataSet.addAll(results.values as ArrayList<Repo>)
            notifyDataSetChanged()
        }
    }

    init {
        dataSet.addAll(mUserList)
        repoOriginalFullList = mUserList
        mOnUserItemClickedListener = onUserItemClickedListener
    }
}