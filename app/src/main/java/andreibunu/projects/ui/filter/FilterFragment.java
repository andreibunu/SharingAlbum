package andreibunu.projects.ui.filter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.databinding.FragmentFilterBinding;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.adapter.ChosenFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.FilterFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.TagsAdapter;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.ui.gallery.FriendsGalleryFragment;
import andreibunu.projects.ui.gallery.GalleryTypeFragment;
import andreibunu.projects.ui.profile.ProfileFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class FilterFragment extends BaseFragment {

    @Inject
    DatabaseHandler databaseHandler;

    private FragmentFilterBinding binding;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    private DatabaseReference myRef;


    //handles where this fragment was created
    private String from;

    //people filter
    private List<FriendFilter> friendToChooseList;
    private List<FriendFilter> allFriendsCopy;
    private List<FriendFilter> chosenFriendsList;
    private FilterFriendsAdapter filterFriendsAdapter;
    private ChosenFriendsAdapter filterChosenFriendsAdapter;

    //tags
    private TagsAdapter tagsAdapter;
    private HashSet<String> tagsToChoose;
    private List<String> chosenTags;

    //date
    private DatePickerDialog fromDate;
    private DatePickerDialog toDate;
    private Date dateFilterFrom, dateFilterTo;
    private String beautifiedFrom, beautifiedTo;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        App.getInstance().getAppComponent().inject(this);

        if (bundle != null) {
            this.from = bundle.getString("from");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        tagsToChoose = new HashSet<>();
        chosenTags = new ArrayList<>();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users")
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("friends");
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
        if (from.equals("friends")) {
            binding.filterWhoTv.setText(R.string.from);
        }

        initializeAdapters();
        getPeople();
        getTags();
        setDropDownListeners();
        setButtonListener();
        setProfileListener();
        setProfilePicture();
        setFriendChooserRecycleView();
        setTagsRecycleView();
        setChosenFriends();
        setCalendarListeners();
        resetDate();
        setTextWatcher();

        ViewCompat.setTransitionName(binding.filterUser, "transition");

    }

    private void setTextWatcher() {
        binding.chooseWhoFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = binding.chooseWhoFind.getText().toString();
                updateFriendToChoose(newText);
            }
        });
    }

    private void updateFriendToChoose(String newText) {
        friendToChooseList.clear();
        if (!newText.isEmpty()) {
            allFriendsCopy.forEach(el -> {
                if (el.getName().contains(newText)) {
                    friendToChooseList.add(el);
                }
            });
        } else {
            friendToChooseList.addAll(allFriendsCopy);
        }
        filterFriendsAdapter.notifyDataSetChanged();
    }

    private void resetDate() {
        beautifiedFrom = null;
        beautifiedTo = null;
        dateFilterTo = null;
        dateFilterFrom = null;
    }

    private void getTags() {
        disposable.add(databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(success -> {
                    success.forEach(image -> {
                        List<String> tags = Arrays.asList(image.getHashtags().split(" "));
                        tagsToChoose.addAll(tags);
                    });
                    tagsToChoose.remove("");
                    setAutoCompleteTextListener();

                    Log.d("allmytags", tagsToChoose.toString());
                }));
    }

    public void setAutoCompleteTextListener() {
        binding.tagsAuto.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(tagsToChoose)));
        binding.tagsAuto.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            chosenTags.add(item);
            tagsAdapter.notifyDataSetChanged();
            binding.tagsAuto.setText("");
        });
    }


    private void initializeAdapters() {
        //friends to choose
        friendToChooseList = new ArrayList<>();
        this.filterFriendsAdapter = new FilterFriendsAdapter(friendFilter -> {
            if (!chosenFriendsList.contains(friendFilter)) {
                chosenFriendsList.add(friendFilter);
                filterChosenFriendsAdapter.notifyDataSetChanged();
            }
            else{
                int index = chosenFriendsList.indexOf(friendFilter);
                chosenFriendsList.remove(index);
                filterChosenFriendsAdapter.notifyItemRemoved(index);            }
        });

        //chosen friends
        chosenFriendsList = new ArrayList<>();
        this.filterChosenFriendsAdapter = new ChosenFriendsAdapter(friend -> {
            int index = chosenFriendsList.indexOf(friend);
            chosenFriendsList.remove(index);
            filterChosenFriendsAdapter.notifyItemRemoved(index);
        });

        chosenTags = new ArrayList<>();
        this.tagsAdapter = new TagsAdapter(tag -> {
            int index = chosenTags.indexOf(tag);
            chosenTags.remove(index);
            tagsAdapter.notifyItemRemoved(index);
        });
    }


    private void setCalendarListeners() {
        ImageView calendarFrom = binding.whenCalendarFrom;
        ImageView calendarTo = binding.whenCalendarTo;
        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String niceMonth = getMonth(month);
            beautifiedFrom = niceMonth + " " + dayOfMonth + " " + year;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DATE, dayOfMonth);
            dateFilterFrom = calendar.getTime();
            updateDate();
        };

        DatePickerDialog.OnDateSetListener dateListener2 = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String niceMonth = getMonth(month);
            beautifiedTo = niceMonth + " " + dayOfMonth + " " + year;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DATE, dayOfMonth);
            dateFilterTo = calendar.getTime();
            updateDate();
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        fromDate = new DatePickerDialog(getContext(), dateListener, year, month, day);
        toDate = new DatePickerDialog(getContext(), dateListener2, year, month, day);
        calendarFrom.setOnClickListener(v -> fromDate.show());
        calendarTo.setOnClickListener(v -> toDate.show());


        binding.filterWhenChoiceFrom.setOnClickListener(v -> {
            beautifiedFrom = null;
            beautifiedTo = null;
            dateFilterTo = null;
            dateFilterFrom = null;
            binding.filterWhenChoiceFrom.setText("");
        });
    }

    private void updateDate() {
        if (beautifiedFrom != null && beautifiedTo != null) {
            String output = beautifiedFrom + " -> " + beautifiedTo;
            binding.filterWhenChoiceFrom.setText(output);
        } else {
            if (beautifiedTo != null) {
                String output = "until " + beautifiedTo;
                binding.filterWhenChoiceFrom.setText(output);
            }
            if (beautifiedFrom != null) {
                String output = "since " + beautifiedFrom;
                binding.filterWhenChoiceFrom.setText(output);
            }
        }
    }

    private String getMonth(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private void setChosenFriends() {
        binding.filterWhoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.filterWhoRv.setAdapter(filterChosenFriendsAdapter);
        filterChosenFriendsAdapter.submitList(chosenFriendsList);
    }

    private void setFriendChooserRecycleView() {
        binding.chooseWhoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.chooseWhoRv.setAdapter(filterFriendsAdapter);
        filterFriendsAdapter.submitList(friendToChooseList);
        filterFriendsAdapter.notifyDataSetChanged();
    }

    private void setTagsRecycleView() {
        binding.choosenTagsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.choosenTagsRv.setAdapter(tagsAdapter);
        tagsAdapter.submitList(chosenTags);
        tagsAdapter.notifyDataSetChanged();
    }

    private void getPeople() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendToChooseList.clear();
                snapshot.getChildren().forEach(friend -> {
                    if (friend.hasChild("duplicate") && friend.child("duplicate").getValue(String.class).equals("0")) {
                        if (from.equals("me")) {
                            FriendFilter friendFilter = new FriendFilter(friend.child("name").getValue(String.class),
                                    friend.child("face").getValue(String.class), friend.child("id").getValue(String.class));
                            friendToChooseList.add(friendFilter);
                        } else {
                            if (friend.hasChild("photos")) {
                                FriendFilter friendFilter = new FriendFilter(friend.child("name").getValue(String.class),
                                        friend.child("face").getValue(String.class), friend.child("id").getValue(String.class));
                                friendToChooseList.add(friendFilter);
                            }
                        }
                    }
                });
                allFriendsCopy = new ArrayList<>();
                allFriendsCopy.addAll(friendToChooseList);
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
            FilterList filterList = getFilterList();
            Bundle bundle = new Bundle();
            bundle.putSerializable("filters", filterList);

            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            if (from.equals("me")) {
                GalleryTypeFragment fragment = new GalleryTypeFragment(filterList);
                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            } else {
                FriendsGalleryFragment fragment = new FriendsGalleryFragment(filterList);
                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            }
            ft.commit();
        });
    }

    private FilterList getFilterList() {
        FilterList filterList = new FilterList();
        filterList.setFriends(this.chosenFriendsList);
        List<Date> when = new ArrayList<>();
        when.add(dateFilterFrom);
        when.add(dateFilterTo);
        filterList.setDate(when);
        filterList.setTags(chosenTags);
        return filterList;
    }

    private void setDropDownListeners() {
        binding.filterWhatLayout.setOnClickListener(v -> hideView(binding.filterHiddenWhatLayout));
        binding.filterWhenLayout.setOnClickListener(v -> hideView(binding.filterWhenHiddenLayout));
        binding.filterWhoLayout.setOnClickListener(v -> hideView(binding.filterWhoHiddenLayout));
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
        binding.filterWhenHiddenLayout.setVisibility(View.GONE);
        binding.filterWhoHiddenLayout.setVisibility(View.GONE);
    }

}