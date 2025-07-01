package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AceptarSolicitud {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public static String currentRFC;

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
        ChromeOptions options = new ChromeOptions();
        // Forzar headless de forma standard y legacy
        options.addArguments("--headless=new");
        options.addArguments("--headless");         // fallback
        // Evitar infobars y mensajes de automatización
        options.setExperimentalOption("excludeSwitches",
                Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);
        // Deshabilitar shortcuts que abren DevTools
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");
        // Tamaño de ventana y sandbox
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        // Inyectar en Chrome un pequeño snippet para desactivar F12 / Ctrl+Shift+I
        ((JavascriptExecutor) driver).executeScript(
                "window.addEventListener('keydown', e => { " +
                        "  if ((e.key === 'F12') || (e.ctrlKey && e.shiftKey && e.key==='I')) {" +
                        "    e.preventDefault(); e.stopPropagation();" +
                        "  }" +
                        "});"
        );

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.get("https://logisticav1.gmtransport.co/");
    }


    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void InicioSeSionLog() {
        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
    }

    @RepeatedTest(134)
    @Order(2)
    @Description("Aceptar Postulacion")
    public void AceptarPostulacionAleatoria() {
        try {

            clickTabCompletos();
            pause();
            ClickIconoPostulaciones();
            pause();
            BotonAceptarSolicitud();
            pause();
            clickBotonEnviarAceptacion();
            pause();
            clickBotonAceptarConfirmacion();
            pause();



        } catch (Exception e) {
            System.out.println("Error dentro de Aceptar Postulacion(): " + e.getMessage());
            try {
                regresarAPaginaSubasta();
            } catch (Exception ex) {
                System.out.println("No se pudo regresar al listado de Subastas. Reiniciando sesión...");
                reiniciarSesionDesdeCero();
            }
        }
    }

    @Description("Reinicia la sesión cerrando el navegador y abriendo uno nuevo.")
    public static void reiniciarSesionDesdeCero() {
        try {
            if (driver != null) {
                driver.quit();
            }

            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");

            Iniciosesion();
            BotonIniciosesion();
            MensajeAlerta();

            System.out.println("Sesión reiniciada correctamente.");
        } catch (Exception e) {
            System.out.println("Fallo al reiniciar sesión: " + e.getMessage());
            Assertions.fail("No se pudo reiniciar la sesión.");
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
            System.out.println("No se pudo hacer clic en 'Subasta viajes': " + e.getMessage());
            throw e;
        }
    }

    @Description("Llena los campos de inicio de sesión con información fija.")
    public static void Iniciosesion() {
        Cliente[] clientes = {
                new Cliente("IIA040805DZ4", "elisa.logistica@gmtransporterp.com", "123456"),
                new Cliente("LOGI2222224T5", "logistico2@gmail.com", "123456"),
                new Cliente("LOGI3333335T6", "logi3@gmail.com", "123456"),
                new Cliente("LOGI4444445T6", "logi4@gmail.com", "123456"),
                new Cliente("LOGI1111112Q4", "logistico1@gmail.com", "123456")
        };

        Cliente cliente = clientes[0];

        currentRFC = cliente.rfc;

        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-email-login")));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-password-login")));

        inputRFC.clear();
        inputEmail.clear();
        inputContrasena.clear();

        inputRFC.sendKeys(cliente.rfc);
        inputEmail.sendKeys(cliente.email);
        inputContrasena.sendKeys(cliente.contrasena);

        System.out.println("Intentando iniciar sesión con: " + cliente.email);
    }

    @Description("Da clic en el botón de 'Iniciar sesión'.")
    public static void BotonIniciosesion() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='root']/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
        ));
        submitButton.click();
    }

    @Description("Maneja alerta de sesión previa.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró alerta o no era necesaria.");
        }
    }

    @Description("Hace clic en la pestaña 'Completos'.")
    public static void clickTabCompletos() {
        try {
            // Esperar a que desaparezca cualquier backdrop/modal que impida el clic
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.MuiBackdrop-root")
            ));

            WebElement botonCompletos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Completos']")
            ));
            botonCompletos.click();
            System.out.println("Se hizo clic en la pestaña 'Completos'.");
        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en 'Completos': " + e.getMessage());
            Assertions.fail("Fallo al hacer clic en la pestaña 'Completos'");
        }
    }


    @Description("Da clic en el botón de postulaciones.")
    public static void ClickIconoPostulaciones() {
        List<WebElement> botones = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//button[contains(@class, 'rounded-lg') and .//p]")
        ));

        if (!botones.isEmpty()) {
            botones.get(0).click();
            System.out.println("Clic en el primer botón de postulaciones.");
        } else {
            throw new RuntimeException("No se encontraron botones de postulaciones.");
        }
    }

    @Description("Aceptar una solicitud de postulación aleatoria.")
    public static void BotonAceptarSolicitud() {
        List<WebElement> botones = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//button[contains(@class, 'filled-button') and contains(@class, 'enabled') and text()='Aceptar postulación']")
        ));

        if (!botones.isEmpty()) {
            int randomIndex = (int) (Math.random() * botones.size());
            botones.get(randomIndex).click();
            System.out.println("Clic en un botón aleatorio de 'Aceptar postulación'.");
        } else {
            throw new RuntimeException("No hay botones de 'Aceptar postulación'.");
        }
    }

    @Description("Clic en el botón 'Enviar' para aceptar la postulación.")
    public static void clickBotonEnviarAceptacion() {
        try {
            WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and normalize-space()='Enviar']")
            ));

            // Scroll al botón, por si está fuera de vista
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonEnviar);

            // Clic forzado (opcional si hay overlays)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonEnviar);

            System.out.println("Botón 'Enviar' clickeado exitosamente.");
        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en 'Enviar': " + e.getMessage());
            Assertions.fail("Fallo al hacer clic en el botón 'Enviar'.");
        }
    }

    @Description("Hace clic en el botón 'Aceptar' del mensaje de confirmación.")
    public static void clickBotonAceptarConfirmacion() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and normalize-space()='Aceptar']")
            ));

            // Asegurarse de que está visible
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonAceptar);

            // Forzar clic con JavaScript en caso de overlays o ripple effects
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonAceptar);

            System.out.println("Botón 'Aceptar' clickeado exitosamente.");
        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en 'Aceptar': " + e.getMessage());
            Assertions.fail("Fallo al hacer clic en el botón 'Aceptar'.");
        }
    }

    /** Pausa la ejecución 2 segundos sin propagar la InterruptedException. */
    private static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
