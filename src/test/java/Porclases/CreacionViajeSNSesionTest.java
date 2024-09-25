package Porclases;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
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
import java.util.List;

public class CreacionViajeSNSesionTest {

    // Variables estáticas para WebDriver, WebDriverWait y Random
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Random random = new Random();
    private int viajeCount = 1;

    @BeforeAll
    public static void setup() {
        // Configura la propiedad del driver de Chrome y crea una instancia de ChromeDriver
        //System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navega a la URL de la aplicación
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");

        // Llama a métodos para manejar elementos y configuraciones iniciales
        fillForm(); // Llena el formulario de inicio de sesión
        submitForm(); // Envía el formulario de inicio de sesión

        // Maneja alertas y otros elementos que solo necesitan ser gestionados una vez
        handleAlert(Duration.ofSeconds(15)); // Maneja las alertas si existen
        handletipodecambio(Duration.ofSeconds(5)); // Maneja el tipo de cambio
        handleNovedadesScreen(Duration.ofSeconds(5)); // Maneja la pantalla de novedades
        handleImageButton(Duration.ofSeconds(5)); // Maneja el botón de la imagen del módulo
        handleSubMenuButton(Duration.ofSeconds(5)); // Maneja el botón del submenú

    }

    @RepeatedTest(1)
    public void testCrearViaje() {
        // Ejecuta el flujo de pruebas repetidamente para crear un viaje
        handleAdditionalButton(Duration.ofSeconds(5)); // Maneja el botón adicional
        handleTipoDocumento(); // Maneja el tipo de documento
        handleNumeroViaje(); // Maneja el número de viaje
        handleMoneda(); // Maneja la moneda
        handleCodigoCliente(); // Asigna un cliente al viaje
        handleFolioRuta(); // Maneja el folio de ruta
        handleNumeroEconomicoConvoy(); // Maneja el número económico del convoy
        handleSeleccionarPestana(); // Selecciona la pestaña de materiales carga
        handleAdditionalFields(); // Llena campos adicionales en la pestaña de materiales carga

        // Realiza clics en los botones para finalizar el proceso
        clickAceptarButton(); // Clica en el botón Aceptar
        clickYesButtonTimbrar(); // Clica en el botón Sí para Timbrar
        clickYesButtonEnviarCorreo(); // Clica en el botón Sí para Enviar Correo
        clickRegresarButton(); // Clica en el botón Regresar
    }

    @AfterAll
    public static void tearDown() {
        // Cierra el navegador después de que todas las pruebas han terminado
        try {
            // Esperar 15 segundos para asegurar que todos los procesos se completen
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        } finally {
            if (driver != null) {
                // Cierra el navegador
                driver.quit();
            }
        }
    }

    @Step("Llenar el formulario")
    private static void fillForm() {
        // Encuentra los campos del formulario por su ID y llena los valores
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        // Ingresa los valores en los campos de formulario
        inputEmpresa.sendKeys("KIJ0906199R1");
        inputUsuario.sendKeys("LUIS");
        inputContrasena.sendKeys("Lasa1992#23");
    }

    @Step("Enviar el formulario")
    private static void submitForm() {
        // Espera hasta que el botón de enviar esté clickable y luego lo clickea
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();
    }

    private static void handleAlert(Duration timeout) {
        // Maneja alertas en la página
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept(); // Acepta la alerta
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró una alerta o ocurrió un error.");
        }
    }

    private static void handletipodecambio(Duration timeout) {
        // Maneja el campo de tipo de cambio
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20"); // Ingresa un valor en el campo
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click(); // Clickea en el botón de aceptar
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no encontrada.");
        }
    }

    private static void handleNovedadesScreen(Duration timeout) {
        // Maneja la pantalla de novedades
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click(); // Clickea en el botón de aceptar
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    private static void handleImageButton(Duration timeout) {
        // Maneja el botón de la imagen del módulo
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO1.JPG']")));
            imageButton.click(); // Clickea en el botón de la imagen
        } catch (Exception e) {
            System.out.println("Botón Módulo Tráfico no funciona.");
        }
    }

    private static void handleSubMenuButton(Duration timeout) {
        // Maneja el botón del submenú
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.JPG']")));
            subMenuButton.click(); // Clickea en el botón del submenú
        } catch (Exception e) {
            System.out.println("Botón listado de viajes no funciona.");
        }
    }

    private static void handleAdditionalButton(Duration timeout) {
        // Maneja el botón adicional
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click(); // Clickea en el botón adicional
            wait.withTimeout(Duration.ofSeconds(10));
        } catch (Exception e) {
            System.out.println("Botón adicional no encontrado o no clickeable.");
        }
    }

    @Step("Manejar Tipo de Documento")
    private void handleTipoDocumento() {
        // Maneja el combo box del tipo de documento
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")));
            Select comboBox = new Select(tipoDocumentoCombo);
            comboBox.selectByVisibleText("CARTA PORTE CFDI - TRASLADO"); // Selecciona el tipo de documento
        } catch (Exception e) {
            System.out.println("Error al manejar el Tipo de Documento.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Número de Viaje")
    private void handleNumeroViaje() {
        // Maneja el campo del número de viaje
        try {
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            numeroViajeField.sendKeys("PA" + viajeCount++); // Ingresa un número de viaje único
        } catch (Exception e) {
            System.out.println("Error al manejar el Número de Viaje.");
        }
    }

    @Step("Manejar Moneda")
    private void handleMoneda() {
        try {
            // Espera hasta que el combo box sea visible
            WebElement monedaCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));

            // Crea un objeto Select con el combo box
            Select comboBox = new Select(monedaCombo);

            // Selecciona una opción aleatoria si hay opciones disponibles
            List<WebElement> opciones = comboBox.getOptions();
            if (!opciones.isEmpty()) {
                int randomIndex = random.nextInt(opciones.size());
                comboBox.selectByIndex(randomIndex);
            } else {
                System.out.println("No hay opciones disponibles.");
            }
        } catch (Exception e) {
            System.out.println("Error al manejar la Moneda.");
            e.printStackTrace();
        }
    }

    @Step("Asignar Cliente al Viaje")
    private void handleCodigoCliente() {
        // Asigna un cliente al viaje
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroCliente.click();
            NumeroCliente.sendKeys("000001"); // Ingresa el número del cliente
            NumeroCliente.sendKeys(Keys.TAB); // Tabula fuera del campo
            Thread.sleep(3000); // Espera para asegurarse de que la acción se complete
        } catch (Exception e) {
            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Folio Ruta")
    private void handleFolioRuta() {
        // Maneja el campo del folio de ruta
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys("000004"); // Ingresa el folio de ruta
            folioRutaField.sendKeys(Keys.TAB); // Tabula fuera del campo
            Thread.sleep(3000); // Espera para asegurarse de que la acción se complete
        } catch (Exception e) {
            System.out.println("Error al manejar el Folio Ruta.");
            e.printStackTrace();
        }
    }

    @Step("Manejar Número Económico Convoy")
    private void handleNumeroEconomicoConvoy() {
        // Maneja el campo del número económico del convoy
        try {
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", numeroEconomicoConvoyField);
            Thread.sleep(2000); // Espera para asegurarse de que el campo sea visible

            numeroEconomicoConvoyField.sendKeys("E3"); // Ingresa el número económico del convoy
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER); // Presiona Enter
            Thread.sleep(5000); // Espera para asegurarse de que la acción se complete
        } catch (Exception e) {
            System.out.println("Error al manejar el Número Económico Convoy.");
            e.printStackTrace();
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void handleSeleccionarPestana() {
        try {
            WebElement pestañaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            pestañaMateriales.click();
            Thread.sleep(3000); // Esperar 3 segundos después de hacer clic en la pestaña
        } catch (Exception e) {
            System.out.println("Error al seleccionar la pestaña de Materiales Carga.");
            e.printStackTrace();
        }
    }

    @Step("Llenar campos adicionales después de cambiar a la pestaña")
    private void handleAdditionalFields() {
        try {
            // Genera un número aleatorio entre 1 y 1000 para el campo cantidad
            int randomNumberCantidad = random.nextInt(1000) + 1;
            String randomValueCantidad = String.valueOf(randomNumberCantidad);

            // Espera a que el campo zrl_1_ATT_CANTIDAD sea visible
            WebElement cantidadField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("zrl_1_ATT_CANTIDAD")));
            // Usar JavaScript para desplazar la vista hacia el campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cantidadField);
            // Usar JavaScript para establecer el valor del campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", cantidadField, randomValueCantidad);
            // Simular la presión de la tecla Enter
            cantidadField.sendKeys(Keys.ENTER);

            // Opcionalmente, puedes agregar un pequeño retraso para asegurar que la acción se complete
            Thread.sleep(2000);

            // Genera un número aleatorio entre 1 y 1000 para el campo peso
            int randomNumberPeso = random.nextInt(1000) + 1;
            String randomValuePeso = String.valueOf(randomNumberPeso);

            // Espera a que el campo zrl_1_ATT_PESO sea visible
            WebElement pesoField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("zrl_1_ATT_PESO")));
            // Usar JavaScript para desplazar la vista hacia el campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pesoField);
            // Usar JavaScript para establecer el valor del campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", pesoField, randomValuePeso);
            // Simular la presión de la tecla Enter
            pesoField.sendKeys(Keys.ENTER);

            // Opcionalmente, puedes agregar un pequeño retraso para asegurar que la acción se complete
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Step("Aceptar el viaje")
    private void clickAceptarButton() {
        try {
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            aceptarButton.click();
            System.out.println("Botón Aceptar presionado.");
            // Espera a que el siguiente botón esté disponible
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
        } catch (Exception e) {
            System.out.println("Error al buscar o presionar el botón Aceptar.");
            e.printStackTrace();
        }
    }

    @Step("Timbrar el documento")
    private void clickYesButtonTimbrar() {
        try {
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            yesButton.click();
            System.out.println("Botón Yes (Timbrar) presionado.");
            // Espera a que el siguiente botón esté disponible
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
        } catch (Exception e) {
            System.out.println("Error al buscar o presionar el botón Yes (Timbrar).");
            e.printStackTrace();
        }
    }

    @Step("Enviar por correo")
    private void clickYesButtonEnviarCorreo() {
        try {
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            yesButton.click();
            System.out.println("Botón Yes (Enviar por correo) presionado.");
            // Espera a que el siguiente botón esté disponible
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));
        } catch (Exception e) {
            System.out.println("Error al buscar o presionar el botón Yes (Enviar por correo).");
            e.printStackTrace();
        }
    }

    @Step("Imprimir documento")
    private void clickRegresarButton() {
        try {
            WebElement regresarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));
            regresarButton.click();
            System.out.println("Botón Regresar presionado.");
        } catch (Exception e) {
            System.out.println("Error al buscar o presionar el botón Regresar.");
            e.printStackTrace();
        }
    }


}
