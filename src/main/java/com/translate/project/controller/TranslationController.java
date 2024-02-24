package com.translate.project.controller;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.translate.project.model.ErrorResponse;
import com.translate.project.model.TranslationRequest;
import com.translate.project.model.TranslationResponse;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TranslationController {

    private static final String RAPIDAPI_BASE_URL = "https://text-translator2.p.rapidapi.com/translate";
    private static final String RAPIDAPI_API_KEY = "3370a1739dmsh58965d18ef789bfp19de1fjsn3a66fb586775";

    @PostMapping("/translate")
    public ResponseEntity<?> translateText(@RequestBody TranslationRequest request) {
        if (request.getText() == null || request.getText().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Missing 'text' key in the request body"));
        }

        try {
            // Translate the text using RapidAPI Text-Translation2 API
            String url = RAPIDAPI_BASE_URL;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Host", "text-translator2.p.rapidapi.com");
            headers.set("X-RapidAPI-Key", RAPIDAPI_API_KEY);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("text", request.getText());
            body.add("target_language", "fr"); // French translation
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            String translatedText = responseEntity.getBody();

            JsonObject jsonObject = JsonParser.parseString(translatedText).getAsJsonObject();
            JsonObject jsonObject1 =jsonObject.getAsJsonObject("data");
            String translation= String.valueOf(jsonObject1.get("translatedText")).replaceAll("\"", "");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new TranslationResponse(translation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Translation failed: " + e.getMessage()));
        }
    }
}
