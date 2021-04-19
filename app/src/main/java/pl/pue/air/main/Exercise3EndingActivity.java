package pl.pue.air.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class Exercise3EndingActivity extends AppCompatActivity implements View.OnClickListener {

    Button backToMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excercise3_end_screen);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        backToMenuButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backToMenuButton:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;

            default:
                break;
        }
    }

}
