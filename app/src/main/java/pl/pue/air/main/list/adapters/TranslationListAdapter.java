package pl.pue.air.main.list.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import pl.pue.air.main.MainActivity;
import pl.pue.air.main.R;
import pl.pue.air.main.list.TranslationFormItem;

public class TranslationListAdapter extends RecyclerView.Adapter<TranslationListAdapter.TranslationListHolder> {

    private ArrayList<TranslationFormItem> formItems;

    private Context context;

    private OnItemListClickListener onItemClickListener;

    public TranslationListAdapter(ArrayList<TranslationFormItem> formItems, Context context) {
        this.formItems = formItems;
        this.context = context;
    }

    public interface OnItemListClickListener {
        void onItemClick(File file, int position, ImageView imageView);
    }

    @NonNull
    @Override
    public TranslationListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_translation, parent, false);
        return new TranslationListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TranslationListHolder holder, final int position) {
        final TranslationFormItem formItem = formItems.get(position);
        holder.setDetails(formItem);
        holder.translationResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formItems.get(position).setTranslationResult(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        holder.comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formItems.get(position).setComment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return formItems.size();
    }

    public ArrayList<TranslationFormItem> getFormItems() {
        return formItems;
    }

    public class TranslationListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView translationImage;

        private TextView translationName;

        private EditText translationResult;

        private EditText comment;

        MediaPlayer mediaPlayer;

        String filePath;

        private boolean isPlaying = false;

        public TranslationListHolder(@NonNull View itemView) {
            super(itemView);

            translationImage = itemView.findViewById(R.id.translationImage);
            translationName = itemView.findViewById(R.id.translationName);
            translationResult = itemView.findViewById(R.id.translationResult);
            comment = itemView.findViewById(R.id.comment);
            translationImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void setDetails(TranslationFormItem formItem) {
            String name = formItem.getIndex() + ". " + formItem.getName();
            translationName.setText(name);
            translationResult.setText(formItem.getTranslationResult());
            if (formItem.getTranslationResult().equals(formItem.getName())) {
                comment.setHint("(poprawne) Wstaw komentarz...");
            } else {
                comment.setHint("(niepoprawne) Wstaw komentarz...");
            }
            filePath = formItem.getFilePath();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.translationImage) {
                playRecording();
            }
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

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isPlaying = false;
                        mediaPlayer.release();
                    }
                });
            }
        }
    }

}
