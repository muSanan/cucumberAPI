package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.CustomResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import utilities.CashwiseAuthorization;
import utilities.Config;

public class CashwiseBankAccountTests {

    @Test
    public void getAllBankAccount(){
        String token = CashwiseAuthorization.getToken();
        Response response = RestAssured.given().auth().oauth2(token).
                get(Config.getValue("cashwiseApiUrl")+"api/myaccount/bankaccount");
        System.out.println(response.statusCode());
        //response.prettyPrint();

        int size = response.jsonPath().getInt("$.size()");
        System.out.println(size);

        for (int i = 0; i < size; i++){
           String bankAccountName = response.jsonPath().getString("["+i+"].bank_account_name");
           String typeOfPay = response.jsonPath().getString("["+i+"].type_of_pay");
           String balance = response.jsonPath().getString("["+i+"].balance");
           //System.out.println(balance);
           //System.out.println("__________$");
           Assert.assertFalse("bank account name is empty: " + i,bankAccountName.trim().isEmpty());
           Assert.assertFalse("type of pay is empty: " + i,typeOfPay.trim().isEmpty());
           Assert.assertFalse("balance is empty: " + i,balance.trim().isEmpty());
        }
    }

    @Test
    public void getAllAccounts() throws JsonProcessingException {
        String token = CashwiseAuthorization.getToken();
        String url = Config.getValue("cashwiseApiUrl") + "api/myaccount/bankaccount";

        Response response = RestAssured.given().auth().oauth2(token).get(url);

        ObjectMapper mapper = new ObjectMapper();
        CustomResponse[] customResponse = mapper.readValue(response.asString(), CustomResponse[].class);
        System.out.println(customResponse.length);

        for (int i = 0; i < customResponse.length; i++){
            int balance = customResponse[i].getBalance();
            String id = customResponse[i].getId();
            if (balance <= 1000){
                String url1 = Config.getValue("cashwiseApiUrl") + "api/myaccount/bankaccount/" + id;
                Response response1 = RestAssured.given().auth().oauth2(token).delete(url1);
                System.out.println(response1.statusCode());
                response1.prettyPrint();

                Assert.assertEquals(200,response1.statusCode());
            }
        }
    }

}
