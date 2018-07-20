package misfit.testing.trackme.helper.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;

import misfit.testing.trackme.util.AppConstant;

/**
 * Created by Thu Nguyen on 7/19/2018.
 */

public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient locationClient;
    LocationRequest locationRequest = new LocationRequest();
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest.setInterval(AppConstant.LOCATION_INTERVAL);
        locationRequest.setFastestInterval(AppConstant.LOCATION_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(locationClient, builder.build());
        result.setResultCallback(locationSettingsResult -> {

            final Status status = locationSettingsResult.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location requests here
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        stopSelf();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(status);
                            }
                        });

                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
        locationClient.connect();
        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> result = settingsClient
//                .checkLocationSettings(locationSettingsRequest);
//        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
//                try {
//                    LocationSettingsResponse response =
//                            task.getResult(ApiException.class);
//                } catch (ApiException ex) {
//                    switch (ex.getStatusCode()) {
//                        case LocationSettingsStatusCodes.SUCCESS:
//                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//
//                            break;
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            ResolvableApiException resolvableApiException =
//                                    (ResolvableApiException) ex;
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    EventBus.getDefault().post(resolvableApiException);
//                                }
//                            });
//
//                            break;
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//
//                            break;
//                    }
//                }
//            }
//        });
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Also stop update location listener and disconnect
        if(fusedLocationProviderClient != null && locationClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            locationClient.disconnect();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location newLocation = locationResult.getLastLocation();
            if(newLocation != null){
                // Post to UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(newLocation);
                    }
                });
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };
}
