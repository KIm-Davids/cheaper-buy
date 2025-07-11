package com.packages.scraperapi.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.packages.scraperapi.exceptions.ErrorMatchingProductException;
import com.packages.scraperapi.models.ProductResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.json.Json;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class BestMatchingProductResultImpl implements BestMatchingProductResultInterface{

    @Override
    public List<ProductResult> getBestMatchingTitles(String query, List<ProductResult> products) {
        Dotenv dotenv = Dotenv.load(); // This reads the .env file
        String OPEN_API_KEY = dotenv.get("OPENAI_API_KEY");

        String titlesList = "";
        for(int counter = 0; counter < products.size(); counter++){
            titlesList += (counter + 1) + "." + products.get(counter) + "\n";
        }

        StringBuilder productList = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            ProductResult p = products.get(i);
            productList.append(i + 1)
                    .append(". Title: ").append(p.title)
                    .append(" | Price: ").append(p.price)
                    .append(" | Link: ").append(p.link)
                    .append("\n");
        }

        String prompt = "User searched for: \"" + query + "\"\n\n" +
                "Here is a list of scraped products (with titles and prices):\n\n" +
                productList.toString() +
                "\nFrom the list above, return only the products that are **actual " + query +
                "** (not accessories).\nThen **sort by the lowest price**.\n\nRespond in JSON like:\n" +
                "[\n  {\"title\": \"...\", \"price\": \"...\", \"reason\": \"...\"},\n  ...\n]";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);

        ObjectNode payload = mapper.createObjectNode();
        payload.put("model","gpt-3.5-turbo");
        payload.putArray("messages").add(message);
        payload.put("temperature","0.2");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + OPEN_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        List<ProductResult> filteredResult = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🔎 Raw GPT API response:\n" + response.body()); // ADD THIS LINE
            JsonNode root = mapper.readTree(response.body());
            String content = root.get("choices").get(0).get("message").get("content").asText();

            JsonNode array = mapper.readTree(content);

            for(JsonNode node : array){
                String title = node.get("title").asText();
                String price = node.get("price").asText(); // ✅ Corrected
                String link = node.has("link") ? node.get("link").asText() : "";

                filteredResult.add(new ProductResult("", title, price, link));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ErrorMatchingProductException("An error occurred whilst matching the best product !!!");
        }

        return filteredResult;

    }


}
