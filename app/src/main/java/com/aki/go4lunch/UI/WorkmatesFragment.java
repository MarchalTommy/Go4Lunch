package com.aki.go4lunch.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunch.R;
import com.aki.go4lunch.databinding.FragmentWorkmatesBinding;
import com.aki.go4lunch.events.FromWorkmatesListToFragment;
import com.aki.go4lunch.models.User;
import com.aki.go4lunch.viewmodels.RestaurantViewModel;
import com.aki.go4lunch.viewmodels.UserViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;
    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    User localUser = User.getInstance();
    NavController navController;
    private WorkmatesAdapter adapter;
    private User workmateClicked = new User();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentWorkmatesBinding.bind(view);
        adapter = new WorkmatesAdapter(this.getContext());
        navController = Navigation.findNavController(view);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        binding.workmatesRecyclerView.setHasFixedSize(true);
        binding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.workmatesRecyclerView.setAdapter(adapter);

        //Observing LiveData to populate RV
        userViewModel.getAllUsers().observe(requireActivity(), users -> adapter.updateList(users));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void fromList(FromWorkmatesListToFragment event) {
        workmateClicked = event.user;

        if (workmateClicked.getPlaceBooked() == null | workmateClicked.getPlaceBooked().isEmpty()) {
            Snackbar.make(requireView(), R.string.user_no_lunch, BaseTransientBottomBar.LENGTH_LONG).show();
        } else {
            restaurantViewModel.getRestaurantFromName(workmateClicked.getPlaceBooked(), localUser.getLocation(), requireContext())
                    .observe(getViewLifecycleOwner(), result -> {
                        if (result != null) {
                            restaurantViewModel.getRestaurantDetail(result.getPlaceId(), getContext())
                                    .observe(getViewLifecycleOwner(), resultDetails -> {
                                        if (resultDetails != null) {
                                            restaurantViewModel.setLocalCachedDetails(resultDetails.getResult());
                                            navController.navigate(R.id.detailFragment);
                                        }
                                    });
                        }
                    });
        }

    }
}
