package com.clickclack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public HuggingFaceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generatePoem(String topic) {
        String prompt = "Escreva em pt-br um poema bonito sobre " + topic;
        return generateText("gpt2", prompt);
    }


    public String generateLetter(String recipient, String message) {
        String prompt = "Escreva em pt-br uma carta sincera para " + recipient + ". A mensagem é: " + message;
        return generateText("gpt2", prompt);
    }


    public String generateText(String modelName, String inputText) {
        // URL da API do Hugging Face
        String url = apiUrl + modelName;

        // Configuração dos cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Corpo da requisição
        Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputText);

        // Requisição HTTP
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Chamada à API
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Parse do JSON retornado
        String responseBody = response.getBody();
        if (responseBody != null) {
            // Extrair o texto gerado da resposta JSON
            // Supondo que a resposta seja um array de objetos, onde o campo "generated_text" é o que queremos
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Parse o JSON em uma lista de objetos
                List<Map<String, String>> jsonResponse = objectMapper.readValue(responseBody, List.class);
                // Retorna o texto gerado (extraímos o valor de "generated_text")
                return jsonResponse.get(0).get("generated_text");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null; // Retorna null caso o processamento falhe
    }

}
