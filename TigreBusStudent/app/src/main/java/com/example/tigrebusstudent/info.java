package com.example.tigrebusstudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);



        //toolbar
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
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


    }


    //AGREGAR SI HAY ICONOS EN LA TOOLBAR
    //Menu toolbar
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_info,menu);

        return true;
    }

    //Metodo para seleccionar ccada item del toolbar
    public boolean onOptionsItemSelected (MenuItem menuItem){
        //obtener id de cada item
        switch (menuItem.getItemId()){

            // casos dependiendo que se ha seleccionado
            case R.id.menu_perfil2:
                startActivity(new Intent(getApplicationContext(),Perfil.class));
                break;



            //flecha android
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }



}
