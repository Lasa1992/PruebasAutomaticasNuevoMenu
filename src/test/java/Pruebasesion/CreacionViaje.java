package Pruebasesion;

import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;

public class CreacionViaje {

    private WebDriver driver;
    private WebDriverWait wait;
    private int viajeCount = 1; // Contador para número de viaje
    private Random random;

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Inicializar WebDriverWait con tiempo de espera de 10 segundos
        random = new Random(); // Inicializar Random
    }

    @Test
    public void testCrearViaje() {
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
        fillForm();
        submitForm();
        handleTipoDocumento();  // Manejar Tipo de Documento
        handleNumeroViaje();    // Manejar Número de Viaje
        handleMoneda();         // Manejar Moneda
        handleCodigoCliente();  // Asignar Cliente al Viaje
        handleFolioRuta();      // Manejar Folio Ruta
        handleNumeroEconomicoConvoy(); // Manejar Número Económico Convoy
        handleSeleccionarPestana(); // Seleccionar Pestaña de Materiales Carga
    }

    @After
    public void tearDown() {
        try {
            // Esperar 15 segundos antes de cerrar el navegador
            Thread.sleep(15000); // 15 segundos en milisegundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Step("Llenar el formulario")
    private void fillForm() {
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        inputEmpresa.sendKeys("KIJ0906199R1");
        inputUsuario.sendKeys("LUIS");
        inputContrasena.sendKeys("Lasa1992#23");
    }

    @Step("Enviar el formulario")
    private void submitForm() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();

        handleAlert(Duration.ofSeconds(15));
        handleAdditionalFields(Duration.ofSeconds(5));
        handleNovedadesScreen(Duration.ofSeconds(5));
        handleImageButton(Duration.ofSeconds(5));
        handleSubMenuButton(Duration.ofSeconds(5));
        handleAdditionalButton(Duration.ofSeconds(5));
    }

    private void handleAlert(Duration timeout) {
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
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no encontrada.");
        }
    }

    private void handleNovedadesScreen(Duration timeout) {
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    private void handleImageButton(Duration timeout) {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO1.JPG']")));
            imageButton.click();
        } catch (Exception e) {
            System.out.println("Botón Módulo Tráfico no funciona.");
        }
    }

    private void handleSubMenuButton(Duration timeout) {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.JPG']")));
            subMenuButton.click();
        } catch (Exception e) {
            System.out.println("Botón listado de viajes no funciona.");
        }
    }

    private void handleAdditionalButton(Duration timeout) {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
            wait.withTimeout(Duration.ofSeconds(10)); // Espera de 10 segundos después de hacer clic en el botón adicional
        } catch (Exception e) {
            System.out.println("Botón adicional no encontrado o no clickeable.");
        }
    }

    @Step("Manejar Tipo de Documento")
    private void handleTipoDocumento() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")));
            Select comboBox = new Select(tipoDocumentoCombo);
            comboBox.selectByVisibleText("CARTA PORTE CFDI - TRASLADO"); // Selecciona por texto visible
        } catch (Exception e) {
            System.out.println("Error al manejar el Tipo de Documento.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Número de Viaje")
    private void handleNumeroViaje() {
        try {
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            numeroViajeField.sendKeys("PA" + viajeCount++);
        } catch (Exception e) {
            System.out.println("Error al manejar el Número de Viaje.");
        }
    }

    @Step("Manejar Moneda")
    private void handleMoneda() {
        try {
            WebElement monedaCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(monedaCombo);
            int randomIndex = random.nextInt(comboBox.getOptions().size()); // Generar un índice aleatorio
            comboBox.selectByIndex(randomIndex);
        } catch (Exception e) {
            System.out.println("Error al manejar la Moneda.");
            e.printStackTrace();
        }
    }

    @Step("Asignar Cliente al Viaje")
    private void handleCodigoCliente() {
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroCliente.click();
            NumeroCliente.sendKeys("000001");
            NumeroCliente.sendKeys(Keys.TAB);
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Folio Ruta")
    private void handleFolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys("000004");
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Error al manejar el Folio Ruta.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Número Económico Convoy")
    private void handleNumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            // Asegúrate de que el elemento es visible en el viewport
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", numeroEconomicoConvoyField);
            Thread.sleep(1000); // Espera un momento para asegurar el desplazamiento
            // Usar JavaScript para hacer clic si el clic directo falla
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", numeroEconomicoConvoyField);
            numeroEconomicoConvoyField.sendKeys("E3");
            // Enviar Tab para mover el foco al siguiente elemento
            numeroEconomicoConvoyField.sendKeys(Keys.TAB);
        } catch (Exception e) {
            System.out.println("Error al manejar el Número Económico Convoy.");
            e.printStackTrace();
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void handleSeleccionarPestana() {
        try {
            // Encuentra el elemento por su id y haz clic en él
            WebElement elemento = driver.findElement(By.id("TAB_TAB1_2"));
            elemento.click();
        } catch (Exception e) {
            System.out.println("Error al seleccionar la pestaña de Materiales Carga.");
            e.printStackTrace();
        }
    }
}