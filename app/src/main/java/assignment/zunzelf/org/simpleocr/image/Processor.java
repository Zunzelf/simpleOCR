package assignment.zunzelf.org.simpleocr.image;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Processor {

    static final int white = 0xFFFFFFFF;
    static final int black = 0xFF000000;
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
        bm.recycle();
        return resizedBitmap;
    }
    public void seekObjects(Bitmap bm, Bitmap bm2){
        int w = bm.getWidth();
        int h = bm.getHeight();
        int x = 0, y = 0;
        int clr;
        while(y < h){
            clr = bm.getPixel(x,y);
            if(clr != white){
                Log.d(TAG, "X : "+ x +", Y : "+y);
                getChainCode(bm, x, y, bm2);
                break;

            }
            if(x == w-1){
                x = 0;
                y += 1;
            }else
                x += 1;
        }
    }
    public void getChainCode(Bitmap bm, int initX, int initY, Bitmap bm2){
        String chainCode = "0";
        boolean start = true;
        int x = initX;
        int y = initY;
        int xMax = x, xMin = x, yMax = y, yMin = y;
        int[] temp = new int[2];
        int loop = 0;
        int dir = 0; //starting direction
        while(true){
            if(x == initX && y == initY && !start) break;
            bm.setPixel(x,y,Color.BLUE);
            int[] left = leftSide(dir); //get leftsides
            int[] right = rightSide(dir); //get rightsides
            int[] up = translate(x,y,dir); //get up coordinate
            List<Integer> dirL = checkSide(bm, x, y, left);
            List<Integer> dirR = checkSide(bm, x, y, right);
            int upV = bm.getPixel(up[0], up[1]);
            // checking per-dir
            if(dirL.size() > 0){
                Log.d(TAG, "L");
                if(dirL.size() > 1 && upV != white){
                    dir = dirL.get(dirL.size() - 1);
                }else dir = dirL.get(0);
                temp = translate(x, y, dir);
            }
            else if(upV != white){
                Log.d(TAG, "U");
                temp = up;
            }
            else if(dirR.size() > 0){
                Log.d(TAG, "R");
                if(dirR.size() > 1 && upV != white){
                    dir = dirR.get(dirR.size() - 1);
                }else dir = dirR.get(0);
                temp = translate(x, y, dir);
            }
            else {
                System.out.println("eop");
                break;
            }
            chainCode += ""+dir;
            x = temp[0];
            y = temp[1];
            start = false;
            Log.d(TAG, chainCode);
        }
    }
    public List<Integer> checkSide(Bitmap bm, int x, int y, int[] side){
        List<Integer> res = new ArrayList<Integer>();
        for (int dir : side) {
            int[] temp = translate(x, y, dir);
            if(bm.getPixel(temp[0], temp[1]) != white){
                res.add(dir);
            }
        }
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
        for (int b = y; b < j; b++){
            for (int a = x; a < i; a++)
                bm.setPixel(a, b, white);
        }
    }
}
