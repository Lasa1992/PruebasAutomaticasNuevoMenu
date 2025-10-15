package CuentasPorPagar;
import Facturacion.FacturaConceptoTimbrada;
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
public class ContraRecibos {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String CODIGO_PROVEEDOR = Variables.PROVEEDOR;
    //String documento = PagoPasivos.numeroDocumentoGenerado;

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
    @Order(2)
    @Description("Se genera un Contra Recibo")
    public void ContraRecibo() {

        //Accedemos a los metodos de la clase PasivoManual
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
        pasivo.AceptarPasivo();
       // pasivo.AceptarPoliza();

        //Metodos para crear Contra Recibo
        BotonCuentasPorPagar();
        BotonContraRecibos();
        AgregarContraRecibo();
        QuitarCampoFecha();
        CodigoProveedor();
        seleccionarCheckboxPasivo();
        BotonGenerarContraRecibo();
        BotonImprimir();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Contra Recibos...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Hacer clic en el botón de Cuentas por Pagar")
    public void BotonCuentasPorPagar() {
        try {
            // Esperar a que el botón de Cuentas por Pagar esté presente y clickeable
            WebElement botonCuentasPorPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"sidebar\"]/div/ul/li[8]")));

            // Hacer clic en el botón
            botonCuentasPorPagar.click();
            System.out.println("Se hizo clic en el botón de Cuentas por Pagar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Cuentas por Pagar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de Contra Recibos")
    public void BotonContraRecibos() {
        try {
            // Esperar a que el botón de Contra Recibos esté presente y clickeable
            WebElement botonPagos = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"submenuCUENTASPORPAGAR\"]/li[3]/a")));

            // Hacer clic en el botón
            botonPagos.click();
            System.out.println("Se hizo clic en el botón de Contra Recibos.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Contra Recibos: " + e.getMessage());
        }
    }


    @Step("Hace clic en el botón Agregar")
    public void AgregarContraRecibo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Esperar que el botón "Agregar" esté presente en el DOM usando XPath
            WebElement botonAgregar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"BTN_AGREGAR\"]"
            )));
            wait.until(ExpectedConditions.visibilityOf(botonAgregar));

            // Hacer clic en la opción del menú usando Actions para mayor estabilidad
            botonAgregar.click();
            System.out.println("Se hizo clic en el botón Agregar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Agregar: " + e.getMessage());
        }
    }

    public void QuitarCampoFecha() {
        try {
            Thread.sleep(3000);
            // Localizamos el elemento que queremos validar (reemplaza el XPath por el correcto)
            WebElement campoFecha = driver.findElement(By.xpath("//*[@id=\"EDT_FECHACONTRARECIBO\"]"));
            int maxIntentos = 3;
            int intentos = 0;

            // Verificamos si el elemento tiene el foco y repetimos el clic en el body si es necesario
            while(campoFecha.equals(driver.switchTo().activeElement()) && intentos < maxIntentos) {
                System.out.println("El campo está seleccionado. Haciendo clic en el body para quitar la selección... (Intento " + (intentos+1) + ")");
                WebElement body = driver.findElement(By.tagName("body"));
                body.click();
                intentos++;
            }

            // Validamos el estado final
            if (!campoFecha.equals(driver.switchTo().activeElement())) {
                System.out.println("El campo se deseleccionó correctamente tras " + intentos + " intento(s).");
            } else {
                System.out.println("El campo sigue seleccionado tras " + intentos + " intento(s).");
            }
        } catch (Exception e) {
            System.err.println("Error al deseleccionar el campo: " + e.getMessage());
        }
    }


    @Step("Ingresar el número 1 en el campo Código de Proveedor")
    public void CodigoProveedor() {
        try {
            WebElement inputCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_NUMEROPROVEEDOR\"]")));
            inputCodigo.clear();
            inputCodigo.sendKeys(CODIGO_PROVEEDOR);
            System.out.println("Se ingresó el número " + CODIGO_PROVEEDOR + " en el campo Código de Proveedor.");
        } catch (Exception e) {
            System.err.println("Error en CódigoProveedor: " + e.getMessage());
        }
    }


    public void seleccionarCheckboxPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROPASIVOS_TITRES_RECH_3\"]")));
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
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_NODOCUMENTO')]//div[text()='" + Variables.DocumentoGeneradoPasivo + "']]")));
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

    @Step("Hacer clic en el botón para generar Contra Recibo")
    public void BotonGenerarContraRecibo() {
        try {
            // **Localizar el botón usando el XPath proporcionado**
            WebElement botonGenerarContraRecibo = driver.findElement(By.xpath(
                    "//*[@id=\"BTN_ACEPTAR\"]"));

            // **Hacer clic en el botón**
            botonGenerarContraRecibo.click();
            System.out.println("Se hizo clic en el botón para generar Contra Recibo.");
            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error en BotonGenerarContraRecibo: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Imprimir")
    public void BotonImprimir() {
        try {
            // Esperar a que el botón Imprimir esté presente y clickeable
            WebElement botonImprimir = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_REGRESAR\"]")));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón Imprimir.");

            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Imprimir: " + e.getMessage());
        }
    }


}
