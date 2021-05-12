package andreibunu.projects.ui.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.domain.PhonePhoto;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.FilterList;
import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;
import andreibunu.projects.utils.ImageUtils;
import andreibunu.projects.utils.Utils;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class GalleryTypeFragment extends BaseFragment {

    @Inject
    FaceRecognitionHandler handler;

    @Inject
    DatabaseHandler databaseHandler;

    FirebaseUser firebaseAuth;

    public static final int EXTERNAL_STORAGE_REQUEST_CODE = 101;
    public static final int EXTERNAL_STORAGE_REQUEST_CODE_WRITE = 102;

    List<Object> phonePhotos;
    GalleryAdapter adapter;
    FilterList filterList;
    protected CompositeDisposable disposables = new CompositeDisposable();

    public GalleryTypeFragment(FilterList filtersList) {
        this.filterList = filtersList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phonePhotos = new ArrayList<>();
        adapter = new GalleryAdapter();
        adapter.submitList(phonePhotos);
        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_galley_type, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getInstance().getAppComponent().inject(this);
        RecyclerView recyclerView = view.findViewById(R.id.gallery_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        if (phonePhotos.size() == 0) {
            checkWritePermission();
            checkPermission();
        }
    }

    @Override
    protected void attach() {
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_galley_type;
    }

    @Override
    protected void injectDependencies() {
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginProcess();
            }
        }

        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginProcess();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            beginProcess();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE_WRITE);
        }
    }


    /**
     * DESIGN PATTERNS, Behavioral : Observable
     * Observable objects will emit values.
     * The observer (subscriber) listens to data and acts when received
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void beginProcess() {
        disposables.add(this.databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::loadPhotos));
    }

    /**
     * @param databaseImages list of images from local database
     * @throws ParseException Logic:
     *                        1. Get all images from phone
     *                        2. Send all the photos that are not in the database to the server to be processed
     *                        3. After the server calls are back, filter all photos from database through the filter object
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadPhotos(List<DatabaseImage> databaseImages) throws ParseException {
        ArrayList<PhonePhoto> all = getCameraPhotos();
        List<PhonePhoto> newFiles = findNewPhotosToServer(all, databaseImages);
        if (newFiles.size() == 0) {
            getFilteredPhotos(databaseImages);
        } else {
            sendImages(newFiles);
        }
    }

    private List<PhonePhoto> findNewPhotosToServer(ArrayList<PhonePhoto> all, List<DatabaseImage> databaseImages) {
        List<PhonePhoto> newPhotos = new ArrayList<>();
        for (PhonePhoto el : all) {
            File f = new File(el.getAbsolutePath());
            boolean alreadyExists = false;
            for (DatabaseImage dbImage : databaseImages) {
                if (dbImage.getImageName().equals(f.getAbsolutePath())) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                newPhotos.add(el);
            }
        }
        return newPhotos;
    }

    private void sendImages(List<PhonePhoto> photos) {
        int numberOfCalls = photos.size();
        AtomicInteger count = new AtomicInteger(0);
        photos.forEach(phonePhoto -> handler.sendImage(new File(phonePhoto.getAbsolutePath()), firebaseAuth.getUid()).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribed...");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "success..." + s);
                        count.incrementAndGet();
                        if (numberOfCalls == count.get()) {
                            filterAndDisplayList();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        count.incrementAndGet();
                        if (numberOfCalls == count.get()) {
                            filterAndDisplayList();
                        }
                    }
                }));
    }

    private void filterAndDisplayList() {
        disposables.add(this.databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::getFilteredPhotos));
    }

    private void getFilteredPhotos(List<DatabaseImage> databaseImages) {
        ArrayList<DatabaseImage> ret = new ArrayList<>();
        for (DatabaseImage photo : databaseImages) {
            List<Integer> people = Utils.getListFromStringifiedList(photo.getPerson());
            //check if people contain all the ids in filter list
            if (checkIfPeopleAppear(people)) {
                ret.add(photo);
            }
        }
        updateList(ret);

    }

    private boolean checkIfPeopleAppear(List<Integer> people) {
        for (FriendFilter el : filterList.getFriends()) {
            if (!people.contains(Integer.parseInt(el.getId()))) {
                return false;
            }
        }
        return true;
    }

    private void updateList(List<DatabaseImage> ret) {
        List<PhonePhoto> all = new ArrayList<>();
        ret.forEach(dbImage -> {
            File f = new File(dbImage.getImageName());
            try {
                PhonePhoto phonePhoto = new PhonePhoto(dbImage.getImageName(), ImageUtils.getDateFromName(f.getName()));
                all.add(phonePhoto);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        all.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        createPairs(all);
    }

    private void createPairs(List<PhonePhoto> all) {
        phonePhotos.clear();
        if (all.size() == 0) {
            //todo what is it supposed to do here? la iubitu ii place tastatura luata de min ehihiihihihih
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

    private Calendar getCalendar(PhonePhoto phonePhoto) {
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTime(phonePhoto.getDate());
        return calendarCurrent;
    }

    private void addPairElement(PhonePhoto left, PhonePhoto right) {
        PhotoPair pair = new PhotoPair(left, right);
        this.phonePhotos.add(pair);
    }

    private ArrayList<PhonePhoto> getCameraPhotos() throws ParseException {
        ArrayList<PhonePhoto> images = new ArrayList<>();
        File[] files = ImageUtils.getCameraPhotos();
        for (File file : files) {
            String path = file.getAbsolutePath();
            if (path.contains("jpg")) {
                Date date = ImageUtils.getDateFromName(file.getName());
                images.add(new PhonePhoto(path, date));
            }
        }
        return images;
    }

    public boolean isSameMonthAndYear(Calendar calendar, int month, int year) {
        return calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year;
    }

}