package com.aki.go4lunch.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.aki.go4lunch.R;
import com.aki.go4lunch.databinding.LoginActivityBinding;
import com.aki.go4lunch.models.User;
import com.aki.go4lunch.viewmodels.RestaurantViewModel;
import com.aki.go4lunch.viewmodels.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LoginActivity extends AppCompatActivity {

    private static final int AUTH_REQUEST_CODE = 123;
    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    User localUser = User.getInstance();
    LoginActivityBinding binding;
    FusedLocationProviderClient client;
    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater(), null, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        setContentView(R.layout.login_activity);


        client = LocationServices.getFusedLocationProviderClient(this);
        //INIT LOCATION MANAGER
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (userViewModel.getCurrentFirebaseUser() != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                localUser.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() + "," + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());

                //API CALL WITHOUT ANY LISTENER JUST TO CACHE SOME DATA IN ADVANCE
                restaurantViewModel.getRestaurantsAround(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()
                                + "," + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
                        this);
            }

            // INTENT TO MAIN ACTIVITY
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getPermissions();
        }

    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Login_theme)
                        .setLogo(R.drawable.logo_title)
                        .build(), AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {

                    localUser.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()
                            + "," + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());

                    //API CALL WITHOUT ANY LISTENER JUST TO CACHE SOME DATA IN ADVANCE
                    restaurantViewModel.getRestaurantsAround(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()
                                    + "," + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
                            this);

                    if (response != null && response.isNewUser()) {
                        userViewModel.createCurrentUserInFirestore();
                    }
                    // INTENT TO MAIN ACTIVITY
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showSnackBar(getCurrentFocus(), getString(R.string.location_mandatory));
                }


            } else {
                if (response == null) {
                    showSnackBar(getCurrentFocus().getRootView(), getString(R.string.auth_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(getCurrentFocus().getRootView(), getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getCurrentFocus().getRootView(), getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    //-----------------------------------------
    //LOCATION PERMISSION
    //-----------------------------------------

    public void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    explainPermissions();
                } else {
                    askPermissions();
                }
            }
        } else {
            startSignInActivity();
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                //When location service is enabled, get last location
                client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    /**
                     * SuppressLint is okay because this is called ONLY if the permissions have already been granted
                     */
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            localUser.setLocation(location.getLatitude() + "," + location.getLongitude());
                        } else {
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(5000)
                                    .setNumUpdates(1);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    Location location1 = locationResult.getLastLocation();
                                    localUser.setLocation(location1.getLatitude() + "," + location1.getLongitude());
                                }
                            };
                            //REQUEST LOCATION UPDATES
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }
                });
            } else {
                //IF LOCATION SERVICE IS NOT ENABLED
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
    }

    private void explainPermissions() {
        Snackbar.make(binding.splashScreenBackgroundBlurry,
                getResources().getString(R.string.location_required),
                BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.authorize), view -> askPermissions())
                .show();
    }

    private void askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    private void displayOptions() {
        Snackbar.make(binding.splashScreenBackgroundBlurry, getString(R.string.permission_denied), BaseTransientBottomBar.LENGTH_LONG)
                .setAction(getString(R.string.settings_menu), view -> {
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    final Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .show();
    }

    /**
     * SuppressLint("MissingPermission")
     * It's because we aren't gonna check permissions in the case where they just got granted.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                //ACCESS LOCATION, IT MEANS THAT PERMISSIONS ARE GOOD
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //When location service is enabled, get last location
                    client.getLastLocation().addOnCompleteListener(task -> {
                        Location location = task.getResult();
                        if (location != null) {
                            localUser.setLocation(location.getLatitude() + "," + location.getLongitude());
                        }
                    });
                }
                startSignInActivity();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(permissions[0]) && !shouldShowRequestPermissionRationale(permissions[1])) {
                    displayOptions();
                } else {
                    explainPermissions();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
