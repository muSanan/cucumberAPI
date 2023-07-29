package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.CustomResponse;
import entities.RequestBody;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import utilities.CashwiseAuthorization;
import utilities.Config;

public class CashwiseCategoriesTests {

    @Test
    public void createCategory() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        RequestBody requestBody = new RequestBody();
        requestBody.setCategory_title("online consulting");
        requestBody.setCategory_description("for online customers");
        requestBody.setFlag(true);
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl")+"api/myaccount/categories";

        Response response = RestAssured.given().auth().oauth2(token).contentType(ContentType.JSON).
                body(requestBody).post(url);
        System.out.println(response.statusCode());
        response.prettyPrint();

        ObjectMapper mapper = new ObjectMapper();
        CustomResponse customResponse = mapper.readValue(response.asString(), CustomResponse.class);
        System.out.println(customResponse.getCategory_id());
        System.out.println(customResponse.getCreated());
    }

}
