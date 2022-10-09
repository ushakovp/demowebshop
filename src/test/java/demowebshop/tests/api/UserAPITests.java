package demowebshop.tests.api;

import com.codeborne.selenide.WebDriverRunner;
import com.github.javafaker.Faker;
import demowebshop.pages.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@DisplayName("API User tests")
public class UserAPITests {

    private final String authCookieName = "NOPCOMMERCE.AUTH";
    private final String verificationTokenName = "__RequestVerificationToken";
    private final String verificationTokenInputValue = "10QcxkN4-Gk5PEeZlTrtVTuN7xtnRi_RY4ssN4Kd1kn--wsjFIdx3MtZG3cs6EsIYcWSCd3dIikpNcaAeVkJyMaRx50q_u84GfmcWQFqcqw1";
    private final String verificationTokenHeaderValue = "at9THwDl4iOCtU40qL9aL87W4x6vnP7C7vDFXJ6VVruf0QlYjJGo4vKOOZ37as2KBsbYUCMAENIXnFqvW6QHp-85oL4JZadn5TQu5MPCDv41;";
    Faker faker = new Faker();
    User user = new User();
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String anotherEmail;
    private String anotherName;
    private String anotherLastName;

    @BeforeEach
    void setUpUser() {
        password = faker.numerify("##########");
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
        anotherEmail = faker.internet().emailAddress();
        anotherName = faker.name().firstName();
        anotherLastName = faker.name().lastName();
    }

    @Test
    @Feature("Регистрация пользователя")
    @Story("Регистрация пользователя с цифровым паролем ")
    @Owner("Pavel Ushakov")
    @Severity(SeverityLevel.BLOCKER)
    @Link(value = "Testing", url = "https://demowebshop.tricentis.com/register")
    @DisplayName("Регистрация пользователя API тест")
    void registerNewUserAPITest() {
        Response response1 = step("Регистрируем пользователя через API ", () -> given()
                .when()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .formParam(verificationTokenName, verificationTokenInputValue)
                .formParam("FirstName", firstName)
                .formParam("LastName", lastName)
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("ConfirmPassword", password)
                .cookie(verificationTokenName, verificationTokenHeaderValue)
                .post("https://demowebshop.tricentis.com/register"));


        String actualBody = step("Получаем страницу подтверждения регистрации ", () -> given()
                .cookie(response1.getDetailedCookie(authCookieName))
                .expect()
                .statusCode(200)
                .when()
                .get("https://demowebshop.tricentis.com/" + response1.getHeader("Location"))
                .then().extract().body().asString());


        step("Проверяем, что пользователь зарегистрирован", () -> {
            assertThat(actualBody, containsString(email));
        });

    }

    @Test
    @Disabled
    @Feature("Редактирование профиля")
    @Story("Зарегистрированный пользователь может редактировать профиль")
    @Owner("Pavel Ushakov")
    @Severity(SeverityLevel.BLOCKER)
    @Link(value = "Testing", url = "https://demowebshop.tricentis.com/info")
    @DisplayName("Редактирование профиля API тест")
    void userCanModifyProfileAPITest() {
        String authCookieValue = step("Регистрируем пользователя через API ", () -> given()
                .when()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .formParam(verificationTokenName, verificationTokenInputValue)
                .formParam("FirstName", firstName)
                .formParam("LastName", lastName)
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("ConfirmPassword", password)
                .cookie(verificationTokenName, verificationTokenHeaderValue)
                .post("/register")
                .then()
                .extract().cookie(authCookieName));

        step("Подкладываем куки созданного пользователя", () -> {
            open("/Themes/DefaultClean/Content/images/logo.png");
            Cookie authCookie = new Cookie(authCookieName, authCookieValue);
            WebDriverRunner.getWebDriver().manage().addCookie(authCookie);
        });

        step("Открываем профиль", () -> {
            open("/customer/info");
        });

        step("Заполняем профиль новыми занчениями", () -> {
            user
                    .setFirstName(anotherName)
                    .setLastName(anotherLastName)
                    .setEmail(anotherEmail);
        });

        step("Сохраняем профиль", () -> {
            user.saveProfile();
        });

        step("Повторно открываем профиль", () -> {
            user.openProfile();
        });

        step("Проверяем, что изменения сохранились", () -> {
            user.checkChangesSaved(anotherEmail, anotherName, anotherLastName);
        });
    }
}
