package com.zebsoft;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length != 1) {
      throw new IllegalArgumentException("usage: github-activity <username>");
    }
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(String.format("https://api.github.com/users/%s/events", args[0])))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    printActivity(new ObjectMapper().readTree(response.body()));
  }

  private static void printActivity(JsonNode root) {
    if (root.isArray()) {
      for (JsonNode githubElelemnt : root) {
        String type = githubElelemnt.path("type").asText();
        String repo = githubElelemnt.path("repo").path("name").asText();
        switch (type) {
          case "ForkEvent":
            System.out.println(String.format("Fork the repo %s", repo));
            break;
          case "PushEvent":
            System.out.println(
                String.format("Pushed %d commits to %s", githubElelemnt.path("payload").path("commits").size(), repo));
            break;
          case "WatchEvent":
            System.out.println("Starred " + repo);
            break;
          case "CreateEvent":
            System.out.println(
                String.format("Created %s '%s' in %s", githubElelemnt.path("payload").path("ref_type").asText(),
                    githubElelemnt.path("payload").path("ref").asText(), repo));
            break;
          default:
            break;
        }
      }
    }
  }
}