package andreibunu.projects.ui.gallery;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.FilterList;
import andreibunu.projects.utils.ImageUtils;

public class GalleryFragment extends BaseFragment {

    private GalleryTypeFragment personalGalleryFragment;
    private GalleryTypeFragment friendsGalleryFragment;
    private TextView personalGalleryTextView, friendsGalleryTextView;
    private int numberOfCameraPhotos;
    private FilterList filtersList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.filtersList = (FilterList) bundle.getSerializable("filters");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createBothFragments();
        setFragment(this.personalGalleryFragment);
        numberOfCameraPhotos = ImageUtils.getCameraPhotos() != null ? ImageUtils.getCameraPhotos().length : 0;
        setTextViews(view);
        setTextViewListeners();
    }

    @Override
    protected void attach() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected void injectDependencies() {

    }

    private void setTextViews(View view) {
        this.personalGalleryTextView = view.findViewById(R.id.gallery_menu_personal);
        this.friendsGalleryTextView = view.findViewById(R.id.gallery_menu_friends);
        this.personalGalleryTextView.setText(getResources().getString(R.string.your_photos_n, numberOfCameraPhotos));
        //TODO set the real number of pics from friends
        //TODO maybe try to handle in another way the setting of the numbers, or send the lists to
        // the type fragment, because the files with photos are now received twice
        this.friendsGalleryTextView.setText(getResources().getString(R.string.pics_from_friends_n, 12));

    }

    private void setTextViewListeners() {
        final int textColorBlack50 = getResources().getColor(R.color.textColorBlack40, Objects.requireNonNull(getContext()).getTheme());
        final int textColorBlack = getResources().getColor(R.color.textColorBlack, getContext().getTheme());

        personalGalleryTextView.setOnClickListener(v -> {
            setFragment(personalGalleryFragment);
            setAnimatedText(personalGalleryTextView, textColorBlack50, textColorBlack);
            setAnimatedText(friendsGalleryTextView, textColorBlack, textColorBlack50);
        });

        friendsGalleryTextView.setOnClickListener(v -> {
            setFragment(friendsGalleryFragment);
            setAnimatedText(friendsGalleryTextView, textColorBlack50, textColorBlack);
            setAnimatedText(personalGalleryTextView, textColorBlack, textColorBlack50);
        });

    }

    private void setFragment(GalleryTypeFragment fragment) {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft.replace(R.id.gallery_fragment, fragment).addToBackStack(fragment.TAG);
        ft.commit();
    }

    private void createBothFragments() {
        this.personalGalleryFragment = new GalleryTypeFragment(filtersList);
        this.friendsGalleryFragment = new GalleryTypeFragment(filtersList);
    }

    public void setAnimatedText(final TextView tv, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(animator -> tv.setTextColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

}
