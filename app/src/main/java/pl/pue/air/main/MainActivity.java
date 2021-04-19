package pl.pue.air.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import pl.pue.air.main.exercise.Exercise1Activity;
import pl.pue.air.main.exercise.DiagnozaActivity;
import pl.pue.air.main.exercise.Exercise2Activity;
import pl.pue.air.main.list.view.RecordingList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MICROPHONE_PERMISSION_CODE = 100;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button recordingButton = findViewById(R.id.buttonRecording);
        recordingButton.setOnClickListener(this);
        button3.setOnClickListener(this);

        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);

        checkPermission(Manifest.permission.RECORD_AUDIO,
                MICROPHONE_PERMISSION_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button1:
                Intent intent1 = new Intent(this, Exercise1Activity.class);
                startActivity(intent1);
                break;

            case R.id.button2:
                Intent intent2 = new Intent(this, DiagnozaActivity.class);
                startActivity(intent2);
                break;

            case R.id.button3:
                Intent intent3 = new Intent(this, Exercise2Activity.class);
                startActivity(intent3);
                break;

            case R.id.buttonRecording:
                Intent intent4 = new Intent(this, RecordingList.class);
                startActivity(intent4);
                break;

            case R.id.buttonSend:
                Intent intent5 = new Intent(this, EmailSender.class);
                startActivity(intent5);
                break;

            default:
                break;
        }
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Audio Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
