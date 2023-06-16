package com.example.myreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppDatabase appDatabase;
    private AlarmeDao alarmeDao;
    private ListView listView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ici on va initialiser la base de donnée.......
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
        alarmeDao = appDatabase.alarmeDao();

        // affichons en prémier la liste view..........
        listView = findViewById(R.id.taskListView);
        List<Alarme> alarmeList = new ArrayList<>();
        alarmeList = Alarme.initAlarme();
        //GetAllsAsyncTask alarmeList1 = new GetAllsAsyncTask(alarmeDao);
        //alarmeList1.execute();

        AlarmeAdapter alarmeAdapter= new AlarmeAdapter(this, R.layout.item_list, alarmeList);
        listView.setAdapter(alarmeAdapter);

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
                selectTime(mTimebtn);                                                                       //when we click on the choose time button it calls the select time method
            }
        });
        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(mDatebtn);
            }                                        //when we click on the choose date button it calls the select date method
        });


        Button addButton = popupView.findViewById(R.id.btnSubmit);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitledit.getText().toString().trim();
                String date = mDatebtn.getText().toString().trim();
                String time = mTimebtn.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter text", Toast.LENGTH_SHORT).show();
                } else {
                    insertNewAlarme(title, date, time);
                    Toast.makeText(MainActivity.this, "Title:"+title+" DateTime: "+date+time, Toast.LENGTH_SHORT).show();
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
                // Appeler la méthode pour planifier l'alarme ici
                //scheduleAlarm(timeTonotify); // Appel à une nouvelle méthode pour planifier l'alarme
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
    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr format and assigns am or pm
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
        scheduleNotif(alarme);
    }


    /**
     *
     * @param alarme
     */
    public void scheduleNotif(Alarme alarme){
        Intent notificationIntent = new Intent(MainActivity.this, NotificationReceiver.class);
        notificationIntent.putExtra("title", alarme.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, convertDateTimeToMillis(alarme.getCreate_date(), alarme.getTime()), pendingIntent);
    }

    /**
     * @param date
     * @param time
     * @return
     */
    public long convertDateTimeToMillis(String date, String time){
        // on va en prémier cinder la date....
        String[] dateParts = date.split("-");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1])-1;
        int year = Integer.parseInt(dateParts[2]);

        // on va cinder le temp
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1].split(" ")[0]); // Ignorer "AM" ou "PM"


        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

}