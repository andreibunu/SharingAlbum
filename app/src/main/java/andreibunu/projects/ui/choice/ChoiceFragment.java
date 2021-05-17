package andreibunu.projects.ui.choice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentChoiceBinding;
import andreibunu.projects.ui.filter.FilterFragment;

import static andreibunu.projects.MainActivity.TAG;


public class ChoiceFragment extends Fragment {

    private FragmentChoiceBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mine.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            FilterFragment fragment = new FilterFragment();
            Bundle bundle = new Bundle();
            bundle.putString("from", "me");
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });

        binding.friends.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            FilterFragment fragment = new FilterFragment();
            Bundle bundle = new Bundle();
            bundle.putString("from", "friends");
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
    }


}