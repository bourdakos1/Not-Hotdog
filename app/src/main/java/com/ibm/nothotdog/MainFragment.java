package com.ibm.nothotdog;

/**
 * Created by niko on 8/12/17.
 */

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.xlythe.fragment.camera.CameraFragment;
import com.xlythe.view.camera.Exif;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainFragment extends CameraFragment {
    private static final String TAG = "CameraSample";

    private class ClassifyTask extends AsyncTask<File, Integer, VisualClassification> {
        @Override
        protected VisualClassification doInBackground(File... files) {

            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
            service.setApiKey("2d7f02e6708f3562a043ebf31159ff849d94d123");

            Log.d(TAG, "Classify an image");
            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(files[0])
                    .classifierIds("default")
                    .build();
            VisualClassification result = service.classify(options).execute();
            return result;
        }

        @Override
        protected void onPostExecute(VisualClassification result) {
            Log.d(TAG, result.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.fragment_main, container);
    }

    @Override
    public void onImageCaptured(final File file) {
        new ClassifyTask().execute(file);
    }

    @Override
    public void onVideoCaptured(final File file) {
    }

    @Override
    protected void onRecordStart() {
        report("Recording");
    }

    @Override
    public void onFailure() {
        report("Failure");
    }

    private void report(String msg) {
        Log.d(TAG, msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void broadcastPicture(File file) {
        if (Build.VERSION.SDK_INT < 24) {
            Intent intent = new Intent(Camera.ACTION_NEW_PICTURE);
            intent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(intent);
        }
    }

    private void broadcastVideo(File file) {
        if (Build.VERSION.SDK_INT < 24) {
            Intent intent = new Intent(Camera.ACTION_NEW_VIDEO);
            intent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(intent);
        }
    }
}
