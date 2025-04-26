package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Modo headless usando el nuevo modo de Chrome
        options.addArguments("--window-size=1920,1080"); // Definir tamaño para evitar problemas de render
        options.addArguments("--disable-gpu"); // Opcional, para compatibilidad total
        options.addArguments("--no-sandbox"); // Opcional, para ambientes Linux o CI

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
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

    @RepeatedTest(3464)
    @Order(2)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AceptarSolicitud() {
        try {
            //ClickBotonCompletos();
            //ClickIconoPostulaciones();
            //BotonAceptarSolicitud();
            //CheckDocumento();
            //ValidarTablaYSubirDocumento();
            //clickBotonEnviarAceptacion();
            //clickBotonAceptar();
        } catch (Exception e) {
            System.out.println("Error dentro de Envio documentos transportista(): " + e.getMessage());
            try {
                regresarAPaginaSubasta();
            } catch (Exception ex) {
                System.out.println("No se pudo regresar a Subasta. Reiniciando sesión...");
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
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
                new Cliente("GMTHCDEMO018", "jose.calidad@gmtransporterp.com", "123456"),
                new Cliente("TEGR820530HTC", "trans2@gmail.com", "123456"),
                new Cliente("CAMR951214MKL", "trans3@gmail.com", "123456"),
                new Cliente("LOGI4444445T6", "logi4@gmail.com", "123456"),
                new Cliente("LOGI1111112Q4", "logistico1@gmail.com", "123456")
        };

        Cliente cliente = clientes[0];

        currentRFCTRAN = cliente.rfc;

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
}