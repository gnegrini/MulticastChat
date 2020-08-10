import java.util.Arrays;
import java.util.List;

public class FakeNewsAnalyzer {
    
    static List<String> Blocklist = Arrays
            .asList(new String[] { "kit gay", "mamadeira de piroca", 
                "cloroquina cura covid", "whatsapp pago" });

    public static String containsFakeNews(String news) {
        String newsToTest = news.toLowerCase();

        for (String subject : Blocklist) {
            if(newsToTest.contains(subject)){                
                return subject;
            }
        }
        return null;
    }


}