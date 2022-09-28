package fragment.util;

import com.example.myapplication.R;

import java.util.Random;

public class ColorUtils {
    public static int getColor(){
        int[] c = new int[]{R.color.color1,R.color.purple_200,R.color.purple_500,R.color.purple_700,
                R.color.teal_200,R.color.teal_700,R.color.black};
        return c[new Random().nextInt(6)];
    }
}
