package assignment.zunzelf.org.simpleocr.image;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;


public class Processor {
    // manual stored chaincode model arial
    // TODO : 1. Memory optimization for detecting multiple objects
    // TODO : 2. Using more efective method for classification. (possibly using vector?)
    String[] models = new String[]{ // 7-section number models
            "000000000000000000000000000000000000000000000000000000000000000000000000000000006666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666664444444444444444444444444444444444444444444444444444444444444444444444444444444222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222",
            "000000000000000000006666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666664444444444444444444222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222",
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000666666666666666666666666666666666666666666666666666666666664444444444444444444444444444444444444444444444444444444444456666666666666666666700000000000000000000000000000000000000000000000000000000000666666666666666666644444444444444444444444444444444444444444444444444444444444444444444444444444442222222222222222222222222222222222222222222222222222222222200000000000000000000000000000000000000000000000000000000000122222222222222222223444444444444444444444444444444444444444444444444444444444442222222222222222222",
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666444444444444444444444444444444444444444444444444444444444444444444444444444444422222222222222222220000000000000000000000000000000000000000000000000000000000012222222222222222222344444444444444444444444444444444444444444444444444444444444222222222222222222200000000000000000000000000000000000000000000000000000000000122222222222222222223444444444444444444444444444444444444444444444444444444444442222222222222222222",
            "000000000000000000006666666666666666666666666666666666666667000000000000000000000000000000000000000122222222222222222222222222222222222222200000000000000000006666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666644444444444444444442222222222222222222222222222222222222222222222222222222222234444444444444444444444444444444444444444444444444444444444422222222222222222222222222222222222222222222222222222222222",
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000666666666666666666644444444444444444444444444444444444444444444444444444444444566666666666666666667000000000000000000000000000000000000000000000000000000000006666666666666666666666666666666666666666666666666666666666644444444444444444444444444444444444444444444444444444444444444444444444444444442222222222222222222000000000000000000000000000000000000000000000000000000000001222222222222222222234444444444444444444444444444444444444444444444444444444444422222222222222222222222222222222222222222222222222222222222",
            "0000000000000000000000000000000000000000000000000000000000000000000000000000000066666666666666666664444444444444444444444444444444444444444444444444444444444456666666666666666666700000000000000000000000000000000000000000000000000000000000666666666666666666666666666666666666666666666666666666666664444444444444444444444444444444444444444444444444444444444444444444444444444444222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222",
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666444444444444444444422222222222222222222222222222222222222222222222222222222222222222222222222222223444444444444444444444444444444444444444566666666666666666664444444444444444444222222222222222222222222222222222222222",
            "000000000000000000000000000000000000000000000000000000000000000000000000000000006666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666664444444444444444444444444444444444444444444444444444444444444444444444444444444222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222",
            "0000000000000000000000000000000000000000000000000000000000000000000000000000000066666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666644444444444444444444444444444444444444444444444444444444444444444444444444444442222222222222222222000000000000000000000000000000000000000000000000000000000001222222222222222222234444444444444444444444444444444444444444444444444444444444422222222222222222222222222222222222222222222222222222222222"
    };
//    String[] models = new String[]{ // arial number models
//            "000000777676666666666565554444433322322222222122111",
//            "0006666666666666666666644222222222222223454522011112",
//            "000000707676665655555555700000007644444444444442211111111112122334345456544212101",
//            "000000707666665577766666554544444333220067770101122233344201012222343455564422111",
//            "000666666666666670064456666442222344444444221121121112112",
//            "0000000000065444444456667000007776766566554544444333230006777010112222233434545442221222212",
//            "00000007766442343455556670100007076766666565454444433323222222222212111",
//            "000000000000066565565656566566666442222212221212112134444444422",
//            "00000007776665567767665655544444433322222211232321211",
//            "0000007776766666666665655544444433220777001111223454444343232222211201"
//    };
    static final int white = 0xFFFFFFFF;
    static final int black = 0xFF000000;
    int obj = 0;
    static final String TAG = "imProc";
    public static Bitmap createBlackAndWhite(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final float factor = 255f;
        final float redBri = 0.2126f;
        final float greenBri = 0.2126f;
        final float blueBri = 0.0722f;

        int length = width * height;
        int[] inpixels = new int[length];
        int[] oupixels = new int[length];

        src.getPixels(inpixels, 0, width, 0, 0, width, height);

        int point = 0;
        for(int pix: inpixels){
            int R = (pix >> 16) & 0xFF;
            int G = (pix >> 8) & 0xFF;
            int B = pix & 0xFF;

            float lum = (redBri * R / factor) + (greenBri * G / factor) + (blueBri * B / factor);

            if (lum > 0.4) {
                oupixels[point] = white;
            }else{
                oupixels[point] = black;
            }
            point++;
        }
        bmOut.setPixels(oupixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
    // resize image using matrix transformation
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
        return resizedBitmap;
    }
    public String seekObjects(Bitmap bm){
        int w = bm.getWidth();
        String chainCode = "number : ";
        int h = bm.getHeight();
        int x = 0, y = 0;
        int clr;
        String ch = "";
        while(y < h){
            clr = bm.getPixel(x,y);
            if(clr != white){
                Log.d(TAG, "X : "+ x +", Y : "+y);
                ch = getChainCode(bm, x, y);
                chainCode += modelMatching(ch, models);
                break; // temporary, soon when optimized for memory will be deleted
            }
            if(x == w-1){
                x = 0;
                y += 1;
            }else
                x += 1;
        }
        return chainCode;
    }
    public String getChainCode(Bitmap bm, int initX, int initY){
        String chainCode = "0";
        boolean start = true;
        int x = initX;
        int y = initY;
        int xMax = x, xMin = x, yMax = y, yMin = y;
        int[] temp = new int[2];
        int dir = 0; //starting direction
        Log.d(TAG, " height : "+bm.getHeight());
        Log.d(TAG, " width : "+bm.getWidth());
        while(true){
            if(x == initX && y == initY && !start) break;
            bm.setPixel(x,y,Color.BLUE);
            int[] left = leftSide(dir); //get leftsides
            int[] right = rightSide(dir); //get rightsides
            int[] up = translate(x,y,dir); //get up coordinate
            int[] dirL = checkSide(bm, x, y, left);
            int[] dirR = checkSide(bm, x, y, right);
            int upV = bm.getPixel(up[0], up[1]);
            // checking per-dir
            if(dirL.length > 0){
                if(dirL.length > 1 && upV != white){
                    dir = dirL[dirL.length - 1];
                }else dir = dirL[0];
                temp = translate(x, y, dir);
            }
            else if(upV != white){
                temp = up;
            }
            else if(dirR.length > 0){
                if(dirR.length > 1 && upV != white){
                    dir = dirR[dirR.length - 1];
                }else dir = dirR[0];
                temp = translate(x, y, dir);
            }
            else {
                Log.d(TAG,"eop");
                break;
            }
            chainCode += ""+dir;
            bm.setPixel(x, y, Color.GREEN);
            x = temp[0];
            y = temp[1];
            if(x > xMax) xMax =x;
            if(x < xMin) xMin = x;
            if(y > yMax) yMax = y;
            if(y < yMin) yMin = y;
            start = false;
        }
        Log.d(TAG, chainCode);
        Log.d(TAG,xMax+","+ yMax+","+xMin+","+ yMin);
//        eraseObject(bm, xMax, yMax, xMin, yMin);
        return chainCode;
    }
    public int[] checkSide(Bitmap bm, int x, int y, int[] side){
        String gets = "";
        for (int dir : side) {
            int[] temp = translate(x, y, dir);
            if(bm.getPixel(temp[0], temp[1]) != white){
                gets += ""+dir;
            }
        }
        int[] res = stringToInts(gets);
        return res;
    }
    public int[] translate(int x, int y, int pos){
        switch (pos){
            case 1 : return new int[]{x+1, y-1};
            case 0 : return new int[]{x+1, y};
            case 7 : return new int[]{x+1, y+1};
            case 6 : return new int[]{x, y+1};
            case 5 : return new int[]{x-1, y+1};
            case 4 : return new int[]{x-1, y};
            case 3 : return new int[]{x-1, y-1};
            case 2 : return new int[]{x, y-1};
            default: return null;
        }
    }
    public int[] leftSide(int pos){
        switch (pos){
            case 0 : return new int[]{1,2,3};
            case 1 : return new int[]{2,3,4};
            case 2 : return new int[]{3,4,5};
            case 3 : return new int[]{4,5,6};
            case 4 : return new int[]{5,6,7};
            case 5 : return new int[]{6,7,0};
            case 6 : return new int[]{7,0,1};
            default : return new int[]{0,1,2};
        }
    }
    public int[] rightSide(int pos){
        switch (pos){
            case 0 : return new int[]{7,6,5};
            case 1 : return new int[]{0,7,6};
            case 2 : return new int[]{1,0,7};
            case 3 : return new int[]{2,1,0};
            case 4 : return new int[]{3,2,1};
            case 5 : return new int[]{4,3,2};
            case 6 : return new int[]{5,4,3};
            default : return new int[]{6,5,4};
        }
    }
    public void eraseObject(Bitmap bm, int x, int y, int i, int j){
        for (int b = j; b <= y; b++){
            for (int a = i; a <= x; a++)
                bm.setPixel(a, b, white);
        }
    }
    public String tweak(String inp, int size){
        String blank = "";
        for (int i = 0; i < size;i++){
            blank += ""+0;
        }
        return inp+blank;
    }
    public int[] stringToInts(String s){
        int[] res = new int[s.length()];
        char[] temp = s.toCharArray();
        for(int i = 0; i < s.length();i++)
            res[i] = Character.getNumericValue(temp[i]);
        return res;
    }
    public int modelMatching(String inp, String[] models){
        double min = 0;
        int res = 0;
        int idx = 0;
        boolean start = true;
        for (String x : models){
            String tempx = x;
            String tempI = inp;
            int gap = Math.abs(inp.length()-x.length());
            if(inp.length()>x.length()){
                tempx = tweak(x, gap);
            }
            else  tempI = tweak(inp, gap);
            double calc = 0;
            for (int i = 0; i < tempI.length(); i++){
                calc += Math.pow((Character.getNumericValue(tempI.toCharArray()[i]) - Character.getNumericValue(tempx.toCharArray()[i])), 2);
            }
            calc = calc/inp.length();
            if (start){
                min = calc;
                start = false;
            }
            Log.d(TAG, "MSE("+idx+") : "+calc);
            if (calc <= min) {
                min = calc;
                res = idx;
            }
            idx += 1;
        }
        return res;
    }

    public int modelMatching2(String inp, String[] models){
        double min = 0;
        int res = 0;
        int idx = 0;
        boolean start = true;

        return res;
    }
}
