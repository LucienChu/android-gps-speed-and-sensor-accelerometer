package com.example.geospeedandaccelerometer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteSpeedToFileIo implements Runnable{
    String fileName;
    String value;
    public WriteSpeedToFileIo(String fileName, String value) {
        this.fileName = fileName;
        this.value = value;
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
            bw.write(value);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
