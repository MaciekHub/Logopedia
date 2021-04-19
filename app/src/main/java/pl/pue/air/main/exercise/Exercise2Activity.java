package pl.pue.air.main.exercise;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import pl.pue.air.main.Exercise3EndingActivity;
import pl.pue.air.main.ImagesStore;
import pl.pue.air.main.MainActivity;
import pl.pue.air.main.R;
import pl.pue.air.main.WavRecorder;
import pl.pue.air.main.list.TranslationFormItem;

public class Exercise2Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView, nextButton, startButton, speechButton, playButton, exitButton, infoButton, previousButton;

    int currentImage = 0;

    private String outputPath;

    private ImagesStore imagesStore;

    WavRecorder wavRecorder;

    private String currentImageName;

    private TextToSpeech textToSpeech;

    private Locale[] languages;

    String filePath;

    String currentTranslationResult;

    Handler handler = new Handler();

    private boolean isRecording = false;

    ArrayList<TranslationFormItem> formItems;

    MediaPlayer mediaPlayer;

    private TextView imageCountText;

    private int imageCount;

    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagesStore = new ImagesStore();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);
        startButton = findViewById(R.id.buttonStart1);
        startButton.setOnClickListener(this);
        nextButton = findViewById(R.id.buttonNext1);
        nextButton.setOnClickListener(this);
        exitButton = findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(this);
        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        infoButton = findViewById(R.id.buttonInfo);
        infoButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        speechButton = findViewById(R.id.buttonSpeech);
        speechButton.setOnClickListener(this);
        previousButton = findViewById(R.id.buttonPrevious);
        previousButton.setOnClickListener(this);
        formItems = new ArrayList<>();
        imageCountText = findViewById(R.id.numberCount);
        imageCount = 0;

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        initSupportedLanguagesLollipop();
        setLanguage();

        disablePlayButton();
        enableRecordButton();
        disablePreviousButton();
        nextDrawing();
        Toast.makeText(getApplicationContext(), "Naciśniij przycisk w lewym górnym roku, aby wyświetlić informacje!", Toast.LENGTH_LONG).show();
    }

    private void setLanguage() {
        Locale language = null;
        for (Locale locale : languages) {
            if (locale.getDisplayName().startsWith("polski"))
                language = locale;
        }
        textToSpeech.setLanguage(language);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart1:
                startRecording();
                break;

            case R.id.buttonNext1:
                addFormItem();
                nextDrawing();
/*                enableRecordButton();
                disablePlayButton();*/
                break;

            case R.id.buttonSpeech:
                speech();
                break;

            case R.id.playButton:
                playRecording();
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

    private void openPopupInfo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_information_exc2, null);
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

    private void playRecording() {
        if (!isPlaying) {
            isPlaying = true;
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            disablePlayButton();
            startButton.setEnabled(false);
            disableNextButton();
            disablePreviousButton();
            disableExitButton();


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    mediaPlayer.release();
                    enableNextButton();
                    enablePreviousButton();
                    enablePlayButton();
                    enableExitButton();
                    startButton.setEnabled(true);
                }
            });
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

    private void setImageCountText(int index) {
        String temp = index + "/" + imagesStore.getImageCount();
        imageCountText.setText(temp);
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

    private void disableExitButton() {
        exitButton.setImageResource(R.drawable.exit_disable);
        exitButton.setEnabled(false);
    }

    private void enableExitButton() {
        exitButton.setImageResource(R.drawable.close);
        exitButton.setEnabled(true);
    }

    private void disableNextButton() {
        nextButton.setImageResource(R.drawable.next_disabled);
        nextButton.setEnabled(false);
    }

    private void enableNextButton() {
        nextButton.setImageResource(R.drawable.next_enabled);
        nextButton.setEnabled(true);
    }

    private void speech() {
        textToSpeech.speak(currentImageName, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void initSupportedLanguagesLollipop() {
        languages = Locale.getAvailableLocales();
    }

    private void enablePlayButton() {
        playButton.setImageResource(R.drawable.play_button_enabled);
        playButton.setEnabled(true);
    }

    private void disablePlayButton() {
        playButton.setImageResource(R.drawable.play_button_disabled);
        playButton.setEnabled(false);
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
            enableRecordButton();
            disablePlayButton();
            if (currentImage != 1) {
                enablePreviousButton();
            }
        } else {
            Intent endingActivity = new Intent(this, Exercise3EndingActivity.class);
            currentImage = 0;
            startActivity(endingActivity);
            finish();
        }
    }

    private void addFormItem() {
        TranslationFormItem formItem = new TranslationFormItem();
        formItem.setName(currentImageName);
        formItem.setRecordingName(currentTranslationResult);
        formItem.setImage(imageView);
        formItems.add(formItem);
    }

    private void stopRecording() {
        wavRecorder.stopRecording();
        isRecording = false;
    }

    private void startRecording() {
        outputPath = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.GERMANY);
        String fileNameRecording = "Nagranie_" + currentImageName + "_" + simpleDateFormat.format(new Date()) + ".wav";
        wavRecorder = new WavRecorder(outputPath + "/" + fileNameRecording, outputPath);
        filePath = outputPath + "/" + fileNameRecording;
        disableNextButton();
        disablePreviousButton();
        disablePlayButton();
        disableRecordButton();
        disableExitButton();
        wavRecorder.startRecording();
        disableRecordButton();
        isRecording = true;
        handler.postDelayed(runnable, 3000);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopRecording();
            enableNextButton();
            enablePreviousButton();
            enablePlayButton();
            enableRecordButton();
            enableExitButton();
        }
    };
}
