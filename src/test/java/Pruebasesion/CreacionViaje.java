package Pruebasesion;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Inicializar WebDriverWait con tiempo de espera de 10 segundos
        random = new Random(); // Inicializar Random

    }

    @RepeatedTest(500)
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

        // Llama al método para llenar los campos adicionales después de cambiar a la pestaña
        handleAdditionalFields();

        // Llama a los métodos para manejar los botones adicionales después de llenar los datos
        clickAceptarButton();        // Presionar botón Aceptar
        clickYesButtonTimbrar();     // Presionar botón Yes para timbrar
        clickYesButtonEnviarCorreo(); // Presionar botón Yes para enviar por correo
        clickRegresarButton();       // Presionar botón Regresar
    }

    @AfterEach
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
            Thread.sleep(2000); // Esperar 1 segundo para asegurar que el elemento está en el viewport

            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(5000);
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
            // Genera un número aleatorio entre 1 y 1000
            int randomNumber = random.nextInt(1000) + 1;
            String randomValue = String.valueOf(randomNumber);

            // Espera a que el campo zrl_1_ATT_CANTIDAD sea visible
            WebElement cantidadField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("zrl_1_ATT_CANTIDAD")));
            // Usar JavaScript para desplazar la vista hacia el campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cantidadField);
            // Usar JavaScript para establecer el valor del campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", cantidadField, randomValue);
            // Simular la presión de la tecla Enter
            cantidadField.sendKeys(Keys.ENTER);

            // Opcionalmente, puedes agregar un pequeño retraso para asegurar que la acción se complete
            Thread.sleep(2000);

            // Espera a que el campo zrl_1_ATT_PESO sea visible
            WebElement pesoField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("zrl_1_ATT_PESO")));
            // Usar JavaScript para desplazar la vista hacia el campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pesoField);
            // Usar JavaScript para establecer el valor del campo
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", pesoField, randomValue);
            // Simular la presión de la tecla Enter
            pesoField.sendKeys(Keys.ENTER);

            // Opcionalmente, puedes agregar un pequeño retraso para asegurar que la acción se complete
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Error al llenar campos adicionales.");
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