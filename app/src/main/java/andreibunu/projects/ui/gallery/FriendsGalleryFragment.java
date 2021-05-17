package andreibunu.projects.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.databinding.FragmentFriendsGalleryBinding;
import andreibunu.projects.domain.FriendPhoto;
import andreibunu.projects.ui.filter.FilterList;
import andreibunu.projects.ui.gallery.domain.FriendPhotoPair;
import andreibunu.projects.utils.ImageUtils;

public class FriendsGalleryFragment extends Fragment {

    @Inject
    FaceRecognitionHandler handler;
    FragmentFriendsGalleryBinding binding;
    FirebaseUser firebaseUser;
    List<Object> phonePhotos;
    GalleryAdapter adapter;
    FilterList filterList;
    private DatabaseReference friendsPicsReference;

    public FriendsGalleryFragment(FilterList filtersList) {
        this.filterList = filtersList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phonePhotos = new ArrayList<>();
        adapter = new GalleryAdapter();
        adapter.submitList(phonePhotos);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        friendsPicsReference = firebaseDatabase.getReference("users").child(Objects.requireNonNull(firebaseUser).getUid()).child("photos");
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFriendsGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getInstance().getAppComponent().inject(this);
        RecyclerView recyclerView = view.findViewById(R.id.gallery_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getPhotos();
    }

    private void getPhotos() {
        this.friendsPicsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phonePhotos.clear();
                List<FriendPhoto> photos = new ArrayList<>();

                snapshot.getChildren().forEach(friend -> {
                    try {
                        photos.add(getFriendPhoto(friend));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                filterPhotos(photos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO handle fb server error
            }
        });
    }

    private FriendPhoto getFriendPhoto(DataSnapshot friend) throws ParseException {
        FriendPhoto friendPhoto = new FriendPhoto();
        friendPhoto.setUrl(friend.child("url").getValue(String.class));
        friendPhoto.setFrom(friend.child("from").getValue(String.class));
        friendPhoto.setTags(friend.child("tags").getValue(String.class));
        friendPhoto.setPeople(friend.child("people").getValue(String.class));
        //todo handle date, needs to be added to firebase in another way
        //todo problem should be from ImageUtils, date format not respected?
        friendPhoto.setDate(
                new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(
                        Objects.requireNonNull(friend.child("date").getValue(String.class))
                ));
        return friendPhoto;
    }

    private void filterPhotos(List<FriendPhoto> photos) {
        createAndDisplayPairs(getSortedPhotos(photos));
    }

    private List<FriendPhoto> getSortedPhotos(List<FriendPhoto> photos) {
        photos.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return photos;
    }

    private void createAndDisplayPairs(List<FriendPhoto> all) {
        phonePhotos.clear();
        if (all.size() == 0) {
            //todo what is it supposed to do here?
            return;
        }
        Calendar calendarCurrent = getCalendar(all.get(0));
        int year = calendarCurrent.get(Calendar.YEAR);
        int month = calendarCurrent.get(Calendar.MONTH);

        this.phonePhotos.add(ImageUtils.getMonthForInt(month) + ", " + year);
        int i;
        for (i = 0; i <= all.size() - 2; i += 2) {
            calendarCurrent = getCalendar(all.get(i));
            Calendar calendarNext = getCalendar(all.get(i + 1));

            if (isSameMonthAndYear(calendarCurrent, month, year)
                    && isSameMonthAndYear(calendarNext, month, year)) {
                addPairElement(all.get(i), all.get(i + 1));
            } else if (isSameMonthAndYear(calendarCurrent, month, year)) {
                addPairElement(all.get(i), null);
                i--;
            } else {
                year = calendarCurrent.get(Calendar.YEAR);
                month = calendarCurrent.get(Calendar.MONTH);
                this.phonePhotos.add(ImageUtils.getMonthForInt(month) + ", " + year);
                i -= 2;
            }
        }
        if (all.size() - 1 == i) {
            addPairElement(all.get(i), null);
        }
        adapter.notifyDataSetChanged();
    }

    public boolean isSameMonthAndYear(Calendar calendar, int month, int year) {
        return calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year;
    }

    private Calendar getCalendar(FriendPhoto friendPhoto) {
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTime(friendPhoto.getDate());
        return calendarCurrent;
    }


    private void addPairElement(FriendPhoto left, FriendPhoto right) {
        FriendPhotoPair pair = new FriendPhotoPair(left, right);
        this.phonePhotos.add(pair);
    }
}