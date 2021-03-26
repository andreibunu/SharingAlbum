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
import andreibunu.projects.utils.ImageUtils;

public class GalleryFragment extends BaseFragment {

    private GalleryTypeFragment personalGalleryFragment;
    private GalleryTypeFragment friendsGalleryFragment;
    private TextView personalGalleryTextView, friendsGalleryTextView;
    private int numberOfCameraPhotos;
    private ImageView profileImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setProfile(view);
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


    private void setProfile(View view) {
        this.profileImage = view.findViewById(R.id.gallery_user);
        Glide.with(Objects.requireNonNull(getContext()))
                .load("data:image/png;base64,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")
                .into(profileImage);
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

        personalGalleryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(personalGalleryFragment);
                setAnimatedText(personalGalleryTextView, textColorBlack50, textColorBlack);
                setAnimatedText(friendsGalleryTextView, textColorBlack, textColorBlack50);
            }
        });

        friendsGalleryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(friendsGalleryFragment);
                setAnimatedText(friendsGalleryTextView, textColorBlack50, textColorBlack);
                setAnimatedText(personalGalleryTextView, textColorBlack, textColorBlack50);
            }
        });

    }

    private void setFragment(GalleryTypeFragment fragment) {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft.replace(R.id.gallery_fragment, fragment).addToBackStack(fragment.TAG);
        ft.commit();
    }

    private void createBothFragments() {
        this.personalGalleryFragment = new GalleryTypeFragment();
        this.friendsGalleryFragment = new GalleryTypeFragment();
    }

    public void setAnimatedText(final TextView tv, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                tv.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

}
