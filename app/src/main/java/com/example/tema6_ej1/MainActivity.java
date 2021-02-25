package com.example.tema6_ej1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_DE_PERMISO = 12345;

    TextView llamadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llamadas = (TextView) findViewById(R.id.llamadas);

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED) {

            //El permiso no está concedido, pedirlo

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, CODIGO_DE_PERMISO);


        }

        else{

            //El permiso esta concedido
            String texto = historicoLlamadas();

            llamadas.setText(texto);


        }



    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_DE_PERMISO:{
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD


                    String texto = historicoLlamadas();

                    llamadas.setText(texto);

                }
                else {

                    llamadas.setText("No has dado permiso");
                }
                return;
            }

        }
    }



    private String historicoLlamadas() {

        StringBuffer texto = new StringBuffer();

        try
        {
            Cursor cursorDatos = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,null, null, null);

            int tfno = cursorDatos.getColumnIndex(CallLog.Calls.NUMBER);
            int tipollamada = cursorDatos.getColumnIndex(CallLog.Calls.TYPE);
            int fecha = cursorDatos.getColumnIndex(CallLog.Calls.DATE);
            int duracion = cursorDatos.getColumnIndex(CallLog.Calls.DURATION);

            texto.append("HISTORICO DE LLAMADAS:");
            while (cursorDatos.moveToNext()) {
                String numero = cursorDatos.getString(tfno);
                String tipo = cursorDatos.getString(tipollamada);
                Date fechallamada = new Date(Long.valueOf(cursorDatos.getString(fecha)));
                String duracionllamada = cursorDatos.getString(duracion);
                String dir = null;
                switch (Integer.parseInt(tipo)) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "REALIZADA";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "RECIBIDA";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "PERDIDA";
                        break;
                }
                texto.append("\nNumero de telefono:--- " + numero + " \nTipo de llamada:--- "
                        + dir + " \nFecha:--- " + fechallamada
                        + " \nDuracion en segundos :--- " + duracionllamada);
                texto.append("\n----------------------------------");
            }
            cursorDatos.close();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

        return texto.toString();
    }


}