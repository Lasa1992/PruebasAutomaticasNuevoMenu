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
            // Buscar el botón para cerrar el frame si existe
            botonQuitarIndicador = driver.findElement(By.xpath("//*[@id=\"z_BTN_CERRARFRAME1_IMG\"]/span"));
            if (botonQuitarIndicador.isDisplayed()) {
                validarIndicador();
                //InicioSesion.handleNovedadesScreen(wait);
            }

            // Usar XPath para el botón de agregar indicador
            WebElement botonIndicador = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"z_BTN_AGREGARINDICADOR1_IMG\"]/span"))
            );
            botonIndicador.click();
            System.out.println("Se dio clic correctamente en el botón de agregar indicador.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al agregar indicador: ");
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
            InicioSesion.handleNovedadesScreen();

            // Cambiado el selector a XPath
            botonQuitarIndicador = driver.findElement(By.xpath("//*[@id='BTN_CERRARFRAME1']"));

            Thread.sleep(2500);
            UtilidadesAllure.capturaImagen(driver);

            if (botonQuitarIndicador.isDisplayed()) {
                System.out.println("Indicador encontrado");
                botonQuitarIndicador.click();
                InicioSesion.handleAlert();
                System.out.println("Se quitó correctamente el indicador.");
                InicioSesion.handleNovedadesScreen();
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al quitar el indicador: ");
            System.out.println("No se encontró el indicador.");
        }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }
}

