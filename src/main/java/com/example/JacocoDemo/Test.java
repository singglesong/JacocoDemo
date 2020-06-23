package com.example.JacocoDemo;

import org.jacoco.core.tools.ExecDumpClient;
import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String filePath = "jacoco-client.exec";
        ExecDumpClient dumpClient = new ExecDumpClient();
        dumpClient.setDump(true);
        ExecFileLoader execFileLoader = null;
        try {
            execFileLoader = dumpClient.dump(
                    "192.168.0.203",
                    2014);

            execFileLoader.save(new File(filePath), false);
        } catch (IOException e2) {
           e2.printStackTrace();
        }
    }
}
