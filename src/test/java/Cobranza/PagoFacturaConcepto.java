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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoFacturaConcepto {

    private static WebDriver driver;
    private static WebDriverWait wait;
    // Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    private static String FolioFactura; // Variable para almacenar el valor

    private static final String NUMERO_CLIENTE = Variables.CLIENTE; // Número de cliente configurable
    private double MontoaPAGAR;

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
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionporConcepto() throws InterruptedException {
        // Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

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
        factura.BotonPoliza();
        factura.BotonImpresion();

        // Bloque donde se controla el pago de la factura
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonPagosAbonos(); // Selecciona el submódulo pagos/abonos.
        BotonRegistrarPagoAbono(); // Da clic en el botón para agregar pago/abono.
        deseleccionarCampoFecha(); // Deselecciona el campo de fecha.
        CodigoClientPago(); // Introduce el código del cliente para el pago.
        SeleccionarCuentaBancariaAleatoria(); // Selecciona una cuenta bancaria aleatoria para el pago.
        BusquedaFacturaPagar(); // Busca la factura a pagar.
        SeleccionFactura(); // Selecciona la factura a pagar.
        ValidarConversion(); // Valida la conversión de la moneda.
        IntroducirMontoPago(); // Introduce el monto a pagar.
        IntroducirReferencia(); // Introduce una referencia para el pago.
        AceptarPagoAbono(); // Acepta el pago/abono.
        TimbrePago(); // Acepta el timbre del pago.
        //EnvioCorreoPago(); // Envía un correo para el pago (Sí/No).
        AceptarPolizaPago(); // Acepta la póliza del pago.
        deseleccionarCampoFecha2(); // Deselecciona el campo de fecha.
        SalirVentanaPago(); // Sale de la ventana de pago.
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    public void BotonModuloCobranza() {
        try {
            // Espera explícita hasta que el botón (imagen) de Cobranza sea clicable
            WebElement ModuloBotonCobranza = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id=\"sidebar\"]/div/ul/li[4]")
                    )
            );
            // Hacer clic en el botón una vez esté listo
            ModuloBotonCobranza.click();
            System.out.println("Botón Módulo Cobranza presionado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Cobranza no funciona.");
        }
    }

    public void BotonPagosAbonos() {
        try {
            // Espera explícita hasta que el botón de Pagos/Abonos sea clicable
            WebElement pagosAbonosBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"submenuCOBRANZA\"]/li[2]/a")
            ));
            // Hacer clic en el botón
            pagosAbonosBoton.click();
            System.out.println("Submódulo Pagos/Abonos seleccionado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Pagos/Abonos no funciona.");
        }
    }

    public void BotonRegistrarPagoAbono() {
        try {
            // Espera a que la opción principal "Registrar" en el menú sea clickeable
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"OPT_REGISTRARMENU\"]")));
            // Hacer clic en la opción "Registrar" para desplegar el submenú
            menuRegistrar.click();
            System.out.println("Menú 'Registrar' abierto.");
            // Espera a que la opción específica "Registrar" dentro del submenú sea clickeable
            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"OPT_REGISTRAR\"]")));
            // Hacer clic en la opción "Registrar"
            opcionRegistrar.click();
            System.out.println("Opción 'Registrar' seleccionada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }
    }

    public void deseleccionarCampoFecha() {
        try {
            // Localizamos el elemento que queremos validar (reemplaza el XPath por el correcto)
            System.out.println("Desmarcando el campo de fecha...");
            WebElement campoFecha = driver.findElement(By.xpath("//*[@id=\"EDT_FECHA\"]"));
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


    @Step("Asignar Cliente al Pago")
    private void CodigoClientPago() {
        try {
            WebElement NumeroClientePago = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_NUMEROCLIENTE\"]")));
            NumeroClientePago.click();
            NumeroClientePago.sendKeys(NUMERO_CLIENTE);
            NumeroClientePago.sendKeys(Keys.TAB);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al pago");
        }
    }


    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void SeleccionarCuentaBancariaAleatoria() {
        try {
            // Localizar el combo box usando XPath
            WebElement comboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[contains(@class, 'COMBO_CATCUENTASBANCARIAS')]")));
            // Instanciar el objeto Select para manipular el dropdown
            Select comboBox = new Select(comboBoxElement);
            // Obtener todas las opciones disponibles en el combo box
            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            // Verificar si hay opciones disponibles
            if (opcionesDisponibles.size() > 1) { // Excluye la primera opción si es un placeholder
                // Generar un índice aleatorio para seleccionar una opción válida (excluyendo la primera)
                Random random = new Random();
                int indiceAleatorio = random.nextInt(opcionesDisponibles.size() - 1) + 1; // Evita seleccionar la opción 0 si es vacía
                // Obtener la opción seleccionada
                WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
                String textoSeleccionado = opcionSeleccionada.getText();
                // Seleccionar la opción aleatoria por índice
                comboBox.selectByIndex(indiceAleatorio);
                System.out.println("Cuenta bancaria seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("No hay cuentas bancarias disponibles para seleccionar.");
            }
        } catch (TimeoutException | NoSuchElementException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria aleatoria.");
            System.out.println("Error al seleccionar una cuenta bancaria aleatoria.");
        }
    }

    @Step("Buscar factura para pagar")
    public void BusquedaFacturaPagar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            // Localizar el campo de búsqueda usando XPath
            WebElement campoBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'EDT_BUSQUEDAGENERAL')]")));
            campoBusqueda.click();
            // Limpiar el campo antes de ingresar el número de viaje
            campoBusqueda.clear();
            // Ingresar el número de viaje del cliente
            campoBusqueda.sendKeys(Variables.Facturas);
            System.out.println("Número de viaje ingresado en búsqueda: " + Variables.Facturas);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda de factura: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error en la búsqueda de factura.");
        }
    }

    public void SeleccionFactura() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        try {
            // Espera explícita hasta que el checkbox de la factura sea visible y clickeable
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROFACTURAS_0")));
            // Seleccionar el checkbox de la factura
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
            System.out.println("Error al seleccionar la factura");
        }

        // Espera de 3 segundos al finalizar
        Thread.sleep(3000);
    }


    @Step("Validar moneda y calcular el monto a pagar")
    public void ValidarConversion() {
        try {
            // Obtener la moneda seleccionada
            WebElement monedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"tzSTC_MONEDA\"]")));
            String monedaSeleccionada = monedaElement.getText().trim().toUpperCase();
            System.out.println("Moneda detectada: " + monedaSeleccionada);
            // Obtener valores de los campos relevantes
            WebElement montoPesosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_IMPORTEPESOS\"]")));
            WebElement montoDolaresElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_IMPORTEDLLS\"]")));
            WebElement tipoCambioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"EDT_TIPOCAMBIO\"]")));
            WebElement conversionMonedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"tzSTC_CONVERSIONDEMONEDA\"]")));
            // Obtener valores y limpiar caracteres extraños
            String valorPesos = montoPesosElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorDolares = montoDolaresElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorTipoCambio = tipoCambioElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String textoConversion = conversionMonedaElement.getText().trim();
            // Extraer número de tzSTC_CONVERSIONDEMONEDA
            double conversionAutomatica = 0.0;
            Pattern pattern = Pattern.compile("\\$([0-9]+\\.?[0-9]*)");
            Matcher matcher = pattern.matcher(textoConversion);
            if (matcher.find()) {
                conversionAutomatica = Double.parseDouble(matcher.group(1));
            }
            // Convertir valores a double
            double montoPesos = (!valorPesos.isEmpty() && !valorPesos.equals("0.00")) ? Double.parseDouble(valorPesos) : 0.0;
            double montoDolares = (!valorDolares.isEmpty() && !valorDolares.equals("0.00")) ? Double.parseDouble(valorDolares) : 0.0;
            double tipoCambio = (!valorTipoCambio.isEmpty() && !valorTipoCambio.equals("0.00")) ? Double.parseDouble(valorTipoCambio) : 1.0;
            // Lógica de cálculo basada en la moneda detectada
            if (monedaSeleccionada.equals("PESOS")) {
                if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos;
                } else if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares * tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica * tipoCambio;
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else if (monedaSeleccionada.equals("DÓLARES")) {
                if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares;
                } else if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos / tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica;
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else {
                System.out.println("Error: Moneda no reconocida.");
                UtilidadesAllure.manejoError(driver, new Exception("Moneda no válida"), "Error al validar la moneda.");
            }
            System.out.println("Monto calculado: " + String.format(Locale.US, "%.2f", MontoaPAGAR));
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener la moneda y calcular el monto.");
            System.out.println("Error al validar la moneda y calcular el monto.");
        }
    }

    @Step("Introducir monto calculado en el campo de pago")
    public void IntroducirMontoPago() {
        if (MontoaPAGAR == 0.0) {
            System.out.println("Advertencia: El monto a pagar es inválido o cero, no se ingresará en el campo.");
            return;
        }

        try {
            WebElement campoImporte = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTE")));
            campoImporte.clear();
            String montoFormateado = String.format(Locale.US, "%.2f", MontoaPAGAR);
            campoImporte.sendKeys(montoFormateado);
            System.out.println("Monto ingresado en el campo de pago: " + montoFormateado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el monto en el campo de pago.");
            System.out.println("Error al encontrar o ingresar el monto en el campo de pago.");
        }
    }

    @Step("Introducir referencia bancaria")
    public void IntroducirReferencia() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIABANCARIA")));
            campoReferencia.clear();
            String referencia = "pago" + FolioFactura;
            campoReferencia.sendKeys(referencia);
            System.out.println("Referencia ingresada: " + referencia);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia bancaria.");
            System.out.println("Error al ingresar la referencia bancaria.");
        }
    }

    @Step("Aceptar Pago o Abono")
    public void AceptarPagoAbono() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_ACEPTAR')]")));
            btnAceptar.click();
            System.out.println("Pago/Abono aceptado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el pago o abono.");
            System.out.println("Error al aceptar el pago o abono.");
        }
    }

    @Step("Generar Timbre del Pago")
    public void TimbrePago() throws InterruptedException {
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input")
            ));
            btnSi.click();
            System.out.println("Timbre del pago generado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar el timbre del pago.");
            System.out.println("Error al generar el timbre del pago.");
        }

        Thread.sleep(2000); // Espera de 2 segundos al final
    }


    @Step("Enviar Comprobante de Pago por Correo")
    public void EnvioCorreoPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            // Definir los XPaths de los dos elementos
            String xpath1 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input";
            String xpath2 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/input";

            // Esperar a que ambos elementos estén presentes en el DOM
            WebElement elemento1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)));
            WebElement elemento2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath2)));

            // Agregar ambos elementos a una lista
            List<WebElement> elementos = new ArrayList<>();
            elementos.add(elemento1);
            elementos.add(elemento2);

            // Filtrar aquellos que sean visibles y estén habilitados
            List<WebElement> elementosValidos = elementos.stream()
                    .filter(e -> e.isDisplayed() && e.isEnabled())
                    .collect(Collectors.toList());

            if (elementosValidos.isEmpty()) {
                System.out.println("No hay elementos visibles y clickeables. Continuando...");
                return;
            }

            // Seleccionar aleatoriamente uno de los elementos válidos
            WebElement elementoSeleccionado = elementosValidos.get(new Random().nextInt(elementosValidos.size()));

            try {
                System.out.println("Se hizo clic en el elemento seleccionado.");
                elementoSeleccionado.click();
            } catch (Exception e) {
                System.out.println("Click() falló, intentando con JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementoSeleccionado);
                System.out.println("Click ejecutado con JavaScript en el elemento seleccionado.");
            }
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el elemento. Continuando...");
            e.printStackTrace();
        }
    }


    @Step("Aceptar Póliza Contable del Pago")
    public void AceptarPolizaPago() throws InterruptedException {
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

        Thread.sleep(1000); // Espera de 1 segundo al final
    }



    public void deseleccionarCampoFecha2() {
        try {
            // Localizamos el elemento que queremos validar (reemplaza el XPath por el correcto)
            WebElement campoFecha = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/input"));
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

    @Step("Salir de la Ventana de Pago")
    public void SalirVentanaPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            WebElement btnCancelar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div[3]/div/table/tbody/tr/td/input")
            ));
            btnCancelar.click();
            System.out.println("Salida de la ventana realizada.");
        } catch (Exception e) {
            System.err.println("Error al salir de la ventana: " + e.getMessage());
        }
    }

}
