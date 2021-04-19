package pl.pue.air.main.exercise;

import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import pl.pue.air.main.R;

public class Excercise1PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_information_exc1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(height*0.8));
    }
}
