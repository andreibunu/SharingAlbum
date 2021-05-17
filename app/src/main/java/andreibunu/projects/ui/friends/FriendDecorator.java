package andreibunu.projects.ui.friends;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import andreibunu.projects.R;


/**
 * DESIGN PATTERN, STRUCTURAL : Decorator
 */
public class FriendDecorator extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        outRect.right = view.getContext().getResources().getDimensionPixelOffset(R.dimen.friend_margin_horizontal);
    }
}
