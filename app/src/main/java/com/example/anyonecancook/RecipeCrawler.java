package com.example.anyonecancook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RecipeCrawler {
    String name;
    ArrayList<String> urls;

    public RecipeCrawler(String recipe_name) throws IOException {
        name = recipe_name;
        urls = new ArrayList<String>();
        String link = "https://www.allrecipes.com/search?q=simple+";
        link = link + recipe_name.trim().replace(" ", "+");
        Document doc = Jsoup.connect(link).get();

        Elements recipes = doc.select("a.comp.mntl-card-list-items.mntl-document-card.mntl-card.card.card--no-image");
        for (Element element : recipes) {
            String element_link = element.attributes().get("href");
            if (element_link.contains("allrecipes.com/recipe/")) {
                urls.add(element_link);
                if (urls.size() >= 10) {
                    break;
                }
            }
        }
    }

    public String get_url() {
        if (urls.size() == 0) {
            return "";
        }
        Random rand = new Random();
        return urls.get(rand.nextInt(urls.size()));
    }
}
