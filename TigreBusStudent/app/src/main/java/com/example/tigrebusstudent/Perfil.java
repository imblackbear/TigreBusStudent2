package com.example.tigrebusstudent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Perfil extends AppCompatActivity {

    //variables para recibir datos de FireBase
    private TextView txt_nombre,txt_correo,txt_telefono,txt_num_empleado;


    //cerrar sesion
    private Button cerrars;
    public static boolean prueba = true;

    //Variable para usar en el if del contador y parar el metodo
    static boolean zerrar;

    //referencias de FireBase
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Cambiar el valor del contador para se cancele al entrar en esta actitivy
        MapaDriver.cancelar = true;

        //instaciar textview
        txt_nombre = (TextView)findViewById(R.id.txtview_nombre);
        txt_correo = (TextView)findViewById(R.id.txtview_correo);
        txt_telefono = (TextView)findViewById(R.id.txtview_telefono);
        txt_num_empleado = (TextView)findViewById(R.id.txtview_num_empleado);


        //toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        //Cambiar de color la barra de estado
        int myColor = Color.parseColor("#00629F");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(myColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(myColor);
        }



        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();




        //METODO PARA CERRAR SESION
        cerrars = (Button) findViewById(R.id.bt_cerrarsesion2);
        cerrars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //MapaDriver zerrar = new MapaDriver();
               //zerrar.cerrarsesion = true;
               //zerrar = true;
               mAuth.signOut();
               zerrar = true;
               startActivity(new Intent(Perfil.this, MainActivity.class));
               finish();

            }
        });


        //recibir los valores
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuarios").child("Conductores").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //obtener valores

                if (dataSnapshot.exists()){
                    //obtener valores
                    String nombre = dataSnapshot.child("Nombre").getValue().toString();
                    String apellidos = dataSnapshot.child("Apellidos").getValue().toString();
                    String correo = dataSnapshot.child("Correo").getValue().toString();
                    String telefono =dataSnapshot.child("Telefono").getValue().toString();
                    String num_empleado = dataSnapshot.child("Num empleado").getValue().toString();

                    //ponerlo en el TextView
                    txt_nombre.setText(" " + nombre + " " + apellidos);
                    txt_correo.setText(correo);
                    txt_telefono.setText(telefono);
                    txt_num_empleado.setText(num_empleado);




                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
/////////////////////////////////////////////////////////////////////
    /*
    AGREGAR SI HAY ICONOS EN LA TOOLBAR
        Menu toolbar
        public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_perfil,menu);
        return true;
    }

     */
////////////////////////////////////////////////////////////
    //Metodo para seleccionar ccada item del toolbar
    public boolean onOptionsItemSelected (MenuItem menuItem){
        //obtener id de cada item
        switch (menuItem.getItemId()){

            // casos dependiendo que se ha seleccionado
            /*
            case R.id.menu_mapa:
                startActivity(new Intent(getApplicationContext(),MapaDriver.class));
                break;

            */

            //flecha android
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

/////////////////////////////////////////////////////
}
