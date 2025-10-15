package Facturacion;

import Indicadores.InicioSesion;
import Indicadores.Variables; // Clase que contiene las constantes, por ejemplo CLIENTE
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionGeneralDescImpr {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para almacenar información de la factura en el reporte Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    // Se toman las constantes de la clase Variables
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;

    private static String FolioFactura;

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
    @Description("Prueba de Inicio de Sesión - Se utiliza usuario GM")
    public void InicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
        // Pausa de 3 segundos
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
        // Pausa de 3 segundos
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    @Order(3)
    @Description("Ingresar al módulo de facturación.")
    public void IngresarModuloFacturacion() {
        ModuloFacturacion();
        SubmoduloFacturacionGeneral();
        // Pausa de 3 segundos
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionGeneral() {
        // Limpia las variables de reporte en Allure
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        AgregarFacturaGeneral();
        NumeroFactura();
        AsignarCliente();
        MonedaFactura();
        ConceptofacturacionAgregar();
        IngresaValorCantidad();
        AsignarCodigoConceptoFacturacion();
        IngresaPrecioUnitario();
        BotonAgregarConcepto();
        AceptarFactura();
        BotonConcurrenciaFactura();
        BotonTimbre();
        //ValidarYEnviarCorreo();
        //BotonPoliza();
        BotonImpresion();
        BusquedaFacturaListado();
        SeleccionarFactura();
        BotonImprimir();
        SeleccionarFormato();
        Imprimir();
        CerrarVistaPrevia();
        MarcarImpresion();
        BotonDescargar();
        SeleccionarOpcionDescarga();
        // Pausa de 3 segundos
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    // --------------------- Métodos del script ---------------------

    private static void ModuloFacturacion() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"sidebar\"]/div/ul/li[3]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void SubmoduloFacturacionGeneral() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"submenuFACTURACION\"]/li[8]/a")));
            subMenuButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el submódulo de Facturación General.");
            System.out.println("Error al hacer clic en el submódulo de Facturación General: " + e.getMessage());
        }
    }


    private static void AgregarFacturaGeneral() {
        try {
            WebElement elemento = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"tzBTN_AGREGAR\"]")));
            elemento.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en Agregar Factura General.");
            System.out.println("Error al hacer clic en Agregar Factura General: " + e.getMessage());
        }
    }
    @Step("Manejar Número de Factura (obtener folio)")
    private void NumeroFactura() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            FolioFactura = folioField.getAttribute("value");
            System.out.println("El número de factura es: " + FolioFactura);
            Thread.sleep(3000);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Factura");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Asignar Cliente a la Factura")
    private static void AsignarCliente() {
        try {
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            numeroCliente.click();
            numeroCliente.sendKeys(NUMERO_CLIENTE);
            informacionFactura.append("Número de Cliente: ").append(NUMERO_CLIENTE).append("\n");
            numeroCliente.sendKeys(Keys.TAB);

            System.out.println("El campo de cliente tiene información.");
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al realizar la acción en el campo 'Número de Cliente'.");
            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
        }
    }

    private static void MonedaFactura() {
        try {
            Select primerComboBox = new Select(
                    driver.findElement(By.id("COMBO_CATMONEDAS"))
            );
            List<String> opciones = List.of("PESOS", "DÓLARES");
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));
            primerComboBox.selectByVisibleText(opcionSeleccionada);

            System.out.println("La Moneda es: " + opcionSeleccionada);
            informacionFactura.append("Moneda: ").append(opcionSeleccionada).append("\n\n");

            Allure.addAttachment("Informacion Factura", informacionFactura.toString());
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error: " + e.getMessage());
            System.out.println("Se ha producido un error: " + e.getMessage());
        }
    }

    private void ConceptofacturacionAgregar() {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement botonAgregar = localWait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            botonAgregar.click();
            System.out.println("Se hizo clic en el botón Agregar concepto.");
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Agregar Concepto: " + e.getMessage());
            System.out.println("Error al presionar el botón Agregar Concepto: " + e.getMessage());
        }
    }

    private void IngresaValorCantidad() {
        try {
            WebElement nuevoCampo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));
            nuevoCampo.click();

            Random random = new Random();
            double valorAleatorio = 1.0000 + (99.9999 - 1.0000) * random.nextDouble();
            nuevoCampo.sendKeys(String.format("%.4f", valorAleatorio));

            informacionConcepto.append("Cantidad del Concepto: ").append(valorAleatorio).append("\n");
            System.out.println("Cantidad del Concepto ingresada: " + valorAleatorio);
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la cantidad: " + e.getMessage());
            System.out.println("Error al ingresar la cantidad: " + e.getMessage());
        }
    }

    private void AsignarCodigoConceptoFacturacion() {
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
                    System.out.println("Valor válido encontrado: " + codigoConcepto);
                    informacionConcepto.append("Número Concepto: ").append(codigoConcepto).append("\n");
                    informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");
                    Thread.sleep(3000);
                    return;
                } else {
                    System.out.println("Intento " + intentos + ": Valor " + codigoConcepto + " produjo error.");
                }
            }

            System.out.println("No se encontró un valor válido después de 10 intentos.");
            informacionConcepto.append("Concepto: Error, no se encontró un valor válido.\n");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el código de concepto de facturación: " + e.getMessage());
            System.out.println("Error al asignar el código de concepto de facturación: " + e.getMessage());
        }
    }

    private void IngresaPrecioUnitario() {
        try {
            WebElement campoPrecio = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_PRECIOUNITARIO")));
            Random random = new Random();
            double valorAleatorio = 1 + (1000 - 1) * random.nextDouble();
            campoPrecio.sendKeys(Keys.TAB);
            campoPrecio.sendKeys(String.format("%.2f", valorAleatorio));

            informacionConcepto.append("Precio Unitario: ").append(valorAleatorio).append("\n");
            System.out.println("Precio unitario ingresado: " + valorAleatorio);
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el precio unitario: " + e.getMessage());
            System.out.println("Error al ingresar el precio unitario: " + e.getMessage());
        }
    }

    private void BotonAgregarConcepto() {
        try {
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTARDETALLE"));
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

            // Verificar mensaje de confirmación
            try {
                WebElement botonConfirmar = driver.findElement(By.id("BTN_YES"));
                if (botonConfirmar.isDisplayed()) {
                    System.out.println("Se ha mostrado un mensaje de confirmación. Clic en 'Sí'.");
                    botonConfirmar.click();
                }
            } catch (NoSuchElementException e) {
                System.out.println("No se ha mostrado un mensaje de confirmación.");
            }
            Allure.addAttachment("Información del Concepto", informacionConcepto.toString());
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Error al hacer clic en el botón 'Agregar concepto': " + e.getMessage());
        }
    }

    private void AceptarFactura() {
        try {
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));
            botonAgregar.click();
            System.out.println("Se hizo clic en el botón 'Aceptar' de la factura.");
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Error al hacer clic en el botón 'Aceptar' de la factura: " + e.getMessage());
        }
    }

    @Step("Aceptar mensaje de concurrencia si aparece")
    private void BotonConcurrenciaFactura() {
        try {
            // Esperar unos segundos para ver si aparece el mensaje de concurrencia
            WebElement botonAceptarConcurrencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));

            // Si el botón está disponible, hacer clic en él
            botonAceptarConcurrencia.click();
            System.out.println("Mensaje de concurrencia detectado y aceptado.");

            // Llamar a los métodos que deben repetirse

            NumeroFactura();
            AceptarFactura();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    private void BotonTimbre() {
        try {
            WebElement botonAceptar = driver.findElement(By.xpath("//*[@id='BTN_YES']"));
            // Espera que sea clickeable si existe
            botonAceptar.click();
            System.out.println("Se presionó el botón de aceptar Timbre");

        } catch (Exception e) {
            System.out.println("Error al presionar el botón de aceptar Timbre. Continuando...");
            e.printStackTrace();
        }
    }

    public void ValidarYEnviarCorreo() {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> botones = localWait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]"))
            );
            List<WebElement> botonesValidos = botones.stream()
                    .filter(b -> b.isDisplayed() && b.isEnabled())
                    .collect(Collectors.toList());
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }
            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));
            try {
                System.out.println("Se hizo clic en el botón con ID: " + botonSeleccionado.getAttribute("id"));
                botonSeleccionado.click();
            } catch (Exception e) {
                System.out.println("Click() falló, intentando con JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
                System.out.println("Click con JavaScript en el botón con ID: " + botonSeleccionado.getAttribute("id"));
            }
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Error al hacer clic en el botón. Continuando...");
        }
    }

    private void BotonPoliza() {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonAceptar = localWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and contains(@onclick, 'BTN_OK')]")));
            botonAceptar.click();
            System.out.println("Se presionó el botón BTN_OK para la póliza.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("No se encontró el botón BTN_OK o no se pudo hacer clic.");
        }
    }

    private void BotonImpresion() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement botonRegresar = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and @name='BTN_REGRESAR' and contains(@onclick, 'BTN_REGRESAR')]")));
            if (botonRegresar != null) {
                botonRegresar.click();
                System.out.println("Se presionó el botón de regresar en la impresión.");
            } else {
                System.out.println("No se encontró el botón de regresar.");
            }
            Thread.sleep(3000);
        } catch (TimeoutException e) {
            System.out.println("El botón de regresar no se mostró, continuando ejecución.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar en Impresión");
            System.out.println("Error al presionar el botón de regresar en Impresión");
        }
    }

    // ------------------ Buscar Factura en el Listado ------------------

    private static void BusquedaFacturaListado() {
        try {
            // Buscar el campo de búsqueda con XPath en lugar de CSS Selector
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"TABLE_ProFacturasGenerales_filter\"]/label/input")));

            busquedaField.clear();
            busquedaField.sendKeys(FolioFactura);
            System.out.println("Se ingresó el folio de la factura para su búsqueda: " + FolioFactura);
            busquedaField.sendKeys(Keys.ENTER);

            // Esperar a que la tabla muestre el folio buscado
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='TABLE_ProFacturasGenerales']//tbody"), FolioFactura));

            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la factura: " + FolioFactura);
        }
    }

    @Step("Seleccionar Factura en el Listado")
    private static void SeleccionarFactura() {
        try {
            Thread.sleep(3000);
            WebElement tablaFacturas = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"TABLE_ProFacturasGenerales_wrapper\"]/div[2]/div[2]")));

            WebElement fila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@id='TABLE_ProFacturasGenerales']//tr[td[contains(text(),'" + FolioFactura + "')]]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
            try {
                fila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fila);
            }
            System.out.println("Factura seleccionada correctamente: " + FolioFactura);
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No se encontró la factura con folio: " + FolioFactura);
            UtilidadesAllure.manejoError(driver, e, "No se encontró la factura con el número: " + FolioFactura);
        } catch (TimeoutException e) {
            System.out.println("ERROR: La tabla no cargó los resultados a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "La tabla de facturas no cargó correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR: Ocurrió un problema al seleccionar la factura.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la factura.");
        }
    }
    // ------------------ Impresión y Descarga ------------------

    @Step("Hacer clic en el botón de impresión")
    public void BotonImprimir() {
        try {
            WebElement botonImprimir = driver.findElement(
                    By.xpath("//*[@id=\"BTN_IMPRIMIR\"]"));
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de impresión.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de impresión: " + e.getMessage());
        }
    }

    @Step("Seleccionar el último formato de impresión")
    public void SeleccionarFormato() {
        try {
            WebElement comboFormatos = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/div/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select"));
            Select selectFormato = new Select(comboFormatos);
            int ultimaOpcion = selectFormato.getOptions().size() - 1;
            selectFormato.selectByIndex(ultimaOpcion);
            System.out.println("Se seleccionó el último formato de impresión.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el formato de impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de imprimir")
    public void Imprimir() {
        try {
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/table/tbody/tr/td/input"));
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
            WebElement botonCerrar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[3]/div/table/tbody/tr/td/a/span/span"));
            botonCerrar.click();
            System.out.println("Se hizo clic en el botón para cerrar la vista previa.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón para cerrar la vista previa: " + e.getMessage());
        }
    }

    @Step("Marcar impresión eligiendo aleatoriamente entre dos opciones")
    public void MarcarImpresion() {
        try {
            Random random = new Random();
            int opcion = random.nextInt(2);

            String xpathOpcion1 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input";
            String xpathOpcion2 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/input";

            String xpathSeleccionado = (opcion == 0) ? xpathOpcion1 : xpathOpcion2;
            WebElement opcionSeleccionada = driver.findElement(By.xpath(xpathSeleccionado));
            opcionSeleccionada.click();
            System.out.println("Se marcó la impresión con la opción: " + (opcion + 1));
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al marcar la impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de descargar carta porte")
    public void BotonDescargar() {
        try {
            WebElement botonDescargar = driver.findElement(
                    By.xpath("//*[@id=\"tzOPT_DESCARGARMENU\"]"));
            botonDescargar.click();
            System.out.println("Se hizo clic en el botón de descargar carta porte.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de descargar carta porte: " + e.getMessage());
        }
    }

    @Step("Seleccionar la opción de descarga y verificar si se descargó un archivo")
    public void SeleccionarOpcionDescarga() {
        try {
            WebElement opcionDescarga = driver.findElement(
                    By.xpath("//*[@id=\"tzOPT_DESCARGARXML\"]"));
            opcionDescarga.click();
            System.out.println("Se seleccionó la opción de descarga.");
            Thread.sleep(5000);

            if (verificarDescarga()) {
                System.out.println("El archivo se descargó correctamente.");
            } else {
                System.out.println("No se encontró el archivo descargado.");
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La pausa fue interrumpida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al seleccionar la opción de descarga: " + e.getMessage());
        }
    }

    // Método auxiliar para verificar si se descargó un archivo recientemente
    private boolean verificarDescarga() {
        String rutaDescargas = System.getProperty("user.home") + "/Downloads/";
        File carpetaDescargas = new File(rutaDescargas);
        File[] archivos = carpetaDescargas.listFiles();
        long tiempoActual = System.currentTimeMillis();

        if (archivos == null) {
            return false;
        }

        for (File archivo : archivos) {
            if (archivo.isFile() && (tiempoActual - archivo.lastModified()) <= 10000) {
                System.out.println("Archivo descargado: " + archivo.getName());
                return true;
            }
        }
        return false;
    }
}
