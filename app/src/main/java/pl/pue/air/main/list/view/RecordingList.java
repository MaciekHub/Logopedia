package pl.pue.air.main.list.view;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pl.pue.air.main.EmailSender;
import pl.pue.air.main.R;
import pl.pue.air.main.list.adapters.RecordingListAdapter;


public class RecordingList extends AppCompatActivity implements View.OnClickListener, RecordingListAdapter.OnItemListClickListener {

    private RecyclerView recordingList;

    // private File[] files;

    private ArrayList<File> files;

    private ArrayList<String> filesToSend;

    private ArrayList<Integer> filesToDelete;

    private Map<Integer, String> actionFiles;

    RecordingListAdapter recordingListAdapter;

    private Button buttonSend, buttonDeleteAll;

    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_list);
        recordingList = findViewById(R.id.recordingList);
        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);
        buttonDeleteAll = findViewById(R.id.buttonSelectAll);
        buttonDeleteAll.setOnClickListener(this);
        actionFiles = new HashMap<>();

        addFiles();

        recordingListAdapter = new RecordingListAdapter(this, files, this);
        recordingList.setHasFixedSize(true);
        recordingList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recordingList.setAdapter(recordingListAdapter);

    }

    private void addFiles() {
        String path = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        File directory = new File(path);
        files = new ArrayList<>();
        files.addAll(Arrays.asList(Objects.requireNonNull(directory.listFiles())));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonSend:
                Intent intent1 = new Intent(this, EmailSender.class);
                mapToList();
                intent1.putExtra("filesToSend", filesToSend);
                startActivity(intent1);
                break;

            case R.id.buttonSelectAll:
                deleteAll();
                break;

            default:
                break;
        }
    }

    private void deleteAll() {
        String path = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        int size = files.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                File file = new File(path, files.get(0).getName());
                file.delete();
                files.remove(0);
            }
            recordingListAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onItemClick(File file, int position, ImageView recordingImage) {
        if (!actionFiles.containsKey(position)) {
            actionFiles.put(position, file.getName());
            //filesToSend.add(file.getName());
            //filesToDelete.add(position);
            recordingImage.setImageResource(R.drawable.tick);
        } else {
            actionFiles.remove(position);
            //filesToSend.remove(file.getName());
            //filesToDelete.remove(getIndexToRemove(position));
            recordingImage.setImageResource(R.drawable.empty_checkbox);
        }
    }

    @Override
    public void onDeleteClick(int position, String fileName) {
        removeItem(position, fileName);
    }

    @Override
    public void onPlayClick(int position, String fileName) {
        playRecording(fileName);
    }

    private void removeItem(int position, String fileName) {
        String path = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        actionFiles.remove(position);
        recordingListAdapter.notifyItemRemoved(position);
        File file = new File(path, fileName);
        file.delete();
        files.remove(position);
    }

    private int getIndexToRemove(int position) {
        int index = 0;
        for (int el : filesToDelete) {
            if (el == position) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void mapToList() {
        filesToSend = new ArrayList<>();
        if (!actionFiles.isEmpty())
            for (Map.Entry<Integer, String> el : actionFiles.entrySet()) {
                filesToSend.add(el.getValue());
            }
    }

    private void playRecording(String filePath) {
        if (!isPlaying) {
            isPlaying = true;
            String path = Objects.requireNonNull(this.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path + "/" + filePath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    isPlaying = false;
                }
            });
        }
    }

}

