package GMLogistico;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfirmacionTransportistaYDoc {

    public static WebDriver driver;
    public static WebDriverWait wait;
    public static String currentRFCTRAN;

    static class Cliente {
        String rfc;
        String email;
        String contrasena;
        public Cliente(String rfc, String email, String contrasena) {
            this.rfc = rfc;
            this.email = email;
            this.contrasena = contrasena;
        }
    }

    @BeforeAll
    public static void setup() {
        try {
            ChromeOptions options = new ChromeOptions();
            // Evitar infobars y mensajes de automatización
            options.setExperimentalOption("excludeSwitches",
                    Arrays.asList("enable-automation", "enable-logging"));
            options.setExperimentalOption("useAutomationExtension", false);
            // Tamaño de ventana y sandbox
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");
        } catch (Exception e) {
            System.out.println("Error en setup: " + e.getMessage());
            Assertions.fail("No se pudo inicializar el navegador: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    public void InicioSeSionLog() {
        try {
            Iniciosesion();
            BotonIniciosesion();
            MensajeAlerta();
        } catch (Exception e) {
            System.out.println("Error en InicioSeSionLog: " + e.getMessage());
            Assertions.fail("Fallo en InicioSeSionLog: " + e.getMessage());
        }
    }

    @RepeatedTest(2)
    @Order(2)
    @Description("Confirmacion TransportistaYDoc")
    public void AceptarSolicitud() {
        try {
            // 1) Buscar en el Excel y aplicar filtro


            // 2) Clic en la pestaña 'Comprometidos'
            ClickBotonComprometidos();
            buscarYProcesarSubasta();

            // 3) Abrir menú de opciones y seleccionar 'Enviar documentos'
            try {
                BotonOpcionesYEnviarDocumentos();
            } catch (Exception e) {
                System.out.println("❌ Error en BotonOpcionesYEnviarDocumentos, continúo: " + e.getMessage());
            }

            // 4) Clic en 'Enviar documentos' y luego en confirmación
            ClickBotonEnviarDocumentosFinal();
            ClickBotonAceptarConfirmacion();

        } catch (Exception e) {
            System.out.println("⚠️ Error inesperado en AceptarSolicitud: " + e.getMessage());
            reiniciarSesionDesdeCero();
        }
    }

    @Description("Llena los campos de inicio de sesión con información fija.")
    public static void Iniciosesion() {
        Cliente[] clientes = {
                new Cliente("GMTHCDEMO018","jose.calidad@gmtransporterp.com","123456"),
                new Cliente("TEGR820530HTC","trans2@gmail.com","123456"),
                new Cliente("CAMR951214MKL","trans3@gmail.com","123456"),
                new Cliente("XIA190128J61","xenon@gmail.com","123456"),
                new Cliente("VARG001010PLQ","trans1@gmail.com","123456")
        };
        Cliente cliente = clientes[1];
        currentRFCTRAN = cliente.rfc;

        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-rfc-login")));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-email-login")));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-password-login")));

        inputRFC.clear(); inputRFC.sendKeys(cliente.rfc);
        inputEmail.clear(); inputEmail.sendKeys(cliente.email);
        inputContrasena.clear(); inputContrasena.sendKeys(cliente.contrasena);
        System.out.println("Intentando iniciar sesión con: " + cliente.email);
    }

    @Description("Da clic en el botón de 'Iniciar sesión'.")
    public static void BotonIniciosesion() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='root']/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")));
        submitButton.click();
    }

    @Description("Maneja alerta de sesión previa.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (TimeoutException e) {
            System.out.println("No se encontró alerta o no era necesaria.");
        }
    }

    @Description("Reinicia la sesión cerrando el navegador y abriendo uno nuevo.")
    public static void reiniciarSesionDesdeCero() {
        if (driver != null) driver.quit();
        setup();
        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
        System.out.println("Sesión reiniciada.");
    }

    @Description("Da clic en el botón 'Comprometidos'.")
    public static void ClickBotonComprometidos() {
        By locator = By.xpath("//button[normalize-space(text())='Comprometidos']");
        WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", boton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);
        System.out.println("Botón 'Comprometidos' clickeado.");
    }



    public static void buscarYProcesarSubasta() {
        String excelPath = "C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\java\\GMLogistico\\Subastas en Espera documentación 1.xlsx";
        String folioEncontrado = null;
        int filaProcesada = -1;

        // 1) Leer el Excel y buscar la PRIMERA fila con el RFC que NO esté ya procesada
        try ( FileInputStream fis = new FileInputStream(excelPath);
              Workbook workbook = WorkbookFactory.create(fis) ) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String rfc = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .getStringCellValue().trim();
                if (!rfc.equalsIgnoreCase(currentRFCTRAN)) {
                    continue;  // no es tu RFC
                }

                Cell procCell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String estado = procCell.getStringCellValue().trim();
                if ("Procesado".equalsIgnoreCase(estado)) {
                    // ya lo procesaste en una iteración anterior: saltar al siguiente
                    System.out.println("Fila " + (i+1) + " ya procesada, buscando siguiente...");
                    continue;
                }

                // ¡esta es la primera fila NO procesada!
                Cell folioCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                folioEncontrado = folioCell.getStringCellValue().trim();
                filaProcesada = i;

                // marcarla como procesada
                procCell.setCellValue("Procesado");
                break;
            }

            // 2) Guardar los cambios sólo si encontramos algo para procesar
            if (folioEncontrado != null) {
                try ( FileOutputStream fos = new FileOutputStream(excelPath) ) {
                    workbook.write(fos);
                }
                System.out.println("Fila " + (filaProcesada + 1) + " marcada como 'Procesado'.");
            } else {
                System.out.println("No hay más subastas nuevas para RFC " + currentRFCTRAN + ". Se omite esta iteración.");
                return;  // salir sin hacer el filtro UI
            }

        } catch (IOException e) {
            System.out.println("Error accediendo al Excel: " + e.getMessage());
            Assertions.fail("No se pudo leer/escribir el Excel.");
        }

        // 3) Aplicar el folio encontrado en la UI
        WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Buscar']")));
        campoBusqueda.clear();
        campoBusqueda.sendKeys(folioEncontrado);
        System.out.println("Folio ingresado en búsqueda: " + folioEncontrado);

        WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Aplicar']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonAplicar);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonAplicar);
        System.out.println("Botón 'Aplicar' clickeado correctamente.");
    }


    @Description("Abre el menú de opciones (botón “more”) y selecciona 'Enviar documentos'.")
    public void BotonOpcionesYEnviarDocumentos() {
        try {
            // 1) Localizar y hacer clic en el botón de “more” (tres puntos)
            WebElement btnMore = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='more']")
            ));
            // Asegurar que está visible
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnMore);
            try {
                btnMore.click();
            } catch (ElementClickInterceptedException ex) {
                System.out.println("Click interceptado en 'more', usando JS fallback.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnMore);
            }
            System.out.println("✅ Menú 'more' abierto.");

            // 2) Esperar y seleccionar la opción 'Enviar documentos'
            WebElement enviarDocsItem = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[normalize-space()='Enviar documentos']")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", enviarDocsItem);
            try {
                enviarDocsItem.click();
            } catch (ElementClickInterceptedException ex) {
                System.out.println("Click interceptado en 'Enviar documentos', usando JS fallback.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", enviarDocsItem);
            }
            System.out.println("✅ 'Enviar documentos' seleccionado.");
        } catch (Exception e) {
            System.out.println("Error en BotonOpcionesYEnviarDocumentos: " + e.getMessage());
            Assertions.fail("Fallo en BotonOpcionesYEnviarDocumentos: " + e.getMessage());
        }
    }


    @Description("Da clic en el botón de envío final 'Enviar documentos'.")
    public static void ClickBotonEnviarDocumentosFinal() {
        try {
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Enviar documentos']");
            WebElement btnEnviar = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnEnviar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnEnviar);
            System.out.println("✔ Botón 'Enviar documentos' clickeado.");
        } catch (Exception e) {
            System.out.println("Error en ClickBotonEnviarDocumentosFinal: " + e.getMessage());
            Assertions.fail("Fallo en ClickBotonEnviarDocumentosFinal: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón 'Aceptar' del diálogo de confirmación.")
    public static void ClickBotonAceptarConfirmacion() {
        try {
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Aceptar']");
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnAceptar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnAceptar);
            System.out.println("✔ Botón 'Aceptar' clickeado.");
        } catch (Exception e) {
            System.out.println("Error en ClickBotonAceptarConfirmacion: " + e.getMessage());
            Assertions.fail("Fallo en ClickBotonAceptarConfirmacion: " + e.getMessage());
        }
    }
}
