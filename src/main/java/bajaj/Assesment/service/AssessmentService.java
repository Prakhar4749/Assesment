package bajaj.Assesment.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Service
public class AssessmentService {

    public void executeAssessment() {
        try {
            System.out.println(">>> Starting Assessment Execution...");

            RestTemplate restTemplate = new RestTemplate();
            String genUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            System.out.println(">>> Webhook generation URL: " + genUrl);

            Map<String, Object> body = new HashMap<>();
            body.put("name", "Prakhar Sakhare");
            body.put("regNo", "0101IT221051");  // your odd regNo
            body.put("email", "prakharsakhare2226@gmail.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            System.out.println(">>> Sending request to generate webhook...");
            ResponseEntity<Map> response = restTemplate.postForEntity(genUrl, entity, Map.class);
            System.out.println(">>> Response from webhook generation: " + response);

            if (response.getBody() == null) {
                throw new RuntimeException("Response body is null");
            }

            Object webhookObj = response.getBody().get("webhook");
            Object tokenObj = response.getBody().get("accessToken");

            if (webhookObj == null || tokenObj == null) {
                throw new RuntimeException("webhookUrl or accessToken missing: " + response.getBody());
            }

            String webhookUrl = webhookObj.toString();
            String accessToken = tokenObj.toString();

            System.out.println(">>> Webhook URL: " + webhookUrl);
            System.out.println(">>> Access Token: " + accessToken);

            String sqlQuery = "SELECT p.amount AS SALARY, " +
                    "CONCAT(e.firstname, ' ', e.lastname) AS NAME, " +
                    "FLOOR(DATEDIFF(CURRENT_DATE, e.dob) / 365) AS AGE, " +
                    "d.departmentname AS DEPARTMENTNAME " +
                    "FROM payments p " +
                    "JOIN employee e ON p.empid = e.empid " +
                    "JOIN department d ON e.department = d.departmentid " +
                    "WHERE DAY(p.paymenttime) != 1 " +
                    "AND p.amount = ( " +
                    "    SELECT MAX(amount) " +
                    "    FROM payments " +
                    "    WHERE DAY(paymenttime) != 1 " +
                    ");";

            System.out.println(">>> Prepared SQL Query: " + sqlQuery);

            Map<String, String> ansPayload = new HashMap<>();
            ansPayload.put("finalQuery", sqlQuery);

            HttpHeaders answerHeaders = new HttpHeaders();
            answerHeaders.setContentType(MediaType.APPLICATION_JSON);
            answerHeaders.set("Authorization", accessToken);

            HttpEntity<Map<String, String>> ansEntity = new HttpEntity<>(ansPayload, answerHeaders);

            System.out.println(">>> Submitting final query to webhook...");
            ResponseEntity<String> submitResp = restTemplate.postForEntity(webhookUrl, ansEntity, String.class);

            System.out.println(">>> Submission Response: " + submitResp.getBody());

            System.out.println(">>> Assessment Execution Completed Successfully!");

        } catch (Exception e) {
            System.err.println(">>> Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
