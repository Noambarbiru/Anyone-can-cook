package com.example.anyonecancook;

import java.io.*;
import java.net.*;

import org.json.*;

public class ChatGPT {
    private static final String API_KEY = "${YOUR_API_KEY_HERE}";
    private static final String model = "gpt-3.5-turbo-0613";

    String prompt_before_recipe = "you are a Chef Remy, a bot that guides users through recipe making process.\n" +
            "The recipe consists ingredients and instructions and the instructions are divided to steps. \n" +
            "Here is the recipe between $ characters,\n";
    String prompt_after_recipe = "\n\n" +
            "You respond to the user's question in a short, informative and friendly style.\n" +
            "Your response must be up to 25 words. \n" +
            "Here is the user's question between # characters,";

    public ChatGPT() {
    }

    public String generateText(String question) throws Exception {
//        URL url = new URL("https://api.openai.com/v1/completions");
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        conn.setConnectTimeout(100000);
        conn.setReadTimeout(100000);

        JSONArray historyJsonArray = new JSONArray();
        String text_msg = prompt_before_recipe + "$" + EnterRecipeLink.recipeText + "$" + prompt_after_recipe + "#" + question + "? #";
        JSONObject messageObj = new JSONObject();
        messageObj.put("role", "user");
        messageObj.put("content", text_msg);
        historyJsonArray.put(messageObj);

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", historyJsonArray);
        payload.put("temperature", 0);
        payload.put("max_tokens", 300);

        OutputStream os = conn.getOutputStream();
        os.write(payload.toString().getBytes());
        os.flush();

        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader((conn.getInputStream())));
        } catch (Exception e) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        String response = "";
        String output;
        while ((output = br.readLine()) != null) {
            response += output;
        }

        conn.disconnect();

        JSONObject json = new JSONObject(response);
        String completion = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        return completion;
    }


    public String answer(String text) throws Exception {
        String answer = generateText(text);
        return answer;
    }
}
