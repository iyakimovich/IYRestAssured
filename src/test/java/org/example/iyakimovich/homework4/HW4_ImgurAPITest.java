package org.example.iyakimovich.homework4;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.iyakimovich.ImgurAPIProps;
import org.example.iyakimovich.homework4.dto.CommentDTO;
import org.example.iyakimovich.homework4.dto.ImageDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HW4_ImgurAPITest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HW4_ImgurAPITest.class);

    private static final Properties props = new Properties();

    private static String clientID;
    private static String clientSecret;
    private static String accessToken;
    private static String username;
    private static String imageId;
    private static String pin;

    //private static Map<String, String> headers = new HashMap<>();

    private static ResponseSpecification responseSpecHTTP200;
    private static ResponseSpecification responseSpecHTTP409;
    private static RequestSpecification requestSpecHeaders;

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

        //headers.put("Authorization", "Bearer " + accessToken);

        responseSpecHTTP200 = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();

        responseSpecHTTP409 = new ResponseSpecBuilder()
                .expectStatusCode(409)
                .expectContentType(ContentType.JSON)
                .build();

        requestSpecHeaders = new RequestSpecBuilder().
                addHeader("Authorization", "Bearer " + accessToken)
                .build();

        //use headers globally
        RestAssured.requestSpecification = requestSpecHeaders;

        LOGGER.info("runBeforeAllTests() - done");
    }


    @Test
    public void testAccountImageCount() {
        LOGGER.info("testAccountImageCount(): started");
        int count = given()
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/images/count", username)
                .then()
                .spec(responseSpecHTTP200)
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
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/images/ids/", username)
                .then()
                .spec(responseSpecHTTP200)
                .extract()
                .response()
                .jsonPath()
                .getList("data");

        LOGGER.info("Count of images returned : " + list.size());

        String imageId = list.get(0);
        LOGGER.info("First image ID : " + imageId);

        //Using POJO and serialization
        ImageDTO image = given()
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/image/{imageId}", username, imageId)
                .then()
                .spec(responseSpecHTTP200)
                .extract()
                .body()
                .as(ImageDTO.class);

        LOGGER.info("First image link : " + image.getImageData().getLink());
        LOGGER.info("testAccountImageIDs(): completed");
    }

    @Test
    public void testAccountComments() {
        LOGGER.info("testAccountComments(): started");
        CommentDTO comment = given()
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/comments", username)
                .then()
                .spec(responseSpecHTTP200)
                .extract()
                .body()
                .as(CommentDTO.class);

        LOGGER.info("First comment: " + comment.getCommentData()[0].getComment());
        LOGGER.info("testAccountComments(): completed");
    }

    @Test
    public void testAccountTags() {
        LOGGER.info("testAccountTags(): started");
        String error = given()
                .when()
                .post(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/moscow")
                .then()
                .spec(responseSpecHTTP409)
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");

        LOGGER.info("Error message: " + error);

        given()
                //.headers(headers)
                .when()
                .post(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/funny")
                .then()
                .spec(responseSpecHTTP200);

        LOGGER.info("Tag #funny: followed!");

        given()
                .when()
                .delete(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/funny")
                .then()
                .spec(responseSpecHTTP200);

        LOGGER.info("Tag #funny: unfollowed!");

        given()
                //.headers(headers)
                .when()
                .delete(ImgurAPIProps.API_ACCOUNT_URL + "/me/follow/tag/ny")
                .then()
                .spec(responseSpecHTTP409);

        LOGGER.info("Tag #ny: is not followed!");

        LOGGER.info("testAccountTags(): completed");
    }

    @Test
    public void testAccountAvatar() {
        LOGGER.info("testAccountAvatar(): started");
        String avatarName = given()
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/avatar", username)
                .then()
                .spec(responseSpecHTTP200)
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
                .when()
                .get(ImgurAPIProps.API_ACCOUNT_URL + "/{username}/favorites/0", username)
                .then()
                .spec(responseSpecHTTP200)
                .extract()
                .response()
                .jsonPath()
                .getString("data[0].cover");

        LOGGER.info("Favorite name ID: " + favImageId);
        assertEquals(favImageId, imageId);

        LOGGER.info("testAccountFavorites(): completed");
    }

}
