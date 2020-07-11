package com.example.kitchen_assistant.clients;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import com.example.kitchen_assistant.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class BarcodeReader {

    private static final String TAG = "BarcodeReader";

    public static String getCodeFromImg(String pathName, Context context) {
        Bitmap myBitmap = BitmapFactory.decodeFile(pathName);
        Barcode thisCode = null;
        try {
            BarcodeDetector detector =
                    new BarcodeDetector.Builder(context)
                            .setBarcodeFormats(Barcode.EAN_13 | Barcode.UPC_A | Barcode.UPC_E | Barcode.QR_CODE)
                            .build();

            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            thisCode = barcodes.valueAt(0);

            Log.e(TAG, thisCode.rawValue);
        } catch (Exception e) {
            Log.e(TAG, "Cannot identify barcode");
        }
        return thisCode.rawValue;
    }
}
