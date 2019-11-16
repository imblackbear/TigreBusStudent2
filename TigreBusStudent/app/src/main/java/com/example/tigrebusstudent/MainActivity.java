package com.example.tigrebusstudent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth Auth;
    DatabaseReference Database;
    private int MY_PERMISSIONS_REQUESTREAD_CONTACTS;
    //FirebaseAuth mAuth;
    //DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUESTREAD_CONTACTS);
            return;
        }

        //regresar valores para que funcione
        MapaDriver.cancelar = false;
        MapaDriver.iniciarcu = true;


    }


    //metodo para avanzar a la activity ingresar por medio de su boton
    public void ingresar (View view){
        Intent ingresar = new Intent(this,ingresar_driver.class);
        startActivity(ingresar);
        //lanza un segundo activity con el metodo creado ingresar
    }


    //metodo para avanzar a la activity registrar por medio de su boton
    public void registrar (View view){
        Intent registrar = new Intent(this,registro_driver.class);
        startActivity(registrar);
        //lanza un segundo activity con el metodo creado registrar
    }

    // METODO PARA MANTENER SESION INICIADA
    @Override
    protected void onStart() {
        super.onStart();

        if (Auth.getCurrentUser() != null ){
            FirebaseUser usuario = Auth.getCurrentUser();
            //comprueba si el usuario ya verifico su correo
            if(usuario.isEmailVerified()) {
                startActivity(new Intent(MainActivity.this, MapaDriver.class));
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);


    }
}
