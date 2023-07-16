package com.example.anyonecancook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Crawler {

    public String title;
    public ArrayList<String> ingredients;
    public ArrayList<String> instructions;

    public Crawler(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();

        Elements title_element = doc.select("h1.comp.type--lion.article-heading.mntl-text-block");
        title = title_element.get(0).text();
        Elements ingredients_elements = doc.select("li.mntl-structured-ingredients__list-item");
        ingredients = new ArrayList<String>();
        for (Element e :
                ingredients_elements) {
            ingredients.add(e.text().trim());
        }
        Elements steps_blocks = doc.select("li.comp.mntl-sc-block-group--LI.mntl-sc-block.mntl-sc-block-startgroup");
        ArrayList<String> steps = new ArrayList<String>();
        instructions = new ArrayList<String>();
        for (Element e :
                steps_blocks) {
            for (Element i :
                    e.select("p.comp.mntl-sc-block.mntl-sc-block-html")) {
                steps.add(i.text().trim());
            }
        }
        for (String step :
                steps) {
            String[] curr = step.split("\\.");
            ArrayList<String> temp = new ArrayList<String>();
            int i = 0;
            while (i < curr.length) {
                String splited = curr[i] + ".";
                if (i + 1 < curr.length && curr[i + 1].trim().charAt(0) == '(') {
                    splited = splited + " ";
                    splited = splited + curr[i + 1] + ".";
                    i = i + 1;
                }
                temp.add(splited.trim());
                i = i + 1;
            }
            i = 0;
            while (i < temp.size()) {
                String splited = temp.get(i);
                while (i + 1 < temp.size() && splited.split("\\(").length - 1 > splited.split("\\)").length - 1) {
                    splited = splited + " ";
                    splited = splited + temp.get(i + 1);
                    i = i + 1;
                }
                instructions.add(splited.trim());
                i = i + 1;
            }
        }
    }
}



