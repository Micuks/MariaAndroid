package com.wql_2020211597.mariaandroid.history;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryStorage {
    private static final String TAG = "HistoryStorage";
    private static final String FILENAME = "history.json";
    private static HistoryStorage instance;
    private Context context;
    private Gson gson;
    private ArrayList<HistoryEntry> historyList;

    private HistoryStorage(Context context) {
        // Use the application context. This can avoid memory leak.
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.historyList=loadHistory();
    }

    public static synchronized HistoryStorage getInstance(Context context) {
        if (instance==null){
            instance=new HistoryStorage(context);
        }
        return instance;
    }

    public void add(HistoryEntry historyEntry) {
        historyList.add(historyEntry);
        saveHistory(historyList);
    }

    public void saveHistory(ArrayList<HistoryEntry> history) {
        String json = gson.toJson(history);
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(TAG,
                    "Error writing history " + history.toString() + " to json");
            e.printStackTrace();
        }
    }

    public ArrayList<HistoryEntry> loadHistory() {
        ArrayList<HistoryEntry> history = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            byte[] bytes = new byte[(int) new File(FILENAME).length()];
            fis.read(bytes);
            String json = new String(bytes);
            Type type = new TypeToken<ArrayList<HistoryEntry>>() {
            }.getType();
            history = gson.fromJson(json, type);
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading history from json " + FILENAME);
            e.printStackTrace();
        }
        return history == null ? new ArrayList<>() : history;
    }
}
