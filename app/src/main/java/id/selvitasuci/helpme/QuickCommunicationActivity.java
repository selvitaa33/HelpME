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

public class QuickCommunicationActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;
    private CardView cardTextDisplay;
    private TextView tvTextDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_communication);
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
                speak("Komunikasi Cepat. Pilih kalimat untuk disampaikan.", false);
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

        ((CardView) findViewById(R.id.btnNeedHelp)).setOnClickListener(v ->
                speak("Saya butuh bantuan. Tolong bantu saya sekarang.", true));
        ((CardView) findViewById(R.id.btnHungry)).setOnClickListener(v ->
                speak("Saya lapar. Saya ingin makan sekarang.", true));
        ((CardView) findViewById(R.id.btnThirsty)).setOnClickListener(v ->
                speak("Saya haus. Saya ingin minum air sekarang.", true));
        ((CardView) findViewById(R.id.btnToilet)).setOnClickListener(v ->
                speak("Saya ingin ke kamar mandi. Tolong antar saya.", true));
        ((CardView) findViewById(R.id.btnPain)).setOnClickListener(v ->
                speak("Saya merasa sakit. Tolong perhatikan kondisi saya.", true));
        ((CardView) findViewById(R.id.btnHappy)).setOnClickListener(v ->
                speak("Saya senang. Saya merasa baik-baik saja hari ini.", true));
        ((CardView) findViewById(R.id.btnSad)).setOnClickListener(v ->
                speak("Saya sedih. Saya membutuhkan perhatian.", true));
        ((CardView) findViewById(R.id.btnTired)).setOnClickListener(v ->
                speak("Saya lelah dan ingin beristirahat sekarang.", true));
        ((CardView) findViewById(R.id.btnCallFamily)).setOnClickListener(v ->
                speak("Tolong hubungkan saya dengan keluarga saya.", true));
        ((CardView) findViewById(R.id.btnThankYou)).setOnClickListener(v ->
                speak("Terima kasih banyak atas bantuan Anda.", true));

        ((FloatingActionButton) findViewById(R.id.btnStop)).setOnClickListener(v -> {
            if (tts != null) tts.stop();
            cardTextDisplay.setVisibility(View.GONE);
        });
    }

    private void speak(String text, boolean showText) {
        if (ttsReady && tts != null)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
