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
import de.hdodenhof.circleimageview.CircleImageView;

public class TagsAdapter extends ListAdapter<String, RecyclerView.ViewHolder> {

    private static TagsAdapter.FilterDiffCallBack diffCallback = new TagsAdapter.FilterDiffCallBack();
    private TagsAdapter.ClickListener clickListener;

    public TagsAdapter(TagsAdapter.ClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.tag, parent, false);
        return new TagsAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TagsAdapter.TagViewHolder) holder).onBind(getItem(position));
        holder.itemView.setOnClickListener(v -> {
            clickListener.onClick(((TagViewHolder) holder).tag.getText().toString());
        });
    }

    public static class FilterDiffCallBack extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return false;
        }
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {

        private TextView tag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tag);
        }

        public void onBind(String newTag) {
            tag.setText(newTag);
        }
    }

    public interface ClickListener {
        void onClick(String String);
    }
}
