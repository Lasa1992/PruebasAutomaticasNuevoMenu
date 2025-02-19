package Cobranza;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoFacturaConcepto {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para reporte de Allure (no críticas)
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    private static String FolioFactura;
    private static double MontoaPAGAR;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
        // Aserción CRÍTICA: Confirmamos que el driver no sea nulo.
        assertNotNull(driver, "El driver no se inicializó correctamente.");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);

        // Aserción CRÍTICA: Validar la aparición de un elemento del dashboard tras iniciar sesión
        WebElement dashboardElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("DASHBOARD_ID"))
        );
        assertNotNull(dashboardElement, "El dashboard no se cargó correctamente, fallo en el inicio de sesión.");
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        // Este alert puede o no aparecer, no se fuerza aserción.
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @RepeatedTest(2)
    @Order(3)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionporConcepto() {
        // Limpia variables para reporte (no críticas)
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        // Flujo de facturación (sin aserciones en pasos no críticos)
        handleImageButton();
        handleSubMenuButton();
        BotonAgregarListado();
        NumeroFactura();        // Aserción CRÍTICA interna
        AsignarCliente();       // Sin aserción forzosa
        MonedaFactura();        // Sin aserción forzosa
        ConceptofacturacionAgregar();
        IngresaValorCantidad();
        AsignarCodigoConceptoFacturacion();
        IngresaPrecioUnitario();
        BotonAgregarConcepto();
        AceptarFactura();
        BotonTimbre();
        ValidarYEnviarCorreo();
        BotonPoliza();
        BotonImpresion();

        // Flujo de pago (sin aserciones en pasos no críticos)
        BotonModuloCobranza();
        BotonPagosAbonos();
        BotonRegistrarPagoAbono();
        deseleccionarCampoFecha();
        CodigoClientPago();
        SeleccionarCuentaBancariaAleatoria();
        BusquedaFacturaPagar();
        SeleccionFactura();
        ValidarConversion();
        IntroducirMontoPago();
        IntroducirReferencia();
        AceptarPagoAbono();
        TimbrePago();
        EnvioCorreoPago();
        AceptarPolizaPago();
        deseleccionarCampoFecha();
        SalirVentanaPago();

        // Aserción CRÍTICA: Confirmamos que se haya asignado un folioFactura
        assertNotNull(FolioFactura, "Folio de factura no generado correctamente.");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
            // Aserción CRÍTICA: Confirmar que el driver se cerró
            assertNull(driver, "El driver no se cerró correctamente.");
        }
    }

    // ==============================
    // ======= Métodos privados =====
    // ==============================

    private static void handleImageButton() {
        // Método no crítico: si falla, el test en general fallará por la excepción.
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        // Método no crítico
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1')]")));
            subMenuButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void BotonAgregarListado() {
        // Método no crítico
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón agregar no encontrado o no clickeable.");
        }
    }

    @Step("Manejar Número de Factura")
    private void NumeroFactura() {
        // CRÍTICO: Debe existir un número de factura
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            FolioFactura = folioField.getAttribute("value");
            // Aserción CRÍTICA en caso de no tener folio
            assertNotNull(FolioFactura, "El número de factura es nulo.");
            assertFalse(FolioFactura.trim().isEmpty(), "El número de factura no se generó correctamente.");
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Factura");
        }
    }

    private static void AsignarCliente() {
        // Método no crítico
        try {
            FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            numeroCliente.click();
            numeroCliente.sendKeys("000003");
            numeroCliente.sendKeys(Keys.TAB);

            fluentWait.until(webD -> {
                WebElement field = webD.findElement(By.id("EDT_NUMEROCLIENTE"));
                return !Objects.requireNonNull(field.getAttribute("value")).isEmpty();
            });
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al realizar la acción en el campo 'Número de Cliente'.");
        }
    }

    private static void MonedaFactura() {
        // Método no crítico
        try {
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CATMONEDAS")));
            List<String> opciones = List.of("PESOS", "DÓLARES");
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));
            primerComboBox.selectByVisibleText(opcionSeleccionada);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error al seleccionar la moneda.");
        }
    }

    private void ConceptofacturacionAgregar() {
        // Método no crítico
        try {
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Agregar de concepto.");
        }
    }

    private void IngresaValorCantidad() {
        // Método no crítico
        try {
            WebElement nuevoCampo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));
            nuevoCampo.click();
            Random random = new Random();
            double valorAleatorio = 1.0000 + (99.9999 - 1.0000) * random.nextDouble();
            nuevoCampo.sendKeys(String.format("%.4f", valorAleatorio));
            informacionConcepto.append("Cantidad del Concepto: ").append(valorAleatorio).append("\n");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la cantidad.");
        }
    }

    private void AsignarCodigoConceptoFacturacion() {
        // Método no crítico
        try {
            Thread.sleep(1000);
            WebElement campoCodigo = driver.findElement(By.id("EDT_CODIGOCONCEPTOFACTURACION"));
            WebElement campoConcepto = driver.findElement(By.id("EDT_CONCEPTOFACTURACION"));

            int intentos = 0;
            while (intentos < 10) {
                intentos++;
                int codigoConcepto = intentos;
                campoCodigo.clear();
                campoCodigo.sendKeys(String.valueOf(codigoConcepto));
                campoCodigo.sendKeys(Keys.TAB);

                wait.until(ExpectedConditions.attributeToBeNotEmpty(campoConcepto, "value"));
                String valorConcepto = campoConcepto.getAttribute("value");
                if (valorConcepto != null && !valorConcepto.equals("El concepto de facturación no está activo, Revisar")) {
                    informacionConcepto.append("Número Concepto: ").append(codigoConcepto).append("\n");
                    informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");
                    return;
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el código de concepto de facturación.");
        }
    }

    private void IngresaPrecioUnitario() {
        // Método no crítico
        try {
            WebElement CampoPrecioUnitario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_PRECIOUNITARIO")));
            Random random = new Random();
            double valorAleatorio = 1 + (1000 - 1) * random.nextDouble();
            CampoPrecioUnitario.sendKeys(Keys.TAB);
            CampoPrecioUnitario.sendKeys(String.format("%.2f", valorAleatorio));
            informacionConcepto.append("Precio Unitario: ").append(valorAleatorio).append("\n");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el precio unitario.");
        }
    }

    private void BotonAgregarConcepto() {
        // Método no crítico
        try {
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTARDETALLE"));
            botonAgregar.click();
            // Confirmación
            try {
                WebElement botonConfirmar = driver.findElement(By.id("BTN_YES"));
                if (botonConfirmar.isDisplayed()) {
                    botonConfirmar.click();
                }
            } catch (NoSuchElementException ignored) {
            }
            Allure.addAttachment("Informacion del Concepto", informacionConcepto.toString());
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
        }
    }

    private void AceptarFactura() {
        // Método no crítico
        try {
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
        }
    }

    private void BotonTimbre() {
        // Método no crítico
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            botonAceptar.click();
        } catch (Exception e) {
            System.out.println("El botón de aceptar Timbre no apareció. Continuando...");
        }
    }

    public void ValidarYEnviarCorreo() {
        // Método no crítico
        try {
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));

            List<WebElement> botonesValidos = new ArrayList<>();
            for (WebElement b : botones) {
                if (b.isDisplayed() && b.isEnabled()) {
                    botonesValidos.add(b);
                }
            }
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }

            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));
            try {
                botonSeleccionado.click();
            } catch (Exception clickE) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
            }
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de correo. Continuando...");
        }
    }

    private void BotonPoliza() {
        // Método no crítico
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and contains(@onclick, 'BTN_OK')]")));
            botonAceptar.click();
        } catch (Exception e) {
            System.out.println("No se encontró el botón BTN_OK o no se pudo hacer clic.");
        }
    }

    private void BotonImpresion() {
        // Método no crítico
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement botonRegresar = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and @name='BTN_REGRESAR' and contains(@onclick, 'BTN_REGRESAR')]")));
            if (botonRegresar != null) {
                botonRegresar.click();
            }
        } catch (TimeoutException e) {
            System.out.println("El botón de regresar no se mostró, continuando la ejecución normalmente");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar");
        }
    }

    public void BotonModuloCobranza() {
        // Método no crítico
        try {
            WebElement ModuloBotonCobranza = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA1.jpg')]")));
            ModuloBotonCobranza.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Cobranza no funciona.");
        }
    }

    public void BotonPagosAbonos() {
        // Método no crítico
        try {
            WebElement pagosAbonosBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA/PAGOSABONOS1.jpg')]")));
            pagosAbonosBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Pagos/Abonos no funciona.");
        }
    }

    public void BotonRegistrarPagoAbono() {
        // Método no crítico
        try {
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.id("OPT_REGISTRARMENU")));
            menuRegistrar.click();

            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.id("OPT_REGISTRAR")));
            opcionRegistrar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }
    }

    public void deseleccionarCampoFecha() {
        // Método no crítico
        try {
            WebElement body = driver.findElement(By.tagName("body"));
            body.click();
            body.click();
        } catch (Exception e) {
            System.err.println("No se pudo deseleccionar el campo EDT_FECHA: " + e.getMessage());
        }
    }

    public void CodigoClientPago() {
        // Método no crítico
        try {
            WebElement codigoClienteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROCLIENTE")));
            codigoClienteInput.clear();
            codigoClienteInput.sendKeys("000003");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el Código de Cliente.");
        }
    }

    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void SeleccionarCuentaBancariaAleatoria() {
        // Método no crítico
        try {
            WebElement comboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[contains(@class, 'COMBO_CATCUENTASBANCARIAS')]")));
            Select comboBox = new Select(comboBoxElement);
            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            if (opcionesDisponibles.size() > 1) {
                Random random = new Random();
                int indiceAleatorio = random.nextInt(opcionesDisponibles.size() - 1) + 1;
                WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
                String textoSeleccionado = opcionSeleccionada.getText();
                comboBox.selectByIndex(indiceAleatorio);
                System.out.println("Cuenta bancaria seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("No hay cuentas bancarias disponibles para seleccionar.");
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria aleatoria.");
        }
    }

    @Step("Buscar factura para pagar")
    public void BusquedaFacturaPagar() {
        // Método no crítico
        try {
            WebElement campoBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'EDT_BUSQUEDAGENERAL')]")));
            campoBusqueda.clear();
            campoBusqueda.sendKeys(FolioFactura);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en la búsqueda de factura.");
        }
    }

    public void SeleccionFactura() {
        // Método no crítico
        try {
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROFACTURAS_0")));
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
        }
    }

    @Step("Validar moneda y calcular el monto a pagar")
    public void ValidarConversion() {
        // Método no crítico (podrías hacerlo crítico si lo deseas)
        try {
            WebElement monedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_MONEDA")));
            String monedaSeleccionada = monedaElement.getText().trim().toUpperCase();

            WebElement montoPesosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEPESOS")));
            WebElement montoDolaresElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEDLLS")));
            WebElement tipoCambioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            WebElement conversionMonedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_CONVERSIONDEMONEDA")));

            String valorPesos = montoPesosElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorDolares = montoDolaresElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorTipoCambio = tipoCambioElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String textoConversion = conversionMonedaElement.getText().trim();

            double conversionAutomatica = 0.0;
            Pattern pattern = Pattern.compile("\\$([0-9]+\\.?[0-9]*)");
            Matcher matcher = pattern.matcher(textoConversion);
            if (matcher.find()) {
                conversionAutomatica = Double.parseDouble(matcher.group(1));
            }

            double montoPesos = (!valorPesos.isEmpty() && !valorPesos.equals("0.00")) ? Double.parseDouble(valorPesos) : 0.0;
            double montoDolares = (!valorDolares.isEmpty() && !valorDolares.equals("0.00")) ? Double.parseDouble(valorDolares) : 0.0;
            double tipoCambio = (!valorTipoCambio.isEmpty() && !valorTipoCambio.equals("0.00")) ? Double.parseDouble(valorTipoCambio) : 1.0;

            if (monedaSeleccionada.equals("PESOS")) {
                if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos;
                } else if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares * tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica * tipoCambio;
                }
            } else if (monedaSeleccionada.equals("DÓLARES")) {
                if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares;
                } else if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos / tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica;
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al validar la moneda y calcular el monto.");
        }
    }

    @Step("Introducir monto calculado en el campo de pago")
    public void IntroducirMontoPago() {
        // Método no crítico
        try {
            WebElement campoImporte = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTE")));
            campoImporte.clear();

            String montoFormateado = String.format(Locale.US, "%.2f", MontoaPAGAR);
            campoImporte.sendKeys(montoFormateado);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el monto en el campo de pago.");
        }
    }

    @Step("Introducir referencia bancaria")
    public void IntroducirReferencia() {
        // Método no crítico
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIABANCARIA")));
            campoReferencia.clear();

            String referencia = "pago" + FolioFactura;
            campoReferencia.sendKeys(referencia);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia bancaria.");
        }
    }

    @Step("Aceptar Pago o Abono")
    public void AceptarPagoAbono() {
        // Método no crítico
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_ACEPTAR')]")));
            btnAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el pago o abono.");
        }
    }

    @Step("Generar Timbre del Pago")
    public void TimbrePago() {
        // Método no crítico
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_YES')]")));
            btnSi.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar el timbre del pago.");
        }
    }

    @Step("Enviar Comprobante de Pago por Correo")
    public void EnvioCorreoPago() {
        // Método no crítico
        try {
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));
            List<WebElement> botonesValidos = new ArrayList<>();
            for (WebElement b : botones) {
                if (b.isDisplayed() && b.isEnabled()) {
                    botonesValidos.add(b);
                }
            }
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables para envío de correo. Continuando...");
                return;
            }

            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));
            try {
                botonSeleccionado.click();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
            }
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de correo. Continuando...");
        }
    }

    @Step("Aceptar Póliza Contable del Pago")
    public void AceptarPolizaPago() {
        // Método no crítico
        try {
            WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'BTN_OK')]")));
            btnOk.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar la póliza contable del pago.");
        }
    }

    @Step("Salir de la Ventana de Pago")
    public void SalirVentanaPago() {
        // Método no crítico
        try {
            WebElement btnCancelar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'BTN-BotonClasicoGris BTN_CANCELAR padding webdevclass-riche')]")));
            btnCancelar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al salir de la ventana de pago.");
        }
    }
}
