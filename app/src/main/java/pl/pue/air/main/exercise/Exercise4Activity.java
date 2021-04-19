package pl.pue.air.main.exercise;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
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
import pl.pue.air.main.MicrosoftSpeechToText;
import pl.pue.air.main.R;
import pl.pue.air.main.WavRecorder;
import pl.pue.air.main.list.TranslationFormItem;
import pl.pue.air.main.list.view.TranslationFormList;

public class Exercise4Activity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageView, nextButton, startButton, stopButton, speechButton;

    int currentImage = 0;

    private MediaRecorder myAudioRecorder;

    private String outputPath;

    private ImagesStore imagesStore;

    private String fileName;

    private SpeechRecognizer mSpeechRecognizer;

    private Intent mSpeechRecognizerIntent;

    private EditText translatedText;

    private ImageView buttonTranslate;

    private String currentImageName;

    private TextToSpeech textToSpeech;

    private Locale[] languages;

    private String lastTranslatedResult;

    private Map<String, String> results;

    String filePath;

    MicrosoftSpeechToText microsoftSpeechToText;

    WavRecorder wavRecorder;

    ArrayList<String> translationResults;

    String currentTranslationResult;

    Handler handler = new Handler();

    private boolean isRecording = false;

    ArrayList<TranslationFormItem> formItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagesStore = new ImagesStore();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);
        startButton = findViewById(R.id.buttonStart1);
        startButton.setOnClickListener(this);
        stopButton = findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(this);
        nextButton = findViewById(R.id.buttonNext1);
        nextButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
     //   translatedText = findViewById(R.id.transaltedText);
     //   buttonTranslate = findViewById(R.id.buttonTranslate);
        speechButton = findViewById(R.id.buttonSpeech);
        speechButton.setOnClickListener(this);
        results = new HashMap<>();
        formItems = new ArrayList<>();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        initSupportedLanguagesLollipop();
        setLanguage();

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        nextDrawing();
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null) {
                    lastTranslatedResult = matches.get(0);
                    translatedText.setText(lastTranslatedResult);
                }
                buttonTranslate.setImageResource(R.drawable.microphone);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

/*        findViewById(R.id.buttonTranslate).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        translatedText.setHint("Wynik...");
                        buttonTranslate.setImageResource(R.drawable.microphone);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        buttonTranslate.setImageResource(R.drawable.microphone_on);
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        translatedText.setText("");
                        translatedText.setHint("...");
                        break;
                }
                return false;
            }
        });*/
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

            case R.id.buttonStop:
                //wavRecorder.stopRecording();
                break;

            case R.id.buttonNext1:
                addFormItem();
                nextDrawing();
                startButton.setEnabled(true);
                break;

            case R.id.buttonSpeech:
                speech();
                break;

            default:
                break;
        }
    }

    private void microsoftTranslate() {
        microsoftSpeechToText = new MicrosoftSpeechToText();
        try {
            String result = microsoftSpeechToText.getTransaltion(filePath);
            currentTranslationResult = result;
            translationResults.add(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewResult() {
        results.put(currentImageName, lastTranslatedResult);
    }

    private void speech() {
        textToSpeech.speak(currentImageName, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void initSupportedLanguagesLollipop() {
        languages = Locale.getAvailableLocales();
    }

    private void disableNextButton() {
        nextButton.setImageResource(R.drawable.next_disabled);
        nextButton.setEnabled(false);
    }

    private void enableNextButton() {
        nextButton.setImageResource(R.drawable.next_enabled);
        nextButton.setEnabled(true);
    }

    private void stopListening() {
        mSpeechRecognizer.stopListening();
        translatedText.setHint("You will see input here");
    }

    private void startListening() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        translatedText.setText("");
        translatedText.setHint("Listening...");
    }

    private void nextDrawing() {
        imageView.setImageResource(imagesStore.getNextImage(currentImage));
        int imageNumber = imagesStore.getNextImage(currentImage);
        if (imageNumber != 0) {
            currentImageName = getBaseContext().getResources().getResourceEntryName(imageNumber);
            currentImage++;
            disableNextButton();
        } else {
            Intent formActivity = new Intent(this, TranslationFormList.class);
            if (isRecording) {
                stopRecording();
            }
            formActivity.putParcelableArrayListExtra("formItems", formItems);
            currentImage = 0;
            startActivity(formActivity);
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
        stopButton.setEnabled(false);
        //microsoftTranslate();
        addNewResult();
        enableNextButton();
        isRecording = false;
    }

    private void startRecording() {
/*        outputPath = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.GERMANY);
        fileName = "Nagranie_" + simpleDateFormat.format(new Date());
        String fileNameRecording = "Nagranie_" + simpleDateFormat.format(new Date()) + ".mp3";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        filePath = outputPath + "/" + fileNameRecording;
        myAudioRecorder.setOutputFile(filePath);
        stopButton.setEnabled(true);
        startStopButton.setEnabled(false);
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Rozpoczeto nagrywanie", Toast.LENGTH_LONG).show();*/
        outputPath = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.GERMANY);
        String fileNameRecording = "Nagranie_" + simpleDateFormat.format(new Date()) + ".wav";
        wavRecorder = new WavRecorder(outputPath + "/" + fileNameRecording, outputPath);
        filePath = outputPath + "/" + fileNameRecording;
        stopButton.setEnabled(true);
        startButton.setEnabled(false);
        wavRecorder.startRecording();
        Toast.makeText(getApplicationContext(), "Rozpoczeto nagrywanie", Toast.LENGTH_LONG).show();
        isRecording = true;
        handler.postDelayed(runnable, 100);
    }

    public void generateResultsFile(Context context, String sFileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for(Map.Entry<String, String> result : results.entrySet()) {
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopRecording();
        }
    };
}
