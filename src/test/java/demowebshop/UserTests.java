package demowebshop;


import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class UserTests extends TestBase {
    Faker faker = new Faker();
    private final String PASSWORD = faker.numerify("##########");
    private final String FIRSTNAME = faker.name().firstName();
    private final String LASTNAME = faker.name().lastName();
    private final String EMAIL = faker.internet().emailAddress();


        @Test
        @Feature("Регистрация пользователя")
        @Story("Регистрация пользователя с цифровым паролем ")
        @Owner("Pavel Ushakov")
        @Severity(SeverityLevel.BLOCKER)
        @Link(value = "Testing", url = "https://demowebshop.tricentis.com/register")
        @DisplayName("Регистрация пользователя")
        void registerNewUserTest() {

            step("Открываем страницу регистрации пользователя", () -> {
                open("/register");
            });

            step("Заполняем обязательные поля формы регистрации", () -> {
                $("#FirstName").setValue(FIRSTNAME);
                $("#LastName").setValue(LASTNAME);
                $("#Email").setValue(EMAIL);
                $("#Password").setValue(PASSWORD);
                $("#ConfirmPassword").setValue(PASSWORD);
            });

            step("Отправляем заполненную форму на сервер", () -> {
                $("#register-button").click();
            });

            step("Проверяем, что пользователь зарегистрирован", () -> {
                $(".result").should(appear).shouldHave(text("Your registration completed"));
                $(".account").shouldBe(visible).shouldHave(text(EMAIL));
            });
        }
    }
