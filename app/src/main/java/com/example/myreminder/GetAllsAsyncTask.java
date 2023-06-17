package com.example.myreminder;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class GetAllsAsyncTask extends AsyncTask<Void, Void, List<Alarme>> {

    private AlarmeDao alarmeDao;
    private AsyncTaskListener<List<Alarme>> listener;

    public GetAllsAsyncTask(AlarmeDao alarmeDao, AsyncTaskListener<List<Alarme>> listener) {
        this.alarmeDao = alarmeDao;
        this.listener = listener;
    }

    @Override
    protected List<Alarme> doInBackground(Void... voids) {
        // Récupérer tous les éléments de la base de données
        return alarmeDao.getAllAlarme();
    }

    @Override
    protected void onPostExecute(List<Alarme> alarmeList) {
        super.onPostExecute(alarmeList);
        if (listener != null) {
            listener.onTaskComplete(alarmeList);
        }
    }

    public interface AsyncTaskListener<T> {
        void onTaskComplete(T result);
    }

}
