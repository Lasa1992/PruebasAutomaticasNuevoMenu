package Porclases;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FacturacionConceptoTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Random random = new Random();


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

    @RepeatedTest(50)
    public void testFacturacionporConcepto() {
        handleBotonAgregarListado();
        handleAsignaCliente();
        handleCondiciondePago();
        HandleComboMetodoPago();
        HandleComboUsoCFDI();
        HandleMonedas();
        handleBotonCancelarFactura();
        HandleConceptofacturacionAgregar();
        IngresaValorCantidad();
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
            System.out.println("Botón Módulo Tráfico no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1.JPG')]")));
            subMenuButton.click();
        } catch (Exception e) {
            System.out.println("Botón listado de viajes no funciona.");
        }
    }

    private static void handleBotonAgregarListado() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (Exception e) {
            System.out.println("Botón adicional no encontrado o no clickeable.");
        }
    }

    @Step("Asignar El cliente que se le va Facturar")
    private static void handleAsignaCliente() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        try {
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            numeroCliente.click();
            numeroCliente.sendKeys("000001");
            numeroCliente.sendKeys(Keys.TAB);

            fluentWait.until(new Function<WebDriver, Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    WebElement field = driver.findElement(By.id("EDT_NUMEROCLIENTE"));
                    return !field.getAttribute("value").isEmpty();
                }
            });

            System.out.println("El campo de cliente tiene información.");
        } catch (Exception e) {
            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
            e.printStackTrace();
        }
    }

        private void handleCondiciondePago() {
            try {
                // Encuentra el primer combo box (select) por ID
                Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CONDICIONESPAGO")));

                // Elige aleatoriamente entre "Contado" y "Crédito"
                Random random = new Random();
                boolean esContado = random.nextBoolean(); // true para contado, false para crédito

                if (esContado) {
                    // Selecciona la opción "Contado" en el primer combo box
                    primerComboBox.selectByVisibleText("CONTADO");

                    // Espera a que el segundo combo box se haga visible
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement segundoComboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMETODOSPAGOS")));

                    // Encuentra el segundo combo box y abre la lista de opciones
                    Select segundoComboBox = new Select(segundoComboBoxElement);

                    // Obtiene todas las opciones del segundo combo box
                    List<WebElement> opciones = segundoComboBox.getOptions();

                    // Verifica que haya más de una opción
                    if (opciones.size() > 1) {
                        // Elige una opción aleatoria
                        int indexToSelect = random.nextInt(opciones.size());

                        // Selecciona la opción aleatoria
                        segundoComboBox.selectByIndex(indexToSelect);

                        // Imprime la opción seleccionada
                        System.out.println("Opción seleccionada en segundo combo box: " + opciones.get(indexToSelect).getText());
                    } else {
                        System.out.println("El Combo Forma de Pago esta vacio");
                    }
                } else {
                    // Selecciona la opción "Crédito" en el primer combo box
                    primerComboBox.selectByVisibleText("CREDITO");
                    System.out.println("Se seleccionó 'Crédito'. La Forma de pago es POR DEFINIR por default");
                }
            } catch (Exception e) {
                // Maneja cualquier excepción que ocurra
                System.out.println("Se ha producido un error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    private void HandleComboMetodoPago() {
        try {
            // Encuentra el combo box por su id
            WebElement comboBoxElement = driver.findElement(By.id("COMBO_METODOPAGO"));

            // Verifica si el combo box está habilitado
            if (comboBoxElement.isEnabled()) {
                // Inicializa el combo box con Selenium Select
                Select comboBox = new Select(comboBoxElement);

                // Obtiene todas las opciones del combo box
                List<WebElement> opciones = comboBox.getOptions();

                // Verifica que haya más de una opción disponible
                if (opciones.size() > 1) {
                    // Genera un número aleatorio para seleccionar una opción
                    Random random = new Random();
                    int indexToSelect = random.nextInt(opciones.size());

                    // Selecciona la opción aleatoria
                    comboBox.selectByIndex(indexToSelect);

                    // Imprime la opción seleccionada
                    System.out.println("Opción seleccionada en combo box (Método de Pago): " + opciones.get(indexToSelect).getText());
                } else {
                    System.out.println("Error no carga ninguna informacion el Metodo de pago");
                }
            } else {
                // Si el combo box no está habilitado
                System.out.println("El check Permitir seleccionar Método de pago esta deshabilitado.");
            }
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error al intentar seleccionar Metodo de Pago: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void HandleComboUsoCFDI() {
        try {
            // Encuentra el combo box por su id
            WebElement comboBoxElement = driver.findElement(By.id("COMBO_USOCFDI"));

            // Inicializa el combo box con Selenium Select
            Select comboBox = new Select(comboBoxElement);

            // Obtiene todas las opciones del combo box
            List<WebElement> opciones = comboBox.getOptions();

            // Verifica que haya más de una opción disponible
            if (opciones.size() > 1) {
                // Genera un número aleatorio para seleccionar una opción
                Random random = new Random();
                int indexToSelect = random.nextInt(opciones.size());

                // Selecciona la opción aleatoria
                comboBox.selectByIndex(indexToSelect);

                // Imprime la opción seleccionada
                System.out.println("Opción seleccionada en combo (Uso CFDI): " + opciones.get(indexToSelect).getText());
            } else {
                System.out.println("El combo Uso de CFDI no tiene Informacion");
            }
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error al intentar seleccionar la opción Uso de CFDI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void HandleMonedas() {
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
            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void HandleConceptofacturacionAgregar() {
        try {
            // Encuentra el botón agregar concepto
            WebElement botonAgregar = driver.findElement(By.id("BTN_AGREGAR"));

            // Presiona el botón
            botonAgregar.click();

            // Imprime un mensaje para confirmar que se ha presionado el botón
            System.out.println("Se ha presionado el botón Agregar.");
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error al presionar el botón Agregar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void IngresaValorCantidad() {
        try {
            // Espera a que el nuevo campo sea visible (ajusta el selector según tu campo)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement nuevoCampo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));

            // Genera un valor aleatorio entre 1.0000 y 99.9999
            Random random = new Random();
            double valorAleatorio = 1.0000 + (99.9999 - 1.0000) * random.nextDouble();

            // Ingresa el valor aleatorio en el nuevo campo
            nuevoCampo.clear(); // Limpia el campo antes de ingresar el nuevo valor
            nuevoCampo.sendKeys(String.format("%.4f", valorAleatorio)); // Formatea el valor a 4 decimales

            // Imprime el valor aleatorio ingresado
            System.out.println("Se ingresó la Cantidad de : " + String.format("%.4f", valorAleatorio));
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error al ingresar la cantidad." + e.getMessage());
            e.printStackTrace();
        }
    }


    @Step("Cancelar factura")
    public static void handleBotonCancelarFactura() {
        try {
            // Espera explícita de 15 segundos antes de intentar encontrar el botón
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // Espera hasta que el botón de cancelar sea clickeable
            WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_CANCELAR")));
            cancelButton.click();
            // Espera opcional para asegurarse de que el botón ya no sea clickeable (opcional)
            wait.until(ExpectedConditions.stalenessOf(cancelButton));
        } catch (Exception e) {
            System.out.println("Botón de cancelar factura no encontrado o no clickeable.");
            e.printStackTrace();
        }
    }
}
