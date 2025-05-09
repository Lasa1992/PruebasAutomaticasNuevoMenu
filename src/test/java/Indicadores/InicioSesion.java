package Indicadores;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class InicioSesion {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String[]> credentialThreadLocal = new ThreadLocal<>();

    // Lista de credenciales para pruebas en paralelo
    private static final String[][] CREDENTIALS = {
            {"UsuarioPrueba1", "Prueba.0000"},
            {"UsuarioPrueba2", "Prueba.0000"},
            {"UsuarioPrueba3", "Prueba.0000"},
            {"UsuarioPrueba4", "Prueba.0000"}
    };

    private static final String RFCPRUEBA = Variables.RFC;

    // Contador atómico para asignar credenciales únicas en pruebas concurrentes
    private static final AtomicInteger credentialCounter = new AtomicInteger(0);

    /**
     * Inicializa el WebDriver en función del navegador especificado.
     * @param navegador Nombre del navegador ("chrome", "firefox", "edge").
     */
    public static void setup(String navegador) {
        if (driverThreadLocal.get() == null) {
            WebDriver driver;

            switch (navegador.toLowerCase()) {
                case "firefox":
                    // System.out.println("🦊 Iniciando pruebas en Firefox...");
                    System.setProperty("webdriver.gecko.driver", "C:\\RepositorioPrueAuto\\Mozila\\geckodriver.exe");
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    //System.out.println("🌐 Iniciando pruebas en Edge...");
                    System.setProperty("webdriver.edge.driver", "C:\\RepositorioPrueAuto\\Edge\\msedgedriver.exe");
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--inprivate");  // Modo incógnito
                    edgeOptions.addArguments("--disable-features=EdgeSignin"); // Desactiva autenticación automática

                    driver = new EdgeDriver(edgeOptions);
                    break;
                case "chrome":
                default:
                    //System.out.println("🔵 Iniciando pruebas en Chrome...");
                    System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless");
                    driver = new ChromeDriver();
                    break;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.manage().window().maximize();
            driver.get("https://www.softwareparatransporte.com/");

            driverThreadLocal.set(driver);
            waitThreadLocal.set(wait);

            //System.out.println("🌍 WebDriver creado en " + navegador + " para hilo " + Thread.currentThread().getId());
        }
    }

    /**
     * Obtiene el WebDriver del hilo actual.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Obtiene el WebDriverWait del hilo actual.
     */
    public static WebDriverWait getWait() {
        return waitThreadLocal.get();
    }

    /**
     * Asigna una credencial única a cada hilo y la devuelve.
     */
    private static String[] getCredential() {
        if (credentialThreadLocal.get() == null) {
            int index = credentialCounter.getAndIncrement() % CREDENTIALS.length;
            credentialThreadLocal.set(CREDENTIALS[index]);
            System.out.println("🧵 Hilo " + Thread.currentThread().getId() + " usando credencial: " + CREDENTIALS[index][0]);
        }
        return credentialThreadLocal.get();
    }

    /**
     * Completa el formulario de inicio de sesión con credenciales únicas por hilo.
     */
    public static void fillForm() {
        try {
            String[] credential = getCredential();
            String username = credential[0];
            String password = credential[1];

            WebDriver driver = getDriver();
            WebDriverWait wait = getWait();

            System.out.println("🔑 Iniciando sesión con usuario: " + username);

            WebElement inputEmpresa = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_EMPRESA")));
            WebElement inputUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_USUARIO")));
            WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CONTRASENA")));

            inputEmpresa.sendKeys(Variables.RFC);
            inputUsuario.sendKeys(username);
            inputContrasena.sendKeys(password);

            System.out.println("✅ Usuario ingresado: " + username);
        } catch (Exception e) {
            System.err.println("⚠ Error en fillForm(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía el formulario de inicio de sesión.
     */
    public static void submitForm() {
        try {
            WebDriverWait wait = getWait();
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));

            System.out.println("🖱️ Haciendo clic en el botón de inicio de sesión...");
            submitButton.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("⚠ Error en submitForm(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja alertas emergentes después del inicio de sesión.
     */
    public static void handleAlert() {
        try {
            WebDriverWait wait = getWait();
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("✅ Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("⚠ No se encontró una alerta.");
        }
    }

    /**
     * Maneja la pantalla de tipo de cambio si aparece.
     */
    public static void handleTipoCambio() {
        try {
            WebDriver driver = getDriver();
            WebDriverWait wait = getWait();
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("⚠ Ventana tipo de cambio no encontrada.");
        }
    }

    /**
     * Maneja la pantalla de novedades si aparece.
     */
    public static void handleNovedadesScreen() {
        try {
            WebDriverWait wait = getWait();
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));

            WebElement acceptButton = novedadesScreen.findElement(By.xpath("//*[@id=\"z_BTN_ACEPTAR_IMG\"]"));
            acceptButton.click();

            System.out.println("✅ Pantalla de novedades cerrada con éxito.");
        } catch (Exception e) {
            System.out.println("⚠ Pantalla de novedades no encontrada.");
        }
    }

    /**
     * Cierra la sesión y libera los recursos del WebDriver.
     */
    public static void cerrarSesion() {
        try {
            WebDriver driver = getDriver();
            if (driver != null) {
                System.out.println("🔓 Cerrando sesión y WebDriver...");
                driver.quit();
                driverThreadLocal.remove();
                waitThreadLocal.remove();
                credentialThreadLocal.remove();
            }
        } catch (Exception e) {
            System.err.println("⚠ Error en cerrarSesion(): " + e.getMessage());
        }
    }
}