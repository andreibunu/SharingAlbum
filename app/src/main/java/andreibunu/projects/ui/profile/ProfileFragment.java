package andreibunu.projects.ui.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentProfileBinding;
import andreibunu.projects.ui.base.BaseFragment;


public class ProfileFragment extends BaseFragment {


    FirebaseAuth firebaseAuth;
    private FragmentProfileBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition sharedElementsTransition = TransitionInflater.from(getActivity())
                .inflateTransition(R.transition.shared_image);
        Transition sharedElementReturnTransition = TransitionInflater.from(getActivity())
                .inflateTransition(R.transition.shared_image);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_image);
        setSharedElementEnterTransition(transition);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        postponeEnterTransition(10, TimeUnit.SECONDS);

        binding.edit.setOnClickListener(v -> {
            ViewCompat.setTransitionName(binding.user, "transition");
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            SettingsFragment fragment = new SettingsFragment();
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
        ViewCompat.setTransitionName(binding.user, "transition");
        setUserData();
        setDatabaseUserData();

    }

    private void setUserData() {
        binding.name.setText(firebaseUser.getDisplayName());
        binding.email.setText(firebaseUser.getEmail());
        Glide.with(Objects.requireNonNull(getContext()))
                .load(firebaseUser.getPhotoUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.image);
        Glide.with(getContext())
                .load(firebaseUser.getPhotoUrl())
                .into(binding.user);

    }

    @Override
    protected void attach() {

    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected void injectDependencies() {

    }

    private void setDatabaseUserData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.theme.setText(snapshot.child("theme").getValue(String.class));
                binding.username.setText(snapshot.child("username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}