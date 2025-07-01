package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportacionSubasta {

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
       // options.addArguments("--headless=new"); // Modo headless usando el nuevo modo de Chrome
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

    @RepeatedTest(10)
    @Order(2)
    @Description("Importacion subastas logistico")
    public void ImportacionSubastaexcel() {
        try {

            clickImportarSubastaViajes();
            cargarDocumento();
            BotonImportarSubastas();
            clickAceptar();


        } catch (Exception e) {
            System.out.println("Error dentro de ImportarArchivo(): " + e.getMessage());
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

        Cliente cliente = clientes[4];

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

    @Description("Hace clic en el botón Importar Subasta/Viajes con un solo try-catch.")
    public static void clickImportarSubastaViajes() {
        try {
            WebElement btnImportar = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[.//span[text()='Importar Subasta/Viajes']]")
                    )
            );
            btnImportar.click();
            System.out.println("Botón 'Importar Subasta/Viajes' clickeado.");
        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                System.out.println("El botón no fue encontrado o no estuvo clickeable a tiempo.");
            } else {
                System.out.println("Error al intentar clicar el botón: " + e.getMessage());
            }
        }
    }

    @Description("Carga un documento .xlsm usando el RFC actual como nombre de archivo.")
    public static void cargarDocumento() {
        try {
            // 1. Localiza el input[file] oculto
            WebElement fileInput = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[type='file'][accept='.xlsx,.xlsm']")
                    )
            );

            // 2. Construye la ruta absoluta al archivo .xlsm bajo GMLogistico
            String projectDir = System.getProperty("user.dir");
            String filePath = projectDir
                    + File.separator + "src"
                    + File.separator + "test"
                    + File.separator + "java"
                    + File.separator + "GMLogistico"
                    + File.separator + currentRFC
                    + ".xlsm";

            // 3. Envía la ruta para que Selenium cargue el archivo
            fileInput.sendKeys(filePath);
            System.out.println("Documento cargado: " + filePath);
        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                System.out.println("No se encontró el input de archivo a tiempo.");
            } else {
                System.out.println("Error al cargar el documento: " + e.getMessage());
            }
        }
    }

    @Description("Busca el botón ‘Importar Subastas’ usando Selenium y devuelve el WebElement.")
    public static WebElement BotonImportarSubastas() {
        By locator = By.xpath(
                "//button[contains(@class,'filled-button') and normalize-space(text())='Importar Subastas']"
        );
        try {
            // Espera a que esté visible
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
        } catch (TimeoutException e) {
            System.out.println("Botón 'Importar Subastas' no fue encontrado a tiempo.");
            return null;
        }
    }

    @Description("Hace clic en el botón Aceptar dentro del modal de importación.")
    public static void clickAceptar() {
        try {
            // Espera a que ya no haya overlay bloqueando la pantalla
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.MuiBackdrop-root")
            ));

            WebElement btnAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(@class,'filled-button') and normalize-space(text())='Aceptar']")
                    )
            );

            // Por si algo aún lo intercepta, haz scroll y JS-click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnAceptar);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnAceptar);
            System.out.println("Botón 'Aceptar' clickeado.");

            // Y finalmente espera a que el modal desaparezca
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.MuiDialog-container")
            ));
        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                System.out.println("El botón 'Aceptar' no estuvo disponible a tiempo.");
            } else {
                System.out.println("Error al clicar 'Aceptar': " + e.getMessage());
            }
        }
    }


}