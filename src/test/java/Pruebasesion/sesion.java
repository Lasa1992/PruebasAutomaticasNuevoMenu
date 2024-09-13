package Pruebasesion;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class sesion {
    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Configura la ubicación del chromedriver
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        // Inicializa el WebDriver
        driver = new ChromeDriver();
    }

    @Test
    @Description("Prueba para llenar y enviar el formulario")
    @Severity(SeverityLevel.CRITICAL)
    public void testFillAndSubmitForm() {
        navigateToPage();
        fillForm();
        submitForm();
    }

    @Step("Navegar a la página")
    private void navigateToPage() {
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Step("Llenar el formulario")
    private void fillForm() {
        WebElement inputName = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputEmail = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputMessage = driver.findElement(By.id("EDT_CONTRASENA"));

        inputName.sendKeys("CURS010101111");
        inputEmail.sendKeys("LUIS");
        inputMessage.sendKeys("123456");
    }

    @Step("Enviar el formulario")
    private void submitForm() {
        WebElement submitButton = driver.findElement(By.id("BTN_ENTRAR"));
        submitButton.click();
    }

    @AfterEach
    public void tearDown() {
        // Cierra el navegador
        if (driver != null) {
            driver.quit();
        }
    }
}
