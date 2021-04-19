package pl.pue.air.main;

import android.os.Environment;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SessionEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionCanceledEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamContainerFormat;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.util.EventHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MicrosoftSpeechToText {

    private static String speechSubscriptionKey = "91247003854742a985a1d411237bc5ec";

    private static String serviceRegion = "northeurope";

    private SpeechConfig speechConfig;

    private SpeechSynthesizer synthesizer;

    AudioConfig audioConfig;

    SpeechTranslationConfig speechTranslationConfig;

    public MicrosoftSpeechToText() {
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
    }

    public String getTransaltion(String fileName) throws InterruptedException, ExecutionException, IOException {
        String resultText = "";
        try {
            assert (speechConfig != null);
            speechConfig.setSpeechRecognitionLanguage("pl-PL");
            PullAudioInputStream pullAudio = AudioInputStream.createPullStream(new BinaryAudioStreamReader(fileName),
                    AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.MP3));
            AudioConfig ac = AudioConfig.fromWavFileOutput(fileName);
            //speechConfig.enableDictation();
            SpeechRecognizer reco = new SpeechRecognizer(speechConfig, ac);
            assert (reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert (task != null);

            SpeechRecognitionResult result = task.get();
            assert (result != null);
            resultText = result.getText();
            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert (false);
        }
        return resultText;
    }

    private void setEvents(SpeechRecognizer recognizer) {
        recognizer.recognizing.addEventListener(new EventHandler<SpeechRecognitionEventArgs>() {
            @Override
            public void onEvent(Object s, SpeechRecognitionEventArgs e) {
                System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
            }
        });

        recognizer.recognized.addEventListener(new EventHandler<SpeechRecognitionEventArgs>() {
            @Override
            public void onEvent(Object s, SpeechRecognitionEventArgs e) {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                    System.out.println("NOMATCH: Speech could not be recognized.");
                }
            }
        });

        recognizer.canceled.addEventListener(new EventHandler<SpeechRecognitionCanceledEventArgs>() {
            @Override
            public void onEvent(Object s, SpeechRecognitionCanceledEventArgs e) {
                System.out.println("CANCELED: Reason=" + e.getReason());

                if (e.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
            }
        });

        recognizer.sessionStopped.addEventListener(new EventHandler<SessionEventArgs>() {
            @Override
            public void onEvent(Object s, SessionEventArgs e) {
                System.out.println("\n    Session stopped event.");
            }
        });
    }

}
