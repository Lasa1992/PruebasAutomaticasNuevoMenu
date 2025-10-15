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
    @Description("Inicio de Sesión - Usuario GM")
    public void testInicioSesion() {
        try {
            InicioSesion.fillForm();
            InicioSesion.submitForm();
            InicioSesion.handleAlert();
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
            InicioSesion.handleTipoCambio();
            InicioSesion.handleNovedadesScreen();
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


        SeleccionContaPolizas();
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
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }


    @Step("Navegar a opción de menú específico")
    public void SeleccionContaPolizas() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Primer clic: abre el submenú
            try {
                WebElement primerElemento = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"sidebar\"]/div/ul/li[9]")));
                primerElemento.click();
                System.out.println("Primer elemento del menú clicado.");
            } catch (Exception e) {
                System.out.println("No se pudo hacer clic en el primer elemento del menú. Continuando...");
                return;
            }

            // Segundo clic: selecciona la opción del submenú
            try {
                WebElement segundoElemento = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"submenuCONTABILIDAD\"]/li[3]/a")));
                segundoElemento.click();
                System.out.println("Segunda opción del submenú seleccionada.");
            } catch (Exception e) {
                System.out.println("No se pudo hacer clic en la segunda opción del submenú. Continuando...");
            }

        } catch (Exception e) {
            System.out.println("Error general en la navegación del menú. Continuando...");
            e.printStackTrace();
        }
    }


    @Step("Registrar una nueva póliza")
    private void BotonAgregarManuamente() {
        try {
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")
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
            //String valor = "000-000-000012"; // Cambia esto por el valor que necesites este es de IIA
            //String valor = "101-001-000000"; // Cambia esto por el valor que necesites este es de TST08 o KIJ
            String valor = "201-001-000001"; // Cambia esto por el valor que necesites este es de cacx
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
                    By.xpath("//*[@id=\"BTN_ACEPTAR\"]")
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