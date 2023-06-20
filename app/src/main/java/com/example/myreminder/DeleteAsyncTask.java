package com.example.myreminder;

import android.os.AsyncTask;

public class DeleteAsyncTask extends AsyncTask<Integer, Void, Boolean> {

    private AlarmeDao alarmeDao;
    public DeleteAsyncTask(AlarmeDao alarmeDao){
        this.alarmeDao = alarmeDao;

    }
    @Override
    protected Boolean doInBackground(Integer... integers) {
        int alarmeId = integers[0];
        Alarme alarme = alarmeDao.getAlarme(alarmeId); // Récupérer l'objet Alarme à supprimer en fonction de son ID

        if (alarme != null) {
            alarmeDao.deleteAlarme(alarme); // Supprimer l'objet Alarme de la base de données
        }
        return true;
    }
}
