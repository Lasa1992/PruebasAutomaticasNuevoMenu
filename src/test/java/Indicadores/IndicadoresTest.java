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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Prueba de indicadores")
public class IndicadoresTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    public String nombreIndicador;
    public WebElement botonQuitarIndicador;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @DisplayName("Inicio de Sesion")
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @DisplayName("Alertas - Inicio Sesion")
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @RepeatedTest(13) //MAX 13 repeticiones ya que cada repeticion suma al ciclo.
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
            botonQuitarIndicador = driver.findElement(By.id("dwwBTN_CERRARFRAME1"));
            if (botonQuitarIndicador.isDisplayed()){
                validarIndicador();
                //InicioSesion.handleNovedadesScreen(wait);
            }
            WebElement botonIndicador = wait.until(ExpectedConditions.elementToBeClickable(By.id("z_BTN_AGREGARINDICADOR1_IMG")));
            botonIndicador.click();
            System.out.println("Se dio clic correctamente en el botón de agregar indicador.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver,e,"Error al agregar indicador: ");
            System.out.println("No se encontró el botón para agregar el Indicador.");
        }
    }

    public void seleccionarIndicador(int currentRepetition) {
        try {
            // El ID ahora cambia en función de la repetición actual
            String dynamicId = "TABLE_CATUSUARIOSPROCESOS_" + (currentRepetition - 1) + "_2";  // Restar 1 ya que la repetición empieza desde 1
            WebElement indicador;

            // Intentar encontrar el elemento, y si no está visible, hacer scroll
            try {
                // Intentar localizar el indicador sin hacer scroll inicialmente
                indicador = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(dynamicId)));

                // Mover el scroll hasta el indicador
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", indicador);
            } catch (TimeoutException te) {
                // Si el elemento no está visible o no se encuentra, manejar la excepción
                System.out.println("Indicador no encontrado de inmediato. Intentando hacer scroll.");
                // Puedes mover el scroll más abajo en la página (ajusta los píxeles según sea necesario)
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500);");
                // Vuelve a intentar localizar el indicador
                indicador = wait.until(ExpectedConditions.elementToBeClickable(By.id(dynamicId)));
            }

            // Obtener el texto del indicador
            nombreIndicador = indicador.getText();
            System.out.println("Se seleccionó el indicador con ID: " + dynamicId + " y nombre: " + nombreIndicador);

            // Clic en el indicador correspondiente
            indicador.click();

            // Botón de grabar después de seleccionar el indicador
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_GRABAR")));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar el indicador:");
            System.out.println("No se encontró el indicador al dar clic.");
            e.printStackTrace();
        }
    }

    public void validarIndicador() {
        try {
            InicioSesion.handleNovedadesScreen(wait);
            botonQuitarIndicador = driver.findElement(By.id("BTN_CERRARFRAME1"));
            Thread.sleep(2500);
            UtilidadesAllure.capturaImagen(driver);
            if (botonQuitarIndicador.isDisplayed()) {
                System.out.println("Indicador encontrado");
                botonQuitarIndicador.click();
                InicioSesion.handleAlert(wait);
                System.out.println("Se quitó correctamente el indicador.");
                InicioSesion.handleNovedadesScreen(wait);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver,e,"Error al quitar el indicador: ");
            System.out.println("No se encontró el indicador.");
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
