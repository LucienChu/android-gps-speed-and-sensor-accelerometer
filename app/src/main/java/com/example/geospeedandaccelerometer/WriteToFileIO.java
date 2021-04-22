package com.example.geospeedandaccelerometer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WriteToFileIO implements Runnable {
    private String fileName;
    private ArrayList<HashMap<String, String>> values;
    public WriteToFileIO(String fileName, ArrayList<HashMap<String, String>> values) {
        this.fileName = fileName;
        this.values = values;
    }
    @Override
    public void run() {
        try {
            File file = new File(fileName +".csv");
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            if(file.length() == 0) {
                bw.write("time, x, y, z\n");
            }
            for (int i = 0; i < this.values.size(); i++) {
                HashMap<String, String> temp = values.get(i);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(temp.get("time")).append(", ");
                stringBuilder.append(temp.get("x")).append(", ");
                stringBuilder.append(temp.get("y")).append(", ");
                stringBuilder.append(temp.get("z")).append("\n");
                bw.write(stringBuilder.toString());
            }

            bw.close();
            values.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
