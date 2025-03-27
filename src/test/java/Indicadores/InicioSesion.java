package Indicadores;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.Rectangle;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class InicioSesion {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String[]> credentialThreadLocal = new ThreadLocal<>();

    private static final String[][] CREDENTIALS = {
            {"UsuarioPrueba1", "Prueba.0000"},
            {"UsuarioPrueba2", "Prueba.0000"}
    };

    private static final AtomicInteger credentialCounter = new AtomicInteger(0);

    private static final BlockingQueue<Integer> posicionesDisponibles = new ArrayBlockingQueue<>(2);
    private static final ThreadLocal<Integer> posicionAsignada = new ThreadLocal<>();

    static {
        for (int i = 0; i < 2; i++) {
            posicionesDisponibles.add(i);
        }
    }

    public static void setup(String navegador) {
        if (driverThreadLocal.get() == null) {
            WebDriver driver = null;

            switch (navegador.toLowerCase()) {
                case "firefox":
                    System.out.println("🦊 Iniciando pruebas en Firefox...");
                    System.setProperty("webdriver.gecko.driver", "C:\\RepositorioPrueAuto\\Mozila\\geckodriver.exe");
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    System.out.println("🌐 Iniciando pruebas en Edge...");
                    System.setProperty("webdriver.edge.driver", "C:\\RepositorioPrueAuto\\Edge\\msedgedriver.exe");
                    EdgeOptions edgeOptions = new EdgeOptions();
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    System.out.println("🔵 Iniciando pruebas en Chrome...");
                    System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
                    ChromeOptions chromeOptions = new ChromeOptions();
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            try {
                int posicion = posicionesDisponibles.take(); // 0 o 1
                posicionAsignada.set(posicion);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] pantallas = ge.getScreenDevices();

                if (pantallas.length < 2) {
                    System.out.println("⚠ Solo se detectó un monitor. Ambas ventanas se abrirán en el mismo.");
                }

                GraphicsConfiguration configPantalla = pantallas.length > posicion
                        ? pantallas[posicion].getDefaultConfiguration()
                        : pantallas[0].getDefaultConfiguration();

                Rectangle areaPantalla = configPantalla.getBounds();

                driver.manage().window().setPosition(new org.openqa.selenium.Point(areaPantalla.x, areaPantalla.y));
                driver.manage().window().setSize(new org.openqa.selenium.Dimension(areaPantalla.width, areaPantalla.height));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get("https://www.softwareparatransporte.com/");

            driverThreadLocal.set(driver);
            waitThreadLocal.set(wait);
        }
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWait() {
        return waitThreadLocal.get();
    }

    private static String[] getCredential() {
        if (credentialThreadLocal.get() == null) {
            int index = credentialCounter.getAndIncrement() % CREDENTIALS.length;
            credentialThreadLocal.set(CREDENTIALS[index]);
            System.out.println("🧵 Hilo " + Thread.currentThread().getId() + " usando credencial: " + CREDENTIALS[index][0]);
        }
        return credentialThreadLocal.get();
    }

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

            inputEmpresa.sendKeys("IIA040805DZ4");
            inputUsuario.sendKeys(username);
            inputContrasena.sendKeys(password);

            System.out.println("✅ Usuario ingresado: " + username);
        } catch (Exception e) {
            System.err.println("⚠ Error en fillForm(): " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    public static void handleNovedadesScreen() {
        try {
            WebDriverWait wait = getWait();
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("⚠ Pantalla de novedades no encontrada.");
        }
    }

    public static void cerrarSesion() {
        try {
            WebDriver driver = getDriver();
            if (driver != null) {
                System.out.println("🔓 Cerrando sesión y WebDriver...");
                driver.quit();
                driverThreadLocal.remove();
                waitThreadLocal.remove();
                credentialThreadLocal.remove();

                Integer posicion = posicionAsignada.get();
                if (posicion != null) {
                    posicionesDisponibles.put(posicion);
                    posicionAsignada.remove();
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Error en cerrarSesion(): " + e.getMessage());
        }
    }
}
