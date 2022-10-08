package demowebshop;


import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class UserTests extends TestBase {
    Faker faker = new Faker();
    private final String PASSWORD = faker.numerify("##########");
    private final String FIRSTNAME = faker.name().firstName();
    private final String LASTNAME = faker.name().lastName();
    private final String EMAIL = faker.internet().emailAddress();

    @Test
    void registerNewUserTest() {
        open("/register");
        $("#FirstName").setValue(FIRSTNAME);
        $("#LastName").setValue(LASTNAME);
        $("#Email").setValue(EMAIL);
        $("#Password").setValue(PASSWORD);
        $("#ConfirmPassword").setValue(PASSWORD);
        $("#register-button").click();

        $(".result").should(appear).shouldHave(text("Your registration completed"));
        $(".account").shouldBe(visible).shouldHave(text(EMAIL));
    }
}
