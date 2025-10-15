package Bancos;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovimientoBancario {

    private static WebDriver driver;
    private static WebDriverWait wait;

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
        submoduloMovBancarios();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Generación de Movimiento con Conceptos Aleatorios")
    public void testAgregarMovimientoBancario() {
        RegistrarMovBancario();
        CuentaBancaria();
        NumeroMovimiento();
        Concepto();
        Proveedor();
        Referencia();
        Importe();
        AceptarMovimiento();
        MensajePoliza();
        SalirventanaRegistro();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Movimiento Bancario...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Abrir el módulo de Bancos")
    public void ingresarModuloBancos() {
        try {
            WebElement botonBancos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"sidebar\"]/div/ul/li[7]")
            ));
            botonBancos.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo abrir el módulo de Bancos a tiempo.");
        } catch (NoSuchElementException | ElementClickInterceptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al intentar abrir el módulo de Bancos.");
        }
    }

    @Step("Abrir submódulo de Movimientos Bancarios")
    public void submoduloMovBancarios() {
        try {
            WebElement subModuloMovimientos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"submenuBANCO\"]/li[3]/a")
            ));
            subModuloMovimientos.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "El submódulo de Movimientos Bancarios no se pudo abrir a tiempo.");
        } catch (NoSuchElementException | ElementClickInterceptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al intentar abrir el submódulo de Movimientos Bancarios.");
        }
    }

    @Step("Registrar Movimiento Bancario")
    public void RegistrarMovBancario() {
        try {
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_REGISTRAR\"]")
            ));

            System.out.println("Clic en botón de registrar movimiento bancario.");
            botonRegistrar.click();

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al registrar el movimiento bancario.");
        }
    }

    @Step("Seleccionar Cuenta Bancaria Aleatoria")
    public void CuentaBancaria() {
        try {
            WebElement dropdownCuenta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")
            ));

            Select select = new Select(dropdownCuenta);
            List<WebElement> options = select.getOptions();

            if (!options.isEmpty()) {
                int index = new Random().nextInt(options.size());
                select.selectByIndex(index);
                System.out.println("Cuenta Bancaria seleccionada: " + options.get(index).getText());
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria.");
        }
    }

    @Step("Capturar y Guardar Número de Movimiento")
    public void NumeroMovimiento() {
        try {
            WebElement inputMovimiento = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));

            // Capturar el valor del campo
            String folioMov = inputMovimiento.getAttribute("value");

            // Verificar que el campo tiene un valor antes de almacenarlo
            if (folioMov == null || folioMov.isEmpty()) {
                System.out.println("Advertencia: El campo de Número de Movimiento está vacío.");
            } else {
                System.out.println("Número de movimiento capturado y guardado globalmente: " + folioMov);
            }

            Variables.FolioMovimientoBancario = folioMov;

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al capturar el número de movimiento.");
        }
    }

    @Step("Seleccionar Concepto Aleatorio")
    public void Concepto() {
        try {
            WebElement dropdownConcepto = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")
            ));

            Select select = new Select(dropdownConcepto);
            List<WebElement> options = select.getOptions();

            if (!options.isEmpty()) {
                int index = new Random().nextInt(options.size());
                select.selectByIndex(index);
                System.out.println("Concepto seleccionado: " + options.get(index).getText());
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar un concepto.");
        }
    }

    @Step("Ingresar Número de Proveedor y Avanzar con Tab")
    public void Proveedor() {
        try {
            WebElement inputProveedor = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[15]/input")
            ));

            inputProveedor.click();
            inputProveedor.sendKeys(Variables.PROVEEDOR);
            // Enviar la tecla TAB para avanzar al siguiente campo
            inputProveedor.sendKeys(Keys.TAB);

            Thread.sleep(1000);


            System.out.println("Número de proveedor ingresado y se avanzó con TAB.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el número de proveedor y avanzar.");
        }
    }


    @Step("Ingresar Referencia con Fecha Actual y Número de Movimiento")
    public void Referencia() {
        try {
            WebElement inputReferencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[16]/table/tbody/tr/td/ul/li[2]/textarea")
            ));

            // Obtener la fecha actual en formato "yyyy-MM-dd"
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Construir la referencia con la fecha y el número de movimiento
            String referencia = fechaActual + " - " + Variables.FolioMovimientoBancario;

            inputReferencia.click();
            inputReferencia.sendKeys(referencia);

            System.out.println("Referencia ingresada: " + referencia);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia.");
        }
    }

    @Step("Ingresar Importe Aleatorio")
    public void Importe() {
        try {
            WebElement inputImporte = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/ul/li[2]/input")
            ));

            // Generar un valor aleatorio entre 99 y 9999.9999
            Random random = new Random();
            double importe = 99 + (random.nextDouble() * (9999.9999 - 99));

            // Formatear el número con 4 decimales
            String importeFormateado = String.format("%.4f", importe);

            inputImporte.click();
            inputImporte.sendKeys(importeFormateado);

            System.out.println("Importe ingresado: " + importeFormateado);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el importe.");
        }
    }

    @Step("Seleccionar check Anticipo")
    public void OpcionAnticipo(){
        try{
            WebElement anticipo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"CBOX_MOVIMIENTOPORANTICIPO_1\"]")));
            //Marcar check Anticipo
            anticipo.click();
            System.out.println("Se marco opción Anticipo.");
        } catch (Exception e){
            System.out.println("Error al marcar opción Anticipo: " + e.getMessage());
        }
    }

    @Step("Aceptar Movimiento Bancario")
    public void AceptarMovimiento() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[3]/div/input")
            ));

            botonAceptar.click();
            System.out.println("Movimiento aceptado correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el movimiento.");
        }
    }

    @Step("Hacer clic en el Mensaje de Póliza")
    public void MensajePoliza() {
        try {
            WebElement inputMensajePoliza = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")
            ));

            inputMensajePoliza.click();
            System.out.println("Se acepto el mensaje de poliza");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el campo Mensaje de Póliza.");
        }
    }

    @Step("Salir de la Ventana de Registro")
    public void SalirventanaRegistro() {
        try {
            WebElement botonSalir = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_CANCELAR\"]")
            ));

            botonSalir.click();
            System.out.println("Clic en botón de salir realizado correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón de salir.");
        }
    }

    //Metodo para seleccionar Concepto RETIRO POR TRANSFERENCIA para Aplicar Pagos Anticipados
    @Step("Seleccionar Concepto Retiro Por Transferencia")
    public void ConceptoPagoAnticipado() {
        try {
            By localizarCampo = By.xpath("//*[@id=\"COMBO_CATTIPOSMOVIMIENTOSBANCARIOS\"]");
            // Esperar que el combo esté visible
            WebElement comboEstatusLlanta = wait.until(ExpectedConditions.elementToBeClickable(localizarCampo));
            // Crear el objeto Select y seleccionar por texto visible
            Select selectEstatus = new Select(comboEstatusLlanta);
            selectEstatus.selectByVisibleText("RETIRO POR TRANSFERENCIA");

            System.out.println("Se seleccionó la opción RETIRO POR TRANSFERENCIA.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción RETIRO POR TRANSFERENCIA.");
        }
    }

    //Metodo para capturar importe para Aplicar Pagos Anticipados
    @Step("Ingresar Importe")
    public void ImportePagoAnticipado() {
        try {
            WebElement inputImporte = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/ul/li[2]/input")
            ));
            // Ingresar importe desde la variable global Total
            inputImporte.click();
            inputImporte.sendKeys(Variables.Total);
            System.out.println("Importe ingresado: " + Variables.Total);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el importe.");
        }
    }

    //Metodo para seleccionar cuenta banacaria para Pagos Anticipados.
    @Step("Seleccionar Cuenta Bancaria")
    public void CuentaBancariaPagoAnticipado() {
        try {
            WebElement dropdownCuenta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"COMBO_CATCUENTASBANCARIAS\"]")
            ));
            Select select = new Select(dropdownCuenta);

            //Seleccionar Cuenta Bancaria de PESOS o DÓLARES
            if (Variables.Moneda.equals("0")){ //Indice "0" corresponde a la Moneda PESOS
                //Selecciona Cuenta Bancaria PESOS
                select.selectByVisibleText("0132508532 - BBVA BANCOMER");
                System.out.println("Se selecciono la Cuenta Bancaria 0132508532 - BBVA BANCOMER");
            } else {
                //Selecciona Cuenta Bancaria DÓLARES
                select.selectByVisibleText("9552121223345 - SANTANDER 3345");
                System.out.println("Se selecciono la Cuenta Bancaria 9552121223345 - SANTANDER 3345");
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una Cuenta Bancaria.");
        }
    }


}
