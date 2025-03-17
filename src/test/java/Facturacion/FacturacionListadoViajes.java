package Facturacion;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionListadoViajes {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // =======================================================================================
    // Variables configurables
    // =======================================================================================
    // Cambia estos valores según lo requieras para tus pruebas:
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;  // Número de cliente
    private static final String NUMERO_RUTA = Variables.RUTA;     // Número de ruta
    private static final String TIPO_DOCUMENTO   = Variables.DocumentoIngreso;

    // =======================================================================================
    // Variables para almacenar valores reutilizables
    // =======================================================================================
    private static String numeroViajeCliente;
    private static String monedaSeleccionada;
    private static String folioGuardado;

    @BeforeAll
    public static void setup() {
        // Puedes ajustar la ruta del driver si lo requieres o removerla si ya la tienes configurada
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad
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


    @RepeatedTest(2)
    @Order(3)
    @Description("Metodos para entrar al listado de viajes, crear un viaje y posteriormente presionar el botón de facturación.")
    public void EntrarAViajes() {
        BotonModuloTrafico();        // Selecciona el módulo de tráfico en la interfaz de usuario.
        BotonListadoViajes();        // Abre el listado de viajes en el módulo de tráfico.
        BotonAgregarCartaPorte();    // Agrega un nuevo 'Carta Porte' para crear un viaje.
        TipoDocumentoIngreso();      // Selecciona el tipo de documento en el formulario del viaje.
        GuardarFolio();              // Guarda el folio generado para el viaje.
        NumeroViajeCliente();        // Introduce el número de viaje correspondiente al cliente (basado en el folio).
        CodigoCliente();             // Asigna el cliente correspondiente al viaje (usa NUMERO_CLIENTE).
        MonedaCartaPorte();          // Selecciona aleatoriamente una moneda para el 'Carta Porte'.
        FolioRuta();                 // Introduce la ruta en el formulario del viaje (usa NUMERO_RUTA).
        NumeroEconomicoConvoy();     // Introduce el número económico del convoy.
        SeleccionarPestanaMateriales();
        BotonImportarMaterial();
        ImportacionMaterial();
        BotonAceptarImportacion();
        BotonAceptarViaje();
        BotonConcurrencia();
        EnvioCorreo();               // Envía un correo de confirmación aleatoriamente (Sí/No).
        BotonImpresion();            // Confirma el mensaje relacionado con la impresión.
        SelecionarCartaporteListado();
        PresionarBotonFacturacion();
    }

    @AfterAll
    public static void tearDown() {
        // Cierra el navegador después de que todas las pruebas han terminado
        try {
            Thread.sleep(5000); // Reducido a 5 segundos para optimizar el tiempo de prueba
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reestablecer el estado de interrupción
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // =======================================================================================
    // Métodos del Flujo
    // =======================================================================================

    private static void BotonModuloTrafico() {
        try {
            WebElement ModuloBotonTrafico = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1')]"))
            );
            ModuloBotonTrafico.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            WebElement ListadoBoton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1')]]"))
            );
            ListadoBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarCartaPorte() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }

    @Step("Seleccionar Tipo de Documento (configurable)")
    public void TipoDocumentoIngreso() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")));
            // Se usa la variable TIPO_DOCUMENTO para buscar la opción:
            WebElement opcionIngreso = tipoDocumentoCombo.findElement(By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));
            opcionIngreso.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }

    @Step("Guardar Folio del Viaje")
    private void GuardarFolio() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            folioGuardado = folioField.getAttribute("value");
            System.out.println("El folio guardado es: " + folioGuardado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al guardar el folio del viaje");
            System.out.println("Error al guardar el folio del viaje: " + e.getMessage());
        }
    }

    @Step("Asignar Número de Viaje al Cliente (basado en Folio)")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");

            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            numeroViajeCliente = "PAING" + folioValue; // Guarda el valor en la variable
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViajeCliente);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }

    @Step("Asignar Cliente al Viaje (usa NUMERO_CLIENTE)")
    private void CodigoCliente() {
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroCliente.click();
            NumeroCliente.sendKeys(NUMERO_CLIENTE);  // Se usa la variable NUMERO_CLIENTE
            NumeroCliente.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Seleccionar Moneda de la Carta Porte Aleatoriamente")
    private void MonedaCartaPorte() {
        monedaSeleccionada = "";
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            Random random = new Random();
            int indiceAleatorio = random.nextInt(opcionesDisponibles.size());

            WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
            String valorASeleccionar = opcionSeleccionada.getAttribute("value");

            // Forzar evento change con JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'))",
                    tipoDocumentoCombo,
                    valorASeleccionar
            );

            wait.until(ExpectedConditions.textToBePresentInElement(tipoDocumentoCombo, opcionSeleccionada.getText()));

            monedaSeleccionada = opcionSeleccionada.getText().trim().toUpperCase();
            System.out.println("La moneda seleccionada es: " + monedaSeleccionada);

            // Validar que se aplicó el cambio
            String valorActual = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            if (!valorActual.equals(monedaSeleccionada)) {
                throw new Exception("Error: no se pudo cambiar la moneda. Valor actual sigue siendo: " + valorActual);
            }
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda en MonedaCartaPorte");
            System.out.println("Error al manejar la Moneda en MonedaCartaPorte: " + e.getMessage());
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al cambiar la moneda en MonedaCartaPorte");
            System.out.println("Error al cambiar la moneda en MonedaCartaPorte: " + e.getMessage());
        }
    }

    @Step("Asignar Folio de Ruta (usa NUMERO_RUTA)")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys(NUMERO_RUTA); // Se usa la variable NUMERO_RUTA
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }

    @Step("Manejar Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestanaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES")));
            botonImportar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Ajusta la ruta a tu archivo local
            String rutaArchivo = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                inputArchivo.sendKeys(rutaArchivo);
            } else {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }

            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));
            botonImportar.click();

            Thread.sleep(3000);

            // Verificar si la importación fue exitosa
            try {
                WebElement iconoExito = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver);
                    throw new Exception("La importación de materiales falló, se mostró el icono de error.");
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al importar el archivo de materiales");
        }
    }

    @Step("Aceptar Importación de Materiales")
    private void BotonAceptarImportacion() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }

    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar para confirmar el viaje");
        }
    }

    @Step("Aceptar mensaje de concurrencia si aparece")
    private void BotonConcurrencia() {
        try {
            // Esperar unos segundos para ver si aparece el mensaje de concurrencia
            WebElement botonAceptarConcurrencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));

            // Si el botón está disponible, hacer clic en él
            botonAceptarConcurrencia.click();
            System.out.println("Mensaje de concurrencia detectado y aceptado.");

            // Llamar a los métodos que deben repetirse
            GuardarFolio();
            NumeroViajeCliente();
            BotonAceptarViaje();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    @Step("Enviar Por Correo (Sí/No)")
    private void EnvioCorreo() {
        try {
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    @Step("Botón de Impresión")
    private void BotonImpresion() {
        try {
            WebElement botonRegresar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));
            botonRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Regresar para impresión");
        }
    }

    @Step("Seleccionar Carta Porte en el Listado")
    private void SelecionarCartaporteListado() {
        try {
            WebElement campoBusqueda = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#TABLE_ProViajes_filter input[type='search']"))
            );
            campoBusqueda.clear();
            campoBusqueda.sendKeys(folioGuardado);
            System.out.println("Folio ingresado en la búsqueda: " + folioGuardado);

            Thread.sleep(2000);

            WebElement tablaViajes = driver.findElement(By.id("TABLE_ProViajes"));
            List<WebElement> filas = tablaViajes.findElements(By.tagName("tr"));

            for (WebElement fila : filas) {
                if (fila.getText().contains(folioGuardado)) {
                    fila.click();
                    System.out.println("Se seleccionó el folio en la tabla: " + folioGuardado);
                    break;
                }
            }
        } catch (NoSuchElementException | TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la Carta Porte del listado");
            System.out.println("Error al seleccionar la Carta Porte del listado: " + e.getMessage());
        }
    }

    @Step("Presionar Botón Facturación y manejar resultados")
    private void PresionarBotonFacturacion() {
        try {
            WebElement botonFacturar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_FACTURAR")));
            botonFacturar.click();

            Thread.sleep(2000);

            // Caso 1: Revisa si sigue en el listado (facturación terminada)
            try {
                WebElement listadoElement = driver.findElement(By.id("TABLE_ProViajes"));
                if (listadoElement.isDisplayed()) {
                    System.out.println("Redirigido al listado. El proceso de facturación ha finalizado.");
                    return;
                }
            } catch (NoSuchElementException e) {
                // Si no está en el listado, se continúa con más pasos de facturación
            }

            // Caso 2: Completamos facturación en ventana actual
            MonedaAFacturar();
            AceptarFactura();
            BotonConcurrenciaFactura();
            AceptarTimbre();
            AceptarEDI();
            EnvioCorreoFactura();
            AceptarPoliza();
            AceptarImpresion();
            AceptarImpresion();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón 'Facturar' o manejar los resultados.");
        }
    }

   @Step("Seleccionar la Moneda a Facturar")
    private void MonedaAFacturar() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("COMBO_CATMONEDAS")));
            wait.until(ExpectedConditions.visibilityOf(tipoDocumentoCombo));
            wait.until(ExpectedConditions.elementToBeClickable(tipoDocumentoCombo));

            Select comboBox = new Select(tipoDocumentoCombo);
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesDisponibles = comboBox.getOptions();

            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();
            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText().trim().toUpperCase())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles.get(random.nextInt(opcionesValidasDisponibles.size()));
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la opción moneda a facturar: " + opcionAleatoria.getText());
            }
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error: no se pudo encontrar el elemento de selección de moneda a tiempo.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la moneda a facturar");
        }
    }

    private static void AceptarFactura() {
        try {
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            aceptarButton.click();
            System.out.println("Se presionó el botón de aceptar factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar factura");
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

            AceptarFactura();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    private static void AceptarTimbre() {
        try {
            WebElement aceptarEDIButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            aceptarEDIButton.click();
            System.out.println("Se presionó el botón de aceptar Timbre");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar Timbre");
        }
    }

    private static void AceptarEDI() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement aceptarEDIButton = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));

            if (aceptarEDIButton != null) {
                aceptarEDIButton.click();
                System.out.println("Se presionó el botón de aceptar EDI");
            } else {
                System.out.println("El botón de aceptar EDI no se encontró, continuando con la ejecución");
            }
        } catch (TimeoutException e) {
            System.out.println("El botón de aceptar EDI no se mostró, continuando la ejecución normalmente");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar EDI");
        }
    }

    private static void EnvioCorreoFactura() {
        try {
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    private static void AceptarPoliza() {
        try {
            WebElement aceptarPolizaButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
            aceptarPolizaButton.click();
            System.out.println("Se presionó el botón de aceptar póliza");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar póliza");
        }
    }

    private static void AceptarImpresion() {
        try {
            WebElement regresarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));
            regresarButton.click();
            System.out.println("Se presionó el botón de regresar después de la impresión");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar después de la impresión");
        }
    }
}
