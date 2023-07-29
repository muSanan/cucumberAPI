package utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.CustomResponse;
import entities.RequestBody;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;


public class APIRunner {
    private static CustomResponse customResponse;
    public static void runGET(String path){
        String url = Config.getValue("cashwiseApiUrl") + path;
        String token = CashwiseAuthorization.getToken();
        Response response = RestAssured.given().auth().oauth2(token).get(url);
        System.out.println("Status code: " + response.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        try {
            customResponse = mapper.
                    readValue(response.asString(), CustomResponse.class);
        } catch (JsonProcessingException e) {
            System.out.println("Probably list response");
        }
    }
    public static void runGET(String path, Map<String, Object> params){
        String url = Config.getValue("cashwiseApiUrl") + path;
        String token = CashwiseAuthorization.getToken();
        Response response = RestAssured.given().auth().oauth2(token).params(params).get(url);
        System.out.println("Status code: " + response.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        try {
            customResponse = mapper.
                    readValue(response.asString(), CustomResponse.class);
            customResponse.setResponseBody(response.asString());
        } catch (JsonProcessingException e) {
            System.out.println("Probably list response");
        }
    }
    public static CustomResponse getCustomResponse(){
        return customResponse;
    }
    public static void runPOST(String path, RequestBody requestBody){
        String url = Config.getValue("cashwiseApiUrl") + path;
        String token = CashwiseAuthorization.getToken();
        Response response = RestAssured.given().auth().oauth2(token).
                contentType(ContentType.JSON).body(requestBody).post(url);

        System.out.println("Status code: " + response.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        try {
            customResponse = mapper.
                    readValue(response.asString(), CustomResponse.class);
            customResponse.setResponseBody(response.asString());
        } catch (JsonProcessingException e) {
            System.out.println("Probably list response");
        }
    }
}
