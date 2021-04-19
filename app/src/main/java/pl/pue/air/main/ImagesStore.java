package pl.pue.air.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ImagesStore {

    private Integer[] images;

    LinkedHashMap<Integer, String> imagesMap = new LinkedHashMap<Integer, String>() {
        {
            put(R.drawable.szalik, "szalik");

        }};

    public ImagesStore() {
        images = new Integer[]{R.drawable.szalik, R.drawable.koszyk, R.drawable.mysz, R.drawable.zaba, R.drawable.lyzwy, R.drawable.czapka,
                R.drawable.rekawiczki, R.drawable.klucz, R.drawable.dzem,R.drawable.ryba, R.drawable.narty, R.drawable.ser,
                R.drawable.slimak, R.drawable.hustawka, R.drawable.mis,R.drawable.ziemniaki, R.drawable.lazienka, R.drawable.ciastka,
                R.drawable.bocian, R.drawable.lokiec, R.drawable.dzieci,R.drawable.ludzie};
    }

    public Integer getNextImage(int currentImage) {
        if (currentImage < images.length)
            return images[currentImage];
        else {
            return 0;
        }
    }

    public Integer getPreviousImage(int currentImage) {
        if(currentImage >= 0) {
            return images[currentImage];
        } else {
            return 0;
        }
    }

    public int getImageCount() {
        return images.length;
    }
}
