import javax.xml.parsers.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class Blog {
    private URL url;

    // Constructeur
    public Blog(URL url) {
        this.url = url;
    }

    // Getter
    public URL getURL() {
        return url;
    }

    public Iterable<Article> iterArticles() throws IOException, ParserConfigurationException, SAXException {
        // je fonctionne avec des arraylist (choix arbitraire)
        ArrayList<Article> tabArticle = new ArrayList<Article>();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // défini la réponse en xml
        conn.setRequestProperty("Accept", "application/xml");

        int status = conn.getResponseCode(); // Recupère le status code

        if (status != 200) {
            System.out.println("erreur récupération d'article");
        } else {

            InputStream xml = conn.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml, url.toString());
            // parsage du xml
            NodeList nl = doc.getElementsByTagName("article");
            for(int i = 0; i < nl.getLength(); i++) {
                Element hrefArticle = (Element) nl.item(i);
                // recupère l'attribut href dans la balise article
                URL url = new URL(hrefArticle.getAttribute("href"));
                Article newArticle = new Article(url);
                tabArticle.add(newArticle);
            }
            conn.disconnect();
        }
        // return l'arraylist
        return tabArticle;
    }

    public Iterable<Article> iterArticles(String filtre) throws IOException, ParserConfigurationException, SAXException {
        ArrayList<Article> tabArticle = new ArrayList<Article>();
        // encode la chaine de caractère entrée en paramètre
        String parametre = "search=" + URLEncoder.encode(filtre, StandardCharsets.UTF_8.toString());
        // création du coup d'un nouvel url en récupérant l'ancien et on y concaténant le paramètre encoder
        URL newUrl = new URL(this.url.toString() + "?" + parametre);
        HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();
        // défini la réponse en xml
        conn.setRequestProperty("Accept", "application/xml");

        int status = conn.getResponseCode();

        if (status != 200) {
            System.out.println("erreur récupération d'article");
        } else {

            InputStream xml = conn.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml, url.toString());
            // parsage du xml
            NodeList nl = doc.getElementsByTagName("article");
            for(int i = 0; i < nl.getLength(); i++) {
                Element hrefArticle = (Element) nl.item(i);
                // recupère l'attribut href dans la balise article
                URL url = new URL(hrefArticle.getAttribute("href"));
                Article newArticle = new Article(url);
                tabArticle.add(newArticle);
            }
            conn.disconnect();
        }
        // return l'arraylist
        return tabArticle;
    }

    public Article createArticle(String title, String body) throws IOException, ParserConfigurationException, SAXException {
        URL url = this.url;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // envoie de donnée donc post
        conn.setRequestMethod("POST");
        // ajout de propriété comme quoi le contenu de la requête est du xml
        conn.addRequestProperty("content-type", "text/xml");
        conn.setDoOutput(true);
        OutputStream stream = conn.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);

        // création du xml
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element article = doc.createElement("article");
        doc.appendChild(article);

        Element titre = doc.createElement("title");
        titre.appendChild(doc.createTextNode(title));
        article.appendChild(titre);

        Element corps = doc.createElement("body");
        corps.appendChild(doc.createTextNode(body));
        article.appendChild(corps);

        DOMImplementationLS domImplementation =
                (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig()
                .setParameter("xml-declaration", Boolean.FALSE);
        String xml = lsSerializer.writeToString(doc);

        writer.write(xml);
        writer.close();

        int status = conn.getResponseCode();
        String result = "";
        BufferedReader reader = null;

        if (status != 201) {
            System.out.println("erreur création d'article");
        } else {
            // récupération de la réponse pour l'url de l'article crée
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                result+=ligne;
            }
        }
        URL urlArticle = new URL(result);
        Article newArticle = new Article(urlArticle);

        conn.disconnect();
        return newArticle;
    }
}
