package pl.pue.air.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import pl.pue.air.main.list.TranslationFormItem;

public class DeleteManager {

    public void deleteFiles(ArrayList<TranslationFormItem> items, String path) {
        int size = items.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                File file = new File(items.get(0).getFilePath());
                file.delete();
                items.remove(0);
            }
        }
    }

}
