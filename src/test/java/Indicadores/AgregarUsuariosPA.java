package Indicadores;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Orden de ejecución
public class AgregarUsuariosPA {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Tiempo de espera explícito
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Ejecuta el inicio de sesión completo con manejo de alertas y ventanas emergentes")
    public void InicioSesionCompleto() {
        // **Rellenar formulario de inicio de sesión**
        driver.findElement(By.id("EDT_EMPRESA")).sendKeys("IIA040805DZ4");
        driver.findElement(By.id("EDT_USUARIO")).sendKeys("GM");
        driver.findElement(By.id("EDT_CONTRASENA")).sendKeys("");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();
        System.out.println("Inicio de sesión completado.");

        // **Manejar alerta si aparece**
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (Exception e) {
            System.out.println("No se encontró ninguna alerta.");
        }

        // **Manejo de la ventana de tipo de cambio**
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
            System.out.println("Tipo de cambio establecido.");
        } catch (Exception e) {
            System.out.println("Ventana de tipo de cambio no encontrada.");
        }

        // **Manejo de la ventana de novedades**
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
            System.out.println("Pantalla de novedades cerrada.");
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    @Test
    @Order(2)
    @Description("Accede al módulo de configuración y listado de usuarios")
    public void AccedeListadoUsuarios() {
        // **Ingresar al módulo de Configuración**
        WebElement moduloConf = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION1')]")));
        moduloConf.click();
        System.out.println("Ingresó al módulo de Configuración.");

        // **Ingresar al listado de Usuarios**
        WebElement subUsua = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION/USUARIOS1')]")));
        subUsua.click();
        System.out.println("Ingresó al listado de usuarios.");
    }

    @RepeatedTest(3)
    @Order(3)
    @Description("Agregar un nuevo usuario al sistema")
    public void AgregarUsuarios(RepetitionInfo repetitionInfo) {

        // Obtiene el número de la repetición actual
        int repeticionActual = repetitionInfo.getCurrentRepetition();

        // Presiona el Boton Agregar
        WebElement BotonAgregar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/div/div[1]/div/table/tbody/tr/td/a/span/span")));
        BotonAgregar.click();
        System.out.println("Presionó el botón Agregar Usuario.");

        // Campo Usuario
        WebElement CampoUsuario = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[2]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));
        CampoUsuario.click();

        // Generar usuario dinámico: UsuarioPrueba1, UsuarioPrueba2, UsuarioPrueba3...
        String usuarioDinamico = "UsuarioPrueba" + repeticionActual;
        CampoUsuario.sendKeys(usuarioDinamico);
        System.out.println("Se ingresó el usuario: " + usuarioDinamico);

        WebElement CampoNombre = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[3]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));
        CampoNombre.click();
        CampoNombre.sendKeys("Usuario Para Pruebas Automaticas" + repeticionActual);
        System.out.println("Se ingresó el nombre del usuario: Usuario Para Pruebas Automaticas" + repeticionActual);

        WebElement CampoNombrecorto = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[4]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));
        CampoNombrecorto.click();
        CampoNombrecorto.sendKeys("UPPA" + repeticionActual);
        System.out.println("Se ingresó el nombre corto del usuario: UPPA" + repeticionActual);

        WebElement CampoContrasena = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/div/input")));
        CampoContrasena.click();
        CampoContrasena.sendKeys("Prueba.0000");
        CampoContrasena.sendKeys(Keys.TAB);

        WebElement CampoContrasena2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[6]/div[1]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/div/input")));
        CampoContrasena2.click();
        CampoContrasena2.sendKeys("Prueba.0000");
        CampoContrasena2.sendKeys(Keys.TAB);

        WebElement CampoCorreo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[8]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));
        CampoCorreo.click();
        CampoCorreo.sendKeys("prueba@prueba.com");

        WebElement AceptarUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
        AceptarUsuario.click();
        System.out.println("Se ha creado el usuario: " + usuarioDinamico);

        WebElement AceptarMensaje = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
        AceptarMensaje.click();
        System.out.println("Se ha aceptado el mensaje de confirmación.");
    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        if (driver != null) {
            System.out.println("Esperando 5 segundos antes de cerrar el navegador...");
            Thread.sleep(5000); // Espera de 5 segundos (5,000 milisegundos)

            driver.quit();
            System.out.println("Navegador cerrado correctamente.");
        }
    }
}