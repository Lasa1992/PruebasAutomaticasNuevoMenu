package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfirmacionTransportistaYDoc2 {

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
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");
        } catch (Exception e) {
            System.out.println("Error en setup: " + e.getMessage());
            Assertions.fail("No se pudo inicializar el navegador: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error en tearDown: " + e.getMessage());
        }
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

    @RepeatedTest(5000)
    @Order(2)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AceptarSolicitud() {
        try {
            ClickBotonComprometidos();
            OrdenarPorEstatusDobleClick();

            // Si falla abrir opciones, igual seguimos al siguiente paso
            try {
                BotonOpcionesYEnviarDocumentos();
            } catch (Exception e) {
                System.out.println("❌ Error en BotonOpcionesYEnviarDocumentos, continúo: " + e.getMessage());
            }

            // Si falla subir documento, seguimos con el envío final
            try {
              // SubirDocumentoTransportista();
            } catch (Exception e) {
                System.out.println("❌ Error en SubirDocumentoTransportista, continúo: " + e.getMessage());
            }

            ClickBotonEnviarDocumentosFinal();
            ClickBotonAceptarConfirmacion();

        } catch (Exception e) {
            System.out.println("⚠️ Error inesperado en AceptarSolicitud: " + e.getMessage());
            // Reiniciamos navegador y sesión antes de salir:
            reiniciarSesionDesdeCero();
            // y retornamos para que JUnit pase a la siguiente repetición
            return;
        }
    }


    @Description("Reinicia la sesión cerrando el navegador y abriendo uno nuevo.")
    public static void reiniciarSesionDesdeCero() {
        try {
            if (driver != null) driver.quit();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");

            Iniciosesion();
            BotonIniciosesion();
            MensajeAlerta();

            System.out.println("Sesión reiniciada correctamente.");
        } catch (Exception e) {
            System.out.println("Fallo al reiniciar sesión: " + e.getMessage());
            Assertions.fail("No se pudo reiniciar la sesión: " + e.getMessage());
        }
    }

    @Description("Intenta regresar a la página de 'Subasta viajes'.")
    public static void regresarAPaginaSubasta() {
        try {
            WebElement botonSubasta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h5[contains(text(),'Subasta viajes')]")
            ));
            botonSubasta.click();
            System.out.println("Regresando a 'Subasta viajes'.");
        } catch (Exception e) {
            System.out.println("Error en regresarAPaginaSubasta: " + e.getMessage());
            Assertions.fail("No se pudo regresar a Subasta: " + e.getMessage());
        }
    }

    @Description("Llena los campos de inicio de sesión con información fija.")
    public static void Iniciosesion() {
        try {
            Cliente[] clientes = {
                    new Cliente("GMTHCDEMO018", "jose.calidad@gmtransporterp.com", "123456"),
                    new Cliente("TEGR820530HTC", "trans2@gmail.com", "123456"),
                    new Cliente("CAMR951214MKL", "trans3@gmail.com", "123456"),
                    new Cliente("XIA190128J61", "xenon@gmail.com", "123456"),
                    new Cliente("VARG001010PLQ", "trans1@gmail.com", "123456")
            };
            Cliente cliente = clientes[4];
            currentRFCTRAN = cliente.rfc;

            WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
            WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-email-login")));
            WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-password-login")));

            inputRFC.clear(); inputRFC.sendKeys(cliente.rfc);
            inputEmail.clear(); inputEmail.sendKeys(cliente.email);
            inputContrasena.clear(); inputContrasena.sendKeys(cliente.contrasena);

            System.out.println("Intentando iniciar sesión con: " + cliente.email);
        } catch (Exception e) {
            System.out.println("Error en Iniciosesion: " + e.getMessage());
            Assertions.fail("Fallo en Iniciosesion: " + e.getMessage());
        }
    }

    @Description("Da clic en el botón de 'Iniciar sesión'.")
    public static void BotonIniciosesion() {
        try {
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='root']/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
            ));
            submitButton.click();
        } catch (Exception e) {
            System.out.println("Error en BotonIniciosesion: " + e.getMessage());
            Assertions.fail("Fallo en BotonIniciosesion: " + e.getMessage());
        }
    }

    @Description("Maneja alerta de sesión previa.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (TimeoutException e) {
            System.out.println("No se encontró alerta o no era necesaria.");
        } catch (Exception e) {
            System.out.println("Error en MensajeAlerta: " + e.getMessage());
            Assertions.fail("Fallo en MensajeAlerta: " + e.getMessage());
        }
    }

    @Description("Da clic en el botón 'Comprometidos'.")
    public static void ClickBotonComprometidos() {
        try {
            By locator = By.xpath("//button[normalize-space(text())='Comprometidos']");
            WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", boton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);
            System.out.println("Botón 'Comprometidos' clickeado.");
        } catch (Exception e) {
            System.out.println("Error en ClickBotonComprometidos: " + e.getMessage());
            Assertions.fail("Fallo en ClickBotonComprometidos: " + e.getMessage());
        }
    }

    @Description("Aplica doble clic en el encabezado de la columna Estatus para forzar la ordenación deseada.")
    public static void OrdenarPorEstatusDobleClick() {
        try {
            By sortBtnLocator = By.xpath("//span[normalize-space(text())='Estatus']/button");
            WebElement sortBtn = wait.until(ExpectedConditions.elementToBeClickable(sortBtnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sortBtn);
            wait.until(ExpectedConditions.stalenessOf(sortBtn));

            sortBtn = wait.until(ExpectedConditions.elementToBeClickable(sortBtnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sortBtn);
            wait.until(ExpectedConditions.stalenessOf(sortBtn));
            System.out.println("✔ Encabezado 'Estatus' clickeado dos veces para ordenar correctamente.");
        } catch (Exception e) {
            System.out.println("Error en OrdenarPorEstatusDobleClick: " + e.getMessage());
            Assertions.fail("Fallo en OrdenarPorEstatusDobleClick: " + e.getMessage());
        }
    }

    @Description("Abre el menú de opciones y selecciona 'Enviar documentos'")
    public void BotonOpcionesYEnviarDocumentos() {
        try {
            // 1) Localizar el botón de opciones
            WebElement btnOpciones = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[3]/div/div[5]/div[2]/div/table/tbody/tr/td[15]/div/button")
            ));

            // 2) Intentar click normal, si falla usar JS-click
            try {
                btnOpciones.click();
            } catch (ElementClickInterceptedException ex) {
                System.out.println("Click interceptado en opciones, aplicando JS fallback.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnOpciones);
            }
            System.out.println("✅ Menú de opciones abierto.");

            // 3) Esperar el primer elemento del menú y seleccionarlo
            WebElement enviarDocs = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@class='list-none p-0 m-0']/li[1]")
            ));
            try {
                enviarDocs.click();
            } catch (ElementClickInterceptedException ex) {
                System.out.println("Click interceptado en 'Enviar documentos', aplicando JS fallback.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", enviarDocs);
            }
            System.out.println("✅ 'Enviar documentos' seleccionado.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar 'Enviar documentos': " + e.getMessage());
            // Para que la prueba falle en caso de error, descomenta:
            // Assertions.fail("Fallo en BotonOpcionesYEnviarDocumentos: " + e.getMessage());
        }
    }




    @Description("Sube el PDF usando el input[type='file'] oculto en la página.")
    public static void SubirDocumentoTransportista() {
        try {
            List<WebElement> fileInputs = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("input[type='file']"))
            );
            WebElement inputFile = fileInputs.get(0);
            inputFile.sendKeys("C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\java\\GMLogistico\\Documento 9mb.pdf");
            System.out.println("✔ Documento adjuntado: Documento 9mb.pdf");
        } catch (Exception e) {
            System.out.println("Error en SubirDocumentoTransportista: " + e.getMessage());
            Assertions.fail("Fallo en SubirDocumentoTransportista: " + e.getMessage());
        }
    }

    @Description("Da clic en el botón de envío final 'Enviar documentos'.")
    public static void ClickBotonEnviarDocumentosFinal() {
        try {
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Enviar documentos']");
            WebElement btnEnviar = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnEnviar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnEnviar);
            System.out.println("✔ Botón 'Enviar documentos' (submit) clickeado.");
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
