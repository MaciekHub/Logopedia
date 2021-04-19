package pl.pue.air.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class Exercise1EndingActivity extends AppCompatActivity implements View.OnClickListener {

    Button goSendButton, backToMenuButton;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excercise1_end_screen);

        goSendButton = findViewById(R.id.goToSendButton);
        goSendButton.setOnClickListener(this);

        backToMenuButton = findViewById(R.id.backToMenuButton);
        backToMenuButton.setOnClickListener(this);

        fileName = getIntent().getStringExtra("fileName");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backToMenuButton:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.goToSendButton:
                sendEmail();
                break;


            default:
                break;
        }
    }

    private void sendEmail() {
        if( fileName != null && !fileName.isEmpty()) {
            Intent intent = new Intent(this, EmailSender.class);
            ArrayList<String> fileToSend = new ArrayList<>();
            fileToSend.add(fileName);
            intent.putExtra("filesToSend", fileToSend);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Nie nagrałeś audio!", Toast.LENGTH_SHORT).show();
        }
    }
}
