package id.selvitasuci.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTTS();
        setupCards();
        setupBottomNav();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("id", "ID"));
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED)
                    tts.setLanguage(Locale.getDefault());
                ttsReady = true;
                speak("Selamat datang di aplikasi HelpME. Silakan pilih menu.");
            }
        });
    }

    private void setupCards() {
        ((CardView) findViewById(R.id.cardQuickComm)).setOnClickListener(v -> {
            speak("Komunikasi Cepat.");
            goTo(QuickCommunicationActivity.class);
        });
        ((CardView) findViewById(R.id.cardSOS)).setOnClickListener(v -> {
            speak("S O S Darurat.");
            goTo(SOSActivity.class);
        });
        ((CardView) findViewById(R.id.cardActivityGuide)).setOnClickListener(v -> {
            speak("Panduan Aktivitas.");
            goTo(ActivityGuideActivity.class);
        });
        ((CardView) findViewById(R.id.cardContacts)).setOnClickListener(v -> {
            speak("Kontak Penting.");
            goTo(ImportantContactsActivity.class);
        });
        ((CardView) findViewById(R.id.cardReminder)).setOnClickListener(v -> {
            speak("Pengingat Minum Obat.");
            goTo(ReminderActivity.class);
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) { speak("Beranda."); return true; }
            else if (id == R.id.nav_comm) { speak("Komunikasi."); goTo(QuickCommunicationActivity.class); return true; }
            else if (id == R.id.nav_contacts) { speak("Kontak."); goTo(ImportantContactsActivity.class); return true; }
            else if (id == R.id.nav_guide) { speak("Panduan."); goTo(ActivityGuideActivity.class); return true; }
            return false;
        });
    }

    private void goTo(Class<?> cls) {
        new Handler().postDelayed(() ->
                startActivity(new Intent(MainActivity.this, cls)), 500);
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