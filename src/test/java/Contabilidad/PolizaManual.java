package Contabilidad;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PolizaManual {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String FolioPoliza = "";

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
        System.out.println("Setup: Navegador iniciado y URL cargada correctamente.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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


    @Test
    @Order(3)
    @Description("Acceder al módulo de Bancos")
    public void testIngresarModuloBancos() {
        ingresarModuloBancos();
        submoduloCheques();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Importación de Pólizas y Prepólizas")
    public void ImportacionPoliyPrep() {


        BotonAgregarManuamente();
        TipoPoliza();
        ConceptoPoliza();
        AgregarMovimiento();
        CuentaContable();
        Importe();
        Referencia();
        ConceptoMovimiento();
        AceptarMovimiento();
        CerrarVentanaMovimiento();
        AceptarPoliza();
        AceptarAlerta();



    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("TearDown: Navegador cerrado correctamente.");
        }
    }

    @Step("Abrir el módulo de Bancos")
    private void ingresarModuloBancos() {
        try {
            WebElement botonBancos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONTABILIDAD1')]")
            ));
            botonBancos.click();
            System.out.println("Módulo Bancos: Módulo de Bancos abierto correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el módulo de Contabilidad.");
        }
    }

    @Step("Abrir submódulo de Cheques")
    private void submoduloCheques() {
        try {
            WebElement subPolizascontables = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONTABILIDAD/POLIZASCONTABLES')]")
            ));
            subPolizascontables.click();
            System.out.println("Submódulo Cheques: Submódulo de Cheques abierto correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el submódulo Polizas Contables.");
        }
    }

    @Step("Registrar un nuevo Cheque")
    private void BotonAgregarManuamente() {
        try {
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div/input")
            ));
            botonRegistrar.click();
            System.out.println("Botón Agregar: El botón fue presionado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Agregar.");
        }
    }

    @Step("Seleccionar un tipo de póliza aleatorio")
    private void TipoPoliza() {
        try {
            // Localiza el combo para el tipo de póliza
            WebElement comboTipoPoliza = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")
            ));
            // Crear objeto Select para manejar el combo
            Select selectTipo = new Select(comboTipoPoliza);
            int totalOpciones = selectTipo.getOptions().size();
            // Generar un índice aleatorio entre 0 y (totalOpciones - 1)
            int indiceAleatorio = (int)(Math.random() * totalOpciones);
            selectTipo.selectByIndex(indiceAleatorio);
            System.out.println("TipoPoliza: Se seleccionó la opción aleatoria con índice " + indiceAleatorio);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar el tipo de póliza aleatorio.");
        }
    }

    @Step("Establecer el concepto de póliza")
    private void ConceptoPoliza() {
        try {
            // Localiza el campo de concepto de la póliza
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            // Obtener la fecha actual en formato "yyyy-MM-dd"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaActual = sdf.format(new Date());
            String concepto = "poliza manual " + fechaActual;
            campoConcepto.clear();
            campoConcepto.sendKeys(concepto);
            System.out.println("ConceptoPoliza: Se estableció el concepto: " + concepto);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer el concepto de póliza.");
        }
    }

    @Step("Agregar un movimiento")
    private void AgregarMovimiento() {
        try {
            // Localiza el botón para agregar movimiento
            WebElement btnAgregarMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div/div[1]/div[1]/div/table/tbody/tr/td/input")
            ));
            btnAgregarMov.click();
            System.out.println("AgregarMovimiento: Movimiento agregado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al agregar movimiento.");
        }
    }

    @Step("Establecer cuenta contable")
    private void CuentaContable() {
        try {
            WebElement campoCuenta = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@id='EDT_CODIGOCUENTACONTABLE']")
            ));
            // Forzar la limpieza del campo y la escritura usando JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Borrar el contenido actual (forzado)
            js.executeScript("arguments[0].value = '';", campoCuenta);
            js.executeScript("arguments[0].dispatchEvent(new Event('input'));", campoCuenta);

            // Forzar la escritura del nuevo valor
            String valor = "000-000-000012";
            js.executeScript("arguments[0].value = arguments[1];", campoCuenta, valor);
            js.executeScript("arguments[0].dispatchEvent(new Event('change'));", campoCuenta);

            System.out.println("CuentaContable: Se estableció la cuenta contable: " + valor);
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer la cuenta contable.");
        }
    }




    @Step("Establecer importe aleatorio")
    private void Importe() {
        try {
            // Localiza el campo para el importe
            WebElement campoImporte = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            campoImporte.clear();
            // Generar un importe aleatorio entre 0 y 9999.9999
            double randomImporte = Math.random() * 9999.9999;
            String importeStr = String.format("%.4f", randomImporte);
            campoImporte.sendKeys(importeStr);
            System.out.println("Importe: Se estableció el importe aleatorio: " + importeStr);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer el importe aleatorio.");
        }
    }

    @Step("Establecer referencia de movimiento")
    private void Referencia() {
        try {
            // Localiza el campo de referencia
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[1]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            campoReferencia.clear();
            campoReferencia.sendKeys("movimiento manual");
            System.out.println("Referencia: Se estableció la referencia: movimiento manual");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer la referencia de movimiento.");
        }
    }

    @Step("Establecer concepto de movimiento")
    private void ConceptoMovimiento() {
        try {
            WebElement campoConceptoMov = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[1]/div[3]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            campoConceptoMov.clear();
            campoConceptoMov.sendKeys("Concepto Momiviento prueba");
            System.out.println("ConceptoMovimiento: Se estableció 'Concepto Movimiento prueba'");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer el concepto de movimiento.");
        }
    }


    @Step("Aceptar el movimiento")
    private void AceptarMovimiento() {
        try {
            // Localiza y presiona el botón para aceptar el movimiento
            WebElement btnAceptarMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[4]/div[1]/div/table/tbody/tr/td/a")
            ));
            btnAceptarMov.click();
            System.out.println("AceptarMovimiento: Movimiento aceptado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el movimiento.");
        }
    }

    @Step("Cerrar Ventana Movimiento")
    private void CerrarVentanaMovimiento() {
        try {
            WebElement btnCerrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[4]/div[2]/div/table/tbody/tr/td/a")
            ));
            btnCerrar.click();
            System.out.println("CerrarVentanaMovimiento: Ventana de movimiento cerrada correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al cerrar la ventana de movimiento.");
        }
    }


    @Step("Aceptar la Póliza")
    private void AceptarPoliza() {
        try {
            WebElement btnAceptarPoliza = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[3]/div/table/tbody/tr/td/input")
            ));
            btnAceptarPoliza.click();
            System.out.println("AceptarPoliza: Botón presionado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Aceptar Póliza.");
        }
    }

    @Step("Aceptar alerta del navegador")
    private void AceptarAlerta() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            System.out.println("AceptarAlerta: Alerta aceptada correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar la alerta.");
        }
    }


}