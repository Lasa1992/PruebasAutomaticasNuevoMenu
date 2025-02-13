package Trafico;
import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Trafico.CreacionViajeTimbreIngreso;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreacionViajeTimbreIngreso {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para almacenar valores reutilizables
    private static String numeroViajeCliente;
    private static String monedaSeleccionada;


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @Test
    @Order(3)
    @Description("Metodos para entrar al listado de viajes")
    public void EntrarAViajes() {
        BotonModuloTrafico();
        BotonListadoViajes();
    }

    @Test
    @Order(4)
    @Description("Se ingresa al listado de Viajes y se crea una Carta Porte, se factura y timbra el viaje.")
    public void testCrearViaje() {
        // Ejecuta el flujo de pruebas repetidamente para crear un viaje
        BotonAgregarCartaPorte(); // Maneja el botón adicional
        TipoDocumentoIngreso(); // Maneja el tipo de documento
        NumeroViajeCliente(); // Maneja el número de viaje
        CodigoCliente(); // Asigna un cliente al viaje
        MonedaCartaPorte(); // Maneja la moneda
        FolioRuta(); // Maneja el folio de ruta
        NumeroEconomicoConvoy(); // Maneja el número económico del convoy
        SeleccionarPestanaMateriales(); // Selecciona la pestaña de materiales carga
        BotonImportarMaterial(); // Damos Click en el boton importar
        ImportacionMaterial(); // MMetodo que importa Materiales desde un excel
        BotonAceptarImportacion(); // Aceptamos la ventana dell importacion
        BotonAceptarViaje(); // Boton Aceptar Viaje
        EnvioCorreo(); // Envio de Correo Aleatorio
        BotonImpresion(); // Manejamos Mensaje de iMPRECION

    }


    @AfterAll
    public static void tearDown() {
        // Cierra el navegador después de que todas las pruebas han terminado
        try {
            Thread.sleep(5000); // Reducido a 5 segundos para optimizar el tiempo de prueba
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reestablecer el estado de interrupción
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void BotonModuloTrafico() {
        try {
            // Espera explícita hasta que el botón (enlace) que contiene la imagen sea clicable
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1.jpg')]")));

            // Hacer clic en el botón una vez esté listo
            ModuloBotonTrafico.click();

        } catch (Exception e) {
            // Manejo del error utilizando la clase UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            // Espera explícita hasta que el enlace que contiene la imagen sea clicable
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.jpg')]]")));

            // Hacer clic en el enlace una vez esté listo
            ListadoBoton.click();

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarCartaPorte() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }

    @Step("Manejar Tipo de Documento")
    public void TipoDocumentoIngreso() {
        try {
            // Espera que el combo box sea visible
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")));

            // Crear un objeto Select para manipular el combo box
            Select comboBox = new Select(tipoDocumentoCombo);

            // Seleccionar la opción con el texto visible "CARTA PORTE CFDI - INGRESO"
            comboBox.selectByVisibleText("CARTA PORTE CFDI - HMO");

        } catch (Exception e) {
            // Manejo del error utilizando la clase UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento: CARTA PORTE CFDI - INGRESO");
        }
    }


    @Step("Manejar Número de Viaje")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            numeroViajeCliente = "PAING" + folioValue; // Guarda el valor en la variable
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViajeCliente);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }

    @Step("Asignar Cliente al Viaje")
    private void CodigoCliente() {
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroCliente.click();
            NumeroCliente.sendKeys("000003");
            NumeroCliente.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Manejar Moneda")
    private void MonedaCartaPorte() {
        try {
            // Espera a que el combo box de moneda sea visible
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            // Obtener todas las opciones disponibles
            List<WebElement> opcionesDisponibles = comboBox.getOptions();

            // Definir las opciones válidas según el texto visible
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();

            // Filtrar las opciones disponibles para casar solo las válidas
            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            // Elegir una opción aleatoria del combo box entre las opciones válidas
            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles.get(random.nextInt(opcionesValidasDisponibles.size()));

                // Seleccionar la opción aleatoria utilizando el texto visible
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                monedaSeleccionada = opcionAleatoria.getText(); // Guarda el valor en la variable
                System.out.println("Se seleccionó la opción: " + monedaSeleccionada);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda");
        }
    }

    @Step("Manejar Folio Ruta")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys("000089");
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }

    @Step("Manejar Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            // Espera a que el elemento <td> que contiene la pestaña esté visible
            WebElement pestanaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));

            // Busca el elemento <a> dentro de la pestaña y haz clic en él
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();

            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            // Espera a que el botón sea clicable
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES")));

            // Hacer clic en el botón una vez esté listo
            botonImportar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            // Espera a que el campo de archivo sea visible
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Especifica la ruta al archivo que deseas importar
            String rutaArchivo = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                // Enviar la ruta del archivo al campo de tipo "file"
                inputArchivo.sendKeys(rutaArchivo);
            } else {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }

            // Espera a que el botón de importar sea clicable
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));

            // Hacer clic en el botón de importar
            botonImportar.click();

            // Esperar unos 3 segundos después de hacer clic
            Thread.sleep(3000);

            // Verificar si la importación fue exitosa o falló
            try {
                WebElement iconoExito = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver); // Toma captura de pantalla en caso de error
                    throw new Exception("La importación de materiales falló, se mostró el icono de error.");
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al importar el archivo de materiales");
        }
    }

    @Step("Aceptar Importación de Materiales")
    private void BotonAceptarImportacion() {
        try {
            // Espera a que el botón de aceptar sea clicable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));

            // Hacer clic en el botón de aceptar
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }
    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            // Espera a que el botón de aceptar viaje sea clicable
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));

            // Hacer clic en el botón de aceptar viaje
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar para confirmar el viaje");
        }
    }

    @Step("Enviar Por Correo")
    private void EnvioCorreo() {
        try {
            // Elegir aleatoriamente entre el botón Sí o No
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                // Espera a que el botón de Sí sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                // Espera a que el botón de No sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }

            // Hacer clic en el botón elegido
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }
    @Step("Botón de Impresión")
    private void BotonImpresion() {
        try {
            // Espera a que el botón de regresar sea clicable
            WebElement botonRegresar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));

            // Hacer clic en el botón de regresar
            botonRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Regresar para impresión");
        }
    }
}