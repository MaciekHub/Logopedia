package pl.pue.air.main.exercise;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pl.pue.air.main.Exercise1EndingActivity;
import pl.pue.air.main.ImagesStore;
import pl.pue.air.main.MainActivity;
import pl.pue.air.main.R;

public class Exercise1Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView, nextButton, startStopButton, exitButton, infoButton, previousButton;

    int currentImage = 0;

    private MediaRecorder myAudioRecorder;

    private String outputPath;

    private ImagesStore imagesStore;

    private String fileName;

    private SpeechRecognizer mSpeechRecognizer;

    private Intent mSpeechRecognizerIntent;

    private String currentImageName;

    private Map<String, String> results;

    private boolean isRecording = false;

    private TextView imageCountText;

    private int imageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagesStore = new ImagesStore();
        setContentView(R.layout.activity_exercise1);
        startStopButton = findViewById(R.id.buttonStart1);
        startStopButton.setOnClickListener(this);
        nextButton = findViewById(R.id.buttonNext1);
        nextButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        exitButton = findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(this);
        infoButton = findViewById(R.id.buttonInfo);
        infoButton.setOnClickListener(this);
        previousButton = findViewById(R.id.buttonPrevious);
        previousButton.setOnClickListener(this);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        imageCountText = findViewById(R.id.numberCount);
        imageCount = 0;
        results = new HashMap<>();
        startStopButton.setEnabled(true);
        disablePreviousButton();
        nextDrawing();
        Toast.makeText(getApplicationContext(), "Naciśniij przycisk w lewym górnym roku, aby wyświetlić informacje!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart1:
                record();
                break;

            case R.id.buttonNext1:
                nextDrawing();
                break;

            case R.id.buttonExit:
                backToMenu();
                break;

            case R.id.buttonInfo:
                openPopupInfo();
                break;

            case R.id.buttonPrevious:
                previousDrawing();
                break;

            default:
                break;
        }
    }

    private void setImageCountText(int index) {
        String temp = index + "/" + imagesStore.getImageCount();
        imageCountText.setText(temp);
    }

    private void previousDrawing() {
        int imageNumber = imagesStore.getPreviousImage(currentImage - 2);
        imageView.setImageResource(imageNumber);
        currentImageName = getBaseContext().getResources().getResourceEntryName(imageNumber);
        setImageCountText(--imageCount);
        currentImage--;
        if (currentImage <= 1) {
            disablePreviousButton();
        }
    }

    private void disablePreviousButton() {
        previousButton.setImageResource(R.drawable.previous_disabled);
        previousButton.setEnabled(false);
    }

    private void enablePreviousButton() {
        previousButton.setImageResource(R.drawable.previous_enabled);
        previousButton.setEnabled(true);
    }

    private void openPopupInfo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_information_exc1, null);
        dialogBuilder.setView(popupView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void backToMenu() {
        if (isRecording) {
            stopRecording();
            isRecording = false;
        }
        Intent mainActivity = new Intent(this, MainActivity.class);
        currentImage = 0;
        startActivity(mainActivity);
        finish();
    }

    private void disableExitButton() {
        exitButton.setImageResource(R.drawable.exit_disable);
        exitButton.setEnabled(false);
    }

    private void enableExitButton() {
        exitButton.setImageResource(R.drawable.close);
        exitButton.setEnabled(true);
    }

    private void nextDrawing() {
        int imageNumber = imagesStore.getNextImage(currentImage);
        imageView.setImageResource(imageNumber);
        if (imageNumber != 0) {
            currentImageName = getBaseContext().getResources().getResourceEntryName(imageNumber);
            setImageCountText(++imageCount);
            currentImage++;
            if (currentImage != 1) {
                enablePreviousButton();
            }
        } else {
            Intent endingActivity = new Intent(this, Exercise1EndingActivity.class);
            if (isRecording) {
                stopRecording();
                endingActivity.putExtra("fileName", fileName);
            }
            currentImage = 0;
            startActivity(endingActivity);
            finish();
        }
    }

    private void stopRecording() {
        Toast.makeText(getApplicationContext(), "Zakonczono nagrywanie", Toast.LENGTH_LONG).show();
        startStopButton.setEnabled(true);
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        generateResultsFile(this, fileName);
        startStopButton.setImageResource(R.drawable.record);
        isRecording = false;
    }

    private void startRecording() {
        outputPath = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.GERMANY);
        fileName = "Ciagle_nagranie_" + simpleDateFormat.format(new Date()) + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setOutputFile(outputPath + "/" + fileName);

        //stopButton.setEnabled(true);
        //startStopButton.setEnabled(false);
        try {
            this.myAudioRecorder.prepare();
            this.myAudioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startStopButton.setImageResource(R.drawable.stop);
        isRecording = true;
        Toast.makeText(getApplicationContext(), "Rozpoczeto nagrywanie", Toast.LENGTH_LONG).show();
    }

    private void record() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void generateResultsFile(Context context, String sFileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for (Map.Entry<String, String> result : results.entrySet()) {
                writer.append(result.getKey() + " - " + result.getValue());
                writer.append("\n");
            }
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
