package Facturacion;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Indicadores.Variables;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionConceptoDescargaMasiva {

    private static WebDriver driver;
    private static WebDriverWait wait;

    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;

    @BeforeAll
    public static void setup() {
        // System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesión - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @Description("Manejo del tipo de Cambio y de la ventana de novedades")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @Test
    @Order(3)
    @Description("Ingresar al módulo de facturación")
    public void ingresarModuloFacturacion() {
        ModuloFacturacion();
        SubModuloFacturacionConcepto();
    }

    @Test
    @Order(4)
    @Description("Descarga masiva de facturación por concepto, luego verifica la descarga")
    public void FacturacionporConcepto() {
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        FechaDesde();
        BotonAplicar();
        BotonDescargar();
        SeleccionarOpcionDescarga();
        SeleccionarTipoDescarga();
        SeleccionarFormato();
        GenerarDescarga();
        MnesajeDescarga();
        NotificacionDescarga();
        ClickIconoNotificacion();
        ClickVentanaDescarga();
        DescargarArchivo();
        MarcarNotificacion();
        VerificarArchivoDescargado();
        SalirVentanaDescargaMasiva();
        VerificarArchivoDescargado();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void ModuloFacturacion() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void SubModuloFacturacionConcepto() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1')]")));
            subMenuButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    @Step("Ingresar la fecha de hace una semana en el campo Fecha Desde")
    public void FechaDesde() {
        try {
            LocalDate fechaUnaSemanaAtras = LocalDate.now().minusWeeks(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaFormateada = fechaUnaSemanaAtras.format(formatter);

            WebElement inputFechaDesde = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input"
            )));

            inputFechaDesde.click();
            Thread.sleep(300);
            inputFechaDesde.clear();
            inputFechaDesde.sendKeys(fechaFormateada);

            System.out.println("Se ingresó la fecha de hace una semana (" + fechaFormateada + ") en el campo Fecha Desde.");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println("Error en FechaDesde: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Aplicar")
    public void BotonAplicar() {
        try {
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[2]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/a/span/span")
            ));
            botonAplicar.click();
            System.out.println("Se hizo clic en el botón Aplicar.");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Aplicar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de descargar carta porte")
    public void BotonDescargar() {
        try {
            WebElement botonDescargar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td[2]"));
            botonDescargar.click();
            System.out.println("Se hizo clic en el botón de descargar carta porte.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de descargar carta porte: " + e.getMessage());
        }
    }

    @Step("Seleccionar la opción de descarga")
    public void SeleccionarOpcionDescarga() {
        try {
            WebElement opcionDescarga = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr[2]/td[2]/a"));
            opcionDescarga.click();
            System.out.println("Se seleccionó la opción de descarga.");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La pausa fue interrumpida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al seleccionar la opción de descarga: " + e.getMessage());
        }
    }

    @Step("Verificar si se descargó un archivo")
    public void VerificarArchivoDescargado() {
        try {
            boolean descargado = verificarDescarga();
            if (descargado) {
                System.out.println("El archivo se descargó correctamente.");
            } else {
                System.out.println("No se encontró el archivo descargado.");
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error en la pausa tras la verificación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al verificar la descarga: " + e.getMessage());
        }
    }

    @Step("Seleccionar el tipo de descarga")
    public void SeleccionarTipoDescarga() {
        try {
            By xpathTipoDescarga = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr[3]/td/label/input");
            WebElement tipoDescarga = wait.until(ExpectedConditions.elementToBeClickable(xpathTipoDescarga));
            tipoDescarga.click();
            System.out.println("Se seleccionó el tipo de descarga.");
        } catch (Exception e) {
            System.err.println("Error al seleccionar el tipo de descarga: " + e.getMessage());
        }
    }

    @Step("Seleccionar el último formato en el combo")
    public void SeleccionarFormato() {
        try {
            By xpathFormato = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select");
            WebElement combo = wait.until(ExpectedConditions.visibilityOfElementLocated(xpathFormato));
            Select selectFormato = new Select(combo);
            int totalOpciones = selectFormato.getOptions().size();
            if (totalOpciones > 0) {
                selectFormato.selectByIndex(totalOpciones - 1);
                System.out.println("Se seleccionó el último formato.");
            } else {
                System.out.println("No hay formatos disponibles.");
            }
        } catch (Exception e) {
            System.err.println("Error al seleccionar el formato: " + e.getMessage());
        }
    }

    @Step("Generar la descarga")
    public void GenerarDescarga() {
        try {
            By xpathGenerar = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/input");
            WebElement btnGenerarDescarga = wait.until(ExpectedConditions.elementToBeClickable(xpathGenerar));
            btnGenerarDescarga.click();
            System.out.println("Se hizo clic en el botón para generar la descarga.");
        } catch (Exception e) {
            System.err.println("Error al generar la descarga: " + e.getMessage());
        }
    }

    @Step("Capturar/validar el mensaje de la descarga")
    public void MnesajeDescarga() {
        try {
            By xpathMensaje = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input");
            WebElement inputMensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(xpathMensaje));
            String valorMensaje = inputMensaje.getAttribute("value");
            System.out.println("Mensaje de la descarga: " + valorMensaje);
        } catch (Exception e) {
            System.err.println("Error al obtener el mensaje: " + e.getMessage());
        }
    }

    @Step("NotificacionDescarga")
    public void NotificacionDescarga() {
        try {
            System.out.println("NotificacionDescarga: acción en la UI para notificación de la descarga.");
            // Aquí colocarías el código para interactuar con la notificación si existe
        } catch (Exception e) {
            System.err.println("Error en NotificacionDescarga: " + e.getMessage());
        }
    }

    @Step("ClickIconoNotificacion")
    public void ClickIconoNotificacion() {
        try {
            System.out.println("ClickIconoNotificacion: acción en la UI para hacer clic en el ícono de notificación.");
            // Aquí colocarías el XPath y la acción de clic para abrir la notificación
        } catch (Exception e) {
            System.err.println("Error en ClickIconoNotificacion: " + e.getMessage());
        }
    }

    @Step("ClickVentanaDescarga")
    public void ClickVentanaDescarga() {
        try {
            System.out.println("ClickVentanaDescarga: acción para abrir la ventana de descargas.");
            // Aquí colocarías el XPath y la acción de clic para mostrar la ventana de descargas
        } catch (Exception e) {
            System.err.println("Error en ClickVentanaDescarga: " + e.getMessage());
        }
    }

    @Step("DescargarArchivo")
    public void DescargarArchivo() {
        try {
            System.out.println("DescargarArchivo: acción para descargar un archivo específico desde la ventana mostrada.");
            // Aquí colocarías el XPath y la acción de clic para iniciar la descarga
        } catch (Exception e) {
            System.err.println("Error en DescargarArchivo: " + e.getMessage());
        }
    }

    @Step("MarcarNotificacion como leída u otra acción similar")
    public void MarcarNotificacion() {
        try {
            System.out.println("MarcarNotificacion: acción para marcar la notificación como leída.");
            // Aquí colocarías la lógica necesaria para marcar la notificación
        } catch (Exception e) {
            System.err.println("Error en MarcarNotificacion: " + e.getMessage());
        }
    }

    @Step("Salir de la ventana de Descarga Masiva")
    public void SalirVentanaDescargaMasiva() {
        try {
            System.out.println("SalirVentanaDescargaMasiva: acción para cerrar la ventana final de descarga.");
            // Aquí colocarías el XPath y la acción de clic para salir
        } catch (Exception e) {
            System.err.println("Error en SalirVentanaDescargaMasiva: " + e.getMessage());
        }
    }

    private boolean verificarDescarga() {
        String rutaDescargas = System.getProperty("user.home") + "/Downloads";
        File carpetaDescargas = new File(rutaDescargas);

        if (!carpetaDescargas.exists() || !carpetaDescargas.isDirectory()) {
            return false;
        }

        File[] listaArchivos = carpetaDescargas.listFiles();
        if (listaArchivos == null || listaArchivos.length == 0) {
            return false;
        }

        for (File archivo : listaArchivos) {
            if (archivo.getName().toLowerCase().endsWith(".pdf") && archivo.length() > 0) {
                return true;
            }
        }
        return false;
    }
}
