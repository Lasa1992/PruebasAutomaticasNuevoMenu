package Cobranza;

import Facturacion.FacturaConceptoTimbrada;
import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotasDeCredito {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String NUMERO_CLIENTE = Variables.CLIENTE; // Número de cliente configurable

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
        InicioSesion.handleTipoCambio();       // ✅ Sin parámetros
        InicioSesion.handleNovedadesScreen();  // ✅ Sin parámetros
    }

    @RepeatedTest(1)
    @Order(4)
    @Description("Se genera una Nota de credito")
    public void NotaDeCredito() throws InterruptedException {

        //Accedemos a los metodos de la clase FacturaConceptoTimbrado
        FacturaConceptoTimbrada factura = new FacturaConceptoTimbrada();
        factura.setup();
        factura.handleImageButton();
        factura.handleSubMenuButton();
        factura.BotonAgregarListado();
        factura.AsignarCliente();
        factura.MonedaFactura();
        factura.ConceptofacturacionAgregar();
        factura.IngresaValorCantidad();
        factura.AsignarCodigoConceptoFacturacion();
        factura.IngresaPrecioUnitario();
        factura.BotonAgregarConcepto();
        factura.ObtenerFolioFactura();
        factura.AceptarFactura();
        factura.BotonConcurrenciaFactura();
        factura.BotonTimbre();
        //factura.ValidarYEnviarCorreo();
        //factura.BotonPoliza()
        factura.BotonImpresion();

        //Metodos para crear Nota de Crédito
        BotonModuloCobranza();
        BotonNotasDeCredito();
        BotonAgregarNotaDeCredito();
        CodigoCliente();
        SeleccionarUsoCfdi();
        SeleccionarMoneda();
        SeleccionarFacturasNC();
        IntroducirMontoPago();
        IntroducirConcepto();
        AceptarNotaCredito();
        TimbreNC();
        AceptarPolizaNC();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Notas de Credito...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    public void BotonModuloCobranza() {
        try {
            // Espera explícita hasta que el botón (imagen) de Cobranza sea clicable
            WebElement ModuloBotonCobranza = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA1.jpg')]")
                    )
            );
            // Hacer clic en el botón una vez esté listo
            ModuloBotonCobranza.click();
            System.out.println("Botón Módulo Cobranza presionado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Cobranza no funciona.");
        }
    }

    public void BotonNotasDeCredito() {
        try {
            // Espera explícita hasta que el botón de Notas de Credito sea clicable
            WebElement NotaCreditoBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[4]/ul/li[3]/a/img")
            ));
            // Hacer clic en el botón
            NotaCreditoBoton.click();
            System.out.println("Submódulo Notas de Credito seleccionado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Notas de Credito no funciona.");
        }
    }

    public void BotonAgregarNotaDeCredito() {
        try {
            // Espera a que la opción principal "Registrar" en el menú sea clickeable
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"OPT_AGREGARMENU\"]")));
            // Hacer clic en la opción "Registrar" para desplegar el submenú
            menuRegistrar.click();
            System.out.println("Menú 'Registrar' abierto.");
            // Espera a que la opción específica "Registrar" dentro del submenú sea clickeable
            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"OPT_AGREGARNC\"]")));
            // Hacer clic en la opción "Registrar"
            opcionRegistrar.click();
            System.out.println("Opción 'Registrar' seleccionada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }
    }

    @Step("Asignar Cliente a la Nota de Credito")
    public void CodigoCliente() {
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_NUMEROCLIENTE\"]")));
            NumeroCliente.click();
            NumeroCliente.sendKeys(NUMERO_CLIENTE);
            NumeroCliente.sendKeys(Keys.TAB);
            System.out.println("Se asignó el cliente: " + NUMERO_CLIENTE);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente a la Nota de Credito");
        }
    }

    @Step("Seleccionar USO CFDI")
    public void SeleccionarUsoCfdi(){
        try{
            //Esperar a que el combo este visible
            WebElement ComboUsoCfdi = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"COMBO_USOCFDI\"]")));

            //Crear objeto Select y seleccionar texto visible
            Select selectUsoCfdi = new Select(ComboUsoCfdi);
            selectUsoCfdi.selectByVisibleText("PAGOS");
            Thread.sleep(5000);
            System.out.println("Se seleccionó la opción 'PAGOS' en el combo uso CFDI.");

        } catch (TimeoutException e){
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'PAGOS' en el combo USO CFDI.");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Step("Seleccionar Moneda")
    public void SeleccionarMoneda(){
        try{
            //Esperar a que el combo este visible
            WebElement ComboMoneda = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"COMBO_CATMONEDAS\"]")));

            //Crear objeto Select y seleccionar texto visible
            Select Moneda = new Select(ComboMoneda);
            Moneda.selectByVisibleText(Variables.MonedaFactura);
            System.out.println("Se seleccionó Moneda.");

        } catch (TimeoutException e){
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar Moneda.");

        }

    }

    //Método para seleccionar las facturas en Nota de credito
    public void SeleccionarFacturasNC() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROFACTURAS_TITRES_RECH_1\"]")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            System.out.println("valor de numero " + Variables.Facturas);
            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys(Variables.Facturas);
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento: " + Variables.Facturas);
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_DOCUMENTO')]//div[text()='" + Variables.Facturas + "']]")));
            System.out.println("Se encontró la fila del documento: " + Variables.Facturas);

            // **Buscar el checkbox dentro de la fila seleccionada**
            WebElement checkBox = filaSeleccionada.findElement(By.xpath(".//td[contains(@class, 'wbcolCOL_MARCAR')]//input[@type='checkbox']"));

            // **Imprimir el ID del checkbox encontrado**
            String checkBoxId = checkBox.getAttribute("id");
            System.out.println("Checkbox encontrado con ID: " + checkBoxId);

            // **Si el checkbox no es visible, hacer scroll hasta él**
            if (!checkBox.isDisplayed()) {
                System.out.println("El checkbox no es visible, desplazándose hacia él...");
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", checkBox);
                Thread.sleep(500);
            }

            // **Si el checkbox está deshabilitado, habilitarlo**
            if (!checkBox.isEnabled()) {
                System.out.println("El checkbox está deshabilitado, intentando habilitarlo...");
                js.executeScript("arguments[0].removeAttribute('disabled');", checkBox);
            }

            // **Verificar si ya está marcado antes de hacer clic**
            if (!checkBox.isSelected()) {
                try {
                    checkBox.click();
                    System.out.println("Se marcó el checkbox con `click()` estándar.");
                } catch (Exception e) {
                    System.out.println("`click()` falló, probando con JavaScript...");
                    js.executeScript("arguments[0].click();", checkBox);
                    System.out.println("Se marcó el checkbox con `JavaScriptExecutor`.");
                }
            } else {
                System.out.println("El checkbox ya estaba marcado.");
            }

            Thread.sleep(2000);

        } catch (NoSuchElementException e) {
            System.err.println("Elemento no encontrado: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    @Step("Introducir monto calculado en el campo de Importe NC")
    public void IntroducirMontoPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement ImporteNC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"TABLE_PRONOTASCREDITOCONCEPTOSFACTURACION_0_6\"]")));
            ImporteNC.click();
            //Interacción con el campo de Importe NC
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            Random random = new Random();
            double valorAleatorio = 1 + (1000 - 1) * random.nextDouble();
            inputBuscar.sendKeys(String.format("%.2f", valorAleatorio));
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("se ingreso importe");
            Thread.sleep(500);

        } catch (Exception e) {
            System.out.println("Error al ingresar el Importe: " + e.getMessage());
        }

    }

    @Step("Introducir Concepto Nota Crédito")
    public void IntroducirConcepto() {
        try {
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_CONCEPTO\"]")));
            campoConcepto.clear();
            campoConcepto.sendKeys("PRUEBA");
            System.out.println("Concepto ingresado");
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar wl Concepto.");
            System.out.println("Error al ingresar el Concepto.");
        }
    }

    @Step("Aceptar Nota de Crédito")
    public void AceptarNotaCredito() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_ACEPTAR\"]")));
            btnAceptar.click();
            System.out.println("Nota de Crédito aceptada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar Nota de Crédito.");
            System.out.println("Error al aceptar Nota de Crédito.");
        }
    }

    @Step("Generar Timbre de la Nota de Crédito")
    public void TimbreNC() {
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_YES\"]")
            ));
            btnSi.click();
            System.out.println("Timbre de la Nota de Crédito generado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar el timbre de la Nota de Crédito.");
            System.out.println("Error al generar el timbre de la Nota de Crédito.");
        }
    }

    @Step("Aceptar Póliza Contable de la Nota de Crédito")
    public void AceptarPolizaNC() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_OK\"]")
            ));
            btnOk.click();
            System.out.println("Póliza contable aceptada.");
        } catch (Exception e) {
            System.err.println("Error al aceptar la póliza: " + e.getMessage());
        }
    }





}
