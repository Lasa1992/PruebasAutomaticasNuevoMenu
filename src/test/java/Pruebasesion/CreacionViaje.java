package Pruebasesion;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;

public class CreacionViaje {

    private WebDriver driver;
    private WebDriverWait wait;
    private int viajeCount = 1; // Contador para número de viaje

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Inicializar WebDriverWait con tiempo de espera de 10 segundos
    }

    @Test
    public void testCrearViaje() {
        driver.get("https://www.softwareparatransporte.com/");
        fillForm();
        submitForm();
        handleTipoDocumento();  // Manejar Tipo de Documento
        handleNumeroViaje();    // Manejar Número de Viaje
        handleMoneda();         // Manejar Moneda
    }

    @After
    public void tearDown() {
        // Aquí podrías verificar si hay algún reporte pendiente o alguna condición antes de cerrar el navegador
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Llenar el formulario")
    private void fillForm() {
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        inputEmpresa.sendKeys("CURS010101111");
        inputUsuario.sendKeys("LUIS");
        inputContrasena.sendKeys("123456");
    }

    @Step("Enviar el formulario")
    private void submitForm() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();

        handleAlert(Duration.ofSeconds(5));
        handleAdditionalFields(Duration.ofSeconds(5));
        handleNovedadesScreen(Duration.ofSeconds(5));
        handleImageButton(Duration.ofSeconds(5));
        handleSubMenuButton(Duration.ofSeconds(5));
        handleAdditionalButton(Duration.ofSeconds(5));
    }

    private void handleAlert(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
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

    private void handleAdditionalFields(Duration timeout) {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no Encontrada");
        }
    }

    private void handleNovedadesScreen(Duration timeout) {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ID_PANTALLA_NOVEDADES")));
            WebElement checkBox = novedadesScreen.findElement(By.id("ID_CHECKBOX"));
            WebElement acceptButton = novedadesScreen.findElement(By.id("ID_BOTON_ACEPTAR"));
            checkBox.click();
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    private void handleImageButton(Duration timeout) {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO1.JPG']")));
            imageButton.click();
        } catch (Exception e) {
            System.out.println("Botón Módulo Tráfico no funciona");
        }
    }

    private void handleSubMenuButton(Duration timeout) {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.JPG']")));
            subMenuButton.click();
        } catch (Exception e) {
            System.out.println("Botón listado de viajes no Funciona");
        }
    }

    private void handleAdditionalButton(Duration timeout) {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
            Thread.sleep(10000); // Espera de 10 segundos después de hacer clic en el botón adicional
        } catch (Exception e) {
            System.out.println("Botón adicional no encontrado o no clickeable.");
        }
    }

    @Step("Manejar Tipo de Documento")
    private void handleTipoDocumento() {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement tipoDocumentoCombo = driver.findElement(By.id("COMBO_CATTIPOSDOCUMENTOS"));
            tipoDocumentoCombo.click();
            WebElement tipoDocumentoOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='7']")));
            tipoDocumentoOption.click();
        } catch (Exception e) {
            System.out.println("Error al manejar el Tipo de Documento.");
        }
    }

    @Step("Manejar Número de Viaje")
    private void handleNumeroViaje() {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement numeroViajeField = driver.findElement(By.id("EDT_NOVIAJECLIENTE"));
            numeroViajeField.sendKeys("PA" + viajeCount++);
        } catch (Exception e) {
            System.out.println("Error al manejar el Número de Viaje.");
        }
    }

    @Step("Manejar Moneda")
    private void handleMoneda() {
        try {
            Thread.sleep(3000); // Espera de 3 segundos
            WebElement monedaCombo = driver.findElement(By.id("COMBO_CATMONEDAS"));
            monedaCombo.click();
            WebElement monedaOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='" + getRandomMonedaValue() + "']")));
            monedaOption.click();
        } catch (Exception e) {
            System.out.println("Error al manejar la Moneda.");
        }
    }

    private String getRandomMonedaValue() {
        Random random = new Random();
        return random.nextBoolean() ? "1" : "2"; // 1 para PESOS, 2 para DÓLARES
    }
}
