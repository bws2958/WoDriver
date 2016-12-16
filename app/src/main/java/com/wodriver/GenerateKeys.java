package com.wodriver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016-12-07.
 */

public class GenerateKeys {
    Context context;

    ArrayList<HashMap<String, Object>> keysholder = new ArrayList<>();
    List<S3ObjectSummary> s3ObjectSummaries;

    GenerateKeys(Context context){
        this.context = context;
    }

    private class downloadkeys extends AsyncTask<Void, Void, Void>{
        Dialog dialog;
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Loading", "downloading keys");
        }

        protected Void doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            managerClass.getcredentials(context);
            AmazonS3Client s3Client = managerClass.inits3client(context);
            s3ObjectSummaries=s3Client.listObjects("").getObjectSummaries();

            for(S3ObjectSummary summary:s3ObjectSummaries){
                HashMap<String, Object> maps = new HashMap<>();
                maps.put("key", summary.getKey());
                keysholder.add(maps);
            }
            return null;
        }

        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }
}
