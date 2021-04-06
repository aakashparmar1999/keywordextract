package emailhsdextractor;

import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;


public class EmailExtractor {

    public static void extractEmailHSD(BufferedReader br, Pattern p, Pattern q) throws IOException {
        StringBuffer bf = new StringBuffer();
        String line = "";
        Matcher matcher ;
        Matcher matcher1;
        boolean b = false;

        while ((line = br.readLine()) != null) {
            matcher = p.matcher(line);
            if (matcher.find()) {
                b=true;
                continue;
            }
            if(b){
                matcher1 = q.matcher(line);
                if (matcher1.find() == false) {
                    bf.append(line +"\n");
                }
                else {
                    break;
                }
            }
        }
        System.out.print("Body: "+bf);
        br.close();
    }

    public static void main(String[] args) throws IOException {

        Pattern tofrom = Pattern.compile("Subject: (\\w+\\s)*");
        Pattern regards = Pattern.compile("(Thanks and Regards|Thanks & Regards|Thanks|Regards|Sincerely|Cheers|Cheers!|Good luck)(,|,,|...)");

        BufferedReader br= new BufferedReader(new FileReader("C:/Users/HP/Downloads/email1.txt"));
        extractEmailHSD(br,tofrom,regards);


    }
}
