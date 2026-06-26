package id.selvitasuci.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import id.selvitasuci.helpme.data.Contact;
import id.selvitasuci.helpme.data.ContactStorage;

public class AddContactActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private boolean ttsReady = false;

    private EditText etName, etPhone, etRelation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

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
                speak("Tambah kontak baru. Isi nama, nomor telepon, dan hubungan kontak.");
            }
        });
    }

    private void setupViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        etName      = findViewById(R.id.etName);
        etPhone     = findViewById(R.id.etPhone);
        etRelation  = findViewById(R.id.etRelation);
        Button btnSave   = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // TTS saat field di-tap
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) speak("Masukkan nama kontak.");
        });
        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) speak("Masukkan nomor telepon.");
        });
        etRelation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) speak("Masukkan hubungan, misalnya keluarga atau pendamping.");
        });

        btnBack.setOnClickListener(v -> {
            speak("Kembali.");
            new Handler().postDelayed(this::finish, 600);
        });

        btnCancel.setOnClickListener(v -> {
            speak("Dibatalkan.");
            new Handler().postDelayed(this::finish, 600);
        });

        btnSave.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name     = etName.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String relation = etRelation.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            speak("Nama kontak tidak boleh kosong.");
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            speak("Nomor telepon tidak boleh kosong.");
            etPhone.setError("Nomor telepon tidak boleh kosong");
            etPhone.requestFocus();
            return;
        }

        if (relation.isEmpty()) relation = "Kontak";

        Contact contact = new Contact(name, phone, relation);
        ContactStorage.addContact(this, contact);

        speak("Kontak " + name + " berhasil disimpan.");
        Toast.makeText(this, "Kontak berhasil disimpan ✅", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            setResult(RESULT_OK);
            finish();
        }, 1200);
    }

    private void speak(String text) {
        if (ttsReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
