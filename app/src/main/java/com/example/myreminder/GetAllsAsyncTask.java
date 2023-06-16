package com.example.myreminder;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class GetAllsAsyncTask extends AsyncTask<Void, Void, List<Alarme>> {

    private AlarmeDao alarmeDao;
    protected List<Alarme> alarmeList;
    private OnTaskCompletedListener onTaskCompletedListener;


    public GetAllsAsyncTask(AlarmeDao alarmeDao) {
        this.alarmeDao = alarmeDao;
    }
    public GetAllsAsyncTask(AlarmeDao alarmeDao, OnTaskCompletedListener listener) {
        this.alarmeDao = alarmeDao;
        this.onTaskCompletedListener = listener;
    }

    @Override
    protected List<Alarme> doInBackground(Void... voids) {
        // Récupérer toutes les tâches de la base de données
        List<Alarme> alarmeList = new ArrayList<>();
        alarmeList = alarmeDao.getAllAlarme();
        return alarmeList;
    }

    @Override
    protected void onPostExecute(List<Alarme> alarmeList) {
        super.onPostExecute(alarmeList);
        //this.alarmeList = alarmeList;
        onTaskCompletedListener.onTaskCompleted(alarmeList);

    }

    public interface OnTaskCompletedListener {
        void onTaskCompleted(List<Alarme> alarmeList);
    }

}
