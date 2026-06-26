package id.selvitasuci.helpme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import id.selvitasuci.helpme.data.Contact;
import id.selvitasuci.helpme.data.ContactStorage;

public class ImportantContactsActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 102;
    private static final int REQUEST_ADD  = 200;

    private TextToSpeech tts;
    private boolean ttsReady = false;

    private RecyclerView recyclerContacts;
    private LinearLayout layoutEmpty;
    private ContactAdapter adapter;
    private List<Contact> contactList;
    private String pendingCallNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_contacts);

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
                speak("Kontak Penting. Simpan dan hubungi nomor keluarga, pendamping, atau layanan darurat.");
            }
        });
    }

    private void setupViews() {
        ImageButton btnBack       = findViewById(R.id.btnBack);
        Button btnAddContact      = findViewById(R.id.btnAddContact);
        recyclerContacts          = findViewById(R.id.recyclerContacts);
        layoutEmpty               = findViewById(R.id.layoutEmpty);

        btnBack.setOnClickListener(v -> {
            speak("Kembali ke menu utama.");
            new Handler().postDelayed(this::finish, 800);
        });

        btnAddContact.setOnClickListener(v -> {
            speak("Tambah kontak baru.");
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        });

        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        loadContacts();
    }

    private void loadContacts() {
        contactList = ContactStorage.getContacts(this);

        if (contactList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerContacts.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerContacts.setVisibility(View.VISIBLE);

            adapter = new ContactAdapter(contactList,
                    // onCall
                    (contact, position) -> {
                        speak("Menghubungi " + contact.getName());
                        new Handler().postDelayed(() ->
                                makeCall(contact.getPhone()), 1200);
                    },
                    // onDelete
                    (contact, position) -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Hapus Kontak")
                                .setMessage("Hapus kontak " + contact.getName() + "?")
                                .setPositiveButton("Hapus", (d, w) -> {
                                    speak("Kontak " + contact.getName() + " dihapus.");
                                    ContactStorage.deleteContact(this, position);
                                    loadContacts();
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                    });

            recyclerContacts.setAdapter(adapter);
        }
    }

    private void makeCall(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
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
            Toast.makeText(this, "Izin telepon diperlukan untuk menelepon", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD && resultCode == RESULT_OK) {
            speak("Kontak berhasil ditambahkan.");
            loadContacts();
        }
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
