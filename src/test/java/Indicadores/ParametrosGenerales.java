package Indicadores;

import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ParametrosGenerales {

    public static WebDriver driver;
    public static WebDriverWait wait;

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
    @Test()
    @Order(3)
    @DisplayName("Ingresar a Configuracion")
    @Description("Da clic en el boton de agregar indicador, para despues seleccionar un indicador, lo agrega y lo quita. Continua con los demas indicadores sucesivamente.")
    public void ingresarConfiguracion() {
        // Definir el número de repeticiones
        try {
            handleImageButton();
            handleSubMenuButton();
            parametrosGenerales();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION1.JPG')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }
    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION/PARAMETROSGENERALES1.JPG')]")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void parametrosGenerales(){
        WebElement RFC = driver.findElement(By.id("EDT_RFC"));
        WebElement nombreFiscal = driver.findElement(By.id("EDT_NOMBREFISCAL"));
        WebElement nombreComercial = driver.findElement(By.id("EDT_NOMBRECOMERCIAL"));
        WebElement regimenFiscal = driver.findElement(By.id("COMBO_REGIMENFISCAL"));

        // Si necesitas obtener el valor que contiene el input (si es un campo de texto)
        String valorRFC = RFC.getAttribute("value");
        String valorNombreFiscal = nombreFiscal.getAttribute("value");
        String valorNombreComercial = nombreComercial.getAttribute("value");
        String valorRegimenFiscal = regimenFiscal.getAttribute("value");

        System.out.println("RFC: " + valorRFC +"\nNombre Fiscal: "+ valorNombreFiscal + "\nNombre Comercial: "
        + valorNombreComercial + "\nRegimen Fiscal: " + valorRegimenFiscal);
    }

    //VALIDAR QUE GUARDE LA INFORMACION DE LOS CAMPOS Y QUE ESTE PRESENTE LA IMAGEN. EN CASO

/*
    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
*/
}
