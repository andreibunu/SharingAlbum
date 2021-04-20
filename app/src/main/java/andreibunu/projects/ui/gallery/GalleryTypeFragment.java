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

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.domain.PhonePhoto;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.utils.ImageUtils;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class GalleryTypeFragment extends BaseFragment {

    @Inject
    FaceRecognitionHandler handler;

    public static final int EXTERNAL_STORAGE_REQUEST_CODE = 101;
    List<Object> phonePhotos;
    GalleryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phonePhotos = new ArrayList<>();
        adapter = new GalleryAdapter();
        adapter.submitList(phonePhotos);
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
            try {
                checkWritePermission();
                checkPermission();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
                try {
                    loadPhotos();
                } catch (ParseException e) {
                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkPermission() throws ParseException {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            loadPhotos();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadPhotos() throws ParseException {
        ArrayList<PhonePhoto> all = getSortedPhotos();
        all.forEach(el -> testSendImage(new File(el.getAbsolutePath())));
        Calendar calendarCurrent = getCalendar(all.get(0));
        int year = calendarCurrent.get(Calendar.YEAR);
        int month = calendarCurrent.get(Calendar.MONTH);

        this.phonePhotos.add(ImageUtils.getMonthForInt(month) + ", " + year);
        int i;
        for (i = 0; i < all.size() - 2; i += 2) {
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

    private ArrayList<PhonePhoto> getSortedPhotos() throws ParseException {
        ArrayList<PhonePhoto> all = getCameraPhotos();
        all.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return all;
    }

    private void addPairElement(PhonePhoto left, PhonePhoto right) {
        PhotoPair pair = new PhotoPair(left, right);
        pair.setLeftOrientation(ImageUtils.getCameraPhotoOrientation(left.getAbsolutePath()) == 0 ? PhotoPair.Orientation.PORTRAIT : PhotoPair.Orientation.LANDSCAPE);
        if (right != null) {
            pair.setRightOrientation(ImageUtils.getCameraPhotoOrientation(right.getAbsolutePath()) == 0 ? PhotoPair.Orientation.PORTRAIT : PhotoPair.Orientation.LANDSCAPE);
        }
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

    private void testSendImage(File file) {
        handler.sendImage(file).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribed...");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "success..." + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "failed!");
                    }
                });
    }

    public boolean isSameMonthAndYear(Calendar calendar, int month, int year) {
        return calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year;
    }

}