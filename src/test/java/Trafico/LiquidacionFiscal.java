package Trafico;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiquidacionFiscal {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String folioLiquidacion; // Para almacenar el folio de la liquidación


    // Variables definidas a nivel de clase (por ejemplo, en la parte superior del script)
    private static final String VALOR_OPERADOR = Variables.Operador; // Se toma de la clase Variables

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesión - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @Description("Manejo del tipo de cambio y ventana de novedades")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @Test
    @Order(3)
    @Description("Ingresar al módulo de Liquidaciones Fiscales")
    public void ingresarModuloLiquidacionesFiscales() {
        ModuloTrafico();
        LiquidacionesFiscales();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Proceso de Liquidación Fiscal")
    public void ProcesoLiquidacionFiscal() {
        BotonAgregarLiquidacionFiscal();
        FolioLiquidacion();
        Operador();
        DiasaLiquidar();
        AceptarLiquidacion();
        BuscarLiquidacion();
        SeleccionarLiquidacion();
        BotonPagar();
        Aceptarpago();
        MensajeCFDI();
        MensajeTimbre();
        MensajePoliza();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void ModuloTrafico() {
        try {
            WebElement moduloTrafico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, 'TRAFICO')]")));
            moduloTrafico.click();
            System.out.println("Se ingresó al módulo de Tráfico.");
        } catch (Exception e) {
            System.err.println("Error al ingresar al módulo de Tráfico: " + e.getMessage());
        }
    }

    private void LiquidacionesFiscales() {
        try {
            WebElement liquidacionesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@onclick, 'PAGE_ProLiquidacionesFiscalesListado')]")));
            liquidacionesButton.click();
            System.out.println("Se accedió a Liquidaciones Fiscales.");
        } catch (Exception e) {
            System.err.println("Error al seleccionar Liquidaciones Fiscales: " + e.getMessage());
        }
    }

    private void BotonAgregarLiquidacionFiscal() {
        try {
            WebElement btnAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            btnAgregar.click();
            System.out.println("Se presionó el botón 'Agregar' para Liquidación Fiscal.");
        } catch (Exception e) {
            System.err.println("Error al presionar 'Agregar': " + e.getMessage());
        }
    }

    private void FolioLiquidacion() {
        try {
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            folioLiquidacion = campoFolio.getAttribute("value").trim();
            System.out.println("Folio de la Liquidación: " + folioLiquidacion);
        } catch (Exception e) {
            System.err.println("Error al obtener el folio de la Liquidación: " + e.getMessage());
        }
    }


    private void Operador() {
        try {
            WebElement campoOperador = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_NUMEROOPERADOR")));
            campoOperador.sendKeys(VALOR_OPERADOR);
            System.out.println("Se ingresó el operador: " + VALOR_OPERADOR);
        } catch (Exception e) {
            System.err.println("Error al ingresar el operador: " + e.getMessage());
        }
    }

    private void DiasaLiquidar() {
        try {
            WebElement campoDias = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_DIAS")));
            Random random = new Random();
            int diasAleatorios = random.nextInt(14) + 2; // Número entre 2 y 14
            campoDias.clear();
            campoDias.sendKeys(String.valueOf(diasAleatorios));
            System.out.println("Días a Liquidar: " + diasAleatorios);
        } catch (Exception e) {
            System.err.println("Error al ingresar los días a liquidar: " + e.getMessage());
        }
    }

    private void AceptarLiquidacion() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            btnAceptar.click();
            System.out.println("Se aceptó la liquidación.");
        } catch (Exception e) {
            System.err.println("Error al aceptar la liquidación: " + e.getMessage());
        }
    }

    private void BuscarLiquidacion() {
        try {
            WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='search'][aria-controls='TABLE_ProLiquidacionesFiscales']")));
            campoBusqueda.clear();
            campoBusqueda.sendKeys(folioLiquidacion);
            campoBusqueda.sendKeys(Keys.ENTER);
            System.out.println("Se buscó la liquidación con folio: " + folioLiquidacion);
        } catch (Exception e) {
            System.err.println("Error al buscar la liquidación: " + e.getMessage());
        }
    }

    private void SeleccionarLiquidacion() {
        try {
            WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("TABLE_ProLiquidacionesFiscales")));
            List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));

            for (WebElement fila : filas) {
                String folioEncontrado = fila.findElement(By.xpath("./td[1]")).getText().trim();
                if (folioEncontrado.equals(folioLiquidacion)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
                    fila.click();
                    System.out.println("✅ Liquidación seleccionada: " + folioLiquidacion);
                    return;
                }
            }
            System.err.println("❌ Liquidación no encontrada: " + folioLiquidacion);
        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar la liquidación: " + e.getMessage());
        }
    }


    private void BotonPagar() {
        try {
            WebElement btnPagar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_PAGARLIQUIDACION")));
            btnPagar.click();
            System.out.println("Se presionó el botón 'Pagar'.");
        } catch (Exception e) {
            System.err.println("Error al presionar 'Pagar': " + e.getMessage());
        }
    }

    private void Aceptarpago() {
        try {
            WebElement btnAceptarPago = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            btnAceptarPago.click();
            System.out.println("Se aceptó el pago.");
        } catch (Exception e) {
            System.err.println("Error al aceptar el pago: " + e.getMessage());
        }
    }

    private void MensajeCFDI() {
        waitAndClick("BTN_YES", "CFDI");
    }

    private void MensajeTimbre() {
        waitAndClick("BTN_YES", "Timbre");
    }

    private void MensajePoliza() {
        waitAndClick("BTN_OK", "Póliza");
    }

    private void waitAndClick(String id, String mensaje) {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
            btn.click();
            System.out.println("Se aceptó el mensaje de " + mensaje);
        } catch (Exception e) {
            System.err.println("Error al aceptar mensaje de " + mensaje + ": " + e.getMessage());
        }
    }
}