package Indicadores;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Test
    @Order(3)
    @DisplayName("Agregar Indicador")
    public void agregarIndicador(){
        try {
            botonQuitarIndicador = driver.findElement(By.id("z_BTN_CERRARFRAME1_IMG"));
            if (botonQuitarIndicador.isDisplayed())
            {
                validarIndicador();
                InicioSesion.handleNovedadesScreen(wait);
            }else{
                WebElement botonIndicador = wait.until(ExpectedConditions.elementToBeClickable(By.id("z_BTN_AGREGARINDICADOR1_IMG")));
                botonIndicador.click();
                System.out.println("Se dio clic correctamente en el boton de agregar indicador.");
            }
        }catch(Exception e) {
                System.out.println("No se encontro el boton para agregar el Indicador.");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Seleccionar Indicador")
    public void seleccionarIndicador(){
        try{
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_GRABAR")));
            WebElement indicador = wait.until(ExpectedConditions.elementToBeClickable(By.id("TABLE_CATUSUARIOSPROCESOS_0_2")));
            indicador.click();
            nombreIndicador = indicador.getText();
            System.out.println("Se agrego el indicador: " + nombreIndicador);
            botonAgregar.click();
        }catch(Exception e){
            System.out.println("No se encontro el indicador al dar clic.");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Quitar el Indicador agregado")
    public void validarIndicador(){
        try{
            InicioSesion.handleNovedadesScreen(wait);
            // Ahora busca el texto después de manejar novedades
            botonQuitarIndicador = driver.findElement(By.id("z_BTN_CERRARFRAME1_IMG"));

            // Valida si se encontró el texto en la página
            if (botonQuitarIndicador.isDisplayed()) {
                System.out.println("Indicador encontrado");
                botonQuitarIndicador.click();
                InicioSesion.handleAlert(wait);
                System.out.println("Se quito correctamente el indicador.");
                InicioSesion.handleNovedadesScreen(wait);
            } else {
                System.out.println("El indicador no se encontró.");
            }
        }catch(Exception e){
            System.out.println("No se encontro el indicador.");
        }
    }
}
