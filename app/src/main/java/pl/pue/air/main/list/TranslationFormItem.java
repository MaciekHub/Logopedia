package pl.pue.air.main.list;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;

public class TranslationFormItem implements Parcelable {

    private String name;

    private String translationResult;

    private String recordingName;

    private String filePath;

    private String comment;

    private ImageView image;

    private int index;

    public TranslationFormItem() {
    }

    public TranslationFormItem(Parcel in) {
        name = in.readString();
        translationResult = in.readString();
        recordingName = in.readString();
        filePath = in.readString();
        comment = in.readString();
        index = in.readInt();
    }

    public static final Creator<TranslationFormItem> CREATOR = new Creator<TranslationFormItem>() {
        @Override
        public TranslationFormItem createFromParcel(Parcel in) {
            return new TranslationFormItem(in);
        }

        @Override
        public TranslationFormItem[] newArray(int size) {
            return new TranslationFormItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getTranslationResult() {
        return translationResult;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public ImageView getImage() {
        return image;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTranslationResult(String translationResult) {
        this.translationResult = translationResult;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }


    public void setImage(ImageView image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(translationResult);
        dest.writeString(recordingName);
        dest.writeString(filePath);
        dest.writeString(comment);
        dest.writeInt(index);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
