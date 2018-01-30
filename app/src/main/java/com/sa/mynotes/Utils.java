package com.sa.mynotes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by deepak on 26/11/17.
 */

public class Utils {

    public static final String IMAGE_DIRECTORY_NAME = "NOTE_IMAGES";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 999;

    public static String getTodaysDateFormatinString(String formatType) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(new Date());
    }

    public static int getRandomNumber() {
        return new Random().nextInt(50) + 1;
    }
}
