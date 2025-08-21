package CuentasPorPagar;

import Bancos.MovimientoBancario;
import Indicadores.InicioSesion;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AplicarPagosAnticipados {

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

    @RepeatedTest(1)
    @Order(3)
    @Description("Generación de Pagos Anticipados")
    public void testAplicarPagosAnticipados() {
        //Generar Pasivo
        PasivoManual pasivo = new PasivoManual();
        pasivo.setup();
        pasivo.BotonCuentasPorPagar();
        pasivo.BotonPasivos();
        pasivo.AgregarPasivo();
        pasivo.QuitarCampoFecha();
        pasivo.CodigoProveedor();
        pasivo.NoDocumento();
        pasivo.CopiarFolio();
        pasivo.MonedaPasivo();
        pasivo.SubtotalPasivo();
        pasivo.IVAPasivo();
        pasivo.TotalPasivo();
        pasivo.AceptarPasivo();
        pasivo.AceptarPoliza();

        //Generar Movimiento Bancario Tipo Retiro por Transferencia
        MovimientoBancario movimiento= new MovimientoBancario();
        movimiento.setup();
        movimiento.ingresarModuloBancos();
        movimiento.submoduloMovBancarios();
        movimiento.RegistrarMovBancario();
        movimiento.CuentaBancariaPagoAnticipado();
        movimiento.NumeroMovimiento();
        movimiento.ConceptoPagoAnticipado();
        movimiento.Proveedor();
        movimiento.Referencia();
        movimiento.ImportePagoAnticipado();
        movimiento.OpcionAnticipo();
        movimiento.AceptarMovimiento();
       // movimiento.MensajePoliza();
        movimiento.SalirventanaRegistro();

        //Aplicar Pagos Anticipados
        BotonCuentasPorPagar();
        BotonAplicarPagosAnticipados();
        AgregarPagoAnticipado();
        AgregarProveedor();
        seleccionarMovimientoBancario();
        seleccionarCheckPasivo();
        BotonAceptarPagoPasivo();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Aplicar Pagos Anticipados...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Hacer clic en el botón de Cuentas por Pagar")
    public void BotonCuentasPorPagar() {
        try {
            // Esperar a que el botón de Cuentas por Pagar esté presente y clickeable
            WebElement botonCuentasPorPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[7]/a/img")));

            // Hacer clic en el botón
            botonCuentasPorPagar.click();
            System.out.println("Se hizo clic en el botón de Cuentas por Pagar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Cuentas por Pagar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de Aplicar Pagos Anticipados")
    public void BotonAplicarPagosAnticipados() {
        try {
            // Esperar a que el botón de Aplicar Pagos Anticipados esté presente y clickeable
            WebElement botonPagosAnticipados = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[7]/ul/li[6]/a/img")));

            // Hacer clic en el botón
            botonPagosAnticipados.click();
            System.out.println("Se hizo clic en el botón de Aplicar Pagos Anticipados.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Aplicar Pagos Anticipados: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Registrar")
    public void AgregarPagoAnticipado(){
        try{
            //Esperar a que el botón Registrar este visible
            WebElement BotonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath
                    ("//*[@id=\"BTN_REGISTRAR\"]")));

            //Hacer clic en botón Registrar
            BotonRegistrar.click();
            System.out.println("Se hizo clic en el botón Registrar.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en el botón Registrar: " + e.getMessage());
        }
    }

    @Step("Ingresar Proveedor 1 al agregar pasivo")
    public void AgregarProveedor(){
        try {
            //Esperar a que campo Proveedor sea visible
            WebElement Proveedor = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"EDT_NUMEROPROVEEDOR\"]")));
            //Ingresar proveedor 1
            Proveedor.sendKeys(Variables.PROVEEDOR);
            System.out.println("Se ingresó el número " + Variables.PROVEEDOR + " en el campo Código de Proveedor.");

        }catch (Exception e){
            System.out.println("Error al ingresar Proveedor: " + e.getMessage());
        }
    }

    @Step("Seleccionar Movimiento Bancario del listado")
    public void seleccionarMovimientoBancario() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROMOVIMIENTOSBANCARIOS_TITRES_RECH_1\"]")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            System.out.println("valor de numero " + Variables.FolioMovimientoBancario);
            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys(Variables.FolioMovimientoBancario);
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento: " + Variables.FolioMovimientoBancario);
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_FOLIO')]//div[text()='" + Variables.FolioMovimientoBancario + "']]")));
            System.out.println("Se encontró la fila del documento: " + Variables.FolioMovimientoBancario);

        } catch (NoSuchElementException e) {
            System.err.println("Elemento no encontrado: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    @Step("Seleccionar Pasivo del listado")
    public void seleccionarCheckPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROPASIVOS_TITRES_RECH_1\"]")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            System.out.println("valor de numero " + Variables.DocumentoGeneradoPasivo);
            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys(Variables.DocumentoGeneradoPasivo);
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento: " + Variables.DocumentoGeneradoPasivo);
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_DOCUMENTO')]//div[text()='" + Variables.DocumentoGeneradoPasivo + "']]")));
            System.out.println("Se encontró la fila del documento: " + Variables.DocumentoGeneradoPasivo);

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

    @Step("Aceptar Pago Pasivo Anticipado")
    public void BotonAceptarPagoPasivo(){
        try{
            //Esperar a que el Botón Aceptar sea visible
            WebElement BotonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_ACEPTAR\"]")));
            BotonAceptar.click();
            System.out.println("Se hizo clic en el Botón Aceptar.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en botón Aceptar: " + e.getMessage());
        }
    }



}
