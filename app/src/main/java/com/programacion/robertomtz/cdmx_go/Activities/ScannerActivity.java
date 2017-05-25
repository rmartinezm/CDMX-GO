package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;
import com.programacion.robertomtz.cdmx_go.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView;
    private String codigoCorrecto;
    private Bundle bundle;

    public static int CORRECTO = 1;
    public static int INCORRECTO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        codigoCorrecto = "";

        bundle = getIntent().getExtras();

        if (bundle != null)
            codigoCorrecto = bundle.getString("codigo");

        scannerQR();
    }

    private void scannerQR(){
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (scannerView != null)
            scannerView.startCamera();
    }

    @Override
    public void handleResult(final Result result) {

        Intent resultado = new Intent();
        if (result.getText().equals(codigoCorrecto))
            resultado.putExtra("result", CORRECTO);
        else
            resultado.putExtra("result", INCORRECTO);

        resultado.putExtra("position", bundle.getInt("position"));

        setResult(2, resultado);

        finish();
    }

    /**


     */
}
