package pl.pue.air.main.list.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import pl.pue.air.main.R;

public class RecordingListAdapter extends RecyclerView.Adapter<RecordingListAdapter.RecordingViewHolder> {

    private OnItemListClickListener onItemClickListener;

    void setOnItemClickListener(OnItemListClickListener listener){
        onItemClickListener = listener;
    }

    public interface OnItemListClickListener {
        void onItemClick(File file, int position, ImageView imageView);
        void onDeleteClick(int position, String fileName);
        void onPlayClick(int position, String fileName);
    }

    //private File[] files;
    private ArrayList<File> files;
    private Context context;

    public RecordingListAdapter(Context ctx, ArrayList<File> files, OnItemListClickListener onItemClickListener) {
        this.files = files;
        this.context = ctx;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recording, parent, false);
        return new RecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
/*        String path = Objects.requireNonNull(context.getApplicationContext().getExternalFilesDir("/")).getAbsolutePath();
        File directory = new File(path);
        files = directory.listFiles();*/
        String recordingTitle = files.get(position).getName().replace("Nagranie_", "");
        holder.filePath = files.get(position).getName();
        holder.recordingTitle.setText(recordingTitle);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder {

        private ImageView recordingImage;

        private TextView recordingTitle;

        private ImageView deleteImage;

        String filePath;

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);

            recordingImage = itemView.findViewById(R.id.recordingImage);
            recordingTitle = itemView.findViewById(R.id.recordingTitle);
            deleteImage = itemView.findViewById(R.id.deleteImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        onItemClickListener.onPlayClick(getAdapterPosition(), filePath);
                    }
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        onItemClickListener.onDeleteClick(getAdapterPosition(), filePath);
                    }
                }
            });

            recordingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(files.get(getAdapterPosition()), getAdapterPosition(), recordingImage);
                    }
                }
            });
        }
/*
        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(files[getAdapterPosition()], getAdapterPosition(), recordingImage);
            if (v.getId() == R.id.deleteImage) {
                onItemClickListener.onDeleteClick(getAdapterPosition());
        }
        }*/
    }
}
