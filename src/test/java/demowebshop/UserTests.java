package demowebshop;


import com.codeborne.selenide.WebDriverRunner;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class UserTests extends TestBase {
    Faker faker = new Faker();
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    @BeforeEach
    void setUpUser() {
        password = faker.numerify("##########");
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
    }

    @AfterEach
    public void clearBrowser() {
        clearBrowserCookies();
        clearBrowserLocalStorage();
    }

    @Test
    @Feature("Регистрация пользователя")
    @Story("Регистрация пользователя с цифровым паролем ")
    @Owner("Pavel Ushakov")
    @Severity(SeverityLevel.BLOCKER)
    @Link(value = "Testing", url = "https://demowebshop.tricentis.com/register")
    @DisplayName("Регистрация пользователя UI тест")
    void registerNewUserTest() {

        step("Открываем страницу регистрации пользователя", () -> {
            open("/register");
        });

        step("Заполняем обязательные поля формы регистрации", () -> {
            $("#FirstName").setValue(firstName);
            $("#LastName").setValue(lastName);
            $("#Email").setValue(email);
            $("#Password").setValue(password);
            $("#ConfirmPassword").setValue(password);
        });

        step("Отправляем заполненную форму на сервер", () -> {
            $("#register-button").click();
        });

        step("Проверяем, что пользователь зарегистрирован", this::checkUserCreated);
    }

    @Test
    @Feature("Регистрация пользователя")
    @Story("Регистрация пользователя с цифровым паролем ")
    @Owner("Pavel Ushakov")
    @Severity(SeverityLevel.BLOCKER)
    @Link(value = "Testing", url = "https://demowebshop.tricentis.com/register")
    @DisplayName("Регистрация пользователя API тест")
    void registerNewUserAPITest() {
        String authCookieName = "NOPCOMMERCE.AUTH";

        String authCookieValue = step("Регистрируем пользователя через API ", () -> given()
                .when()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .formParam("__RequestVerificationToken", "10QcxkN4-Gk5PEeZlTrtVTuN7xtnRi_RY4ssN4Kd1kn--wsjFIdx3MtZG3cs6EsIYcWSCd3dIikpNcaAeVkJyMaRx50q_u84GfmcWQFqcqw1")
                .formParam("FirstName", firstName)
                .formParam("LastName", lastName)
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("ConfirmPassword", password)
                .cookie("__RequestVerificationToken", "at9THwDl4iOCtU40qL9aL87W4x6vnP7C7vDFXJ6VVruf0QlYjJGo4vKOOZ37as2KBsbYUCMAENIXnFqvW6QHp-85oL4JZadn5TQu5MPCDv41;")
                .post("/register")
                .then()
                .extract().cookie(authCookieName));


        step("Подкладываем куки созданного пользователя", () -> {
            open("/Themes/DefaultClean/Content/images/logo.png");
            Cookie authCookie = new Cookie(authCookieName, authCookieValue);
            WebDriverRunner.getWebDriver().manage().addCookie(authCookie);
        });

        step("Открываем сайт", () -> {
            open("/registerresult/1");
        });

        step("Проверяем, что пользователь зарегистрирован", this::checkUserCreated);
    }

    private void checkUserCreated() {
        $(".result").should(appear).shouldHave(text("Your registration completed"));
        $(".account").shouldBe(visible).shouldHave(text(email));
    }

}
