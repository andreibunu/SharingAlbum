package andreibunu.projects.ui.friends;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.databinding.FragmentFriendBinding;
import andreibunu.projects.ui.filter.adapter.FilterFriendsAdapter;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FriendFragment extends Fragment {
    private static final String TAG = FriendFragment.class.getCanonicalName();
    private FriendFilter friendFilter;
    private DatabaseReference friendDbInstance;
    private DatabaseReference friendsDbList;
    private FragmentFriendBinding binding;
    private List<FriendFilter> duplicates;
    private List<FriendFilter> friendsList;
    private FilterFriendsAdapter friendsAdapter;
    private DatabaseReference usersInstance;
    protected CompositeDisposable disposables = new CompositeDisposable();
    private boolean shouldLink = true;

    @Inject
    DatabaseHandler databaseHandler;
    private FirebaseUser firebaseUser;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.friendFilter = (FriendFilter) bundle.getSerializable("friend");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        friendDbInstance = firebaseDatabase.getReference("users").child(Objects.requireNonNull(firebaseUser).getUid()).child("friends").child(friendFilter.getId());
        friendsDbList = firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("friends");
        usersInstance = firebaseDatabase.getReference("users");
        getCurrentUserUsername();

    }

    private void getCurrentUserUsername() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initializeAdapter();
        getPeople();
        duplicates = new ArrayList<>();
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getInstance().getAppComponent().inject(this);
        setData();
        setButtons();
        setFriendsAdapter();
    }

    private void initializeAdapter() {
        friendsList = new ArrayList<>();
        this.friendsAdapter = new FilterFriendsAdapter(friendFilter -> duplicates.add(friendFilter));
    }

    private void getPeople() {
        friendsDbList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                snapshot.getChildren().forEach(friend -> {
                    if (!Objects.equals(friend.child("id").getValue(String.class), friendFilter.getId())
                            && Objects.equals(friend.child("duplicate").getValue(String.class), "0")) {
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

    private void setFriendsAdapter() {
        binding.friendsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        FriendDecorator friendDecorator = new FriendDecorator();
        binding.friendsRv.addItemDecoration(friendDecorator);
        binding.friendsRv.setAdapter(friendsAdapter);
        friendsAdapter.submitList(friendsList);
        friendsAdapter.notifyDataSetChanged();
    }


    private void setButtons() {
        binding.save.setOnClickListener(v -> updateFirebaseDatabase());
        binding.cancel.setOnClickListener(v -> Objects.requireNonNull(getFragmentManager()).popBackStack());
    }

    private void setData() {
        friendDbInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.name.setText(snapshot.child("name").getValue(String.class));
                String friendUsername = snapshot.child("username").getValue(String.class);
                shouldLink = Objects.equals(friendUsername, "not set");
                binding.username.setText(snapshot.child("username").getValue(String.class));
                Glide.with(Objects.requireNonNull(getContext()))
                        .load(snapshot.child("face").getValue(String.class))
                        .into(binding.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "error getting the info from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFirebaseDatabase() {
        friendDbInstance.child("username")
                .setValue(Objects.requireNonNull(binding.username.getText()).toString());
        friendDbInstance.child("name")
                .setValue(Objects.requireNonNull(binding.name.getText()).toString());
        mergePeopleInFirebaseDatabase();
        mergePeopleInLocalDatabase();
        if (shouldLink) {
            linkAccount();
        }
    }

    private void mergePeopleInFirebaseDatabase() {
        for (FriendFilter duplicate : this.duplicates) {
            friendsDbList.child(duplicate.getId()).child("duplicate").setValue("1");
        }
    }

    private void mergePeopleInLocalDatabase() {

        disposables.add(databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(images -> {
                    int numberOfCalls = images.size();
                    AtomicInteger count = new AtomicInteger(0);
                    images.forEach(image -> {
                        List<Integer> list = Utils.getListFromStringifiedList(image.getPerson());
                        duplicates.forEach(duplicate -> {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).toString().equals(duplicate.getId())) {
                                    list.set(i, Integer.parseInt(friendFilter.getId()));
                                }
                            }
                        });
                        image.setPerson(list.toString());
                        disposables.add(databaseHandler.updatePersonIndex(image)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(success -> {
                                            count.incrementAndGet();
                                            Log.d(TAG, "updated indexes with index" + friendFilter.getId());
                                            if (numberOfCalls == count.get()) {
                                                Objects.requireNonNull(getFragmentManager()).popBackStack();
                                            }
                                        },
                                        fail -> {
                                            count.incrementAndGet();
                                            Log.d(TAG, "error updating indexes: " + fail.getMessage());
                                            if (numberOfCalls == count.get()) {
                                                Objects.requireNonNull(getFragmentManager()).popBackStack();
                                            }
                                        }));
                    });
                }));
    }

    public void linkAccount() {
        List<DatabaseImage> imagesToSend = new ArrayList<>();
        disposables.add(databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(images -> {
                    images.forEach(image -> {
                        if (image.getPerson().contains(friendFilter.getId())) {
                            imagesToSend.add(image);
                        }
                    });
                    addCommonImages(imagesToSend);
                }));
    }

    private void addCommonImages(List<DatabaseImage> imagesToSend) {
        String friendUsername = binding.username.getText().toString();
        usersInstance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (Objects.equals(user.child("username").getValue(String.class), friendUsername)) {
                        AtomicLong index = new AtomicLong(user.child("photos").getChildrenCount());
                        imagesToSend.forEach(img -> {
                            addCommonItemInfo(user, index.getAndIncrement(), img);
                            addImageCommon(img, friendUsername, user, index.get());
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addImageCommon(DatabaseImage img, String friendUsername, DataSnapshot user, long index) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(img.getImageName()));
        StorageReference riversRef = storageRef.child(firebaseUser.getUid()).child("shared").child(friendUsername);

        riversRef.listAll().addOnSuccessListener(listResult -> {
            //todo imagename is absolute path rn, a/b/c/d/e... will create inner directories in firebase storage
            //working tho
            UploadTask uploadTask = riversRef.child(img.getImageName()).putFile(file);
            uploadTask.continueWithTask(task -> riversRef.child(img.getImageName()).getDownloadUrl()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    user.child("photos").child((index - 1) + "").child("url").getRef().setValue(Objects.requireNonNull(downloadUri).toString());
                }
            });

        });
    }

    private void addCommonItemInfo(DataSnapshot user, long index, DatabaseImage date) {
        user.child("photos").child(index + "").child("from").getRef().setValue(username);
        user.child("photos").child(index + "").child("tags").getRef().setValue("");
        user.child("photos").child(index + "").child("date").getRef().setValue(date.getDate());
        user.child("photos").child(index + "").child("people").getRef().setValue("");

    }
}