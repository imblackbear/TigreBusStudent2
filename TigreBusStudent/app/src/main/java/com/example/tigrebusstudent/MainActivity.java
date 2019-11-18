package com.example.tigrebusstudent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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

    final  private int REQUEST_CODE_ASK_PERMISSION=111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance().getReference();

        //Cambiar de color la barra de estado
        int myColor = Color.parseColor("#004D9F");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(myColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(myColor);
        }


        solicitarpermisos();
        /*
        //PERMISOS
        //PERMISO UBICACION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUESTREAD_CONTACTS);
            return;
        }

        //PERMISO ENVIAR SMS
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ){ActivityCompat.requestPermissions(MainActivity.this,new String []
                {
                        Manifest.permission.SEND_SMS,
                },1000);

                }else {

        }

         */


        //regresar valores para que funcione
        MapaStudent.cancelar = false;
        MapaStudent.iniciarcu = true;


    }


    //metodo para avanzar a la activity ingresar por medio de su boton
    public void ingresar (View view){
        Intent ingresar = new Intent(this, ingresar_student.class);
        startActivity(ingresar);
        //lanza un segundo activity con el metodo creado ingresar
    }


    //metodo para avanzar a la activity registrar por medio de su boton
    public void registrar (View view){
        Intent registrar = new Intent(this, registro_student.class);
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
                startActivity(new Intent(MainActivity.this, MapaStudent.class));
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


    private void solicitarpermisos(){
        int permisofinelocation = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permisocoarselocation = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION);
        int permisoenviarsms = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS);
        int permisoleersms = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_SMS);
        int permisorecibirsms = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECEIVE_SMS);

        if (permisocoarselocation != PackageManager.PERMISSION_GRANTED || permisofinelocation != PackageManager.PERMISSION_GRANTED || permisoenviarsms != PackageManager.PERMISSION_GRANTED ||
                permisoleersms != PackageManager.PERMISSION_GRANTED || permisorecibirsms != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},REQUEST_CODE_ASK_PERMISSION);
            }

        }

    }

}
