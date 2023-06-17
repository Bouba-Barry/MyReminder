package com.example.myreminder;

import android.os.AsyncTask;

public class DeleteAsyncTask extends AsyncTask<Integer, Void, Alarme> {

    private AlarmeDao alarmeDao;
    public DeleteAsyncTask(AlarmeDao alarmeDao){
        this.alarmeDao = alarmeDao;

    }
    @Override
    protected Alarme doInBackground(Integer... integers) {

        return null;
    }
}
