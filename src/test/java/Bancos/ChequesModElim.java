package Bancos;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChequesModElim {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String folioCheque = ""; // Variable global para el número de cheque

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


    @Test
    @Order(3)
    @Description("Acceder al módulo de Bancos")
    public void testIngresarModuloBancos() {
        ingresarModuloBancos();
        submoduloCheques();
    }

    @RepeatedTest(3)
    @Order(4)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AgregarCheque() {
        RegistrarCheque();
        CuentaBancaria();
        NumeroCheque();
        Proveedor();
        Importe();
        Concepto();
        AceptarMovimiento();
        MensajePoliza();


        BuscarCheque();
        SeleccionarCheque();
        BotonModificar();
        AgregarObservacion();
        AceptarMovimiento();

        BotonCancelar();
        MotivoCancelacion();
        AceptarMovimiento();

        EliminarCheque();



    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Abrir el módulo de Bancos")
    private void ingresarModuloBancos() {
        try {
            WebElement botonBancos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/BANCO1')]")
            ));
            botonBancos.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el módulo de Bancos.");
        }
    }

    @Step("Abrir submódulo de Cheques")
    private void submoduloCheques() {
        try {
            WebElement subCheques = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/BANCO/CHEQUES')]")
            ));
            subCheques.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el submódulo de Cheques.");
        }
    }

    @Step("Registrar un nuevo Cheque")
    private void RegistrarCheque() {
        try {
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div/input")
            ));
            botonRegistrar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al registrar el cheque.");
        }
    }

    @Step("Seleccionar Cuenta Bancaria Aleatoria")
    private void CuentaBancaria() {
        try {
            WebElement dropdownCuenta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[7]/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")
            ));

            Select select = new Select(dropdownCuenta);
            List<WebElement> options = select.getOptions();

            if (!options.isEmpty()) {
                int index = new Random().nextInt(options.size());
                select.selectByIndex(index);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria.");
        }
    }

    @Step("Capturar y Guardar Número de Cheque")
    private void NumeroCheque() {
        try {
            WebElement inputCheque = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/ul/li[2]/input")
            ));

            folioCheque = inputCheque.getAttribute("value");

            if (folioCheque == null || folioCheque.isEmpty()) {
                System.out.println("Advertencia: No se encontró número de cheque.");
            } else {
                System.out.println("Número de cheque capturado: " + folioCheque);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al capturar el número de cheque.");
        }
    }

    @Step("Ingresar Proveedor")
    private void Proveedor() {
        try {
            WebElement inputProveedor = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/table/tbody/tr/td/input")
            ));

            inputProveedor.click();
            inputProveedor.sendKeys(Variables.PROVEEDOR);

            inputProveedor.sendKeys(Keys.TAB);

            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el proveedor.");
        }
    }

    @Step("Ingresar Importe Aleatorio")
    private void Importe() {
        try {
            WebElement inputImporte = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[24]/table/tbody/tr/td/ul/li[2]/input")
            ));

            double importe = 100 + (new Random().nextDouble() * (9999.99 - 100));
            String importeFormateado = String.format("%.2f", importe);

            inputImporte.click();
            inputImporte.sendKeys(importeFormateado);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el importe.");
        }
    }

    @Step("Ingresar Concepto con Fecha y Número de Cheque")
    private void Concepto() {
        try {
            WebElement inputConcepto = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/textarea")
            ));

            // Obtener la fecha actual en formato "yyyy-MM-dd"
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Construir el concepto con la fecha y el número de cheque
            String conceptoTexto = "Fecha: " + fechaActual + " - Cheque No: " + folioCheque;

            // Limpiar el campo antes de escribir
            inputConcepto.clear();
            inputConcepto.sendKeys(conceptoTexto);

            System.out.println("Concepto ingresado: " + conceptoTexto);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el concepto.");
        }
    }


    @Step("Aceptar Movimiento")
    private void AceptarMovimiento() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[5]/input")
            ));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el movimiento.");
        }
    }

    @Step("Hacer clic en el Mensaje de Póliza")
    private void MensajePoliza() {
        try {
            WebElement inputMensajePoliza = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")
            ));

            inputMensajePoliza.click();
            System.out.println("Se aceptó el mensaje de póliza");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el campo Mensaje de Póliza.");
        }
    }

    @Step("Buscar Cheque por Número")
    private void BuscarCheque() {
        try {
            WebElement inputBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[1]/div[1]/label/input")
            ));

            // Limpiar campo antes de escribir
            inputBusqueda.clear();
            inputBusqueda.sendKeys(folioCheque);
            inputBusqueda.sendKeys(Keys.ENTER);  // Presionar ENTER para buscar

            System.out.println("Cheque buscado: " + folioCheque);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar el cheque.");
        }
    }

    @Step("Seleccionar Cheque en la Tabla")
    private void SeleccionarCheque() {
        try {
            WebElement listadoCheques = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[2]/div[2]")
            ));

            // Verificar si hay cheques disponibles
            List<WebElement> filas = listadoCheques.findElements(By.tagName("tr"));
            for (WebElement fila : filas) {
                if (fila.getText().contains(folioCheque)) {
                    fila.click();
                    System.out.println("Cheque seleccionado: " + folioCheque);
                    return;
                }
            }

            System.err.println("❌ No se encontró el cheque en la tabla.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar el cheque.");
        }
    }

    @Step("Modificar Cheque")
    private void BotonModificar() {
        try {
            WebElement botonModificar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/input")
            ));

            botonModificar.click();
            System.out.println("Botón Modificar clickeado.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al modificar el cheque.");
        }
    }

    @Step("Agregar Observación al Cheque")
    private void AgregarObservacion() {
        try {
            WebElement inputObservacion = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[30]/table/tbody/tr/td/ul/li[2]/textarea")
            ));

            // Texto a insertar en la observación
            String observacionTexto = "Cheque modificado = " + folioCheque;

            // Limpiar campo antes de escribir
            inputObservacion.clear();
            inputObservacion.sendKeys(observacionTexto);

            System.out.println("Observación agregada: " + observacionTexto);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al agregar la observación.");
        }
    }

    @Step("Cancelar Cheque")
    private void BotonCancelar() {
        try {
            WebElement botonCancelar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/input")
            ));

            botonCancelar.click();
            System.out.println("Botón Cancelar clickeado.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Cancelar.");
        }
    }

    @Step("Ingresar Motivo de Cancelación")
    private void MotivoCancelacion() {
        try {
            WebElement inputMotivo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/textarea")
            ));

            // Texto a insertar en el motivo de cancelación
            String motivoTexto = "Cheque " + folioCheque + " cancelado.";

            // Limpiar campo antes de escribir
            inputMotivo.clear();
            inputMotivo.sendKeys(motivoTexto);

            System.out.println("Motivo de cancelación ingresado: " + motivoTexto);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el motivo de cancelación.");
        }
    }

    @Step("Eliminar Cheque")
    private void EliminarCheque() {
        try {
            WebElement botonEliminar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div/input")
            ));

            botonEliminar.click();
            System.out.println("Cheque eliminado correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al eliminar el cheque.");
        }
    }
}
