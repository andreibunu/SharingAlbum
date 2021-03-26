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
import andreibunu.projects.utils.UiUtils;

public class GalleryAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static PhotoPairItemViewDiffCallBack diffCallback = new PhotoPairItemViewDiffCallBack();
    private Context context;

    protected GalleryAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
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
        if(getItem(position) instanceof PhotoPair) {
            ((PhotoViewHolder) holder).onBind((PhotoPair) getItem(position));
        }
        else{
            ((DateViewHolder) holder).onBind((String) getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof PhotoPair)
            return 0;
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
//            setDims(item);
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

            }
            else{
                rightConstraint.setVisibility(View.GONE);
            }
        }

        private void setDims(PhotoPair item) {
            int width = UiUtils.getScreenWidth();
            if (item.getLeftOrientation() == PhotoPair.Orientation.PORTRAIT &&
                    item.getRightOrientation() == PhotoPair.Orientation.PORTRAIT) {
                left.getLayoutParams().width = width / 3;
                left.getLayoutParams().height = 400;

                right.getLayoutParams().width = width / 3;
                right.getLayoutParams().height = 400;
            }

            if (item.getLeftOrientation() == PhotoPair.Orientation.PORTRAIT &&
                    item.getRightOrientation() == PhotoPair.Orientation.LANDSCAPE) {
                left.getLayoutParams().width = 100;
                left.getLayoutParams().height = 200;

                right.getLayoutParams().width = 400;
                right.getLayoutParams().height = 200;
            }

            if (item.getLeftOrientation() == PhotoPair.Orientation.LANDSCAPE &&
                    item.getRightOrientation() == PhotoPair.Orientation.PORTRAIT) {
                right.getLayoutParams().width = 400;
                right.getLayoutParams().height = 200;

                right.getLayoutParams().width = 100;
                right.getLayoutParams().height = 200;
            }

            if (item.getLeftOrientation() == PhotoPair.Orientation.LANDSCAPE &&
                    item.getRightOrientation() == PhotoPair.Orientation.LANDSCAPE) {
                left.getLayoutParams().height = 100;
                left.getLayoutParams().width = 200;

                right.getLayoutParams().height = 100;
                right.getLayoutParams().width = 200;
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
