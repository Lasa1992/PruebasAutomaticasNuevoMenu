package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InicioSesionLog {

    public static WebDriver driver;
    public static WebDriverWait wait;
    @BeforeAll
    public static void setup() {
            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get("http://190.9.53.4:9093/");
    }

    @Description("Llena los campos de inicio de sesion con informacion.")
    public static void fillForm(WebDriver driver) {
        WebElement inputRFC = driver.findElement(By.id("outlined-adornment-rfc-login"));
        WebElement inputEmail = driver.findElement(By.id("outlined-adornment-email-login"));
        WebElement inputContrasena = driver.findElement(By.id("outlined-adornment-password-login"));

        inputRFC.sendKeys("WERX631016S30");
        inputEmail.sendKeys("WERX631016S30@GMAIL.COM");
        inputContrasena.sendKeys("123456");
    }

    @Description("Da clic en el botón de 'Iniciar sesión' para ingresar al sistema.")
    public static void submitForm(WebDriverWait wait) {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Iniciar sesión')]")
        ));
        submitButton.click();
    }

    @Description("Maneja la alerta de inicio de sesion en caso de tener una sesion ya abierta con el usuario.")
    public static void handleAlert(WebDriverWait wait) {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró una alerta o ocurrió un error.");
        }
    }
}
