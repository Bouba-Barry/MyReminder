package com.example.myreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AppDatabase appDatabase;
    private AlarmeDao alarmeDao;
    private ListView listView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        // ici on va initialiser la base de donnée.......
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
        alarmeDao = appDatabase.alarmeDao();

        // affichons en prémier la liste view..........
        listView = findViewById(R.id.taskListView);

        getAll();
        ImageView addTaskIcon = findViewById(R.id.addTaskIcon);
        addTaskIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskPopup(v, addTaskIcon);
            }
        });
    }

    public void showAddTaskPopup(View view, ImageView addTaskIcon) {
        PopupWindow popupWindow = new PopupWindow(MainActivity.this);
        // Inflation du layout du popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_task, null);

        EditText mTitledit = popupView.findViewById(R.id.editTitle);
        Button mDatebtn = popupView.findViewById(R.id.btnDate);
        Button mTimebtn = popupView.findViewById(R.id.btnTime);

        mTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime(mTimebtn);
            }
        });
        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(mDatebtn);
            }
        });


        Button addButton = popupView.findViewById(R.id.btnSubmit);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitledit.getText().toString().trim();
                String date = mDatebtn.getText().toString().trim();
                String time = mTimebtn.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter text"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    insertNewAlarme(title, date, time);
                    Toast.makeText(MainActivity.this, "Title:"+title+
                            " DateTime: "+date+time, Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();
            }
        });
        // ici on va configurer la longueur et larg du popup...
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);

        // Affichage du popup
        popupWindow.showAsDropDown(addTaskIcon);
    }

    public void selectTime(Button mTimebtn) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String timeTonotify = FormatTime(i, i1); // Stocke l'heure sélectionnée avec le format 12 heures
                mTimebtn.setText(timeTonotify); // Définit le texte du bouton avec l'heure sélectionnée
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }


    public void selectDate(Button mDatebtn){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDatebtn.setText(day + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    public String FormatTime(int hour, int minute) {
        String time;
        String formattedMinute;
        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }
        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }

    /**
     * @param title le tire de la tâche...
     * @param date
     * @param time
     */
    public void insertNewAlarme(String title, String date, String time){
        Alarme alarme = new Alarme(title, date, time);
        InsertAsyncTask insertAsyncTask = new InsertAsyncTask(alarmeDao);
        insertAsyncTask.execute(alarme); // Passer votre objet Alarme ici
        // on va planifier automatiquement la tâche ajouté....
        System.out.println("Temps En Millis Seconde = "+convertDateTimeToMillis(date, time));
        scheduleNotif(alarme);

        //setAlarme(title, date, time);
        getAll();
    }


    /**
     *
     * @param alarme
     */
    public void scheduleNotif(Alarme alarme){
        Intent notificationIntent = new Intent(MainActivity.this,
                NotificationReceiver.class);
        notificationIntent.putExtra("title", alarme.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = convertDateTimeToMillis(alarme.getCreate_date(), alarme.getTime());

        // Utilisation du gestionnaire de tâches pour afficher la notification à l'heure prévue
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }



    /**
     * @param date
     * @param time
     * @return
     */
    public long convertDateTimeToMillis(String date, String time) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US);

        try {
            Date dateTime = format.parse(date + " " + time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);

            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // ou une autre valeur par défaut en cas d'erreur
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.channel_name);
            String channelId = getString(R.string.channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName, importance);

            NotificationManager notificationManager;
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getAll(){
        GetAllsAsyncTask asyncTask = new GetAllsAsyncTask(alarmeDao,
                new GetAllsAsyncTask.AsyncTaskListener<List<Alarme>>() {
            @Override
            public void onTaskComplete(List<Alarme> alarmeList) {
                AlarmeAdapter alarmeAdapter = new AlarmeAdapter(MainActivity.this,
                        R.layout.item_list, alarmeList, alarmeDao);
                listView.setAdapter(alarmeAdapter);
            }
        });
        asyncTask.execute();
    }
}