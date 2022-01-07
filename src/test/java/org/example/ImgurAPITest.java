package org.example;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class ImgurAPITest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ImgurAPITest.class);

    private static final Properties props = new Properties();

    private static String clientID;
    private static String clientSecret;
    private static String accessToken;
    private static String username;
    private static String imageId;
    private static String pin;

    static Map<String, String> headers = new HashMap<>();

    @BeforeAll
    public static void runBeforeAllTests() throws IOException {
        LOGGER.info("runBeforeAllTests() - started");

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());

        FileInputStream fis = new FileInputStream(ImgurAPIProps.PROPS_FILE);
        props.load(fis);

        clientID = props.getProperty("clientID");
        clientSecret = props.getProperty("clientSecret");
        accessToken = props.getProperty("accessToken");
        username = props.getProperty("username");
        imageId = props.getProperty("imageId");
        pin = props.getProperty("pin");

        headers.put("Authorization", "Bearer " + accessToken);

        LOGGER.info("runBeforeAllTests() - done");
    }


    @Test
    public void testAccountImageCount() {
        LOGGER.info("testAccountImageCount(): started");
        int count = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/images/count", username)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getInt("data");

        LOGGER.info("Images count: " + count);
        assertTrue(count > 1);
        LOGGER.info("testAccountImageCount(): completed");
    }

    @Test
    public void testAccountImageIDs() {
        LOGGER.info("testAccountImageIDs(): started");
        List<String> list = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/images/ids/", username)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getList("data");

        LOGGER.info("Count of images returned : " + list.size());

        String imageId = list.get(0);
        LOGGER.info("First image ID : " + imageId);

        String imageLink = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/image/{imageId}", username, imageId)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.link");

        LOGGER.info("First image link : " + imageLink);
        LOGGER.info("testAccountImageIDs(): completed");
    }

    @Test
    public void testAccountComments() {
        LOGGER.info("testAccountComments(): started");
        String comment = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/comments", username)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data[0].comment");

        LOGGER.info("First comment: " + comment);
        LOGGER.info("testAccountComments(): completed");
    }

    @Test
    public void testAccountTags() {
        LOGGER.info("testAccountTags(): started");
        String error = given()
                .headers(headers)
                .when()
                .post(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/moscow")
                .then()
                .statusCode(409)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");

        LOGGER.info("Error message: " + error);

        given()
                .headers(headers)
                .when()
                .post(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/funny")
                .then()
                .statusCode(200);

        LOGGER.info("Tag #funny: followed!");

        given()
                .headers(headers)
                .when()
                .delete(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/funny")
                .then()
                .statusCode(200);

        LOGGER.info("Tag #funny: unfollowed!");

        given()
                .headers(headers)
                .when()
                .delete(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/ny")
                .then()
                .statusCode(409);

        LOGGER.info("Tag #ny: is not followed!");

        LOGGER.info("testAccountTags(): completed");
    }

    @Test
    public void testAccountAvatar() {
        LOGGER.info("testAccountAvatar(): started");
        String avatarName = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/avatar", username)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.avatar_name");

        assertEquals(avatarName, "flavor/face-10");

        LOGGER.info("Avatar name: " + avatarName);
        LOGGER.info("testAccountAvatar(): completed");
    }

    @Test
    public void testAccountFavorites() {
        LOGGER.info("testAccountFavorites(): started");
        String favImageId = given()
                .headers(headers)
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/favorites/0", username)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data[0].cover");

        LOGGER.info("Favorite name ID: " + favImageId);
        assertEquals(favImageId, imageId);

        LOGGER.info("testAccountFavorites(): completed");
    }

}
