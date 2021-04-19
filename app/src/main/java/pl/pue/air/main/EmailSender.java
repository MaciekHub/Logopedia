package pl.pue.air.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import pl.pue.air.main.list.TranslationFormItem;

public class EmailSender extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail;

    private EditText etSubject;

    private EditText etMessage;

    private Button buttonSend, buttonMenu;

    private String email;

    private String subject;

    private String message;

    private String zipFileName;

    private ArrayList<String> filesToSend;

    private ArrayList<TranslationFormItem> form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_email);
        etEmail = findViewById(R.id.etTo);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        buttonSend = findViewById(R.id.buttonSendEmail);
        buttonMenu = findViewById(R.id.buttonBackToMenu);
        filesToSend = new ArrayList<>();
        filesToSend = getIntent().getStringArrayListExtra("filesToSend");
        form = new ArrayList<>();
        form = getIntent().getParcelableArrayListExtra("form");
        zipFileName = getIntent().getStringExtra("zipFileName");
        buttonSend.setOnClickListener(this);
        buttonMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonSendEmail:
                sendEmail();
                break;

            case R.id.buttonBackToMenu:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;

            default:
                break;
        }
    }

    private void sendEmail() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            email = etEmail.getText().toString();
            subject = etSubject.getText().toString();
            message = etMessage.getText().toString();
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            ArrayList<Uri> uris = new ArrayList<>();
            String path = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
            if (filesToSend != null && !filesToSend.isEmpty()) {
                for (String fileName : filesToSend) {
                    File file = new File(path, fileName);
                    uris.add(Uri.fromFile(file));
                }
            }
            if(form != null && !form.isEmpty() && zipFileName != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for(TranslationFormItem item: form) {
                    stringBuilder.append(item.getName()).append(": ").append(item.getTranslationResult()).append("\n");
                    stringBuilder.append("komentarz: ").append(item.getComment()).append("\n").append("\n");
                }
                File zipFile = new File(zipFileName);
                uris.add(Uri.fromFile(zipFile));
                message = message + "\n" + "\n" + "FORMULARZ: \n" + stringBuilder.toString();
                File file = writeToFile(message, path);
                uris.add(Uri.fromFile(file));
                DeleteManager deleteManager = new DeleteManager();
                deleteManager.deleteFiles(form, path);
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent, "Wysyłanie..."));
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed try again: "+ t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public File writeToFile(String data, String filePath)
    {
        // Get the directory for the user's_letter public pictures directory.
        final File path = new File(filePath);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm", Locale.GERMANY);
        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't_letter exit
            path.mkdirs();
        }

        final File file = new File(path, "formularz_" + simpleDateFormat.format(new Date()) + ".txt");

        // Save your stream, don't_letter forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return file;
    }
}
