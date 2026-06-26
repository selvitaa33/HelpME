package id.selvitasuci.helpme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Calendar;
import java.util.Locale;

public class ReminderActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;
    private TextView tvMorningTime, tvAfternoonTime, tvNightTime, tvReminderStatus;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        prefs = getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE);
        initTTS();
        setupViews();
        updateStatus();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int r = tts.setLanguage(new Locale("id", "ID"));
                if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED)
                    tts.setLanguage(Locale.getDefault());
                ttsReady = true;
                speak("Pengingat Minum Obat. Ketuk salah satu pengingat untuk mengatur jam.");
            }
        });
    }

    private void setupViews() {
        tvMorningTime   = findViewById(R.id.tvMorningTime);
        tvAfternoonTime = findViewById(R.id.tvAfternoonTime);
        tvNightTime     = findViewById(R.id.tvNightTime);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);

        loadSavedTimes();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            speak("Kembali.");
            new Handler().postDelayed(this::finish, 500);
        });

        ((CardView) findViewById(R.id.btnSetMorning)).setOnClickListener(v ->
                showTimePicker("Pagi", 0));
        ((CardView) findViewById(R.id.btnSetAfternoon)).setOnClickListener(v ->
                showTimePicker("Siang", 1));
        ((CardView) findViewById(R.id.btnSetNight)).setOnClickListener(v ->
                showTimePicker("Malam", 2));

        ((Button) findViewById(R.id.btnCancelReminder)).setOnClickListener(v -> {
            cancelAllReminders();
            speak("Semua pengingat telah dihapus.");
            Toast.makeText(this, "Semua pengingat dihapus", Toast.LENGTH_SHORT).show();
            updateStatus();
        });
    }

    private void showTimePicker(String label, int slot) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            setReminder(slot, hour, minute, label);
            prefs.edit().putString("time_" + slot, time).putString("label_" + slot, label).apply();
            updateStatus();
            speak("Pengingat " + label + " diatur pada jam " + hour + " " + minute + " menit.");
            Toast.makeText(this, "Pengingat " + label + " diatur: " + time, Toast.LENGTH_SHORT).show();
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        dialog.setTitle("Atur Jam Pengingat " + label);
        dialog.show();
    }

    private void setReminder(int slot, int hour, int minute, String label) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("label", label);
        PendingIntent pi = PendingIntent.getBroadcast(this, slot, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        if (cal.before(Calendar.getInstance())) cal.add(Calendar.DATE, 1);

        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    private void cancelAllReminders() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < 3; i++) {
            Intent intent = new Intent(this, ReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi);
        }
        prefs.edit().clear().apply();
        tvMorningTime.setText("Ketuk untuk atur jam");
        tvAfternoonTime.setText("Ketuk untuk atur jam");
        tvNightTime.setText("Ketuk untuk atur jam");
    }

    private void loadSavedTimes() {
        String[] labels = {"Pagi", "Siang", "Malam"};
        TextView[] views = {tvMorningTime, tvAfternoonTime, tvNightTime};
        for (int i = 0; i < 3; i++) {
            String saved = prefs.getString("time_" + i, null);
            if (saved != null) views[i].setText("Diatur: " + saved);
        }
    }

    private void updateStatus() {
        StringBuilder sb = new StringBuilder();
        String[] labels = {"Pagi", "Siang", "Malam"};
        TextView[] views = {tvMorningTime, tvAfternoonTime, tvNightTime};
        boolean any = false;
        for (int i = 0; i < 3; i++) {
            String saved = prefs.getString("time_" + i, null);
            if (saved != null) {
                views[i].setText("Diatur: " + saved);
                sb.append(labels[i]).append(": ").append(saved).append("  ");
                any = true;
            }
        }
        tvReminderStatus.setText(any ? sb.toString().trim() : "Belum ada pengingat aktif");
    }

    private void speak(String text) {
        if (ttsReady && tts != null)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
