package com.example.kitchen_assistant.fragments.products;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.databinding.FragmentScannerBinding;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Collections;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 0;
    public static final String KEY_CODE = "Barcode";
    private static final String TAG = "ScannerFragment";
    private FragmentScannerBinding fragmentScannerBinding;
    private CameraManager cameraManager;
    private int cameraFacing;
    private String cameraId;
    private Size previewSize;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice cameraDevice;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private TextureView textureView;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    private SurfaceTexture surfaceTexture;
    private CameraDevice.StateCallback stateCallback;
    private boolean mManualFocusEngaged;
    private CameraCharacteristics characteristics;
    private CameraCaptureSession.CaptureCallback captureCallback;
    private BarcodeDetector detector;

    public ScannerFragment() {
    }

    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

        fragmentScannerBinding = FragmentScannerBinding.inflate(getLayoutInflater());
        textureView = fragmentScannerBinding.textureView;

        detector = new BarcodeDetector.Builder(getContext())
                        .setBarcodeFormats(Barcode.EAN_13 | Barcode.UPC_A | Barcode.UPC_E | Barcode.QR_CODE)
                        .build();

        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG, "TOUCHED");
                final int actionMasked = motionEvent.getActionMasked();
                if (actionMasked != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if (mManualFocusEngaged) {
                    Log.d(TAG, "Manual focus already engaged");
                    return true;
                }
                try {
                    characteristics = cameraManager.getCameraCharacteristics(cameraId);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                final Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                final int y = (int)((motionEvent.getX() / (float)view.getWidth())  * (float)sensorArraySize.height());
                final int x = (int)((motionEvent.getY() / (float)view.getHeight()) * (float)sensorArraySize.width());
                final int halfTouchWidth  = (int)motionEvent.getTouchMajor();
                final int halfTouchHeight = (int)motionEvent.getTouchMinor();
                MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth,  0),
                        Math.max(y - halfTouchHeight, 0),
                        halfTouchWidth  * 2,
                        halfTouchHeight * 2,
                        MeteringRectangle.METERING_WEIGHT_MAX - 1);

                CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        mManualFocusEngaged = false;

                        if (request.getTag() == "FOCUS_TAG") {
                            //the focus trigger is complete -
                            //resume repeating (preview surface will get frames), clear AF trigger
                            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                            try {
                                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), captureCallback, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        detectBitmap();
                    }

                    @Override
                    public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                        super.onCaptureFailed(session, request, failure);
                        Log.e(TAG, "Manual AF failure: " + failure);
                        mManualFocusEngaged = false;
                    }
                };

                //first stop the existing repeating request
                try {
                    cameraCaptureSession.stopRepeating();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                //cancel any existing AF trigger (repeated touches, etc.)
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                try {
                    cameraCaptureSession.capture(captureRequestBuilder.build(), captureCallbackHandler, backgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                //Now add a new AF trigger with focus region
                if (isMeteringAreaAFSupported()) {
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
                }
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                captureRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview

                //then we ask for a single request (not repeating!)
                try {
                    cameraCaptureSession.capture(captureRequestBuilder.build(), captureCallbackHandler, backgroundHandler);
                    Log.e(TAG, "CREATED SINGLE REQUEST");
                } catch (CameraAccessException e) {
                    Log.e(TAG, "CANNOT CREATE SINGLE REQUEST");
                    e.printStackTrace();
                }
                mManualFocusEngaged = true;

                return true;
            }
        });

        cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                ScannerFragment.this.cameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                cameraDevice.close();
                ScannerFragment.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                ScannerFragment.this.cameraDevice = null;
            }
        };

        super.onCreate(savedInstanceState);
    }

    private boolean isMeteringAreaAFSupported() {
        return characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
    }

    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return fragmentScannerBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        openBackgroundThread();
        if (textureView.isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void createPreviewSession() {
        try {
            surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);

            captureRequestBuilder.addTarget(previewSurface);

            final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    detectBitmap();
                }
            };
            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (cameraDevice == null) {
                                return;
                            }

                            try {
                                captureRequest = captureRequestBuilder.build();
                                ScannerFragment.this.cameraCaptureSession = cameraCaptureSession;
                                ScannerFragment.this.cameraCaptureSession.setRepeatingRequest(captureRequest, captureCallback, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void detectBitmap() {
        Bitmap bitmap = textureView.getBitmap();
        Log.e(TAG, "Start extracting barcode");
        Barcode resultCode = null;
        try {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            resultCode = barcodes.valueAt(0);
            Log.e(TAG, "FOUND BAR CODE! " + resultCode.rawValue);
        } catch (Exception e) {
            Log.e(TAG, "Cannot identify barcode");
        }
        if (resultCode != null) {
            closeCamera();
            closeBackgroundThread();
            Intent intent = new Intent();
            intent.putExtra(KEY_CODE, resultCode.rawValue);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getActivity().getFragmentManager().popBackStack();
        }
    }
}