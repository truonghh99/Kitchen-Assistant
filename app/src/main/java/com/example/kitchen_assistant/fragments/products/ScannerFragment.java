package com.example.kitchen_assistant.fragments.products;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentScannerBinding;

import java.util.Collections;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends DialogFragment {

    private static final int CAMERA_REQUEST_CODE = 0;
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
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

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
                                ScannerFragment.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
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
}