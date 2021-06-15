package andreibunu.projects.ui.image;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.databinding.FragmentImageBinding;
import andreibunu.projects.domain.PhonePhoto;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.ui.gallery.OnSwipeTouchListener;
import andreibunu.projects.utils.ImageUtils;
import andreibunu.projects.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class ImageFragment extends Fragment {

    public static final String TAG = ImageFragment.class.getCanonicalName();

    @Inject
    DatabaseHandler databaseHandler;

    private FragmentImageBinding binding;
    private String databaseImageName;
    private CompositeDisposable disposables = new CompositeDisposable();

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    private DatabaseReference myRef;

    PeopleToDeleteAdapter adapter;
    List<FriendFilter> friendFilterList;
    List<PhonePhoto> allPhotos;
    int index;
    private RecyclerView peopleRv;
    private DatabaseImage dbImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getAppComponent().inject(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("friends");


        Bundle bundle = getArguments();
        if (bundle != null) {
            this.allPhotos = (List<PhonePhoto>) bundle.getSerializable("list");
            this.index = Integer.parseInt(bundle.getString("index"));
            this.databaseImageName = allPhotos.get(index).getAbsolutePath();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecycleView();
        getImage();
        setHashTagListener();
        setSwipeListener();
        setDetailsListener();
        binding.detailsHandler.bringToFront();
        binding.arrow.bringToFront();
    }

    private void setDetailsListener() {
        View.OnClickListener handler = v -> {
            if (binding.details.getVisibility() == View.VISIBLE) {
                binding.details.setVisibility(View.GONE);
                binding.arrow.setImageResource(R.drawable.arrow_up);
            } else {
                binding.details.setVisibility(View.VISIBLE);
                binding.arrow.setImageResource(R.drawable.arrow_down);
            }
        };
        binding.arrow.setOnClickListener(handler);
        binding.detailsHandler.setOnClickListener(handler);

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
                    ImageFragment fragment = new ImageFragment();
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
                    Objects.requireNonNull(getFragmentManager()).popBackStack();
                } else {
                    FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                            .setCustomAnimations(R.anim.slide_back_in, R.anim.slide_back_out);
                    ImageFragment fragment = new ImageFragment();
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

    private void setHashTagListener() {
        binding.addTag.setOnClickListener(v -> {
            String newHashTag = binding.newtag.getText().toString();
            if (!newHashTag.equals("")) {
                if (newHashTag.contains(" ")) {
                    Toast.makeText(getContext(), "HashTags shouldn't contain spaces", Toast.LENGTH_LONG).show();
                } else {
                    if (dbImage.getHashtags().contains(newHashTag)) {
                        Toast.makeText(getContext(), "HashTags already exists for this photo", Toast.LENGTH_LONG).show();
                    }
                    dbImage.setHashtags(dbImage.getHashtags() + " " + newHashTag);
                    disposables.add(databaseHandler.updatePersonIndex(dbImage).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(success -> {
                                binding.newtag.setText("");
                                binding.tags.setText(dbImage.getHashtags());
                            }));
                }
            }
        });
    }

    private void setRecycleView() {
        this.peopleRv = binding.friendsRv;
        friendFilterList = new ArrayList<>();
        adapter = new PeopleToDeleteAdapter(friend -> {
        });
        adapter.submitList(friendFilterList);
        peopleRv.setAdapter(adapter);
        binding.friendsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void getImage() {
        disposables.add(databaseHandler.getImage(databaseImageName).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(success -> {
                    this.dbImage = success;
                    updateUI(success);
                }));
    }

    private void updateUI(DatabaseImage dbImage) throws ParseException {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(dbImage.getImageName())
                .placeholder(R.drawable.placeholder_square)
                .into(binding.image);
        binding.tags.setText(dbImage.getHashtags().equals("") ? "No tags" : dbImage.getHashtags());
        File f = new File(dbImage.getImageName());
        Date date = ImageUtils.getDateFromName(f.getName());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String beautify = Utils.getMonth(c.get(Calendar.MONTH) + 1) + " " + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
        binding.date.setText(beautify);


        getPeople(dbImage);
    }


    private void getPeople(DatabaseImage dbImage) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendFilterList.clear();
                snapshot.getChildren().forEach(friend -> {
                    if (Utils.getListFromStringifiedList(dbImage.getPerson())
                            .contains(Integer.parseInt(friend.child("id").getValue(String.class)))) {
                        FriendFilter friendFilter = new FriendFilter(friend.child("name").getValue(String.class),
                                friend.child("face").getValue(String.class), friend.child("id").getValue(String.class));
                        friendFilterList.add(friendFilter);
                    }
                });
                adapter.notifyDataSetChanged();
                if (friendFilterList.isEmpty()) {
                    binding.textView3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO handle fb server error
            }
        });
    }
}