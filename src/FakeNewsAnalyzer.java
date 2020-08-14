import java.util.Arrays;
import java.util.List;

public abstract class FakeNewsAnalyzer {
    
    private static List<String> Blocklist = Arrays
            .asList(new String[] { "kit gay", "cloroquina cura covid", "whatsapp pago" });

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