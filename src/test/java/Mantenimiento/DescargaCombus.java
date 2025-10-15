package Mantenimiento;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DescargaCombus {

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
        BotonAgregarDesc();
        GenerarDescarga();

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
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"submenuMANTENIMIENTO\"]/li[4]/a")));
            subMenuButton.click();
            System.out.println("Botón Descarga de combustible seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Ordenes de Servicio no funciona.");
            System.out.println("Botón Descarga de combustible no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar Descarga de Combustible")
    private static void BotonAgregarDesc() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Descarga de Combustible' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Descarga de Combustible.");
        }
    }

    @Step("Se genera la Descarga de Combustible")
    public static void GenerarDescarga() {
        Random random = new Random();

        try {
            // 1. Obtener valor del folio
            WebElement folioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='EDT_FOLIO']")));
            String FolioDesc = folioElement.getAttribute("value");

            // 2. Indicar valor "16" en el campo contenedor
            WebElement contenedor = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_CODIGOCONTENEDOR']")));
            contenedor.clear();
            //contenedor.sendKeys("16"); // cuando es IIA/
            contenedor.sendKeys("05"); // cuando es CACX

            // 3. Fecha actual en formato DD/MM/YYYY
            // Obtener la fecha desde el campo EDT_FECHA
            WebElement campoFechaOriginal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='EDT_FECHA']")));
            String fechaHoy = campoFechaOriginal.getAttribute("value");

            // Establecer esa fecha en el campo EDT_FECHASOLICITADA usando JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "document.getElementById('EDT_FECHASOLICITADA').value = arguments[0];", fechaHoy);

            // (Opcional) Simular pérdida de foco para que el sistema reconozca el cambio
            WebElement fechaSolicitada = driver.findElement(By.id("EDT_FECHASOLICITADA"));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('blur'))", fechaSolicitada);

            System.out.println("Fecha solicitada tomada de EDT_FECHA: " + fechaHoy);



            // 4. Número aleatorio de 4 dígitos
            String numeroOrden = String.format("%04d", random.nextInt(10000));
            WebElement numeroOrdenCampo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_NUMEROORDEN']")));
            numeroOrdenCampo.sendKeys(numeroOrden);
            System.out.println("El número de orden generado es: " + numeroOrden);

            // 5. Número aleatorio de 3 dígitos
            String listros = String.format("%03d", random.nextInt(1000));
            WebElement listrosSolicitados = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"EDT_LITROSSOLICITADOS\"]")));
            listrosSolicitados.sendKeys(listros);
            System.out.println("El número de litros solicitados es: " + listros);

            WebElement listrosDescargados = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"EDT_LITROSDESCARGADOS\"]")));
            listrosDescargados.sendKeys(listros);

            // 6. Número aleatorio entre 24.01 y 26.47
            double costo = 24.01 + (26.47 - 24.01) * random.nextDouble();
            String costoFormateado = String.format("%.2f", costo).replace(",", ".");
            WebElement costoPorLitro = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_COSTOPORLITRO']")));
            costoPorLitro.sendKeys(costoFormateado);
            System.out.println("El costo por litro generado es: " + costoFormateado);

            // 7. Placas fijas
            WebElement placasPipa = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='EDT_PLACASPIPA']")));
            placasPipa.sendKeys("PPA8246");

            // 8. Descripción concatenada
            WebElement descripcion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"EDT_OBSERVACION\"]")));
            descripcion.sendKeys("Descarga de Combustible Generado en Automático " + FolioDesc);

            // 9. Clic en botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en el método GenerarDescarga: " + e.getMessage());
        }
    }

}
