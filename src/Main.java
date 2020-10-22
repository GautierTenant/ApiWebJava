import java.net.URL;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            // Création du blog (constructeur)
            URL url = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/");
            Blog ephemere = new Blog(url);

            // Test createArticle
            //Article a = ephemere.createArticle("testUpdate", "Merci");

            /* Test constructeur Article -> url valide à trouver !
            URL urlA = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/cirutawu");
            Article a = new Article(urlA); */

            /* Test méthode refresh dans les getters
            String titre = a.getTitle();
            String body = a.getBody();
            String date = a.getDate();
            System.out.println("Titre : " + titre);
            System.out.println("Body : " + body);
            System.out.println("Date : " + date); */

            /* Test méthode delete
            a.delete(); */

            /* Test méthode update dans les setters
            a.setTitle("changetitre");*/

            // Test iterArticle avec et sans filtre
            ArrayList<Article> tabArticle;
            tabArticle = (ArrayList<Article>) ephemere.iterArticles("gaut");
            for (Article unArticle : tabArticle) {
                System.out.println("------------------");
                System.out.println("Titre :" + unArticle.getTitle());
                System.out.println("Body : " + unArticle.getBody());
                System.out.println("Date : " + unArticle.getDate());
                // test de combinaison des deux méthodes
                unArticle.setBody("modif body");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}