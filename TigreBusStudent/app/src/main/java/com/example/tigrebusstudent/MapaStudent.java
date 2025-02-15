package com.example.tigrebusstudent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class MapaStudent extends AppCompatActivity implements OnMapReadyCallback{

    private FusedLocationProviderClient fusedLocationClient;
    private int MY_PERMISSIONS_REQUESTREAD_CONTACTS;


    //BOTON FLOTANTE
    FloatingActionButton enviarubi;
    int clicContador =0;
    int clicContador2 = 0;
    int maximo=5;


    static boolean checked = false;
    static boolean cancelar = false;


    //array para almacenar los markadores e irlos eliminando
    private ArrayList<Marker> realtimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realtimeMarkers2 = new ArrayList<>();

    private ArrayList<Marker> eliminarmarker = new ArrayList<>();

    static CountDownTimer realTimeTimer;

    //Variable para guardar el telefono
    String telefonoeme = "" ;
     String Latitud = "";
     String Longitud= "";

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
            setContentView(R.layout.activity_mapa_student);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();


            //SUBIR LA LATITUD Y LOGINTUD AL INICIAR LA APP
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
                            mDatabase.child("Usuarios").child("Alumnos").child(id).updateChildren(LatitudLongitud);


                        }
                    }
                });

             //
            checked=true;

            //BOTON FLOTANTE
            enviarubi = (FloatingActionButton)findViewById(R.id.bt_enviarubi);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //METODO PARA ENVIAR LA ULTIMA UBICACION

             enviarubi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicContador ++;
                obtenervalores();
                clicContador2 ++;
                if(clicContador == maximo)
                {
                        String link = "https://maps.google.com/?q="+Latitud+","+Longitud;

                        /*
                        enviarubi(telefonoeme,"Esta es mi última ubicación: \n" + "Latitud: " + Latitud + "\n" + "Longitud: " + Longitud + "\n" + "\nEnlace a Google Maps:" + "\n https://maps.google.com/?q="+Latitud+","+Longitud +
                                "\n\nPara obtener más información, póngase en contacto al siguiente correo electrónico: \n" + "tigrebussoporte@gmail.com");
                         */

                    enviarubi(telefonoeme,"Esta es mi ultima ubicacion: " + "\n\nEnlace a Google Maps:\n"+link);
                    enviarubi(telefonoeme,"Para obtener mas informacion acerca de este mensaje, consulte al siguiente correo electronico:\ntigrebussoporte@gmail.com");
                        clicContador = 0;

                }
                if(clicContador2 == 15){
                    Toasty.warning(getApplicationContext(),"Realizar muchas veces esta acción consume tu saldo",Toast.LENGTH_SHORT).show();
                    clicContador2 = 0;
                }

            }
        });


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

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



    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    //metodo para subir la latiud y longitud cada x tiempo
    public void contador() {

        if (realTimeTimer != null) {
            realTimeTimer.cancel();
        }
        realTimeTimer = new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e("seconds remaining: ", "" + millisUntilFinished / 1000);

            }

            public void onFinish() {

                //Comprobacion para cerrar la sesion
                if (Perfil.zerrar == true) {
                    iniciarcu = true;

                } else {
                    Toasty.info(MapaStudent.this, "Información actualizada ", Toast.LENGTH_SHORT).show();
                    if (realTimeTimer != null) {
                        realTimeTimer.cancel();
                    }

                    onMapReady(mMap);
                    SubirLatitudLongitud();
                }


            }
        }.start();
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void SubirLatitudLongitud() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapaStudent.this,
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
                            mDatabase.child("Usuarios").child("Alumnos").child(id).updateChildren(LatitudLongitud);

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

                //obtener datos de cada nodo hijo de conductores
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    //obtener latitud y longitud
                    ObtenerLatLng obtener = snapshot.getValue(ObtenerLatLng.class);
                    Double Latitud = obtener.getLatitud();
                    Double Longitud = obtener.getLongitud();


                    //se agregan al marker y se posiciona
                    //MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitud,Longitud))
                       //     .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tigrebus1_foreground)); //se agrega el icono


                   Marker markerp = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitud,Longitud)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tigrebus1_foreground)));
                    realtimeMarkers.add(markerp);

                    eliminarmarker.add(markerp);

                   // realtimeMarkers.add(mMap.addMarker(marker));


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
            case R.id.menu_informacion:
                startActivity(new Intent(getApplicationContext(),info.class));
                break;



            //Markadores de los recorridos
            case R.id.menu_rCA:
               m_CienciasAgropecuarias();
               break;

            case R.id.menu_rCU:
                m_CiudadUniversitaria();
                break;

            case R.id.menu_rUM:
                m_UnidadMederos();
                break;

            case R.id.menu_rCdS:
                m_CienciasSalud();
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
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.768954,-100.293153))
                .title("Parada oficial #1").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 2
        LatLng parada1 = new LatLng(25.781568,-100.292435);
        mMap.addMarker(new MarkerOptions().position(parada1).title("Parada oficial #2").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada1,15));


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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //MARCADORES DE CIUDAD UNIVERSITARIA
    private void m_CiudadUniversitaria(){

        //PARADA 1
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.724764,-100.309409))
                .title("Parada oficial #1").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 2
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.729019,-100.308427))
                .title("Parada oficial #2").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        //PARADA 3
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.728778,-100.311506))
                .title("Parada oficial #3").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 4
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.729647,-100.312793))
                .title("Parada oficial #4").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 5
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.726622,-100.316881))
                .title("Parada oficial #5").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 6
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.723899,-100.316213))
                .title("Parada oficial #6").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 7
        LatLng parada1 = new LatLng(25.723928,-100.313852);
        mMap.addMarker(new MarkerOptions().position(parada1).title("Parada oficial #7").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada1,15));

        //PARADA 8
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.723696,-100.311181))
                .title("Parada oficial #5").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 8
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.724005,-100.309625))
                .title("Parada oficial #6").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

    }

    private void m_UnidadMederos(){

        //PARADA 1
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.615539,-100.275348))
                .title("Parada oficial #1").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 2
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.614453,-100.276113))
                .title("Parada oficial #2").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        //PARADA 3
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.613884,-100.279764))
                .title("Parada oficial #3").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 4
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.614541,-100.282602))
                .title("Parada oficial #4").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 5
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.614929,-100.291090))
                .title("Parada oficial #5").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 6
        LatLng parada1 = new LatLng(25.613272,-100.280471);
        mMap.addMarker(new MarkerOptions().position(parada1).title("Parada oficial #6").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada1,15));

        //PARADA 7
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.614254,-100.276911))
                .title("Parada oficial #7").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

    }


    private void m_CienciasSalud(){


        //PARADA 1
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.691202,-100.346971))
                .title("Parada oficial #1").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 2
        LatLng parada1 = new LatLng(25.692494,-100.346900);
        mMap.addMarker(new MarkerOptions().position(parada1).title("Parada oficial #2").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada1,15));


        //PARADA 3
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.693450,-100.346860))
                .title("Parada oficial #3").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 4
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.695641,-100.344346))
                .title("Parada oficial #4").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 5
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.695729,-100.343773))
                .title("Parada oficial #5").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 6
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.691822,-100.344269))
                .title("Parada oficial #6").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));

        //PARADA 7
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.688559,-100.344627))
                .title("Parada oficial #7").icon(BitmapDescriptorFactory.fromResource(R.drawable.paradaca)));


    }




    private void enviarubi(String numero,String mensaje){
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toasty.warning(getApplicationContext(),"Ultima ubicación enviada",Toasty.LENGTH_SHORT).show();

        }catch (Exception e){
            Toasty.error(getApplicationContext(),"No se logro enviar la ubicación",Toasty.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //OBTENER NUM, LAT Y LNG DE FIREBASE

    public void obtenervalores(){

            String id = mAuth.getCurrentUser().getUid();
            mDatabase.child("Usuarios").child("Alumnos").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        telefonoeme = dataSnapshot.child("Telefono de advertencia").getValue().toString();
                        Latitud = dataSnapshot.child("Latitud").getValue().toString();
                        Longitud = dataSnapshot.child("Longitud").getValue().toString();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
