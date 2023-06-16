package com.example.myreminder;

import android.os.AsyncTask;

public class InsertAsyncTask extends AsyncTask<Alarme, Void, Void> {
    private AlarmeDao alarmeDao;

    public InsertAsyncTask(AlarmeDao alarmeDao) {
        this.alarmeDao = alarmeDao;
    }

    @Override
    protected Void doInBackground(Alarme... alarmes) {
        alarmeDao.insert(alarmes[0]);
        return null;
    }
}

