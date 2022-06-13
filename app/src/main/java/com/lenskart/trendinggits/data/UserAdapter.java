package com.lenskart.trendinggits.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lenskart.trendinggits.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements AdapterView.OnItemClickListener {
    List<Repo> mUserList;
    private OnUserItemClickedListener mOnUserItemClickedListener;

    public UserAdapter(List<Repo> mUserList, OnUserItemClickedListener onUserItemClickedListener) {
        this.mUserList = mUserList;
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
                .load("https://github.com/" + mUserList.get(position).getOwnerAvatar() + ".png")
                .centerCrop()
                .apply(requestOptions)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.repoAvatarImg);
        holder.repoNameTxt.setText(mUserList.get(position).getRepoName());
        holder.repoLinkTxt.setText(mUserList.get(position).getRepoLink());
        holder.repoLangTxt.setText(mUserList.get(position).getLanguage());
        holder.repoForkCountTxt.setText(mUserList.get(position).getTotalForks());
        holder.repoStarTxt.setText(mUserList.get(position).getTotalStars());
        holder.todayRepoStarTxt.setText(mUserList.get(position).getTodayStars());

    }

    @Override
    public int getItemCount() {
        if (mUserList != null) {
            return mUserList.size();
        } else
            return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        TextView repoNameTxt;
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
            onUserItemClickedListener.onUserItemClicked(getAdapterPosition());
        }
    }

    public interface OnUserItemClickedListener {
        void onUserItemClicked(int position);
    }
}
