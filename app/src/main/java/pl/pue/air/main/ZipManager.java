package pl.pue.air.main;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.pue.air.main.list.TranslationFormItem;

public class ZipManager {

    private static int BUFFER = 6 * 1024;

    public String zipFiles(ArrayList<TranslationFormItem> items, String path) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_mm", Locale.GERMANY);
        String zipFileName = path + "/" + "Diagnoza_" + "_" + simpleDateFormat.format(new Date()) + ".zip";
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];
            for (TranslationFormItem item: items) {
                String file = item.getFilePath();
                Log.v("Compress", "Adding: " + item.getName());
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
            return zipFileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
