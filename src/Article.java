import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Article {
    private URL url;
    private String title;
    private String body;
    private String date;

    public Article(URL url) {
        this.url = url;
    }

    protected void refresh() throws IOException, SAXException, ParserConfigurationException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // défini la propriété dans la requete https pour avoir une réponse en xml
        conn.setRequestProperty("Accept", "application/xml");

        int status = conn.getResponseCode();

        if (status != 200) {
            System.out.println("erreur récupération d'article");
        } else {
            InputStream xml = conn.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml, url.toString());
            NodeList nl = doc.getElementsByTagName("article");
            Node article  = nl.item(0);
            // récupération des éléments en parsant le xml
            if (article.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) article;
                this.title = eElement.getElementsByTagName("title").item(0).getTextContent();
                this.body = eElement.getElementsByTagName("body").item(0).getTextContent();
                this.date = eElement.getElementsByTagName("date").item(0).getTextContent();
            }
        }
        conn.disconnect();
    }

    protected void update() throws IOException, ParserConfigurationException {
        URL url = this.url;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // défini la requête en put car modification
        conn.setRequestMethod("PUT");
        // défini la propriété pour dire qu'on envoie du xml
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
        titre.appendChild(doc.createTextNode(this.title));
        article.appendChild(titre);

        Element corps = doc.createElement("body");
        corps.appendChild(doc.createTextNode(this.body));
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

        if (status != 200) {
            System.out.println("erreur modification d'article");
        } else {
            System.out.println("article modifié");
        }
        conn.disconnect();
    }

    public URL getURL() {
        return url;
    }

    public String getTitle() throws IOException, ParserConfigurationException, SAXException {
        // appel dans un premier temps de refresh pour avoir des données à jour
        refresh();
        return title;
    }

    public String getBody() throws IOException, ParserConfigurationException, SAXException {
        // appel dans un premier temps de refresh pour avoir des données à jour
        refresh();
        return body;
    }

    public String getDate() throws IOException, ParserConfigurationException, SAXException {
        // appel dans un premier temps de refresh pour avoir des données à jour
        refresh();
        return date;
    }

    public void setTitle(String newTitle) throws IOException, ParserConfigurationException {
        // défini dans un premier dans le nouveau titre et appel la méthode update qui le récupéra pour modifié sur le blog
        this.title = newTitle;
        update();
    }

    public void setBody(String newBody) throws IOException, ParserConfigurationException {
        // défini dans un premier dans le nouveau titre et appel la méthode update qui le récupéra pour modifié sur le blog
        this.body = newBody;
        update();
    }

    public void delete() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // défini la réquete comme delete pour supprimer un élément
        conn.setRequestMethod("DELETE");

        int status = conn.getResponseCode();

        if (status != 200) {
            System.out.println("erreur récupération d'article");
        } else {
            System.out.println("élément supprimé allez voir !");
            // met les éléments à nul si il est bien supprimé
            this.title = null;
            this.body = null;
            this.date = null;
        }
    }
}
