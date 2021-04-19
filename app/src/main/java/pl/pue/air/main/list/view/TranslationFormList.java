package pl.pue.air.main.list.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.pue.air.main.DeleteManager;
import pl.pue.air.main.EmailSender;
import pl.pue.air.main.MainActivity;
import pl.pue.air.main.R;
import pl.pue.air.main.ZipManager;
import pl.pue.air.main.list.TranslationFormItem;
import pl.pue.air.main.list.adapters.TranslationListAdapter;

public class TranslationFormList extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;

    private TranslationListAdapter translationListAdapter;

    private ArrayList<TranslationFormItem> form;

    private Button backToMenuButton, sendFormButton, scoreButton;

    private ImageView infoButton;

    private double ratio;

    private String score;

    private String outputPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation_form);
        backToMenuButton = findViewById(R.id.buttonBackToMenu);
        backToMenuButton.setOnClickListener(this);
        sendFormButton = findViewById(R.id.buttonSendForm);
        sendFormButton.setOnClickListener(this);
        scoreButton = findViewById(R.id.buttonScore);
        scoreButton.setOnClickListener(this);
        infoButton = findViewById(R.id.buttonInfo);
        infoButton.setOnClickListener(this);
        initializeFormView();
    }

    private void initializeFormView() {
        recyclerView = findViewById(R.id.recyclerViewForm);
        setFormItems();
        ratio = getIntent().getDoubleExtra("finalRatio", 0);
        score = getIntent().getStringExtra("finalScore");
        outputPath = getIntent().getStringExtra("outputPath");
        translationListAdapter = new TranslationListAdapter(form, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(translationListAdapter);
    }

    private void setFormItems() {
        form = getIntent().getParcelableArrayListExtra("formItems");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBackToMenu:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.buttonSendForm:
                sendForm();
                break;

            case R.id.buttonScore:
                popupScore();
                break;

            case R.id.buttonInfo:
                openPopupInfo();
                break;

            default:
                break;
        }
    }

    private void openPopupInfo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_translation_form, null);
        dialogBuilder.setView(popupView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void popupScore() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.pupup_score, null);
        setPupupScoreText(popupView);
        dialogBuilder.setView(popupView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void setPupupScoreText(View view) {
        TextView textScore = view.findViewById(R.id.textScore);
        textScore.setText(score);
        TextView textPercentageScore = view.findViewById(R.id.textScorePercentage);
        String finalScore = 75.0 + " %";
        textPercentageScore.setText(finalScore);
    }

    private void sendForm() {
        Intent formActivity = new Intent(this, EmailSender.class);
        form = translationListAdapter.getFormItems();
        ZipManager zipManager = new ZipManager();
        String zipFileName = zipManager.zipFiles(form, outputPath);
        formActivity.putParcelableArrayListExtra("form", form);
        if(zipFileName != null) {
            formActivity.putExtra("zipFileName", zipFileName);
        }
        startActivity(formActivity);
    }
}
