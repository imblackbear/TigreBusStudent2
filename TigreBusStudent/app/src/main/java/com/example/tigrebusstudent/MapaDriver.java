package com.example.tigrebusstudent;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapaDriver extends AppCompatActivity implements OnMapReadyCallback{

    private FusedLocationProviderClient fusedLocationClient;
    private int MY_PERMISSIONS_REQUESTREAD_CONTACTS;


    static boolean checked = false;
    static boolean cancelar = false;
    //array para almacenar los markadores e irlos eliminando
    private ArrayList<Marker> realtimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realtimeMarkers2 = new ArrayList<>();

    private ArrayList<Marker> eliminarmarker = new ArrayList<>();

    static CountDownTimer realTimeTimer;

    //referencias de FireBase
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;


    //variable para iniciar en CU al iniciar la APP
    public static boolean iniciarcu = true;
    //
    private GoogleMap mMap;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_mapa_driver);

            //
            checked=true;

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            //Cambiar de color la barra de estado
             int myColor = Color.parseColor("#00629F");
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(myColor);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(myColor);
             }




             //variable de la clase perfil para cerrar sesion regresa a false para ejecutar el contador
             Perfil.zerrar =false;

            //toolbar
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //TITULO TOOLBAR
            getSupportActionBar().setTitle("Estado:");
            getSupportActionBar().setSubtitle("Desconectado");



            //SWITCH
            Switch bottons = (Switch)findViewById(R.id.id_switch);



            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();



    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    //metodo para subir la latiud y longitud cada x tiempo
    public void contador() {
        if (checked == false) {

        if (realTimeTimer != null) {
            realTimeTimer.cancel();
        }
        realTimeTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e("seconds remaining: ", "" + millisUntilFinished / 1000);

            }

            public void onFinish() {

                //Comprobacion para cerrar la sesion
                if (Perfil.zerrar == true) {
                    Toast.makeText(MapaDriver.this, "Cerraste sesion", Toast.LENGTH_SHORT).show();
                    iniciarcu = true;

                } else {
                    Toast.makeText(MapaDriver.this, "Puntos Actualizados", Toast.LENGTH_SHORT).show();
                    if (realTimeTimer != null) {
                        realTimeTimer.cancel();
                    }

                    onMapReady(mMap);
                    SubirLatitudLongitud();
                }


            }
        }.start();
    }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void SubirLatitudLongitud() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapaDriver.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUESTREAD_CONTACTS);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.e("Latitud: ",+ location.getLatitude()+"Longitud: "+location.getLongitude());


                            Map<String,Object> LatitudLongitud = new HashMap<>();

                            LatitudLongitud.put("Latitud",location.getLatitude());
                            LatitudLongitud.put("Longitud",location.getLongitude());


                            String id = mAuth.getCurrentUser().getUid();
                            mDatabase.child("Usuarios").child("Conductores").child(id).updateChildren(LatitudLongitud);


                            Toast.makeText(MapaDriver.this, "Recibio", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //Cargar mapa
        try{
            boolean mapstyle = googleMap.setMapStyle(

                    MapStyleOptions.loadRawResourceStyle(this,R.raw.mapstyle));

            if (!mapstyle)
                Log.e("Error","No se encontro el mapa");

        }
        catch (Resources.NotFoundException ex){
            ex.printStackTrace();
        }


        //metodo para iniciar en CU
        iniciarenCU(googleMap);


        //marcador parada oficial1
        LatLng CU_uno = new LatLng(25.7244438,-100.3094848);
        mMap.addMarker(new MarkerOptions().position(CU_uno).title("Parada oficial 1 ").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada2_foreground)));





        ////////////////////////////
        //entrar a latlng en firebase
        EntarEnLatLngFirebase();
        ////////////////////////////






    }
//////////////////AQGRDRRR
    private void EntarEnLatLngFirebase() {
        mDatabase.child("Usuarios").child("Conductores").addListenerForSingleValueEvent(new ValueEventListener() {
            //obtiene los datos cada vez que hay un cambio
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker2:realtimeMarkers2){
                    marker2.remove();
                }

                //obtener datos de cada nodo hijo de Conductores
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    //obtener latitud y longitud
                    ObtenerLatLng obtener = snapshot.getValue(ObtenerLatLng.class);
                    Double Latitud = obtener.getLatitud();
                    Double Longitud = obtener.getLongitud();


                    //se agregan al marker y se posiciona
                   //  MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitud,Longitud))
                    //        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tigrebus1_foreground)); //se agrega el icono


                    Marker markerp = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitud,Longitud)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tigrebus1_foreground)));
                    realtimeMarkers.add(markerp);

                    eliminarmarker.add(markerp);

                    //realtimeMarkers.add(mMap.addMarker(marker));


                }


                realtimeMarkers2.clear();
                realtimeMarkers2.addAll(realtimeMarkers);



                if (cancelar == false){
                    contador();
                }else {
                    realTimeTimer.cancel();
                    contador();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Iniciar el mapa en CU
    private void iniciarenCU(GoogleMap googleMap) {
        if (iniciarcu){

            LatLng cuinicio = new LatLng(25.7274395, -100.3121028);
            mMap.addMarker(new MarkerOptions().position(cuinicio).visible(false));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cuinicio,14));

            iniciarcu = false;

        }
    }


    //


    //Menu toolbar
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //SWITCH
        final MenuItem switchservice = menu.findItem(R.id.menu_switch);
        final  Switch actionView = (Switch) MenuItemCompat.getActionView(switchservice);//final quitar

        SharedPreferences sp = getSharedPreferences("guardar",MODE_PRIVATE);

        //ESTRADOS DEL CONDUCTOR
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //
                    SharedPreferences.Editor editor = getSharedPreferences("guardar",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    actionView.setChecked(true);
                    checked=false;
                    contador();
                    getSupportActionBar().setSubtitle("En línea ");

                }else {
                    //Eliminar LatLang de FireBase
                    String id = mAuth.getCurrentUser().getUid();
                    for(Marker remove:eliminarmarker){
                        remove.remove();
                    }
                    mDatabase.child("Usuarios").child("Conductores").child(id).child("Latitud").removeValue();
                    mDatabase.child("Usuarios").child("Conductores").child(id).child("Longitud").removeValue();

                    realTimeTimer.cancel();
                    getSupportActionBar().setSubtitle("Desconectado");
                }
            }
        });


        return true;
    }


    //Metodo para seleccionar ccada item del toolbar
    public boolean onOptionsItemSelected (MenuItem menuItem){
        //obtener id de cada item
        switch (menuItem.getItemId()){

            // casos dependiendo que se ha seleccionado
            //
            case R.id.menu_perfil:
                startActivity(new Intent(getApplicationContext(),Perfil.class));
                break;

            //
            case R.id.menu_rCA:
               m_CienciasAgropecuarias();
               break;


            //
            case R.id.menu_rCU:
                Toast.makeText(this, "cerrar ses", Toast.LENGTH_SHORT).show();
                break;

            //flecha android
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }


    //MARCADORES DE CIENCIAS AGROPECUARIAS
    private void m_CienciasAgropecuarias() {
        //PARADA 1
        LatLng parada1 = new LatLng(25.768954,-100.293153);
        mMap.addMarker(new MarkerOptions().position(parada1).title("Parada oficial #1").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada1,15));



        //PARADA 2
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.781568,-100.292435))
                .title("Parada oficial #2").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        //PARADA 3
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.784287,-100.285917))
                .title("Parada oficial #3").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 4
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.783325,-100.286812))
                .title("Parada oficial #4").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 5
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.784779,-100.287254))
                .title("Parada oficial #5").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 6
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.785772,-100.287172))
                .title("Parada oficial #6").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
