package com.lenskart.trendinggits.data;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lenskart.trendinggits.R;

import java.util.ArrayList;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> implements AdapterView.OnItemClickListener, Filterable {
    private final ArrayList<Repo> dataSet = new ArrayList<>();
    private ArrayList<Repo> repoOriginalFullList;
    private OnUserItemClickedListener mOnUserItemClickedListener;

    public RepoAdapter(ArrayList<Repo> mUserList, OnUserItemClickedListener onUserItemClickedListener) {
        this.dataSet.addAll(mUserList);
        repoOriginalFullList = mUserList;
        this.mOnUserItemClickedListener = onUserItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_list, parent, false);
        return new ViewHolder(view, mOnUserItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(16));

        Glide.with(holder.view.getContext())
                .load("https://github.com/" + dataSet.get(position).getOwnerAvatar() + ".png")
                .centerCrop()
                .apply(requestOptions)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.repoAvatarImg);
        holder.repoNameTxt.setText(dataSet.get(position).getRepoName());
        holder.repoLinkTxt.setText(dataSet.get(position).getRepoLink());
        holder.repoLangTxt.setText(dataSet.get(position).getLanguage());
        holder.repoForkCountTxt.setText(dataSet.get(position).getTotalForks());
        holder.repoStarTxt.setText(dataSet.get(position).getTotalStars());
        holder.todayRepoStarTxt.setText(dataSet.get(position).getTodayStars());
        if (dataSet.get(position).isSelected()) {
            holder.cardViewItem.setCardBackgroundColor(Color.GRAY);
        } else {
            holder.cardViewItem.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public Filter getFilter() {
        return searchedFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        TextView repoNameTxt;
        CardView cardViewItem;
        ImageView repoAvatarImg;
        TextView repoLinkTxt;
        TextView repoLangTxt;
        TextView repoForkCountTxt;
        TextView repoStarTxt;
        TextView todayRepoStarTxt;
        OnUserItemClickedListener onUserItemClickedListener;

        public ViewHolder(@NonNull View itemView, OnUserItemClickedListener onUserItemClickedListener) {
            super(itemView);
            view = itemView;
            cardViewItem = view.findViewById(R.id.cardViewItem);
            repoNameTxt = view.findViewById(R.id.repoNameTxt);
            repoAvatarImg = view.findViewById(R.id.repoAvatar);
            repoLinkTxt = view.findViewById(R.id.repoLinkTxt);
            repoLangTxt = view.findViewById(R.id.repoLangTxt);
            repoForkCountTxt = view.findViewById(R.id.repoForkCountTxt);
            repoStarTxt = view.findViewById(R.id.repoStarTxt);
            todayRepoStarTxt = view.findViewById(R.id.todayRepoStarTxt);
            this.onUserItemClickedListener = onUserItemClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if(dataSet.get(getAbsoluteAdapterPosition()).isSelected()) {
                cardViewItem.setCardBackgroundColor(Color.WHITE);
                dataSet.get(getAbsoluteAdapterPosition()).setSelected(false);
            }else {
                cardViewItem.setCardBackgroundColor(Color.GRAY);
                dataSet.get(getAbsoluteAdapterPosition()).setSelected(true);
            }

        }
    }

    public interface OnUserItemClickedListener {
        void onUserItemClicked(int itemId);
    }


    private final Filter searchedFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Repo> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(repoOriginalFullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Repo item : repoOriginalFullList) {
                    if (item.getRepoName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataSet.clear();
            dataSet.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}

