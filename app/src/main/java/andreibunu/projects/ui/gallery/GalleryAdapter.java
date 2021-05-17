package andreibunu.projects.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import andreibunu.projects.R;
import andreibunu.projects.ui.gallery.domain.FriendPhotoPair;
import andreibunu.projects.ui.gallery.domain.PhotoPair;

public class GalleryAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static PhotoPairItemViewDiffCallBack diffCallback = new PhotoPairItemViewDiffCallBack();

    protected GalleryAdapter() {
        super(diffCallback);
    }

    /**
     * DESIGN PATTERN, STRUCTURAL : ADAPTER
     * GalleryAdapter uses RecycleView.ViewHolder abstract class
     * A viewHolder is used to display an object inside the list
     * The object needs to be an instance pf a class extending RecycleView.ViewHolder
     * based on the type of the object, two objects (DateViewHolder and PhotoViewHolder) are used
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.gallery_pair, parent, false);
            return new PhotoViewHolder(view, context);
        }
        View view = layoutInflater.inflate(R.layout.gallery_date, parent, false);
        return new GalleryAdapter.DateViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItem(position) instanceof PhotoPair) {
            ((PhotoViewHolder) holder).onBind((PhotoPair) getItem(position));
        } else {
            if (getItem(position) instanceof FriendPhotoPair) {
                ((PhotoViewHolder) holder).onBind((FriendPhotoPair) getItem(position));
            } else {
                ((DateViewHolder) holder).onBind((String) getItem(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof PhotoPair || getItem(position) instanceof FriendPhotoPair)
            return 0;
//        if (getItem(position) instanceof FriendPhotoPair)
//            return 1;

        return 1;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {


        private final ConstraintLayout rightConstraint;
        private Context context;
        private ImageView left, right;

        public PhotoViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            left = itemView.findViewById(R.id.image_pair_left);
            right = itemView.findViewById(R.id.image_pair_right);
            rightConstraint = itemView.findViewById(R.id.gallery_right_constraint);
            left.setClipToOutline(true);
            right.setClipToOutline(true);

        }

        public void onBind(PhotoPair item) {
            Glide.with(context)
                    .load(item.getLeft().getAbsolutePath())
                    .placeholder(R.drawable.placeholder_square)
                    .into(left);
            if (item.getRight() != null) {
                Glide.with(context)
                        .load(item.getRight().getAbsolutePath())
                        .placeholder(R.drawable.placeholder_square)
                        .into(right);
                rightConstraint.setVisibility(View.VISIBLE);

            } else {
                rightConstraint.setVisibility(View.GONE);
            }
        }

        public void onBind(FriendPhotoPair item) {
            Glide.with(context)
                    .load(item.getLeft().getUrl())
                    .placeholder(R.drawable.placeholder_square)
                    .into(left);
            if (item.getRight() != null) {
                Glide.with(context)
                        .load(item.getRight().getUrl())
                        .placeholder(R.drawable.placeholder_square)
                        .into(right);
                rightConstraint.setVisibility(View.VISIBLE);

            } else {
                rightConstraint.setVisibility(View.GONE);
            }
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        TextView date;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.gallery_date);
        }

        public void onBind(String time) {
            date.setText(time);
        }
    }


    public static class PhotoPairItemViewDiffCallBack extends DiffUtil.ItemCallback<Object> {

        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }
    }
}
