package Porclases;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CancelacionSustitucionFacturatest {



    private static WebDriver driver;
    private static WebDriverWait wait;
    //Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();


    @BeforeAll
    public static void setup() {
        //System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        fillForm();
        submitForm();
        handleAlert();
    }
    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        handleTipoCambio();
        handleNovedadesScreen();
    }

    @Test
    @Order(3)
    @Description("Ingresar al modulo de facturación.")
    public void ingresarModuloFacturacion() {
        handleImageButton();
        handleSubMenuButton();
    }

    @RepeatedTest(5)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void testFacturacionporConcepto() {
        //Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);
        handleBotonAgregarListado();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Llenar el formulario")
    private static void fillForm() {
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        inputEmpresa.sendKeys("KIJ0906199R1");
        inputUsuario.sendKeys("LUIS");
        inputContrasena.sendKeys("Lasa1992#23.");
    }

    @Step("Enviar el formulario")
    private static void submitForm() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();
    }

    private static void handleAlert() {
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

    private static void handleTipoCambio() {
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no encontrada.");
        }
    }

    private static void handleNovedadesScreen() {
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1.JPG')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1.JPG')]")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }



    @Step("Seleccionar factura de la lista")
    private void handleFacturaList() {
        // Realiza acciones en el área de scroll donde se encuentran las facturas
        WebElement scrollArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dataTables_scrollBody")));
        Actions builder = new Actions(driver);

        // Simula un clic y mantener en el área de scroll
        builder.moveToElement(scrollArea).clickAndHold().perform();
        builder.moveToElement(scrollArea).release().perform();

        // Selecciona una celda específica en la tabla de facturas (modificar si es necesario)
        WebElement celdaFactura = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#TABLE_ProFacturasPorConcepto_1880 > td:nth-child(20)")));
        celdaFactura.click();
    }

    @Step("Cancelar factura seleccionada")
    private void performCancelacionSustitucion() {
        // Cancelar factura seleccionada
        WebElement btnCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_CANCELAR")));
        btnCancelar.click();

        // Ingresar motivo de cancelación
        WebElement inputMotivo = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_MOTIVO")));
        inputMotivo.sendKeys("prueba automatica");

        // Confirmar cancelación
        WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
        btnAceptar.click();

        // Confirmar dialogos de alerta o confirmación
        WebElement btnYes = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
        btnYes.click();
    }

    @Step("Confirmar sustitución de factura")
    private void confirmarSustitucionFactura() {
        // Rellenar campo de observaciones para la sustitución
        WebElement inputObservaciones = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_OBSERVACIONES")));
        inputObservaciones.sendKeys("sustitucion automatica");

        // Aceptar sustitución
        WebElement btnAceptarSustitucion = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
        btnAceptarSustitucion.click();

        // Confirmar diálogos adicionales
        WebElement btnYesSustitucion = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
        btnYesSustitucion.click();
    }

    @Step("Confirmar la operación final")
    private void confirmarOperacionFinal() {
        // Confirmar operación final
        WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
        btnOk.click();

        WebElement btnYesFinal = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
        btnYesFinal.click();
    }

