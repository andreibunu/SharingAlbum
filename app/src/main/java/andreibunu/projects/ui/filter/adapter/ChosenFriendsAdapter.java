package andreibunu.projects.ui.filter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import andreibunu.projects.R;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChosenFriendsAdapter extends ListAdapter<FriendFilter, RecyclerView.ViewHolder> {

    private static ChosenFriendsAdapter.FilterDiffCallBack diffCallback = new ChosenFriendsAdapter.FilterDiffCallBack();
    private ChosenFriendsAdapter.ClickListener clickListener;

    public ChosenFriendsAdapter(ChosenFriendsAdapter.ClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.face, parent, false);
        return new ChosenFriendsAdapter.FriendViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //currently only one type
        ((ChosenFriendsAdapter.FriendViewHolder) holder).onBind(getItem(position));
        holder.itemView.setOnClickListener(v -> clickListener.unchooseFriend(((FriendViewHolder) holder).friend));
    }

    public static class FilterDiffCallBack extends DiffUtil.ItemCallback<FriendFilter> {

        @Override
        public boolean areItemsTheSame(@NonNull FriendFilter oldItem, @NonNull FriendFilter newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FriendFilter oldItem, @NonNull FriendFilter newItem) {
            return false;
        }
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        Context context;
        private FriendFilter friend;

        public FriendViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            image = itemView.findViewById(R.id.face);
        }

        public void onBind(FriendFilter friendFilter) {
            this.friend = friendFilter;
            Glide.with(context)
                    .load(friendFilter.getUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(image);

        }
    }

    public interface ClickListener {
        void unchooseFriend(FriendFilter friend);
    }
}
