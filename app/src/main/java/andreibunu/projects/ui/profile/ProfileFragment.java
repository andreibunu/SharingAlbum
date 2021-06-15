package andreibunu.projects.ui.profile;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentProfileBinding;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.adapter.FilterFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.ui.friends.FriendFragment;
import andreibunu.projects.ui.login.LoginFragment;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends BaseFragment {


    FirebaseAuth firebaseAuth;
    private FragmentProfileBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private List<FriendFilter> friendsList;
    private FilterFriendsAdapter friendsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_image);
        setSharedElementEnterTransition(transition);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

        initializeAdapter();
        getPeople();
    }

    private void initializeAdapter() {
        friendsList = new ArrayList<>();
        this.friendsAdapter = new FilterFriendsAdapter(friendFilter -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            FriendFragment fragment = new FriendFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("friend", friendFilter);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
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
        setFriendsAdapter();
        setLogoutListener();

    }

    private void setLogoutListener() {
        binding.logout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USERNAME", "-1");
            editor.putString("PASSWORD", "-1");
            editor.apply();
            FragmentManager fm = Objects.requireNonNull(getFragmentManager());
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            LoginFragment fragment = new LoginFragment();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        });

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


    private void getPeople() {
        myRef.child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                snapshot.getChildren().forEach(friend -> {
                    if (Objects.equals(friend.child("duplicate").getValue(String.class), "0")) {
                        FriendFilter friendFilter = new FriendFilter(friend.child("name").getValue(String.class),
                                friend.child("face").getValue(String.class), friend.child("id").getValue(String.class));
                        friendsList.add(friendFilter);
                    }
                });
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO handle fb server error
            }
        });
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
                binding.username.setText(snapshot.child("username").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFriendsAdapter() {
        binding.friendsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.friendsRv.setAdapter(friendsAdapter);
        friendsAdapter.submitList(friendsList);
        friendsAdapter.notifyDataSetChanged();
    }

}