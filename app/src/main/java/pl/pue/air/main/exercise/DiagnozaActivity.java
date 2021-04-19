package pl.pue.air.main.exercise;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import pl.pue.air.main.ImagesStore;
import pl.pue.air.main.MainActivity;
import pl.pue.air.main.MicrosoftSpeechToText;
import pl.pue.air.main.R;
import pl.pue.air.main.WavRecorder;
import pl.pue.air.main.list.TranslationFormItem;
import pl.pue.air.main.list.view.TranslationFormList;

public class DiagnozaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView, nextButton, startButton, exitButton, infoButton, previousButton;

    int currentImage = 0;

    private String outputPath;

    private ImagesStore imagesStore;

    private String fileName;

    private SpeechRecognizer mSpeechRecognizer;

    private Intent mSpeechRecognizerIntent;

    private String currentImageName;

    private Map<String, String> results;

    String filePath;

    MicrosoftSpeechToText microsoftSpeechToText;

    WavRecorder wavRecorder;

    String currentTranslationResult;

    Handler handler = new Handler();

    private boolean isRecording = false;

    ArrayList<TranslationFormItem> formItems;

    private int correctTranslations;

    private int translationsAttempts;

    private int index;

    private TextView imageCountText;

    private int imageCount;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagesStore = new ImagesStore();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnoza);
        startButton = findViewById(R.id.buttonStart1);
        startButton.setOnClickListener(this);
        nextButton = findViewById(R.id.buttonNext1);
        nextButton.setOnClickListener(this);
        exitButton = findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(this);
        infoButton = findViewById(R.id.buttonInfo);
        infoButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        previousButton = findViewById(R.id.buttonPrevious);
        previousButton.setOnClickListener(this);
        imageCountText = findViewById(R.id.numberCount);
        results = new HashMap<>();
        formItems = new ArrayList<>();
        res = getApplicationContext().getResources();
        correctTranslations = 0;
        translationsAttempts = 0;
        index = 1;
        imageCount = 0;
        disablePreviousButton();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        startButton.setEnabled(true);
        nextDrawing();
        Toast.makeText(getApplicationContext(), "Naciśniij przycisk w lewym górnym roku, aby wyświetlić informacje!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart1:
                startRecording();
                break;

            case R.id.buttonNext1:
                //addFormItem();
                nextDrawing();
                startButton.setEnabled(true);
                break;

            case R.id.buttonExit:
                backToMenu();
                break;

            case R.id.buttonInfo:
                openPopupInfo();
                break;

            case R.id.buttonPrevious:
                //addFormItem();
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

    private void openPopupInfo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_information_diagnoza, null);
        dialogBuilder.setView(popupView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void backToMenu() {
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

    private void microsoftTranslate() {
        microsoftSpeechToText = new MicrosoftSpeechToText();
        try {
            currentTranslationResult = microsoftSpeechToText.getTransaltion(filePath);
            if (StringUtils.isNotBlank(currentTranslationResult)) {
                if (currentTranslationResult.replace(".", "").toLowerCase().equals(currentImageName)) {
                    correctTranslations++;
                }
            } else {
                currentTranslationResult = "";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void previousDrawing() {
        int imageNumber = imagesStore.getPreviousImage(currentImage - 2);
        imageView.setImageResource(imageNumber);
        currentImageName = getResources().getString(getResources().getIdentifier(getBaseContext().getResources().getResourceEntryName(imageNumber), "string", getPackageName()));
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
        if (currentImage > 1) {
            previousButton.setImageResource(R.drawable.previous_enabled);
            previousButton.setEnabled(true);
        }
    }

    private void disableNextButton() {
        nextButton.setImageResource(R.drawable.next_disabled);
        nextButton.setEnabled(false);
    }

    private void enableNextButton() {
        nextButton.setImageResource(R.drawable.next_enabled);
        nextButton.setEnabled(true);
    }

    private void enableRecordButton() {
        startButton.setImageResource(R.drawable.record);
        startButton.setEnabled(true);
    }

    private void disableRecordButton() {
        startButton.setImageResource(R.drawable.dot);
        startButton.setEnabled(false);
    }

    private void nextDrawing() {
        imageView.setImageResource(imagesStore.getNextImage(currentImage));
        int imageNumber = imagesStore.getNextImage(currentImage);
        if (imageNumber != 0) {
            currentImageName = getResources().getString(getResources().getIdentifier(getBaseContext().getResources().getResourceEntryName(imageNumber), "string", getPackageName()));
            setImageCountText(++imageCount);
            currentImage++;
            if (currentImage != 1) {
                enablePreviousButton();
            }
        } else {
            if (isRecording) {
                stopRecording();
            }
            Intent formActivity = new Intent(this, TranslationFormList.class);
            formActivity.putParcelableArrayListExtra("formItems", formItems);
            String score = correctTranslations + "/" + formItems.size();
            double ratio = 0;
            if (translationsAttempts != 0) {
                ratio = ((double) correctTranslations / (double) translationsAttempts) * 100.00;
            }
            formActivity.putExtra("finalRatio", Math.round(ratio * 100.0) / 100.0);
            formActivity.putExtra("finalScore", score);
            formActivity.putExtra("outputPath", outputPath);
            currentImage = 0;
            startActivity(formActivity);
            finish();
        }
    }

    private void addFormItem() {
        TranslationFormItem formItem = new TranslationFormItem();
        formItem.setName(currentImageName);
        formItem.setRecordingName(fileName);
        formItem.setFilePath(filePath);
        formItem.setImage(imageView);
        formItem.setIndex(index);
        if (StringUtils.isNotBlank(currentTranslationResult) && currentTranslationResult.length() >= 1) {
            formItem.setTranslationResult(currentTranslationResult.replace(".", "").toLowerCase());
        } else {
            formItem.setTranslationResult("");
        }
        formItems.add(formItem);
    }

    private void stopRecording() {
        wavRecorder.stopRecording();
        microsoftTranslate();
        enableRecordButton();
        enableNextButton();
        enablePreviousButton();
        enableExitButton();
        isRecording = false;
        translationsAttempts++;
        addFormItem();
        index++;
    }

    private void startRecording() {
        outputPath = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.GERMANY);
        fileName = "Nagranie_" + index + "." + currentImageName + "_" + simpleDateFormat.format(new Date()) + ".wav";
        wavRecorder = new WavRecorder(outputPath + "/" + fileName, outputPath);
        filePath = outputPath + "/" + fileName;
        startButton.setEnabled(false);
        wavRecorder.startRecording();
        disableRecordButton();
        disableNextButton();
        disablePreviousButton();
        disableExitButton();
        isRecording = true;
        handler.postDelayed(runnable, 3000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopRecording();
        }
    };
}
