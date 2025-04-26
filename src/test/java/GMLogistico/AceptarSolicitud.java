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
            ClickBotonCompletos();
            ClickIconoPostulaciones();
            BotonAceptarSolicitud();
            CheckDocumento();
            ValidarTablaYSubirDocumento();
            clickBotonEnviarAceptacion();
            clickBotonAceptar();
        } catch (Exception e) {
            System.out.println("Error dentro de AceptarSolicitud(): " + e.getMessage());
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

    @Description("Da clic en el botón 'Completos'.")
    public static void ClickBotonCompletos() {
        WebElement botonCompletos = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'MuiTab-root') and contains(text(), 'Completos')]")
        ));
        botonCompletos.click();
        System.out.println("Botón 'Completos' clickeado.");
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

    @Description("Activar el checkbox de documento.")
    public static void CheckDocumento() {
        WebElement checkboxDoc = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[2]/div[3]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/span")
        ));
        checkboxDoc.click();
        System.out.println("Checkbox documento activado.");
    }

    @Description("Validar tabla y subir documento si está vacía.")
    public static void ValidarTablaYSubirDocumento() {
        List<WebElement> filas = driver.findElements(By.xpath("//tbody[@class='MuiTableBody-root css-1xnox0e']/tr"));

        if (filas.isEmpty()) {
            System.out.println("Tabla vacía. Subiendo documento...");

            WebElement inputFile = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("contained-button-file")
            ));
            inputFile.sendKeys("C:\\Users\\Lasa9\\IdeaProjects\\GMQA\\src\\test\\java\\GMLogistico\\Doc 100 KB.pdf");

            System.out.println("Documento subido exitosamente.");
        } else {
            System.out.println("La tabla ya tiene documentos, no se sube ninguno.");
        }
    }




    @Description("Clic en el botón Enviar para aceptar la postulación.")
    public static void clickBotonEnviarAceptacion() {
        WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Enviar')]")
        ));
        botonEnviar.click();
        System.out.println("Botón Enviar clickeado exitosamente.");
    }

    @Description("Clic en el botón Aceptar para confirmar aceptación.")
    public static void clickBotonAceptar() {
        WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Aceptar')]")
        ));
        botonAceptar.click();
        System.out.println("Botón Aceptar clickeado exitosamente.");
    }
}
