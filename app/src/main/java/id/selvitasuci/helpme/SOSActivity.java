package id.selvitasuci.helpme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

import id.selvitasuci.helpme.data.Contact;
import id.selvitasuci.helpme.data.ContactStorage;

public class SOSActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 101;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean sosActive = false;
    private Handler repeatHandler = new Handler();
    private Runnable repeatRunnable;
    private String pendingCallNumber = null;

    private TextView tvSOSStatus;
    private Button btnSOSMain;

    private static final String SOS_MESSAGE =
            "Tolong! Tolong! Saya membutuhkan bantuan segera! Ini adalah keadaan darurat!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        initTTS();
        setupButtons();
        startRippleAnimation();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("id", "ID"));
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.getDefault());
                }
                ttsReady = true;
                speak("S O S Darurat. Tekan tombol S O S untuk meminta bantuan.");
            }
        });
    }

    private void setupButtons() {
        tvSOSStatus = findViewById(R.id.tvSOSStatus);
        btnSOSMain = findViewById(R.id.btnSOSMain);
        ImageButton btnBack = findViewById(R.id.btnBack);
        CardView btnSOSVoice       = findViewById(R.id.btnSOSVoice);
        CardView btnSOSCallContact = findViewById(R.id.btnSOSCallContact);
        CardView btnSOSCall119     = findViewById(R.id.btnSOSCall119);
        CardView btnSOSCall110     = findViewById(R.id.btnSOSCall110);
        FloatingActionButton btnSOSStop = findViewById(R.id.btnSOSStop);

        btnBack.setOnClickListener(v -> {
            stopSOS();
            speak("Kembali ke menu utama.");
            new Handler().postDelayed(this::finish, 800);
        });

        btnSOSMain.setOnClickListener(v -> {
            if (!sosActive) {
                activateSOS();
            } else {
                stopSOS();
            }
        });

        btnSOSVoice.setOnClickListener(v -> {
            activateSOS();
        });

        btnSOSCallContact.setOnClickListener(v -> {
            callEmergencyContact();
        });

        btnSOSCall119.setOnClickListener(v -> {
            speak("Menghubungi 119. Ambulans segera dipanggil.");
            new Handler().postDelayed(() -> makeCall("119"), 1200);
        });

        btnSOSCall110.setOnClickListener(v -> {
            speak("Menghubungi 110. Polisi segera dipanggil.");
            new Handler().postDelayed(() -> makeCall("110"), 1200);
        });

        btnSOSStop.setOnClickListener(v -> stopSOS());
    }

    private void activateSOS() {
        sosActive = true;
        btnSOSMain.setText("⏹\nSTOP");
        tvSOSStatus.setText("🚨 SOS AKTIF - Memanggil bantuan...");

        // Vibrate
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 500, 300, 500, 300, 500};
            vibrator.vibrate(pattern, -1);
        }

        // Repeat SOS voice every 5 seconds
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                if (sosActive && ttsReady && tts != null) {
                    tts.speak(SOS_MESSAGE, TextToSpeech.QUEUE_FLUSH, null, null);
                    repeatHandler.postDelayed(this, 5000);
                }
            }
        };
        repeatHandler.post(repeatRunnable);
    }

    private void stopSOS() {
        sosActive = false;
        if (repeatRunnable != null) {
            repeatHandler.removeCallbacks(repeatRunnable);
        }
        if (tts != null) tts.stop();

        btnSOSMain.setText("🆘\nSOS");
        tvSOSStatus.setText("Siap digunakan");

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) vibrator.cancel();
    }

    private void callEmergencyContact() {
        List<Contact> contacts = ContactStorage.getContacts(this);
        if (contacts.isEmpty()) {
            speak("Tidak ada kontak darurat yang tersimpan. Silakan tambahkan kontak terlebih dahulu di menu Kontak Penting.");
            Toast.makeText(this, "Belum ada kontak tersimpan", Toast.LENGTH_SHORT).show();
        } else {
            Contact first = contacts.get(0);
            speak("Menghubungi " + first.getName());
            new Handler().postDelayed(() -> makeCall(first.getPhone()), 1500);
        }
    }

    private void makeCall(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        } else {
            pendingCallNumber = number;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (pendingCallNumber != null) makeCall(pendingCallNumber);
        } else {
            Toast.makeText(this, "Izin telepon diperlukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRippleAnimation() {
        // Tidak diperlukan di layout baru
    }

    private void speak(String text) {
        if (ttsReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        stopSOS();
        if (tts != null) {
            tts.shutdown();
        }
        super.onDestroy();
    }
}
