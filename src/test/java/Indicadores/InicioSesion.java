package Indicadores;

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

public class InicioSesion {

    public static WebDriver driver;
    public static WebDriverWait wait;
    @BeforeAll
    public static void setup() {
            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Description("Llena los campos de inicio de sesion con informacion.")
    public static void fillForm(WebDriver driver) {
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        inputEmpresa.sendKeys("IIA040805DZ4");
        inputUsuario.sendKeys("LUIS");
        inputContrasena.sendKeys("Lasa1992#red23");
    }

    @Description("Da clic en el boton de Entrar para ingresar al sistema.")
    public static void submitForm(WebDriverWait wait) {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
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

    @Description("Si aparece la ventana de tipo de cambio agrega un valor y da clic en aceptar.")
    public static void handleTipoCambio(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no encontrada.");
        }
    }

    @Description("Si detecta la ventana de novedades, da clic en aceptar para cerrarla.")
    public static void handleNovedadesScreen(WebDriverWait wait) {
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }
}
