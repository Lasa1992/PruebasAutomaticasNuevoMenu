package Mantenimiento;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CargaCombus {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeEach
    public void setup() {
        // 🛠️ Obtener el navegador dinámicamente desde la variable del sistema
        String navegador = System.getProperty("navegador", "chrome"); // Si no se especifica, usa Chrome
        System.out.println("🌍 Configurando pruebas en: " + navegador.toUpperCase());

        // 🛠️ Configurar el WebDriver con el navegador correcto
        InicioSesion.setup(navegador);
        driver = InicioSesion.getDriver();
        wait = InicioSesion.getWait();
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
    }

    @RepeatedTest(1)
    @Order(4)
    @Description("Se genera un flujo completo de Mantenimiento desde Requisición de Compras.")
    public void MantenimientoPA() throws InterruptedException {

        handleImageButton();
        handleSubMenuButton();

        //Creamos descarga de combustible para el contenedor
        BotonAgregarCarga();
        GenerarCarga();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Inventarios...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"sidebar\"]/div/ul/li[10]")));
            imageButton.click();
            System.out.println("Botón Módulo Mantenimiento seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Mantenimiento no funciona.");
            System.out.println("Botón Módulo Mantenimiento no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"submenuMANTENIMIENTO\"]/li[5]/a")));
            subMenuButton.click();
            System.out.println("Botón Carga de combustible seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Ordenes de Servicio no funciona.");
            System.out.println("Botón Carga de combustible no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar Carga de Combustible")
    private static void BotonAgregarCarga() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Descarga de Combustible' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Descarga de Combustible.");
        }
    }

    @Step("Se genera la Carga de Combustible")
    public static void GenerarCarga() {
        Random random = new Random();

        try {
            // Obtener valor del folio
            WebElement folioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='EDT_FOLIO']")));
            String FolioDesc = folioElement.getAttribute("value");
            System.out.println("El folio generado es: " + FolioDesc);

            //Indicamos la unidad a la que se le va a cargar combustible
            WebElement codigoUnidad = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_CODIGOUNIDAD']")));
            codigoUnidad.click();
            codigoUnidad.sendKeys("T-09");

            // Indicar valor "16" en el campo contenedor
            WebElement contenedor = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_CODIGOCONTENEDOR']")));
            contenedor.clear();
            //contenedor.sendKeys("16"); // cuando es IIA
            contenedor.sendKeys("05"); // cuando es CACX

            // Número aleatorio de 4 dígitos
            String numeroOrden = String.format("%04d", random.nextInt(10000));
            WebElement numeroComprobante = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_NUMEROCOMPROBANTE']")));
            numeroComprobante.sendKeys(numeroOrden);
            System.out.println("El número de comprobante generado es: " + numeroOrden);

            // Número aleatorio de 3 dígitos
            int litros = 40 + random.nextInt(61); // genera un número entre 40 (inclusive) y 100 (inclusive)
            String listros = String.valueOf(litros);

            WebElement listrosSolicitados = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_LITROSCARGADOS\"]")));
            listrosSolicitados.sendKeys(listros);

            System.out.println("El número de litros cargados es: " + listros);

            // Descripción concatenada
            WebElement descripcion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"EDT_REFERENCIA\"]")));
            descripcion.sendKeys("Carga de Combustible Automática :" + FolioDesc);

            // Botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();
            System.out.println("Botón Aceptar fue clickeado correctamente y se genera la carga de combustible.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en el método GenerarCarga: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Aplicar")
    private static void clickBotonAplicar() {
        try {
            // Esperar a que el botón Aplicar sea visible y clickeable
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_APLICAR']")));

            botonAplicar.click();
            System.out.println("Botón 'Aplicar' fue clickeado correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aplicar.");
        }
    }


}
