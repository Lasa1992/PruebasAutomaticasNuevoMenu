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
    private static String ConceptoPoliza = "";

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
    @Description("Inicio de Sesión - Usuario GM")
    public void testInicioSesion() {
        try {
            InicioSesion.fillForm(driver);
            InicioSesion.submitForm(wait);
            InicioSesion.handleAlert(wait);
            System.out.println("Inicio de Sesión: Sesión iniciada exitosamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al iniciar sesión.");
        }
    }

    @Test
    @Order(2)
    @Description("Manejo del tipo de Cambio y la ventana de novedades")
    public void testAlertaTipoCambio() {
        try {
            InicioSesion.handleTipoCambio(driver, wait);
            InicioSesion.handleNovedadesScreen(wait);
            System.out.println("Tipo de Cambio y Novedades: Manejo realizado exitosamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en el manejo de tipo de cambio y novedades.");
        }
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Importación de Pólizas y Prepólizas")
    public void ImportacionPoliyPrep() {


        BotonAgregarManuamente();
        TipoPoliza();
        FolioPolizaManual();
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

        BuscarPoliza();
        SeleccionarPoliza();
        BotonModificar();
        CampoConcepto();
        BotonModificarMovimientos();
        CampoReferenciaMov();
        AceptarModMovimiento();
        AceptarModificacionPoliza();

        BotonConsultar();
        CapturarConcepto();
        SalirConsultar();

        BotonImprimir();
        SeleccionarFormato();
        Imprimir();
        CerrarVistaPrevia();

        EliminarPoliza();
        AceptarAlertaEliminarP();


    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("TearDown: Navegador cerrado correctamente.");
        }
    }

    @Step("Registrar una póliza manual")
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

    @Step("Obtiene el folio de la póliza manual")
    private void FolioPolizaManual() {
        try {
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            FolioPoliza = campoFolio.getAttribute("value");
            System.out.println("FolioPolizaManual: Se obtuvo el folio de póliza: " + FolioPoliza);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer el folio de póliza.");
        }
    }

    @Step("Establecer el concepto de póliza")
    private void ConceptoPoliza() {
        try {
            // Localiza el campo de concepto de la póliza
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            // Crea el concepto de la póliza manual
            ConceptoPoliza= "PÓLIZA MANUAL AU " + FolioPoliza;
            campoConcepto.clear();
            campoConcepto.sendKeys(ConceptoPoliza);
            System.out.println("ConceptoPoliza: Se estableció el concepto: " + ConceptoPoliza);
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
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al establecer la referencia de movimiento.");
        }
    }

    @Step("Establecer concepto de movimiento")
    private void ConceptoMovimiento() {
        try {
            /*WebElement campoConceptoMov = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[1]/div[3]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));*/
            WebElement campoConceptoMov = driver.findElement(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[6]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            );
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

    @Step("Buscar Póliza")
    private void BuscarPoliza() {
        try {
            WebElement inputBuscar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[1]/div[1]/label/input")
            ));
            inputBuscar.clear();
            inputBuscar.sendKeys(ConceptoPoliza);
            System.out.println("Buscar Póliza: Se buscó la póliza con número " + ConceptoPoliza + " correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la póliza con número " + FolioPoliza + ".");
        }
    }

    @Step("Seleccionar Póliza")
    private void SeleccionarPoliza() {
        try {
            WebElement registro = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table[@id='TABLE_ProPolizas']//td[normalize-space(text())='" + ConceptoPoliza + "']")
            ));
            registro.click();
            System.out.println("Seleccionar Póliza: Registro con número " + ConceptoPoliza + " seleccionado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la póliza con número " + ConceptoPoliza + ".");
        }
    }

    @Step("Presionar Botón Modificar")
    private void BotonModificar() {
        try {
            WebElement btnModificar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/input")
            ));
            btnModificar.click();
            System.out.println("Botón Modificar: Se hizo click en el botón Modificar correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Modificar.");
        }
    }

    @Step("Actualizar Campo Concepto")
    private void CampoConcepto() {
        try {
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            String valorActual = campoConcepto.getAttribute("value");
            String nuevoValor = valorActual + " Modificaado";
            campoConcepto.clear();
            campoConcepto.sendKeys(nuevoValor);
            System.out.println("CampoConcepto: Valor actualizado a: " + nuevoValor);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en CampoConcepto.");
        }
    }

    @Step("Presionar Botón Modificar Movimientos")
    private void BotonModificarMovimientos() {
        try {
            WebElement btnModificarMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div/div[1]/div[2]/div/table/tbody/tr/td/input")
            ));
            btnModificarMov.click();
            System.out.println("BotonModificarMovimientos: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en BotonModificarMovimientos.");
        }
    }

    @Step("Actualizar Campo Referencia Movimiento")
    private void CampoReferenciaMov() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[1]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            campoReferencia.clear();
            campoReferencia.sendKeys("movimiento modificado");
            System.out.println("CampoReferenciaMov: Texto 'movimiento modificado' ingresado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en CampoReferenciaMov.");
        }
    }

    @Step("Aceptar Modificación Movimiento")
    private void AceptarModMovimiento() {
        try {
            WebElement btnAceptarModMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[4]/div[1]/div/table/tbody/tr/td/a/span/span")
            ));
            btnAceptarModMov.click();
            System.out.println("AceptarModMovimiento: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en AceptarModMovimiento.");
        }
    }

    @Step("Aceptar Modificación Póliza")
    private void AceptarModificacionPoliza() {
        try {
            WebElement btnAceptarModPol = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[3]/div/table/tbody/tr/td/input")
            ));
            btnAceptarModPol.click();
            System.out.println("AceptarModificacionPoliza: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en AceptarModificacionPoliza.");
        }
    }

    @Step("Presionar Botón Consultar")
    private void BotonConsultar() {
        try {
            WebElement btnConsultar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div/input")
            ));
            btnConsultar.click();
            System.out.println("Botón Consultar: Se presionó correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Consultar.");
        }
    }

    @Step("Capturar Concepto")
    private void CapturarConcepto() {
        try {
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            String valorConcepto = campoConcepto.getAttribute("value");
            System.out.println("Capturar Concepto: Valor capturado: " + valorConcepto);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al capturar el concepto.");
        }
    }

    @Step("Salir de Consultar")
    private void SalirConsultar() {
        try {
            WebElement btnSalir = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[4]/div/table/tbody/tr/td/input")
            ));
            btnSalir.click();
            System.out.println("Salir Consultar: Se presionó el botón de salir correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de salir en Consultar.");
        }
    }

    @Step("Hacer clic en el botón de impresión")
    public void BotonImprimir() {
        try {
            // Localizar el botón de impresión usando el XPath proporcionado
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[6]/div/input"));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de impresión.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de impresión: " + e.getMessage());
        }
    }

    @Step("Seleccionar el último formato de impresión")
    public void SeleccionarFormato() {
        try {
            // Localizar el menú desplegable de formatos usando el XPath proporcionado
            WebElement comboFormatos = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/div/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select"));

            // Crear un objeto Select para interactuar con el menú desplegable
            Select selectFormato = new Select(comboFormatos);

            // Seleccionar la última opción del menú desplegable
            int ultimaOpcion = selectFormato.getOptions().size() - 1;
            selectFormato.selectByIndex(ultimaOpcion);
            System.out.println("Se seleccionó el último formato de impresión.");

        } catch (Exception e) {
            System.err.println("Error al seleccionar el formato de impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de imprimir")
    public void Imprimir() {
        try {
            // Localizar el botón de imprimir usando el XPath proporcionado
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/table/tbody/tr/td/input"));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de imprimir.");

            Thread.sleep(3000);

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de imprimir: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón para cerrar la vista previa")
    public void CerrarVistaPrevia() {
        try {
            // Localizar el botón para cerrar la vista previa usando el XPath proporcionado
            WebElement botonCerrar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[3]/div/table/tbody/tr/td/a/span"));

            // Hacer clic en el botón
            botonCerrar.click();
            System.out.println("Se hizo clic en el botón para cerrar la vista previa.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón para cerrar la vista previa: " + e.getMessage());
        }
    }

    @Step("Eliminar Póliza")
    private void EliminarPoliza() {
        try {
            WebElement btnEliminar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/input")
            ));
            btnEliminar.click();
            System.out.println("Eliminar Poliza: Se presionó el botón de eliminar póliza correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de eliminar póliza.");
        }
    }

    @Step("Aceptar Alerta de Eliminación de Póliza")
    private void AceptarAlertaEliminarP() {
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
            System.out.println("Alerta Eliminación de Póliza Manual aceptada correctamente y eliminada la póliza manual #" + FolioPoliza);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se encontró ninguna alerta para aceptar.");
        }
    }




}