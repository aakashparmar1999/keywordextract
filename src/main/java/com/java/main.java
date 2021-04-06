package com.java;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;


public class main {
    public static void main(String[] args) {
        int res = 0;
        try {
            res = ToolRunner.run(new Configuration(),new KeyPhraseExtraction(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(res);
    }
}
