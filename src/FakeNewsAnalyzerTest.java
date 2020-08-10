import java.util.Arrays;
import java.util.List;

public class FakeNewsAnalyzerTest {

    static List<String> FakeStrings = Arrays.asList(new String[]{
        "Distribuicao de kit gay nas escolas", 
        "Criancas receberao mamadeira de piroca", 
        "Cloroquina cura covid",
        "A partir de semana que vem, o Whatsapp pago"});

    static List<String> TrueStrings = Arrays.asList(new String[]{
        "Venha pegar seu kit natura", 
        "Bebes tomam leite na mamadeira",
        "Cloroquina nao cura covid",
        "Me passa seu whatsapp",
        "Nesta longa estrada da vida"});

    static FakeNewsAnalyzer analyzer;


    public static void main(String[] args) {
    
        analyzer = new FakeNewsAnalyzer();
        FakeStrings_ReturnTrue();
        TrueStrings_ReturnFalse();

    }
    
    public static void FakeStrings_ReturnTrue(){

        int i =1;
        for(String string : FakeStrings){
            System.out.println(i + " Expected true, "+ FakeNewsAnalyzer.containsFakeNews(string));
            i++;
        }        

    }
    
    public static void TrueStrings_ReturnFalse(){

        int i =1;
        for(String string : TrueStrings){
            System.out.println(i + " Expected null, " + FakeNewsAnalyzer.containsFakeNews(string));
            i++;
        }        

    }
    
}