package com.aki.go4lunch.UI;

import android.os.Bundle;
import android.util.Log;
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
import com.aki.go4lunch.databinding.FragmentListBinding;
import com.aki.go4lunch.events.FromAdapterToFragment;
import com.aki.go4lunch.events.FromSearchToFragment;
import com.aki.go4lunch.models.Result;
import com.aki.go4lunch.models.ResultDetailed;
import com.aki.go4lunch.models.User;
import com.aki.go4lunch.viewmodels.RestaurantViewModel;
import com.aki.go4lunch.viewmodels.UserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment {

    RestaurantViewModel restaurantViewModel;
    UserViewModel userViewModel;
    User localUser = User.getInstance();

    ArrayList<User> allUsers = new ArrayList<>();

    ListAdapter adapter;
    NavController navController;
    FragmentListBinding bindings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: FRAGMENT CREATED");
        super.onViewCreated(view, savedInstanceState);
        bindings = FragmentListBinding.bind(view);
        navController = Navigation.findNavController(view);

        adapter = new ListAdapter(this.getContext());

        bindings.restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        bindings.restaurantsRecyclerView.setAdapter(adapter);

        restaurantViewModel.getRestaurantsAround(localUser.getLocation(), requireContext()).observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                adapter.updateList(results, getAllUsers());
            }
        });
    }

    public ArrayList<User> getAllUsers() {
        userViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if(users != null) {
                allUsers.clear();
                allUsers.addAll(users);
            }
        });
        return allUsers;
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
    public void onSearchEvent(FromSearchToFragment event) {

        ResultDetailed resultDetailed = event.result.getResult();

        Result result = new Result();

        result.setPlaceId(resultDetailed.getPlaceId());
        result.setVicinity(resultDetailed.getFormattedAddress());
        result.setPhotos(resultDetailed.getPhotos());
        result.setRating(resultDetailed.getRating());
        result.setOpeningHours(resultDetailed.getOpeningHours());
        result.setName(resultDetailed.getName());
        result.setGeometry(resultDetailed.getGeometry());

        ArrayList<Result>results = new ArrayList<>();
        results.add(result);
        adapter.updateList(results, getAllUsers());
    }

    @Subscribe
    public void onGettingDetail(FromAdapterToFragment event) {
        Log.d(TAG, "onGettingDetail: Event called successfully : \nRestaurant name : " + event.result.getName());
        restaurantViewModel.getRestaurantDetail(event.result.getPlaceId(), requireContext()).observe(getViewLifecycleOwner(), resultDetails -> {
            if(resultDetails != null) {
                restaurantViewModel.setLocalCachedDetails(resultDetails.getResult());

                navController.navigate(R.id.detailFragment);
            }
        });

    }
}
