package andreibunu.projects.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentSettingsBinding;
import andreibunu.projects.ui.base.BaseFragment;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends BaseFragment {

    private static final int RESULT_LOAD_IMG = 123;
    private FragmentSettingsBinding binding;

    private FirebaseUser firebaseUser;

    private Uri uri;
    private DatabaseReference myRef;
    private DatabaseReference users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");
        myRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUserData();
        setDatabaseUserData();
        setImageListener();
        setSaveListener();
    }

    private void setDatabaseUserData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.themeEt.setText(snapshot.child("theme").getValue(String.class));
                binding.usernameEt.setText(snapshot.child("username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserData() {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(firebaseUser.getPhotoUrl())
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.image);
        Glide.with(Objects.requireNonNull(getContext()))
                .load(firebaseUser.getPhotoUrl())
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.user);
        binding.nameEt.setText(firebaseUser.getDisplayName());
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

    private void setSaveListener() {
        binding.save.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            checkUsername();
        });
    }

    private void startSavingProcess() {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        if (uri != null) {
            builder.setPhotoUri(uri);
        }
        if (!Objects.requireNonNull(binding.nameEt.getText()).toString().isEmpty()) {
            builder.setDisplayName(binding.nameEt.getText().toString());
        }
        UserProfileChangeRequest request = builder.build();
        updateUser(request);
    }

    private void checkUsername() {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AtomicBoolean canBeAdded = new AtomicBoolean(true);
                snapshot.getChildren().forEach(user -> {
                    if (user.child("username").getValue(String.class) != null &&
                            binding.usernameEt.getText() != null &&
                            user.child("username").getValue(String.class).equals(binding.usernameEt.getText().toString())) {
                        canBeAdded.set(false);
                    }
                });
                if (canBeAdded.get()) {
                    startSavingProcess();
                } else {
                    Toast.makeText(getContext(), "Username already exists.", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setImageListener() {
        binding.image.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            InputStream imageStream;
            try {
                imageStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                binding.image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUser(UserProfileChangeRequest request) {
        firebaseUser.updateProfile(request)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateFirebaseDatabase();
                        binding.progressBar.setVisibility(View.GONE);
                        Objects.requireNonNull(getFragmentManager()).popBackStack();
                    }
                });

    }

    private void updateFirebaseDatabase() {
        myRef.child("username")
                .setValue(Objects.requireNonNull(binding.usernameEt.getText()).toString());
        myRef.child("theme")
                .setValue(Objects.requireNonNull(binding.themeEt.getText()).toString());
    }

}