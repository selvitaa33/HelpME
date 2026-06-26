package id.selvitasuci.helpme;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class ActivityGuideActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;
    private CardView cardTextDisplay;
    private TextView tvTextDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_guide);
        initTTS();
        setupViews();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int r = tts.setLanguage(new Locale("id", "ID"));
                if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED)
                    tts.setLanguage(Locale.getDefault());
                ttsReady = true;
                speak("Panduan Aktivitas. Pilih aktivitas untuk mendengar panduan.", false);
            }
        });
    }

    private void setupViews() {
        cardTextDisplay = findViewById(R.id.cardTextDisplay);
        tvTextDisplay   = findViewById(R.id.tvTextDisplay);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> {
            speak("Kembali.", false);
            new Handler().postDelayed(this::finish, 500);
        });

        ((CardView) findViewById(R.id.btnWakeUp)).setOnClickListener(v ->
                speak("Panduan Bangun Tidur:\n1. Buka mata perlahan.\n2. Tarik napas dalam-dalam.\n3. Gerakkan tangan dan kaki sebelum berdiri.\n4. Duduklah sebentar di tepi tempat tidur.\n5. Berdiri perlahan dengan bantuan jika perlu.", true));

        ((CardView) findViewById(R.id.btnBrushTeeth)).setOnClickListener(v ->
                speak("Panduan Sikat Gigi:\n1. Ambil sikat gigi dan pasta gigi.\n2. Basahi sikat dengan air.\n3. Sikat gigi selama 2 menit.\n4. Berkumur dengan air bersih.\n5. Bersihkan sikat dan simpan kembali.", true));

        ((CardView) findViewById(R.id.btnBathe)).setOnClickListener(v ->
                speak("Panduan Mandi:\n1. Siapkan air dan peralatan mandi.\n2. Basahi seluruh tubuh.\n3. Sabuni tubuh dari atas ke bawah.\n4. Bilas hingga bersih.\n5. Keringkan dengan handuk.", true));

        ((CardView) findViewById(R.id.btnEat)).setOnClickListener(v ->
                speak("Panduan Makan:\n1. Cuci tangan sebelum makan.\n2. Siapkan makanan di tempat duduk.\n3. Makan perlahan dan kunyah dengan baik.\n4. Minum air putih setelah makan.\n5. Cuci tangan kembali setelah selesai.", true));

        ((CardView) findViewById(R.id.btnWalk)).setOnClickListener(v ->
                speak("Panduan Olahraga Ringan:\n1. Mulai dengan pemanasan ringan.\n2. Jalan santai selama 10 hingga 15 menit.\n3. Istirahat jika merasa lelah.\n4. Lakukan pendinginan setelah selesai.\n5. Minum air putih yang cukup.", true));

        ((CardView) findViewById(R.id.btnDrink)).setOnClickListener(v ->
                speak("Pengingat Minum Air:\nMinumlah air putih yang cukup setiap hari. Minimal 8 gelas atau 2 liter per hari. Minum sedikit demi sedikit sepanjang hari.", true));

        ((CardView) findViewById(R.id.btnRest)).setOnClickListener(v ->
                speak("Panduan Istirahat Siang:\n1. Cari tempat yang nyaman dan tenang.\n2. Berbaring atau duduk dengan santai.\n3. Tutup mata dan rilekskan tubuh.\n4. Istirahat selama 20 hingga 30 menit.\n5. Bangun perlahan setelah istirahat.", true));

        ((CardView) findViewById(R.id.btnNightRoutine)).setOnClickListener(v ->
                speak("Panduan Persiapan Tidur:\n1. Matikan lampu yang tidak diperlukan.\n2. Ganti pakaian tidur.\n3. Sikat gigi sebelum tidur.\n4. Berbaring di tempat tidur.\n5. Tarik napas perlahan dan rileks.", true));

        ((CardView) findViewById(R.id.btnMedicine)).setOnClickListener(v ->
                speak("Panduan Minum Obat:\n1. Siapkan obat sesuai dosis yang ditentukan.\n2. Minum obat dengan segelas air putih.\n3. Jangan lewatkan jadwal minum obat.\n4. Simpan obat di tempat yang sejuk.\n5. Konsultasikan dengan dokter jika ada keluhan.", true));

        ((FloatingActionButton) findViewById(R.id.btnStop)).setOnClickListener(v -> {
            if (tts != null) tts.stop();
            cardTextDisplay.setVisibility(View.GONE);
        });
    }

    private void speak(String text, boolean showText) {
        if (ttsReady && tts != null)
            tts.speak(text.replaceAll("\n", ". "), TextToSpeech.QUEUE_FLUSH, null, null);
        if (showText) {
            tvTextDisplay.setText(text);
            cardTextDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
