package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import entities.CustomResponse;
import entities.RequestBody;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import utilities.APIRunner;
import utilities.CashwiseAuthorization;
import utilities.Config;

import java.util.HashMap;
import java.util.Map;

public class CashwiseSellersTests {
    @Test
    public void getSingleSeller(){
        int id = 118;
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl") + "api/myaccount/sellers/";
        Response response = RestAssured.given().auth().oauth2(token).get(url+id);
        System.out.println(response.statusCode());
        response.prettyPrint();
    }
    @Test
    public void getListOfSellers() throws JsonProcessingException {
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl") + "api/myaccount/sellers/";

        Map<String,Object> params = new HashMap<>();
        params.put("page",1);
        params.put("size",45);
        params.put("isArchived",false);

        Response response = RestAssured.given().auth().oauth2(token).params(params).get(url);
        System.out.println(response.statusCode());
        //response.prettyPrint();

        ObjectMapper mapper = new ObjectMapper();
        CustomResponse customResponse = mapper.readValue(response.asString(),CustomResponse.class);
        int size = customResponse.getResponses().size();
        for (int i = 0; i < size; i++){
            System.out.println(customResponse.getResponses().get(i).getEmail());
        }
    }
    @Test
    public void createSeller() throws JsonProcessingException {
        Faker faker = new Faker();
        RequestBody requestBody = new RequestBody();
        requestBody.setCompany_name(faker.company().name());
        requestBody.setSeller_name(faker.name().firstName()+" "+faker.name().lastName());
        requestBody.setEmail(faker.internet().emailAddress());
        requestBody.setPhone_number("333333333");
        requestBody.setAddress("Chicago");
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl")+"api/myaccount/sellers";

        Response response = RestAssured.given().auth().oauth2(token).contentType(ContentType.JSON).
                body(requestBody).post(url);
        System.out.println(response.statusCode());
        response.prettyPrint();

        ObjectMapper mapper = new ObjectMapper();
        CustomResponse customResponse = mapper.readValue(response.asString(), CustomResponse.class);
        System.out.println(customResponse.getSeller_id());
        int id = customResponse.getSeller_id();
        String token2 = CashwiseAuthorization.getToken();
        String url2 = Config.getValue("cashwiseApiUrl")+"api/myaccount/sellers/" + id;

        System.out.println(token2);
        System.out.println(url2);
        Response response1 = RestAssured.given().auth().oauth2(token2).get(url2);
        response1.prettyPrint();

        Assert.assertEquals(200,response1.statusCode());
    }

    @Test
    public void createManySellers(){
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl")+"api/myaccount/sellers";
        Faker faker = new Faker();

        for (int i = 0; i < 15; i++){
            RequestBody requestBody = new RequestBody();
            requestBody.setCompany_name(faker.company().name());
            requestBody.setSeller_name(faker.name().fullName());
            requestBody.setEmail(faker.internet().emailAddress());
            requestBody.setAddress(faker.address().fullAddress());
            requestBody.setPhone_number(faker.phoneNumber().phoneNumber());

            Response response = RestAssured.given().auth().oauth2(token).contentType(ContentType.JSON).
                    body(requestBody).post(url);
            System.out.println(response.statusCode());
        }
    }
    @Test
    public void deleteCertainSellers() throws JsonProcessingException {
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl") + "api/myaccount/sellers/";

        Map<String,Object> params = new HashMap<>();
        params.put("page",1);
        params.put("size",45);
        params.put("isArchived",false);

        Response response = RestAssured.given().auth().oauth2(token).params(params).get(url);
        System.out.println(response.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        CustomResponse customResponse = mapper.readValue(response.asString(),CustomResponse.class);
        int size = customResponse.getResponses().size();
        for (int i = 0; i < size; i++){
            if (customResponse.getResponses().get(i).getEmail().endsWith("hotmail.com")){
                String seller_name = customResponse.getResponses().get(i).getSeller_name();
                int id = customResponse.getResponses().get(i).getSeller_id();
                System.out.println("seller_name: " + seller_name + ",id: " + id + ", email: " + customResponse.getResponses().get(i).getEmail());

                String urlForArchive = Config.getValue("cashwiseApiUrl") + "api/myaccount/sellers/archive/unarchive";
                Map<String,Object> paramsArchive = new HashMap<>();
                paramsArchive.put("sellersIdsForArchive",id);
                paramsArchive.put("archive",true);
                Response response1 = RestAssured.given().auth().oauth2(token).params(paramsArchive).post(urlForArchive);

                System.out.println(response1.statusCode());
                response1.prettyPrint();
            }
        }
    }
    @Test
    public void getSellers(){
        String path = "api/myaccount/sellers/169";
        APIRunner.runGET(path);
        System.out.println(APIRunner.getCustomResponse().getSeller_name());
        System.out.println(APIRunner.getCustomResponse().getEmail());
    }

    @Test
    public void getSellersList(){
        String path = "api/myaccount/sellers";
        Map<String, Object> params = new HashMap<>();
        params.put("isArchived", false);
        params.put("page", 1);
        params.put("size", 50);
        APIRunner.runGET(path, params);

        for (CustomResponse cr: APIRunner.getCustomResponse().getResponses()){
            System.out.println(cr.getCompany_name());
        }
    }
    @Test
    public void createNewSeller(){
        String path = "api/myaccount/sellers";
        RequestBody body = new RequestBody();
        body.setCompany_name("Gooogle");
        body.setSeller_name("Usik");
        body.setEmail("usik.gmail.com");
        body.setPhone_number("8888888888");
        body.setAddress("Earth");

        APIRunner.runPOST(path, body);
        System.out.println(APIRunner.getCustomResponse().getResponseBody());
    }
    @Test
    public void singleSellerCreation(){
        String pathForPost = "api/myaccount/sellers";
        Faker faker = new Faker();
        String companyName = faker.company().name();
        String sellerName = faker.name().fullName();
        String email = faker.internet().emailAddress();
        String phone = faker.phoneNumber().phoneNumber();

        RequestBody body = new RequestBody();
        body.setCompany_name(companyName);
        body.setSeller_name(sellerName);
        body.setEmail(email);
        body.setPhone_number(phone);

        APIRunner.runPOST(pathForPost, body);

        int id = APIRunner.getCustomResponse().getSeller_id();
        String urlForGet = "api/myaccount/sellers/" + id;
        APIRunner.runGET(urlForGet);

        Assert.assertEquals(companyName, APIRunner.getCustomResponse().getCompany_name());
        Assert.assertEquals(sellerName, APIRunner.getCustomResponse().getSeller_name());
        Assert.assertEquals(email, APIRunner.getCustomResponse().getEmail());
        Assert.assertEquals(phone, APIRunner.getCustomResponse().getPhone_number());


    }

}
