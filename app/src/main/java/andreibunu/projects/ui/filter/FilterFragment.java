package andreibunu.projects.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentFilterBinding;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.adapter.ChosenFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.FilterFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.ui.gallery.GalleryFragment;
import andreibunu.projects.ui.profile.ProfileFragment;


public class FilterFragment extends BaseFragment {

    private FragmentFilterBinding binding;

    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    private List<FriendFilter> friendToChooseList;
    private List<FriendFilter> chosenFriendsList;
    private DatabaseReference myRef;
    private FilterFriendsAdapter filterFriendsAdapter;
    private ChosenFriendsAdapter filterChosenFriendsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("friends");

        initializeAdapters();
//        Transition sharedElementsTransition = TransitionInflater.from(getActivity())
//                .inflateTransition(R.transition.shared_image);
//        Transition sharedElementReturnTransition = TransitionInflater.from(getActivity())
//                .inflateTransition(R.transition.shared_image);
    }

    private void initializeAdapters() {
        //friends to choose
        friendToChooseList = new ArrayList<>();
        this.filterFriendsAdapter = new FilterFriendsAdapter(friendFilter -> {
            if (!chosenFriendsList.contains(friendFilter)) {
                chosenFriendsList.add(friendFilter);
                filterChosenFriendsAdapter.notifyDataSetChanged();
            }
        });
        filterFriendsAdapter.submitList(friendToChooseList);

        //chosen friends
        chosenFriendsList = new ArrayList<>();
        this.filterChosenFriendsAdapter = new ChosenFriendsAdapter(friend -> {
            int index = chosenFriendsList.indexOf(friend);
            chosenFriendsList.remove(index);
            filterChosenFriendsAdapter.notifyItemRemoved(index);
        });
        filterChosenFriendsAdapter.submitList(chosenFriendsList);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDropDownListeners();
        setButtonListener();
        setProfileListener();
        setProfilePicture();
        setFriendChooserRecycleView();
        setChosenFriends();
        ViewCompat.setTransitionName(binding.filterUser, "transition");

    }

    private void setChosenFriends() {
        binding.filterWhoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.filterWhoRv.setAdapter(filterChosenFriendsAdapter);
    }

    private void setFriendChooserRecycleView() {
        binding.chooseWhoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.chooseWhoRv.setAdapter(filterFriendsAdapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(friend -> {
                    FriendFilter friendFilter = new FriendFilter(friend.child("name").getValue(String.class),
                            friend.child("face").getValue(String.class), friend.child("id").getValue(String.class));
                    friendToChooseList.add(friendFilter);
                });
                filterFriendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO handle fb server error
            }
        });
    }

    private void setProfilePicture() {
        FirebaseAuth user = FirebaseAuth.getInstance();
        Glide.with(Objects.requireNonNull(getContext()))
                .load(Objects.requireNonNull(user.getCurrentUser()).getPhotoUrl())
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.filterUser);
    }

    private void setProfileListener() {
        binding.filterUser.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(binding.filterUser, "transition");
            ProfileFragment fragment = new ProfileFragment();
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
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

    private void setButtonListener() {
        binding.btn.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            GalleryFragment fragment = new GalleryFragment();
            Bundle bundle = new Bundle();
            FilterList filterList = getFilterList();
            bundle.putSerializable("filters", filterList);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
    }

    private FilterList getFilterList() {
        FilterList filterList = new FilterList();
        filterList.setFriends(this.chosenFriendsList);
        return filterList;
    }

    private void setDropDownListeners() {
        binding.filterWhatLayout.setOnClickListener(v -> hideView(binding.filterHiddenWhatLayout));
        binding.filterWhenLayout.setOnClickListener(v -> hideView(binding.filterWhenHiddenLayout));
        binding.filterWhoLayout.setOnClickListener(v -> hideView(binding.filterWhoHiddenLayout));
        binding.filterWhereLayout.setOnClickListener(v -> hideView(binding.filterHiddenWhereLayout));
    }

    private void hideView(View view) {
        if (view.getVisibility() == View.GONE) {
            hideAllLayouts();
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void hideAllLayouts() {
        binding.filterHiddenWhatLayout.setVisibility(View.GONE);
        binding.filterHiddenWhereLayout.setVisibility(View.GONE);
        binding.filterWhenHiddenLayout.setVisibility(View.GONE);
        binding.filterWhoHiddenLayout.setVisibility(View.GONE);
    }

}