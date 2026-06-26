package id.selvitasuci.helpme.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactStorage {

    private static final String PREF_NAME = "helpme_contacts";
    private static final String KEY_CONTACTS = "contacts_list";

    public static void saveContacts(Context context, List<Contact> contacts) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        prefs.edit().putString(KEY_CONTACTS, json).apply();
    }

    public static List<Contact> getContacts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CONTACTS, null);
        if (json == null) return new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Contact>>() {}.getType();
        List<Contact> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static void addContact(Context context, Contact contact) {
        List<Contact> contacts = getContacts(context);
        contacts.add(contact);
        saveContacts(context, contacts);
    }

    public static void deleteContact(Context context, int index) {
        List<Contact> contacts = getContacts(context);
        if (index >= 0 && index < contacts.size()) {
            contacts.remove(index);
            saveContacts(context, contacts);
        }
    }
}
