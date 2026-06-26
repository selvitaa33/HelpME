package id.selvitasuci.helpme;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class VoiceGuideActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;

    private static final String GUIDE_APP =
            "Aplikasi HelpME adalah asisten bantu untuk penyandang disabilitas. " +
            "Di halaman utama terdapat 5 menu. " +
            "Pertama, Panduan Suara: membacakan penjelasan setiap fitur menggunakan suara. " +
            "Kedua, Komunikasi Cepat: menekan tombol kalimat agar suara menyampaikan kebutuhan Anda. " +
            "Ketiga, S O S Darurat: untuk meminta bantuan darurat. " +
            "Keempat, Panduan Aktivitas: panduan langkah demi langkah untuk kegiatan sehari-hari. " +
            "Kelima, Kontak Penting: menyimpan dan menelepon nomor keluarga atau pendamping.";

    private static final String GUIDE_VOICE =
            "Fitur Panduan Suara akan membacakan informasi menggunakan teknologi Text to Speech. " +
            "Pastikan volume ponsel Anda sudah dikeraskan agar suara terdengar dengan jelas.";

    private static final String GUIDE_COMM =
            "Fitur Komunikasi Cepat menyediakan tombol-tombol kalimat siap pakai. " +
            "Ketuk item sesuai kebutuhan Anda, dan aplikasi akan mengucapkan kalimat tersebut. " +
            "Tersedia kalimat untuk kebutuhan dasar, emosi, dan permintaan khusus.";

    private static final String GUIDE_SOS =
            "Fitur S O S Darurat sangat penting untuk kondisi darurat. " +
            "Tekan tombol S O S besar di layar untuk mengeluarkan suara minta tolong. " +
            "Anda juga dapat menghubungi kontak darurat, atau menelepon 119 untuk ambulans, dan 110 untuk polisi.";

    private static final String GUIDE_ACTIVITY =
            "Fitur Panduan Aktivitas memberikan panduan langkah demi langkah untuk kegiatan sehari-hari. " +
            "Pilih aktivitas seperti bangun tidur, sikat gigi, mandi, atau makan, " +
            "dan aplikasi akan memandu Anda menggunakan suara.";

    private static final String GUIDE_CONTACTS =
            "Fitur Kontak Penting memungkinkan Anda menyimpan nomor telepon keluarga, pendamping, atau dokter. " +
            "Ketuk ikon telepon hijau untuk langsung menelepon kontak tersebut. " +
            "Ketuk tombol Tambah untuk menambahkan kontak baru.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_guide);
        initTTS();
        setupViews();
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
                speak("Panduan Suara. Pilih topik untuk mendengar penjelasan.");
            }
        });
    }

    private void setupViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            speak("Kembali.");
            new Handler().postDelayed(this::finish, 600);
        });

        ((CardView) findViewById(R.id.btnGuideApp)).setOnClickListener(v      -> speak(GUIDE_APP));
        ((CardView) findViewById(R.id.btnGuideVoice)).setOnClickListener(v    -> speak(GUIDE_VOICE));
        ((CardView) findViewById(R.id.btnGuideComm)).setOnClickListener(v     -> speak(GUIDE_COMM));
        ((CardView) findViewById(R.id.btnGuideSOS)).setOnClickListener(v      -> speak(GUIDE_SOS));
        ((CardView) findViewById(R.id.btnGuideActivity)).setOnClickListener(v -> speak(GUIDE_ACTIVITY));
        ((CardView) findViewById(R.id.btnGuideContacts)).setOnClickListener(v -> speak(GUIDE_CONTACTS));

        FloatingActionButton btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> { if (tts != null) tts.stop(); });
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
