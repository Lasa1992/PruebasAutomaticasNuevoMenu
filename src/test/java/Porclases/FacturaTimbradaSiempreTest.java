package Porclases;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FacturaTimbradaSiempreTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
        fillForm();
        submitForm();
        handleAlert();
        handleTipoCambio();
        handleNovedadesScreen();
        handleImageButton();
        handleSubMenuButton();
    }

    @RepeatedTest(30)
    public void testFacturacionporConcepto() {
        handleBotonAgregarListado();
        asignaCliente();
        seleccionaMonedaFactura();
        agregarConceptoFacturacion();
        agregarConceptoFacturacion(); // Assuming you want to add two concepts
        completaCantidad();
        completaCodigoConcepto();
        completaPrecioUnitario();
        botonAgregarConcepto();
        aceptarFactura();
        botonTimbre();
        validarPosibleErrorPRODIGIA();
        botonEnvioCorre();
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
        inputContrasena.sendKeys("Lasa1992#23");
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
            UtilidadesAllure.manejoError(driver, e, "No se encontró una alerta o ocurrió un error al manejarla.");
        }
    }

    private static void handleTipoCambio() {
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (TimeoutException e) {
            System.out.println("Ventana tipo de Cambio no encontrada, continuando con la prueba...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el tipo de cambio.");
        }
    }

    private static void handleNovedadesScreen() {
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (TimeoutException e) {
            System.out.println("Pantalla de novedades no encontrada, continuando con la prueba...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Pantalla de novedades no encontrada.");
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1.JPG')]")));
            imageButton.click();
        } catch (TimeoutException e) {
            System.out.println("Botón Módulo Facturación no encontrado, continuando con la prueba...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1.JPG')]")));
            subMenuButton.click();
        } catch (TimeoutException e) {
            System.out.println("Botón listado de facturas por concepto no encontrado, continuando con la prueba...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón listado de facturas por concepto no funciona.");
        }
    }

    private static void handleBotonAgregarListado() {
        try {
            WebElement botonAgregar = driver.findElement(By.id("BTN_AGREGAR"));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al intentar encontrar y hacer clic en el botón: ");
        }
    }

    private static void asignaCliente() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        try {
            // Espera y obtiene el campo 'Número de Cliente'
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));

            // Interactuar con el campo
            numeroCliente.click();
            numeroCliente.sendKeys("000001", Keys.TAB);

            // Espera a que el campo cliente mantenga la selección
            fluentWait.until(driver -> !driver.findElement(By.id("EDT_NUMEROCLIENTE")).getAttribute("value").isEmpty());

            System.out.println("Se asignó el Cliente de forma correcta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al realizar la acción en el campo 'Número de Cliente'.");
        }
    }

    private static void seleccionaMonedaFactura() {
        try {
            // Encuentra el primer combo box (select) por ID
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CATMONEDAS")));

            // Define las opciones disponibles
            List<String> opciones = List.of("PESOS", "DÓLARES");

            // Elige aleatoriamente una opción
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));

            // Selecciona la opción en el primer combo box
            primerComboBox.selectByVisibleText(opcionSeleccionada);

            // Imprime la opción seleccionada
            System.out.println("La Moneda es: " + opcionSeleccionada);
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra utilizando UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el combo box de monedas.");
        }
    }

    private static void agregarConceptoFacturacion() {
        try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement botonAgregar = driver.findElement(By.id("BTN_AGREGAR"));
                botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón agregar concepto o botón no encontrado.");
        }
    }

    private static void completaCantidad() {
        try {
            // Espera y manipulación del primer campo: Cantidad
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement inputCantidad = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));
            inputCantidad.sendKeys("50");
            System.out.println("Cantidad completada con éxito.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al completar la cantidad.");
        }
    }

    private static void completaCodigoConcepto() {
        try {
            // Manipulación del segundo campo: Código de concepto de facturación
            WebElement inputCodigo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CODIGOCONCEPTOFACTURACION")));
            inputCodigo.sendKeys("1");
            System.out.println("Código de concepto completado con éxito.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al completar el código de concepto.");
        }
    }

    private static void completaPrecioUnitario() {
        try {
            // Manipulación del tercer campo: Precio unitario
            WebElement inputPrecio = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_PRECIOUNITARIO")));
            inputPrecio.sendKeys("1500");
            System.out.println("Precio unitario completado con éxito.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al completar el precio unitario.");
        }
    }

    private static void botonAgregarConcepto() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_AGREGAR"));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo encontrar o hacer clic en el botón agregar concepto.");
        }
    }

    private static void aceptarFactura() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo aceptar la factura.");
        }
    }

    private static void botonTimbre() {
        try {
            WebElement botonTimbre = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_TIMBRAR")));
            botonTimbre.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo encontrar o hacer clic en el botón timbrar.");
        }
    }

    private static void validarPosibleErrorPRODIGIA() {
        try {
            // Esperar por el error PRODIGIA
            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_ERRORPRODIGIA")));
            String errorMessage = errorElement.getText();
            System.out.println("Mensaje de error: " + errorMessage);
            // Aquí puedes agregar lógica para manejar el mensaje de error si es necesario
        } catch (TimeoutException e) {
            System.out.println("No se encontró mensaje de error PRODIGIA.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al validar el mensaje de error PRODIGIA.");
        }
    }

    private static void botonEnvioCorre() {
        try {
            WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENVIAR")));
            botonEnviar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo encontrar o hacer clic en el botón enviar.");
        }
    }
}
