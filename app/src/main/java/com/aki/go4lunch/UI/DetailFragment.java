package com.aki.go4lunch.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunch.R;
import com.aki.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.aki.go4lunch.events.LunchSelectedEvent;
import com.aki.go4lunch.models.ResultDetailed;
import com.aki.go4lunch.models.User;
import com.aki.go4lunch.viewmodels.RestaurantViewModel;
import com.aki.go4lunch.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class DetailFragment extends Fragment {

    Context context;
    RestaurantViewModel restaurantViewModel;
    UserViewModel userViewModel;
    DetailAdapter adapter;
    ResultDetailed restaurantDetail;
    ArrayList<User> userList = new ArrayList<>();
    FragmentRestaurantDetailBinding bindings;

    User localUser = User.getInstance();

    // Listener
    // Floating action button
    private final OnClickListener fabListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!localUser.getHasBooked()) {
                //Updating data for the com.aki.go4lunch.UI
                EventBus.getDefault().post(new LunchSelectedEvent(userList, restaurantDetail.getName(), restaurantDetail.getFormattedAddress()));
                userViewModel.updateHasBooked(true);
                localUser.setHasBooked(true);
                userViewModel.updatePlaceBooked(restaurantDetail.getName());
                localUser.setPlaceBooked(restaurantDetail.getName());
                updateAdapter();
                bindings.detailFab.setColorFilter(getResources().getColor(R.color.secondaryColor));
            } else if (localUser.getPlaceBooked().equals(restaurantDetail.getName())) {
                DetailFragment.this.FABAlertDialog(1);
            } else {
                DetailFragment.this.FABAlertDialog(2);
            }
        }
    };
    // Button bar (call, like, website)
    @SuppressLint("NonConstantResourceId")
    private final OnClickListener callLikeWebsiteListener = view -> {
        switch (view.getId()) {
            case R.id.call_btn:
                if(restaurantDetail.getInternationalPhoneNumber() != null) {
                    callPermission();
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_phone), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.like_btn:
                if(localUser.getPlaceLiked().contains(restaurantDetail.getPlaceId())){
                    // Updating local user info
                    localUser.removePlaceLiked(restaurantDetail.getPlaceId());
                    // Updating the com.aki.go4lunch.UI
                    bindings.likeBtn.setText(R.string.not_liked);
                    bindings.likeBtn.setTextColor(getResources().getColor(R.color.primaryColor));
                    Drawable[] drawables = bindings.likeBtn.getCompoundDrawables();
                    if (drawables[1] != null) {  // top drawable is 1
                        drawables[1].setColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.MULTIPLY);
                    }
                } else {
                    // Updating local user info
                    localUser.addPlaceLiked(restaurantDetail.getPlaceId());
                    // Updating the com.aki.go4lunch.UI
                    bindings.likeBtn.setText(R.string.liked);
                    bindings.likeBtn.setTextColor(getResources().getColor(R.color.secondaryDarkColor));
                    Drawable[] drawables = bindings.likeBtn.getCompoundDrawables();
                    if (drawables[1] != null) {  // top drawable is 1
                        drawables[1].setColorFilter(getResources().getColor(R.color.secondaryDarkColor), PorterDuff.Mode.MULTIPLY);
                    }
                }
                break;
            case R.id.website_btn:
                if(restaurantDetail.getWebsite() != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantDetail.getWebsite())));
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_website), Toast.LENGTH_LONG).show();
                }
                break;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantDetail = new ResultDetailed();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindings = FragmentRestaurantDetailBinding.bind(view);

        //Getting data and updating the com.aki.go4lunch.UI
        getRestaurantDetail();
        getDataFromViewModel();

        initAdapter();

        //FAB Listener
        bindings.detailFab.setOnClickListener(fabListener);

        bindings.callBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.likeBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.websiteBtn.setOnClickListener(callLikeWebsiteListener);
    }

    public void getRestaurantDetail() {
        restaurantViewModel.getLocalCachedDetails().observe(getViewLifecycleOwner(), resultDetailed -> {
            if (resultDetailed != null)
                restaurantDetail = resultDetailed;
            updateRestaurantUI(restaurantDetail);
        });
    }

    // Initiating the workmates eating here adapter
    private void initAdapter() {
        adapter = new DetailAdapter(context);
        bindings.detailRecyclerview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        bindings.detailRecyclerview.setHasFixedSize(false);
        bindings.detailRecyclerview.setAdapter(adapter);

        //RecyclerView update with users having their lunch set here
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            userList.clear();
            for (User u : users) {
                if (u.getPlaceBooked().equals(restaurantDetail.getName())) {
                    userList.add(u);
                }
            }
            adapter.updateList(userList);
        });

    }

    // Updating the adapter
    private void updateAdapter() {
        userViewModel.getUsersOnPlace(restaurantDetail.getName()).observe(getViewLifecycleOwner(), users -> adapter.updateList(users));
    }

    // Getting the last known data from the ViewModel (mainly for the floating button and like button)
    private void getDataFromViewModel() {
        //Getting current user
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                //Putting the info in local var localUser when data is received
                localUser.setUrlPicture(user.getUrlPicture());
                localUser.setUsername(user.getUsername());
                localUser.setHasBooked(user.getHasBooked());
                localUser.setPlaceBooked(user.getPlaceBooked());
                localUser.setUid(user.getUid());

                //FAB design (green when lunch is here, black otherwise)
                if (localUser.getPlaceBooked().equals(restaurantDetail.getName())) {
                    DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
                } else {
                    DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
                }

                //Like button design (green when place is liked, orange otherwise)
                if (localUser.getPlaceLiked().contains(restaurantDetail.getPlaceId())) {
                    bindings.likeBtn.setText(R.string.liked);
                    bindings.likeBtn.setTextColor(getResources().getColor(R.color.secondaryDarkColor));
                    Drawable[] drawables = bindings.likeBtn.getCompoundDrawables();
                    if (drawables[1] != null) {  // top drawable is 1
                        drawables[1].setColorFilter(getResources().getColor(R.color.secondaryDarkColor), PorterDuff.Mode.MULTIPLY);
                    }
                } else {
                    bindings.likeBtn.setText(R.string.not_liked);
                    bindings.likeBtn.setTextColor(getResources().getColor(R.color.primaryColor));
                    Drawable[] drawables = bindings.likeBtn.getCompoundDrawables();
                    if (drawables[1] != null) {  // top drawable is 1
                        drawables[1].setColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
        });
    }

    // AlertDialog for the different use case of the floating button
    private void FABAlertDialog(int c) {
        switch (c) {
            case 1:
                new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(getString(R.string.dialog_yes), (dialogInterface, i) -> {
                            //Updating data for the com.aki.go4lunch.UI
                            userViewModel.updateHasBooked(false);
                            userViewModel.updatePlaceBooked("");
                            localUser.setHasBooked(false);
                            localUser.setPlaceBooked("");
                            updateAdapter();
                            bindings.detailFab.setColorFilter(getResources().getColor(R.color.white));
                        })
                        .setNegativeButton(getString(R.string.dialog_no), (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage(getString(R.string.lunch_already_placed)).setTitle(getString(R.string.cancel_lunch))
                        .show();
                break;
            case 2:
                new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(getString(R.string.dialog_yes_2), (dialogInterface, i) -> {

                            //Updating data for the com.aki.go4lunch.UI
                            EventBus.getDefault().post(new LunchSelectedEvent(userList, restaurantDetail.getName(), restaurantDetail.getFormattedAddress()));
                            userViewModel.updatePlaceBooked(restaurantDetail.getName());
                            localUser.setPlaceBooked(restaurantDetail.getName());
                            updateAdapter();
                            bindings.detailFab.setColorFilter(getResources().getColor(R.color.secondaryColor));
                        })
                        .setNegativeButton(getString(R.string.dialog_no_2), (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage(getString(R.string.change_lunch)).setTitle(getString(R.string.change_lunch_title))
                        .show();
                break;
        }
    }

    // Updating UI with restaurant information
    public void updateRestaurantUI(ResultDetailed restaurant) {
        //Rating
        bindings.detailRatingBar.setNumStars(3);
        bindings.detailRatingBar.setStepSize(1);
        bindings.detailRatingBar.setIsIndicator(true);
        if (restaurant.getRating() != null) {
            //Rating binding
            if (restaurant.getRating() <= 2 && restaurant.getRating() > 0) {
                bindings.detailRatingBar.setRating(1);
            } else if (restaurant.getRating() <= 4 && restaurant.getRating() > 2) {
                bindings.detailRatingBar.setRating(2);
            } else if (restaurant.getRating() > 4) {
                bindings.detailRatingBar.setRating(3);
            } else {
                bindings.detailRatingBar.setRating(0);
            }
        }

        //Basic info
        bindings.restaurantDetailAddress.setText(restaurant.getFormattedAddress());
        bindings.restaurantDetailName.setText(restaurant.getName());

        //Photo
        if (restaurant.getPhotos() != null) {

            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxheight=400&photoreference=" +
                    restaurant.getPhotos().get(0).getPhotoReference() +
                    "&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY);

            //Photo binding
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        } else {
            Glide.with(context)
                    .load(R.drawable.restaurant_default)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        }
    }

    //------------------------------
    //   PERMISSION FOR CALLING
    //------------------------------

    public void callPermission() {
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                explainPermission();
            } else {
                askPermission();
            }
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurantDetail.getInternationalPhoneNumber())));
        }
    }

    private void explainPermission() {
        Snackbar.make(requireView(),
                getString(R.string.call_permission_explained),
                BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.authorize), view -> askPermission())
                .show();
    }

    private void askPermission() {
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                //MAKE THE PHONE CALL, IT MEANS PERMISSION IS GRANTED
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurantDetail.getInternationalPhoneNumber())));
            } else if (!shouldShowRequestPermissionRationale(permissions[0]) && !shouldShowRequestPermissionRationale(permissions[1])) {
                displayOptions();
            } else {
                explainPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void displayOptions() {
        Snackbar.make(requireView(), getString(R.string.permission_denied), BaseTransientBottomBar.LENGTH_LONG)
                .setAction(getString(R.string.settings_menu), view -> {
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    final Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .show();
    }
}

