package in.prashant.imagetransformer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by prashant on 20/06/17.
 */



public class Blobby {
    private static final int MAX_QUALITY = 100;
    private static final int DEFAULT_COMPRESS_QUALITY = 80;


    private static int getInSampleSize(BitmapFactory.Options options, int w, int h) {
        int inSampleSize = 1;

        if (options.outHeight > h || options.outWidth > w) {
            int halfHeight = options.outHeight / 2;
            int halfWidth = options.outWidth / 2;

            while ((halfHeight / inSampleSize) > h && (halfWidth / inSampleSize) > w) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    protected static Bitmap getSmallBitmap(String filePath, int w, int h) {
        File tempFile = new File(filePath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = getInSampleSize(options, w, h);

        return BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
    }


    protected static Bitmap rescale(String filePath, int destWidth, int destHeight, boolean keepAspect) {
        Bitmap resultBitmap = null;

        if (null != filePath) {
        	Bitmap tempBitmap = Blobby.getSmallBitmap(filePath, destWidth, destHeight);
        	
        	int photoW = tempBitmap.getWidth();
            int photoH = tempBitmap.getHeight();
            int finalWidth = 0, finalHeight = 0;
            float ratioH = (float) photoH / photoW;
            
        	if ( (destWidth == photoW) && (destHeight == photoH) ) {
        		return tempBitmap;
        		
        	} else {
        		if (keepAspect) {
                    if (photoH > photoW) {		// portrait mode pics
                        finalHeight = (photoH > destHeight) ? destHeight : photoH;
                        finalWidth = (int) (finalHeight / ratioH);

                    } else {		// landscape mode pics
                        finalWidth = (photoW > destHeight) ? destHeight : photoW;
                        finalHeight = (int) (finalWidth * ratioH);
                    }

                    resultBitmap = Bitmap.createScaledBitmap(tempBitmap, finalWidth, finalHeight, true);

                } else {
                    resultBitmap = Bitmap.createScaledBitmap(tempBitmap, destWidth, destHeight, true);
                }
        		
        		if (tempBitmap != null && !tempBitmap.isRecycled()) {
                    tempBitmap.recycle();
                }
        	}
        }

        return resultBitmap;
    }


    private static boolean processImage(String filePath, String destFile, int destWidth, int destHeight, boolean keepAspect, int quality) {
        Bitmap scaledBitmap = rescale(filePath, destWidth, destHeight, keepAspect);

        if (scaledBitmap != null) {        	
            try {
                FileOutputStream fos = new FileOutputStream(destFile);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.close();

                if (!scaledBitmap.isRecycled()) {
                    scaledBitmap.recycle();
                }

                return true;

            } catch (Exception e) {
                Log.e("Compress error - ", e.getMessage());
                return false;
            }

        } else {
            return false;
        }
    }


    protected static boolean rescaleAndCompressToFile(String filePath, String destFile, int destWidth, int destHeight, boolean keepAspect, int quality) {
        return processImage(filePath, destFile, destWidth, destHeight, keepAspect, quality);
    }

    protected static boolean rescaleAndCompressToFile(String sourceFile, int destWidth, int destHeight, boolean keepAspect, int quality) {
        return rescaleAndCompressToFile(sourceFile, sourceFile, destWidth, destHeight, keepAspect, quality);
    }

    protected static boolean rescaleAndCompressToFile(String sourceFile, int destWidth, int destHeight, boolean keepAspect) {
        return rescaleAndCompressToFile(sourceFile, sourceFile, destWidth, destHeight, keepAspect, MAX_QUALITY);
    }

    protected static boolean rescaleAndCompressToFile(String sourceFile, int destWidth, int destHeight, int quality) {
        return rescaleAndCompressToFile(sourceFile, sourceFile, destWidth, destHeight, true, quality);
    }

    protected static boolean rescaleAndCompressToFile(String sourceFile, int destWidth, int destHeight) {
        return rescaleAndCompressToFile(sourceFile, sourceFile, destWidth, destHeight, true, MAX_QUALITY);
    }

    protected static boolean rescaleAndCompressToFile(Context context, String sourceFile, String destFile) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return rescaleAndCompressToFile(sourceFile, destFile, displayMetrics.widthPixels, displayMetrics.heightPixels, true, DEFAULT_COMPRESS_QUALITY);
    }

    protected static boolean rescaleAndCompressToFile(Context context, String sourceFile) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return rescaleAndCompressToFile(sourceFile, sourceFile, displayMetrics.widthPixels, displayMetrics.heightPixels, true, DEFAULT_COMPRESS_QUALITY);
    }
}







