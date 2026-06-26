package id.selvitasuci.helpme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.core.app.NotificationCompat;

import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String label = intent.getStringExtra("label");
        if (label == null) label = "Obat";

        // Notifikasi
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel("obat_channel",
                    "Pengingat Obat", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(ch);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "obat_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Waktunya Minum Obat!")
                .setContentText("Jangan lupa minum obat " + label + " sekarang.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
