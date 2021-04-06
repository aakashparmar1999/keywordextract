package com.java;

import keyword.algorithm.KeyPhraseExtractionJ;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class KeyPhraseMapper extends Mapper<Text, Text, Text, Text> {
    /**
     * The \b metacharacter is used to find a match at the beginning or end of a word. */
    private String regex = "\\b(a|able|about|across|after|all|almost|also|am|among|an|and|any|are|as|at|be|because|been|but|by|can|cannot|could|dear|did|do|does|either|else|ever|every|for|from|get|got|had|has|have|he|her|hers|him|his|how|however|i|if|in|into|is|it|its|just|least|let|like|likely|may|me|might|most|must|my|neither|no|nor|not|of|off|often|on|only|or|other|our|own|rather|s|said|say|says|she|should|since|so|some|than|that|the|their|them|then|there|these|they|this|tis|to|too|twas|us|wants|was|we|were|what|when|where|which|while|who|whom|why|will|with|would|yet|you|your)\\b\\s?";
    @Override
    protected void map(Text key, Text value, Context context)
            throws IOException, InterruptedException {
        File file = new File(value.toString());
        StringBuilder br = new StringBuilder();
        String line = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try(reader){
            while (true){
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br.append(line + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = br.toString().toLowerCase().replaceAll(regex, "");
        List<String> lang = KeyPhraseExtractionJ.getKeyword(result);
        context.write(key,new Text(lang.toString().replaceAll("(^\\[|\\]$)", "")));
    }
}
