package Fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationsharing.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapters.CustomInfoWindow;
import Models.Sharedlocation;
import Models.User;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private View mView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private User mUser;
    private ArrayList<Marker> mMarker;
    private ArrayList<Sharedlocation> mGlobalSharedLocationArrayList;

    private CustomInfoWindow mCustomInfoWindow;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private String TAG = "MAP_FRAGMENT_DEBUG_TAG";

    public MapFragment() {

    }
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize Variables
        initializeVariables();
        // Bind Views
        InitializeBindViews();
        // Get User Information
        getUserInformation();
        // Initialize Map
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);

        // Get Current User Location
        initializeUserCurrentLocation();
        return mView;
    }
    private void initializeVariables(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    private void InitializeBindViews(){
        mMapView = mView.findViewById(R.id.mapView);
    }
    private void getUserInformation(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DocumentReference docRef = db.collection("User Information").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        mUser = document.toObject(User.class);
                    }
                    else{
                        Log.d(TAG, "DocumentSnapshot Failed: Document Does Not Exists");
                    }
                }else{
                    Log.d(TAG, "DocumentSnapshot Failed: " + task.getException());
                }
            }
        });
    }
    private void populateMapMarkers(){
        mMarker = new ArrayList<>();

        db.collection("Shared Location")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        ArrayList<Sharedlocation> sharedLocationArrayListArrayList = new ArrayList<>();
                        mGlobalSharedLocationArrayList = new ArrayList<>();
                        for(QueryDocumentSnapshot doc : value){
                            if(doc.get("geoPoint") != null){
                                sharedLocationArrayListArrayList.add(doc.toObject(Sharedlocation.class));
                                mGlobalSharedLocationArrayList.add(doc.toObject(Sharedlocation.class));
                                for(int i = 0; i < sharedLocationArrayListArrayList.size(); i++){

                                    LatLng newlatlng = new LatLng(sharedLocationArrayListArrayList.get(i).getGeoPoint().getLatitude(),
                                            sharedLocationArrayListArrayList.get(i).getGeoPoint().getLongitude());


                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.title(sharedLocationArrayListArrayList.get(i).getLocationTitle());
                                    markerOptions.position(newlatlng);
                                    Marker tempMarker = mGoogleMap.addMarker(markerOptions);
                                    mMarker.add(tempMarker);
                                }
                            }
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                    break;
                                case REMOVED:
                                    for(int i = 0; i < mMarker.size(); i++){
                                        if(mMarker.get(i).getPosition().latitude == dc.getDocument().getGeoPoint("geoPoint").getLatitude() &&
                                                mMarker.get(i).getPosition().longitude ==  dc.getDocument().getGeoPoint("geoPoint").getLongitude()){
                                            mMarker.get(i).remove();

                                            Log.d(TAG, "Removed Location: " + dc.getDocument().getData());
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                            }
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void initializeUserCurrentLocation(){
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            LatLng mLatlng = new LatLng(location.getLatitude(),location.getLongitude());
                            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatlng,15);
                            mGoogleMap.moveCamera(mCameraUpdate);
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mCustomInfoWindow = new CustomInfoWindow(getContext());
        mGoogleMap.setInfoWindowAdapter(mCustomInfoWindow);

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        populateMapMarkers();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        for(int i = 0; i < mGlobalSharedLocationArrayList.size(); i++){
            if(mGlobalSharedLocationArrayList.get(i).getGeoPoint().getLatitude() == marker.getPosition().latitude &&
               mGlobalSharedLocationArrayList.get(i).getGeoPoint().getLongitude() == marker.getPosition().longitude){
                mCustomInfoWindow.setmSharedLocation(mGlobalSharedLocationArrayList.get(i));
            }
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        for(int i = 0; i < mGlobalSharedLocationArrayList.size(); i++){
            if(mGlobalSharedLocationArrayList.get(i).getGeoPoint().getLatitude() == marker.getPosition().latitude &&
                    mGlobalSharedLocationArrayList.get(i).getGeoPoint().getLongitude() == marker.getPosition().longitude){

                Sharedlocation sharedlocation = mGlobalSharedLocationArrayList.get(i);

                Fragment mFragment = null;
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("Shared Location", sharedlocation);
                mBundle.putParcelable("mUser", mUser);
                mFragment = new ViewSharedLocationFragment();
                mFragment.setArguments(mBundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,
                                R.anim.slide_out,
                                R.anim.slide_in,
                                R.anim.slide_out
                        )
                        //.replace(((ViewGroup)getView().getParent()).getId(), mFragment)
                        .replace(R.id.frame_main, mFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}