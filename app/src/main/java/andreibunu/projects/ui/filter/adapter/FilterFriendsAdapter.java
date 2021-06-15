package andreibunu.projects.ui.filter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import andreibunu.projects.R;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import de.hdodenhof.circleimageview.CircleImageView;

public class FilterFriendsAdapter extends ListAdapter<FriendFilter, RecyclerView.ViewHolder> {

    private static FilterDiffCallBack diffCallback = new FilterDiffCallBack();
    private ClickListener clickListener;

    public FilterFriendsAdapter(ClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //currently only one type
        //friend_photo
        View view = layoutInflater.inflate(R.layout.friend_filter, parent, false);
        return new FriendViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FriendViewHolder) holder).onBind(getItem(position));
        holder.itemView.setOnClickListener(v -> {
            CircleImageView friend = ((FriendViewHolder) holder).image;
            if(friend.getBorderWidth() == 4){
                friend.setBorderWidth(0);
            }
            else{
                friend.setBorderWidth(4);
            }
            clickListener.onClick(((FriendViewHolder) holder).friend);
        });
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
        TextView name;
        Context context;
        private FriendFilter friend;

        public FriendViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            image = itemView.findViewById(R.id.filter_friend_photo);
            name = itemView.findViewById(R.id.filter_friend_name);
        }

        public void onBind(FriendFilter friendFilter) {
            this.friend = friendFilter;
            Glide.with(context)
                    .load(friendFilter.getUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(image);
            name.setText(friendFilter.getName());
        }
    }

    public interface ClickListener {
        void onClick(FriendFilter friendFilter);
    }
}
