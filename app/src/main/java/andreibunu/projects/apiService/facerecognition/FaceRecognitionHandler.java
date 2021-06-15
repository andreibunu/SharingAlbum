package andreibunu.projects.apiService.facerecognition;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.utils.ImageUtils;
import andreibunu.projects.utils.Utils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FaceRecognitionHandler {

    private static final String TAG = FaceRecognitionHandler.class.getCanonicalName();
    private final DatabaseHandler databaseHandler;
    private final FirebaseUser firebaseUser;
    private FaceRecognitionProxy proxy;
    private String username;
    private CompositeDisposable disposables = new CompositeDisposable();
    private final Semaphore updatePermit = new Semaphore(1);


    @Inject
    public FaceRecognitionHandler(FaceRecognitionProxy proxy, DatabaseHandler databaseHandler) {
        this.proxy = proxy;
        this.databaseHandler = databaseHandler;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        getCurrentUserUsername();
    }

    public Single<String> sendImage(File file, String uid) {
        return Single.create(emitter -> {
            try {
                List<Integer> faces = proxy.testImg2(file, uid);
                String filename = file.getAbsolutePath();
                Date date = ImageUtils.getDateFromName(file.getName());
                DatabaseImage x = databaseHandler.insertImage(filename, date.toString(), faces.toString(),
                        "", "").blockingGet();
                new Thread(() -> {
                    try {
                        shareImage(x);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                emitter.onSuccess(faces.toString());
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }

    public void shareImage(DatabaseImage databaseImage) throws InterruptedException {
        updatePermit.acquire();
        Log.d("ADDIMAGECOMMON", "acquired");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        List<Integer> people = Utils.getListFromStringifiedList(databaseImage.getPerson());
        for (Integer person : people) {
            disposables.add(databaseHandler.getUsername(person.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(success -> {
                                DatabaseReference usersInstance = firebaseDatabase.getReference("users").child(success.getFriendId());
                                usersInstance.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        AtomicLong index = new AtomicLong(snapshot.child("photos").getChildrenCount());
                                        addImageCommon(databaseImage, success.getUsername(), snapshot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            },
                            error -> {

                            }));

        }
    }


    private void addImageCommon(DatabaseImage img, String friendUsername, DataSnapshot user) {

        long index = user.child("photos").getChildrenCount() + 1;
        Log.d("ADDIMAGECOMMON", index + " index for image " + img.getImageName());
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
                    user.child("photos").child((index) + "").child("url").getRef().setValue(Objects.requireNonNull(downloadUri).toString());
                    addCommonItemInfo(user, index, img);
                    Log.d("ADDIMAGECOMMON", "released");
                    updatePermit.release();
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
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
