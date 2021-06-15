package andreibunu.projects.ui.image;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.databinding.FragmentImageFromFriendBinding;
import andreibunu.projects.domain.FriendPhoto;
import andreibunu.projects.ui.gallery.OnSwipeTouchListener;
import andreibunu.projects.utils.Utils;
import io.reactivex.disposables.CompositeDisposable;


public class ImageFromFriendFragment extends Fragment {


    public static final String TAG = ImageFragment.class.getCanonicalName();

    @Inject
    DatabaseHandler databaseHandler;

    private FragmentImageFromFriendBinding binding;
    private FriendPhoto friendPhoto;
    private CompositeDisposable disposables = new CompositeDisposable();

    List<FriendPhoto> allPhotos;
    int index;

    public ImageFromFriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getInstance().getAppComponent().inject(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            this.allPhotos = (List<FriendPhoto>) bundle.getSerializable("list");
            this.index = Integer.parseInt(bundle.getString("index"));
            this.friendPhoto = allPhotos.get(index);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageFromFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI(friendPhoto);
        setSwipeListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setSwipeListener() {
        binding.image.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                Log.d(TAG, "swipeRGHT");
                if (index == allPhotos.size() - 1) {
                    Objects.requireNonNull(getFragmentManager()).popBackStack();
                } else {
                    FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                            .setCustomAnimations(R.anim.slide_in, R.anim.slide2_out);
                    ImageFromFriendFragment fragment = new ImageFromFriendFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) allPhotos);
                    bundle.putString("index", (index + 1) + "");
                    fragment.setArguments(bundle);
                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                }
            }

            @Override
            public void onSwipeRight() {
                Log.d(TAG, "swipeLEFT");
                if (index == 0) {
                    getFragmentManager().popBackStack();
                } else {
                    FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                            .setCustomAnimations(R.anim.slide_back_in, R.anim.slide_back_out);
                    ImageFromFriendFragment fragment = new ImageFromFriendFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) allPhotos);
                    bundle.putString("index", (index - 1) + "");
                    fragment.setArguments(bundle);
                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                }
            }
        });
    }

    private void updateUI(FriendPhoto friendPhoto) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(friendPhoto.getUrl())
                .placeholder(R.drawable.placeholder_square)
                .into(binding.image);
        binding.from.setText(friendPhoto.getFrom());
        Calendar c = Calendar.getInstance();
        c.setTime(friendPhoto.getDate());
        String beautify = Utils.getMonth(c.get(Calendar.MONTH) + 1) + " " + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
        binding.date.setText(beautify);
    }

}