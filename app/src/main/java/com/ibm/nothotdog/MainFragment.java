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
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.xlythe.fragment.camera.CameraFragment;
import com.xlythe.view.camera.Exif;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainFragment extends CameraFragment {
    private static final String TAG = "CameraSample";
    TextView tv;

    private class ClassifyTask extends AsyncTask<File, Integer, VisualClassification> {
        @Override
        protected VisualClassification doInBackground(File... files) {

            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
            service.setApiKey("4ef2b4c252cbaa92235bd7724d15a9962f59cf85");

            Log.d(TAG, "Classify an image");
            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(files[0])
                    .classifierIds("stuff_1719981427")
                    .build();
            VisualClassification result = service.classify(options).execute();
            return result;
        }

        @Override
        protected void onPostExecute(VisualClassification result) {
            try {
                JSONObject jObj = new JSONObject(result.toString());
                JSONArray jArr = jObj.getJSONArray("images");
                for (int i=0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    JSONArray jArr2 = obj.getJSONArray("classifiers");
                    for (int i2=0; i2 < jArr2.length(); i2++) {
                        JSONObject obj2 = jArr2.getJSONObject(i2);
                        JSONArray jArr3 = obj2.getJSONArray("classes");
                        for (int i3=0; i3 < jArr3.length(); i3++) {
                            JSONObject obj3 = jArr3.getJSONObject(i3);
                            Log.d("obj3", obj3.toString());
                            tv.setText(obj3.toString());
                        }
                    }
                }

            } catch (JSONException e) {

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_main, container);
        tv = (TextView) view.findViewById(R.id.result);
        return view;
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
