package Indicadores;

import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Prueba de indicadores")
public class IndicadoresTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    public String nombreIndicador;
    public WebElement botonQuitarIndicador;

    @BeforeEach
    public void setup() {
        // 🛠️ Obtener el navegador dinámicamente desde la variable del sistema
        String navegador = System.getProperty("navegador", "chrome"); // Si no se especifica, usa Chrome
        System.out.println("🌍 Configurando pruebas en: " + navegador.toUpperCase());

        // 🛠️ Configurar el WebDriver con el navegador correcto
        InicioSesion.setup(navegador);
        driver = InicioSesion.getDriver();
        wait = InicioSesion.getWait();
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesión - Se utiliza un usuario disponible en la cola")
    public void inicioSesion() {
        InicioSesion.fillForm();   // ✅ Sin parámetros
        InicioSesion.submitForm(); // ✅ Sin parámetros
        InicioSesion.handleAlert(); // ✅ Sin parámetros
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio();       // ✅ Sin parámetros
        InicioSesion.handleNovedadesScreen();  // ✅ Sin parámetros
    }


    @RepeatedTest(4) //MAX 13 repeticiones ya que cada repeticion suma al ciclo.
    @Order(3)
    @DisplayName("Agregar, seleccionar y quitar indicador")
    @Description("Da clic en el boton de agregar indicador, para despues seleccionar un indicador, lo agrega y lo quita. Continua con los demas indicadores sucesivamente.")
    @Epic("Indicadores")
    @Story("Indicadores de Cobranza, Mapa ubicaciones, Viajes, etc")
    public void agregarYSeleccionarIndicadorCiclo(RepetitionInfo repetitionInfo) {
        // Definir el número de repeticiones
        try {
            agregarIndicador();
            seleccionarIndicador(repetitionInfo.getCurrentRepetition());
            validarIndicador();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agregarIndicador() {
        try {
            // Cerrar indicador anterior si existe
            List<WebElement> botonesCerrar = driver.findElements(By.xpath("//*[@id=\"z_BTN_CERRARFRAME1_IMG\"]"));
            if (!botonesCerrar.isEmpty() && botonesCerrar.get(0).isDisplayed()) {
                validarIndicador();
            }

            // Verificar si estamos dentro de un iframe
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement iframe : iframes) {
                driver.switchTo().frame(iframe);
                List<WebElement> botonAgregar = driver.findElements(By.xpath("//*[@id=\"z_BTN_AGREGARINDICADOR1_IMG\"]"));
                if (!botonAgregar.isEmpty()) {
                    System.out.println("Botón encontrado dentro de iframe.");
                    WebElement botonIndicador = wait.until(ExpectedConditions.elementToBeClickable(botonAgregar.get(0)));
                    botonIndicador.click();
                    driver.switchTo().defaultContent(); // Siempre regresar al contexto principal
                    System.out.println("Se dio clic correctamente en el botón de agregar indicador.");
                    return;
                }
                driver.switchTo().defaultContent();
            }

            // Si no se encontró en iframes, intentar directamente en el DOM principal
            WebElement botonIndicador = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"z_BTN_AGREGARINDICADOR1_IMG\"]"))
            );
            botonIndicador.click();
            System.out.println("Se dio clic correctamente en el botón de agregar indicador (fuera de iframe).");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al agregar indicador: ");
            System.out.println("No se encontró el botón para agregar el Indicador.");
        }
    }



    public void seleccionarIndicador(int currentRepetition) {
        try {
            // XPath dinámico basado en la repetición actual
            String dynamicXpath = "//*[@id='TABLE_CATUSUARIOSPROCESOS_" + (currentRepetition - 1) + "_2']";
            WebElement indicador;

            try {
                // Intentar localizar el indicador sin hacer scroll inicialmente
                indicador = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXpath)));

                // Hacer scroll hasta el indicador
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", indicador);
            } catch (TimeoutException te) {
                System.out.println("Indicador no encontrado de inmediato. Intentando hacer scroll.");
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500);");
                indicador = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath)));
            }

            nombreIndicador = indicador.getText();
            System.out.println("Se seleccionó el indicador con XPath: " + dynamicXpath + " y nombre: " + nombreIndicador);

            indicador.click();

            // Botón de grabar después de seleccionar el indicador
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_GRABAR\"]")));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar el indicador:");
            System.out.println("No se encontró el indicador al dar clic.");
            e.printStackTrace();
        }
    }

    public void validarIndicador() {
        try {
            InicioSesion.handleNovedadesScreen();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            List<WebElement> botones = driver.findElements(By.xpath("//*[@id=\"z_BTN_CERRARFRAME1_IMG\"]"));

            if (!botones.isEmpty()) {
                WebElement botonQuitarIndicador = wait.until(ExpectedConditions.elementToBeClickable(botones.get(0)));

                UtilidadesAllure.capturaImagen(driver);
                System.out.println("Indicador encontrado");
                botonQuitarIndicador.click();
                InicioSesion.handleAlert();
                System.out.println("Se quitó correctamente el indicador.");
                InicioSesion.handleNovedadesScreen();
            } else {
                System.out.println("No se encontró el indicador. Se continúa sin error.");
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al quitar el indicador: ");
            System.out.println("Excepción al quitar el indicador.");
        }
    }



    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }
}

