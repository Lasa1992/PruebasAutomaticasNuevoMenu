package TransportePersonal;
import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Recorrido {

    private static WebDriver driver;
    private static WebDriverWait wait;


    private static final String NUMERO_CLIENTE = Variables.CLIENTE;       // Número de cliente


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
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
    }

    @Test
    @Order(3)
    @Description("Metodos para entrar al listado de Recorridos")
    public void EntrarAViajes() {
        BotonModuloTransportePersonal();
        BotonListadoRecorridos();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Se ingresa al listado de Viajes y se crea una Carta Porte, se factura y timbra el viaje.")
    public void testCrearViaje() {

        BotonRegistrarRecorrido();
        CampoNumCliente();
        CampoCodigoRuta();
        BotonAgregarOperadorCamion();
        CodigoOperador();
        AceptarVentanaOpe();
        AceptarRecorrido();


    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }


    private static void BotonModuloTransportePersonal() {
        try {
            // Espera explícita hasta que el botón (enlace) que contiene la imagen sea clicable
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRANSPORTEDEPERSONAL')]")));

            // Hacer clic en el botón una vez esté listo
            ModuloBotonTrafico.click();

        } catch (Exception e) {
            // Manejo del error utilizando la clase UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Transporte Personal no funciona.");
        }
    }

    private static void BotonListadoRecorridos() {
        try {
            // Espera explícita hasta que el enlace que contiene la imagen sea clicable
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRANSPORTEDEPERSONAL/RECORRIDOS')]]")));

            // Hacer clic en el enlace una vez esté listo
            ListadoBoton.click();

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Recorridos no funciona.");
        }
    }

    private static void BotonRegistrarRecorrido() {
        try {

            WebElement BotonRecorrido = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_REGISTRAR\"]")));
            BotonRecorrido.click();
            System.out.println("Se presiono el boton agregar recorrido");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Recorridos no funciona.");
        }
    }

    private static void CampoNumCliente() {
        try {
            WebElement CodigoCliente = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/table/tbody/tr[2]/td/div[5]/table/tbody/tr/td/ul/li[2]/input")));
            CodigoCliente.click();
            CodigoCliente.sendKeys(NUMERO_CLIENTE);
            CodigoCliente.sendKeys(Keys.TAB);
            Thread.sleep(1000);
            System.out.println("Se asigno cliente al recorrido");
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al Recorrido");
        }
    }

    private static void CampoCodigoRuta() {
        try {
            WebElement Codigoruta = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[5]/table/tbody/tr/td/ul/li[2]/input")));
            Codigoruta.click();
            Codigoruta.sendKeys("8");
            Codigoruta.sendKeys(Keys.TAB);
            Thread.sleep(1000);
            System.out.println("Se asigno ruta al recorrido");
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar ruta al Recorrido");
        }
    }

    private static void BotonAgregarOperadorCamion() {
        try {

            WebElement Botonasigoper = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div/table/tbody/tr/td/input"));
            Botonasigoper.click();
            System.out.println("Se presiono el boton para asignar operaodr y unidad");
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el boton de asignar operador y unidad al Recorrido");
        }
    }

    private static void CodigoOperador() {
        try {
            WebElement CampoOperador = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/div[6]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div[2]/div/input")));
            CampoOperador.click();
            CampoOperador.sendKeys(Variables.Operador);
            CampoOperador.sendKeys(Keys.TAB);
            System.out.println("Se asigno el operador sin problemas");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar operador");
        }
    }

    private static void AceptarVentanaOpe() {
        try {
            WebElement Aceptarventana = driver.findElement(By.xpath("/html/body/form/div[6]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/table/tbody/tr/td/input"));
            Aceptarventana.click();
            System.out.println("Se acepto ventana de asignacion operador");
        } catch(Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar ventana de asignacion del operador");
        }
    }

    private static void AceptarRecorrido() {
        try {
            WebElement Aceptaregistro = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[16]/table/tbody/tr/td/input")));
            Aceptaregistro.click();
            System.out.println("Se Acepto recorrido");
        } catch(Exception e){
            UtilidadesAllure.manejoError(driver, e, "Se produjo un error al intentar guardar el recorrido");
        }
    }
}



