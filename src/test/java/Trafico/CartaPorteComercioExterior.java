package Trafico;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartaPorteComercioExterior {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // =========================================================================
    // VARIABLES CONFIGURABLES
    // =========================================================================
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;       // Número de cliente
    private static final String NUMERO_RUTA = Variables.RUTA;        // Número de ruta
    private static final String TIPO_DOCUMENTO = Variables.DocumentoTraslado;
    // =========================================================================

    private String tipoCambioObtenido; // Variable global para almacenar el tipo de cambio
    private String folioCartaPorte; // Variable global para almacenar el folio


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.softwareparatransporte.com/");
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
    @Description("Métodos para agregar el tipo de cambio del Banco de Mexico")
    public void EntrarAConfiguracion() {
        BotonModuloConfiguracion();
        BotonTipodeCambio();
        BotonModificarTipoCambio();
        TipoCambioBancomexico();
        AsignarTipoCambioBancoMexico();
        BotonAceptarModificacion();
    }

    @Test
    @Order(4)
    @Description("Métodos para entrar al listado de viajes")
    public void EntrarAViajes() {
        BotonModuloTrafico();
        BotonListadoViajes();
    }

    @RepeatedTest(2)
    @Order(5)
    @Description("Crear y timbrar el viaje con Carta Porte CFDI - TR (Comercio Exterior)")
    public void testCrearViaje() {


        BotonAgregarCartaPorte();
        TipoDocumentoTraslado();
        GuardarFolioCartaPorte();
        NumeroViajeCliente();
        CodigoCliente();
        MonedaCartaPorte();
        FolioRuta();
        ActivarCheckInternacional();
        ActivarCheckExportacion();
        TipoExportacion();
        MensajeBancoMexico();
        ComplementoComercioExterior();
        MotivoTraslado();
        AceptarPropietarioMateriales();
        NumeroEconomicoConvoy();
        SeleccionarPestanaMateriales();
        BotonImportarMaterial();
        ImportacionMaterial();
        BotonAceptarImportacion();
        Observaciones();
        PestanaPedimentoDoc();
        AsignarRegimenesAduanero();
        BotonAceptarViaje();
        BotonConcurrencia();
        AceptarMensajeTimbre();
        AceptarErrorTimbre();
        EnvioCorreo();
        BotonImpresion();

        BusquedaCartaPorte();
        BotonModificarCartaPorte();
        SeleccionarPestanaMateriales2();
        AsignarNoIdentificacion();
        AsignarTarifa();
        BotonAceptarViaje();
        AceptarMensajeTimbre();


    }

    @AfterAll
    public static void tearDown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // =====================================================
    // MÉTODOS DE COMERCIO EXTERIOR
    // =====================================================

    private static void BotonModuloConfiguracion() {
        try {
            WebElement moduloBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION1')]")));
            moduloBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Configuracion no funciona.");
        }
    }

    private static void BotonTipodeCambio() {
        try {
            WebElement moduloBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONFIGURACION/TIPODECAMBIO')]")));
            moduloBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Tipos de Cambio no funciona.");
        }
    }


    private static void BotonModificarTipoCambio() {
        try {
            WebElement btnAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("z_BTN_MODIFICAR_IMG")));
            btnAgregar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Modificar Tipo de Cambio no funciona.");
        }
    }

    @Step("Obtener y guardar el tipo de cambio desde Banxico")
    public void TipoCambioBancomexico() {
        String originalWindow = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://www.banxico.org.mx/tipcamb/main.do?page=tip&idioma=sp");

        String fechaBuscada = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Pattern patronFecha = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

        try {
            WebElement linkMasInfo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Más información')]")));
            linkMasInfo.click();
            Thread.sleep(2000);

            WebElement tablaDatos = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//table[tbody/tr[contains(@class, 'renglonNon')]]")));

            List<WebElement> filas = tablaDatos.findElements(By.xpath(".//tr"));

            for (WebElement fila : filas) {
                List<WebElement> celdas = fila.findElements(By.tagName("td"));

                if (!celdas.isEmpty()) {
                    String fechaEnTabla = celdas.get(0).getText().trim();
                    String valorCambio = celdas.get(celdas.size() - 1).getText().trim();
                    Matcher matcher = patronFecha.matcher(fechaEnTabla);

                    if (matcher.matches() && fechaEnTabla.equals(fechaBuscada)) {
                        tipoCambioObtenido = valorCambio; // Solo guardamos el valor en la variable
                        System.out.println("✅ Tipo de cambio guardado: " + tipoCambioObtenido);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error en TipoCambioBancomexico: " + e.getMessage());
        } finally {
            driver.close();
            driver.switchTo().window(originalWindow);
        }
    }


    public void AsignarTipoCambioBancoMexico() {
        if (tipoCambioObtenido == null || tipoCambioObtenido.isEmpty()) {
            System.out.println("❌ No se pudo asignar el tipo de cambio porque la variable está vacía.");
            return;
        }

        try {
            WebElement campoTipoCambio = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_TIPOCAMBIO")));
            campoTipoCambio.clear();
            campoTipoCambio.sendKeys(tipoCambioObtenido);


            campoTipoCambio.sendKeys(Keys.TAB);

            System.out.println("✅ Tipo de cambio asignado correctamente: " + tipoCambioObtenido);
        } catch (Exception e) {
            System.err.println("❌ Error al asignar el tipo de cambio: " + e.getMessage());
        }
    }


    private static void BotonAceptarModificacion() {
        try {
            WebElement btnAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            btnAgregar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Aceptar Modificacion Tipo de Cambio no funciona.");
        }
    }


    private static void BotonModuloTrafico() {
        try {
            WebElement moduloBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1.jpg')]")));
            moduloBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            WebElement listadoBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.jpg')]]")));
            listadoBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarCartaPorte() {
        try {
            WebElement btnAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            btnAgregar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }


    @Step("Seleccionar Tipo de Documento (variable TIPO_DOCUMENTO)")
    public void TipoDocumentoTraslado() {
        try {
            WebElement comboTipoDoc = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATTIPOSDOCUMENTOS")));
            WebElement opcion = comboTipoDoc.findElement(
                    By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));
            opcion.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }

    public void GuardarFolioCartaPorte() {
        try {
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            folioCartaPorte = campoFolio.getAttribute("value").trim();
            System.out.println("✅ Folio de Carta Porte guardado: " + folioCartaPorte);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener el folio de Carta Porte: " + e.getMessage());
        }
    }

    @Step("Asignar Número de Viaje (concatena 'PA' al folio)")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            String numeroViaje = "PA" + folioValue;
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViaje);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }


    @Step("Asignar Cliente al Viaje (variable NUMERO_CLIENTE)")
    private void CodigoCliente() {
        try {
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            clienteField.click();
            clienteField.sendKeys(NUMERO_CLIENTE);
            clienteField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Seleccionar Moneda (PESOS / DÓLARES)")
    private void MonedaCartaPorte() {
        try {
            WebElement combo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(combo);
            List<WebElement> opciones = comboBox.getOptions();
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();
            for (WebElement opcion : opciones) {
                if (opcionesValidas.contains(opcion.getText().trim().toUpperCase())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }
            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles.get(random.nextInt(opcionesValidasDisponibles.size()));
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la opción: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda");
        }
    }

    @Step("Asignar Folio de Ruta (variable NUMERO_RUTA)")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys(NUMERO_RUTA);
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }

    @Step("Activar Check Internacional")
    public void ActivarCheckInternacional() {
        try {
            WebElement checkInternacional = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("CBOX_VIAJEINTERNACIONAL_1")));
            if (!checkInternacional.isSelected()) {
                checkInternacional.click();
                System.out.println("Se activó el check 'Internacional'.");
            } else {
                System.out.println("El check 'Internacional' ya estaba activado.");
            }
        } catch (Exception e) {
            System.err.println("Error al activar el check internacional: " + e.getMessage());
        }
    }

    @Step("Activar Check Exportacion")
    public void ActivarCheckExportacion() {
        try {
            WebElement radioExportacion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("RADIO_IMPORTACIONEXPORTACION_2")));
            if (!radioExportacion.isSelected()) {
                radioExportacion.click();
                System.out.println("Se activó la opción de Exportación.");
            } else {
                System.out.println("La opción de Exportación ya estaba activa.");
            }
        } catch (Exception e) {
            System.err.println("Error al activar la opción de Exportación: " + e.getMessage());
        }
    }

    @Step("Seleccionar Tipo de Exportación (opción 2)")
    public void TipoExportacion() {
        try {
            WebElement comboClavesSat = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CLAVESSAT")));
            Select select = new Select(comboClavesSat);
            select.selectByValue("2");
            System.out.println("Se seleccionó la opción 'Definitiva con clave A1' (value=2) exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al seleccionar la opción 'Definitiva con clave A1': " + e.getMessage());
        }
    }

    @Step("Mensaje Banco México: Clic en botón 'Aceptar'")
    public void MensajeBancoMexico() {
        try {
            WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
            btnOk.click();
            System.out.println("Se hizo clic en el botón 'Aceptar' del mensaje de Banco México.");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el mensaje de Banco México: " + e.getMessage());
        }
    }

    @Step("Complemento Comercio Exterior: clic en enlace")
    public void ComplementoComercioExterior() {
        try {
            WebElement linkComercioExt = wait.until(ExpectedConditions.elementToBeClickable(By.id("LINK_COMPLEMENTOCOMERCIOEXTERIOR")));
            linkComercioExt.click();
            System.out.println("Se hizo clic en el enlace 'Complemento comercio exterior'.");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en 'Complemento comercio exterior': " + e.getMessage());
        }
    }

    @Step("Motivo Traslado: Seleccionar última opción en COMBO_MOTIVOTRASLADO")
    public void MotivoTraslado() {
        try {
            WebElement combo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_MOTIVOTRASLADO")));
            Select select = new Select(combo);
            select.selectByValue("6"); // "OTROS"
            System.out.println("Se seleccionó la opción 'OTROS' en COMBO_MOTIVOTRASLADO.");
        } catch (Exception e) {
            System.err.println("Error al seleccionar la opción en COMBO_MOTIVOTRASLADO: " + e.getMessage());
        }
    }

    @Step("Aceptar Propietario Materiales: clic en botón 'Aceptar'")
    public void AceptarPropietarioMateriales() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("z_BTN_ACEPTARCOMERCIOEXTERIOR_IMG")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón 'Aceptar' de PropietarioMateriales.");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón 'Aceptar' de PropietarioMateriales: " + e.getMessage());
        }
    }

    @Step("Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            campo.sendKeys("E3");
            campo.sendKeys(Keys.ENTER);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestana = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            WebElement link = pestana.findElement(By.tagName("a"));
            link.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            WebElement btnImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES")));
            btnImportar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));
            String rutaArchivo = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\ImportarMaterialesComercioExterior.xlsx";
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }
            inputArchivo.sendKeys(rutaArchivo);
            WebElement btnImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));
            btnImportar.click();
            Thread.sleep(3000);
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
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            btnAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }

    @Step("Ingresar Observaciones")
    public void Observaciones() {
        try {
            WebElement campoObs = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_OBSERVACIONES")));
            campoObs.clear();
            campoObs.sendKeys("prueba carta porte comercio exterior");
            System.out.println("Se ingresaron las observaciones: 'prueba carta porte comercio exterior'.");
        } catch (Exception e) {
            System.err.println("Error al ingresar las observaciones: " + e.getMessage());
        }
    }

    @Step("Pestaña Pedimentos/Doctos: clic en la pestaña")
    public void PestanaPedimentoDoc() {
        try {
            WebElement pestana = wait.until(ExpectedConditions.elementToBeClickable(By.id("TAB_TAB1_4")));
            pestana.click();
            System.out.println("Se hizo clic en la pestaña 'Pedimentos/Doctos'.");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en la pestaña 'Pedimentos/Doctos': " + e.getMessage());
        }
    }

    @Step("Asignar Régimen Aduanero: escribir ETE, desplazar la pantalla al botón 'Agregar' y hacer clic")
    public void AsignarRegimenesAduanero() {
        try {
            WebElement campoRegimen = wait.until(ExpectedConditions.elementToBeClickable(By.id("EDT_CLAVEREGIMENADUANERO")));
            campoRegimen.clear();
            campoRegimen.sendKeys("ETE");
            campoRegimen.sendKeys(Keys.TAB);

            WebElement botonAgregar = driver.findElement(By.id("BTN_AGREGARREGIMENADUANERO"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonAgregar);

            botonAgregar.click();
            System.out.println("Se escribió 'ETE' y se hizo clic en el botón 'Agregar Régimen Aduanero'.");
        } catch (Exception e) {
            System.err.println("Error al asignar el régimen aduanero: " + e.getMessage());
        }
    }

    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_ACEPTAR")));
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Aceptar para confirmar el viaje");
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
            GuardarFolioCartaPorte();
            NumeroViajeCliente();
            BotonAceptarViaje();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    @Step("Aceptar Mensaje de Timbre (BTN_YES)")
    private void AceptarMensajeTimbre() {
        try {
            WebElement btnTimbre = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            btnTimbre.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Sí para aceptar el mensaje de timbre");
        }
    }


    public void AceptarErrorTimbre() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón 'Aceptar'.");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón 'Aceptar': " + e.getMessage());
        }
    }


    @Step("Enviar Correo de Timbre (Sí/No)")
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

    @Step("Cerrar Ventana de Impresión (BTN_REGRESAR)")
    private void BotonImpresion() {
        try {
            WebElement btnRegresar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));
            btnRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Regresar para impresión");
        }
    }

    @Step("Busqueda Carta Porte en el listado")
    private void BusquedaCartaPorte() {
        try {
            // Buscar el campo de búsqueda y llenar con el valor del folio guardado
            WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#TABLE_ProViajes_filter input[type='search']")));

            campoBusqueda.clear();
            campoBusqueda.sendKeys(folioCartaPorte);
            System.out.println("Folio ingresado en la búsqueda: " + folioCartaPorte);

            // Esperar un momento para que la tabla se filtre correctamente
            Thread.sleep(2000);

            // Buscar en la tabla el elemento que contenga el folio
            WebElement tablaViajes = driver.findElement(By.id("TABLE_ProViajes"));
            List<WebElement> filas = tablaViajes.findElements(By.tagName("tr"));

            // Iterar por las filas de la tabla para encontrar el folio y hacer clic
            for (WebElement fila : filas) {
                if (fila.getText().contains(folioCartaPorte)) {
                    fila.click();
                    System.out.println("Se seleccionó el folio en la tabla: " + folioCartaPorte);
                    break;
                }
            }

        } catch (NoSuchElementException | TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la Carta Porte en el listado");
            System.out.println("Error al buscar la Carta Porte en el listado: " + e.getMessage());
        }
    }

    public void BotonModificarCartaPorte() {
        try {
            // Esperar a que el menú de opciones esté presente y sea clickeable
            WebElement menuModificar = wait.until(ExpectedConditions.elementToBeClickable(By.id("tzOPT_MODIFICAR")));
            menuModificar.click();
            Thread.sleep(1000); // Pequeña espera antes de buscar la opción Modificar

            // Esperar y hacer clic en la opción "Modificar"
            WebElement opcionModificar = wait.until(ExpectedConditions.elementToBeClickable(By.id("OPT_MODIFICAR")));
            opcionModificar.click();

            System.out.println("Se hizo clic en la opción 'Modificar'.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en la opción 'Modificar': " + e.getMessage());
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales2() {
        try {
            WebElement pestana = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            WebElement link = pestana.findElement(By.tagName("a"));
            link.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    public void AsignarNoIdentificacion() {
        try {
            // Crear el valor de identificación como "PA" + folio guardado
            String valorIdentificacion = "PA" + folioCartaPorte;

            // Buscar el campo y asignar el valor
            WebElement campoNoIdentificacion = driver.findElement(By.id("zrl_1_ATT_EDT_NOIDENTIFICACION"));

            campoNoIdentificacion.clear();
            campoNoIdentificacion.sendKeys(valorIdentificacion);
            campoNoIdentificacion.sendKeys(Keys.TAB);

            Thread.sleep(2000);

            System.out.println("Se asignó el número de identificación: " + valorIdentificacion);

        } catch (Exception e) {
            System.err.println("Error al asignar el número de identificación: " + e.getMessage());
        }
    }


    public void AsignarTarifa() {
        try {
            // Generar un número aleatorio entre 99 y 9999 con 2 decimales
            Random random = new Random();
            double valorTarifa = 99 + (random.nextDouble() * (9999 - 99));
            DecimalFormat formato = new DecimalFormat("####.00");
            String tarifaFormateada = formato.format(valorTarifa).replace(",", "."); // Asegurar formato correcto

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            WebElement campoTarifa = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("zrl_1_ATT_EDT_TARIFA")));

            // Desplazar la vista al campo de tarifa
            js.executeScript("arguments[0].scrollIntoView(true);", campoTarifa);
            Thread.sleep(500);

            // Verificar si el campo es interactuable
            campoTarifa = wait.until(ExpectedConditions.elementToBeClickable(campoTarifa));

            // Hacer clic en el campo antes de escribir
            campoTarifa.click();
            Thread.sleep(500);

            // Método 1: Intentar escribir normalmente con retardos
            for (char c : tarifaFormateada.toCharArray()) {
                campoTarifa.sendKeys(String.valueOf(c));
                Thread.sleep(100); // Pequeña pausa entre caracteres
            }
            campoTarifa.sendKeys(Keys.TAB);
            Thread.sleep(2000);

            // Validar si el campo se llenó correctamente
            String valorIngresado = campoTarifa.getAttribute("value");
            if (!valorIngresado.contains(tarifaFormateada)) {
                System.out.println("⚠ El campo no aceptó `sendKeys()`, usando `JavascriptExecutor`.");

                // Método 2: Forzar la asignación con `JavascriptExecutor`
                js.executeScript("arguments[0].value = arguments[1];", campoTarifa, tarifaFormateada);
                Thread.sleep(500);
                campoTarifa.sendKeys(Keys.TAB);
                Thread.sleep(2000);
            }

            System.out.println("✅ Se asignó la tarifa correctamente: " + tarifaFormateada);

        } catch (NoSuchElementException e) {
            System.err.println("❌ Error: El campo de tarifa no se encontró en la página.");
        } catch (TimeoutException e) {
            System.err.println("❌ Error: El campo de tarifa no apareció después de la espera.");
        } catch (Exception e) {
            System.err.println("❌ Error al asignar la tarifa: " + e.getMessage());
        }
    }

}


