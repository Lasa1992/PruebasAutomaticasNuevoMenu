package Trafico;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ViajeACartaPorte {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // =========================================================================
    // VARIABLES CONFIGURABLES
    // =========================================================================
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;  // Número de cliente
    private static final String NUMERO_RUTA    = Variables.RUTA;     // Número de ruta
    private static final String TIPO_DOCUMENTO = Variables.DocumentoTraslado;

    private static final String rutaArchivo = Variables.Docmateriales ;
    // =========================================================================

    // Declaras tu variable a nivel de clase (static o no, según prefieras)
    // Declaras ambas variables a nivel de clase
    private static String numeroViaje;
    private static int contadorViaje = 1;  // Comienza en 1 o en el número que desees

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
    @Description("Métodos para entrar al listado de viajes")
    public void EntrarAViajes() {
        BotonModuloTrafico();
        BotonListadoViajes();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Creamos un Viaje para pasarlo a Carta Porte")
    public void testPasarViajeACP() {
        BotonAgregarViaje();
        NumeroViaje();
        MonedaCartaPorte();
        CodigoCliente();
        FolioRuta();
        NumeroEconomicoConvoy();
        AceptarViaje();
        EnvioCorreo();
        BotonImpresion();

        SelecionarViajeListado();
        BotonPasarACartaPorte();

        TipoDocumentoTraslado();

        SeleccionarPestanaMateriales();
        BotonImportarMaterial();
        ImportacionMaterial();
        BotonAceptarImportacion();
        AceptarViaje();
        BotonConcurrencia();
        AceptarMensajeTimbre();
        //EnvioCorreo();
        //BotonImpresion();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    // =====================================================
    // MÉTODOS DE FLUJO
    // =====================================================

    private static void BotonModuloTrafico() {
        try {
            WebElement ModuloBotonTrafico = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1.jpg')]"))
            );
            ModuloBotonTrafico.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            WebElement ListadoBoton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.jpg')]]"))
            );
            ListadoBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarViaje() {
        try {
            WebElement additionalButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGARVIAJES"))
            );
            additionalButton.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Viaje no funciona.");
        }
    }


    @Step("Manejar Número de Viaje")
    private static void NumeroViaje() {
        try {
            // 1) Leemos la fecha actual de la interfaz
            WebElement FechaField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FECHA"))
            );
            String FechaValue = FechaField.getAttribute("value");

            // 2) Leemos el contador actual desde el archivo
            int contadorViaje = 0;
            Path path = Paths.get("C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\VariableViajeACartaPorte.txt");

            if (Files.exists(path)) {
                try {
                    String contenido = Files.readString(path).trim();
                    contadorViaje = Integer.parseInt(contenido);
                } catch (IOException | NumberFormatException e) {
                    // Si no se puede leer o el contenido no es un número, se queda en 0
                    e.printStackTrace();
                }
            }

            // 3) Construimos el número de viaje con el formato requerido
            //    PA + Fecha (tal como se ve en la UI) + "_" + contador
            numeroViaje = "PA" + FechaValue + "_" + contadorViaje;

            // 4) Incrementamos el contador para la siguiente vez
            contadorViaje++;

            // 5) Guardamos el nuevo valor de contador en el archivo
            try {
                Files.writeString(path, String.valueOf(contadorViaje));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 6) Ingresamos el númeroViaje en el campo correspondiente
            WebElement numeroViajeField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE"))
            );
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViaje);

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }



    @Step("Seleccionar Moneda (PESOS / DÓLARES)")
    private void MonedaCartaPorte() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();

            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText().trim().toUpperCase())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles
                        .get(random.nextInt(opcionesValidasDisponibles.size()));
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la opción: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda");
        }
    }

    @Step("Asignar Cliente al Viaje (variable NUMERO_CLIENTE)")
    private static void CodigoCliente() {
        try {
            WebElement clienteField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE"))
            );
            clienteField.click();
            clienteField.sendKeys(NUMERO_CLIENTE);
            clienteField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Asignar Folio de Ruta (variable NUMERO_RUTA)")
    private static void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA"))
            );
            folioRutaField.click();
            folioRutaField.sendKeys(NUMERO_RUTA);
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }

    @Step("Manejar Número Económico Convoy")
    private static void NumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY"))
            );
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Aceptar Viaje")
    private void AceptarViaje() {
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
            AceptarViaje();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
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

    @Step("Seleccionar Carta Porte Listado")
    private void SelecionarViajeListado() {
        try {
            // Buscar el campo de búsqueda y llenar con el valor del folio guardado
            WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#TABLE_ProViajes_filter input[type='search']")));
            campoBusqueda.clear();
            campoBusqueda.sendKeys(numeroViaje);
            System.out.println("Folio ingresado en la búsqueda: " + numeroViaje);

            // Esperar un momento para que la tabla sea filtrada
            Thread.sleep(2000);

            // Buscar en la tabla el elemento que contenga el folio
            WebElement tablaViajes = driver.findElement(By.id("TABLE_ProViajes"));
            List<WebElement> filas = tablaViajes.findElements(By.tagName("tr"));

            // Iterar por las filas de la tabla para encontrar el folio buscado y hacer clic
            for (WebElement fila : filas) {
                if (fila.getText().contains(numeroViaje)) {
                    fila.click();
                    System.out.println("Se seleccionó el folio en la tabla: " + numeroViaje);
                    break;
                }
            }

        } catch (NoSuchElementException | TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la Carta Porte del listado");
            System.out.println("Error al seleccionar la Carta Porte del listado: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de pasar a carta porte")
    public void BotonPasarACartaPorte() {
        try {
            // Localizar el botón de impresión usando el XPath proporcionado
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/table/tbody/tr/td/button"));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de Pasar A Carta Porte.");

        } catch (Exception e) {
            System.err.println("Se hizo clic en el botón de Pasar A Carta Porte:" + e.getMessage());
        }
    }

    @Step("Seleccionar Tipo de Documento (variable TIPO_DOCUMENTO)")
    public void TipoDocumentoTraslado() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATTIPOSDOCUMENTOS")));
            // Usar la variable TIPO_DOCUMENTO
            WebElement opcionTraslado = tipoDocumentoCombo.findElement(
                    By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));
            opcionTraslado.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestanaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TAB_TAB1_2")));
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_IMPORTARMATERIALES")));
            botonImportar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {


        try {
            // Espera a que el campo de archivo sea visible
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Verificar si el archivo existe
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                // Enviar la ruta del archivo al campo de tipo "file"
                inputArchivo.sendKeys(rutaArchivo);
            } else {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }

            // Espera a que el botón de importar sea clicable
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));

            // Hacer clic en el botón de importar
            botonImportar.click();

            // Esperar unos 3 segundos después de hacer clic
            Thread.sleep(3000);

            // Verificar si la importación fue exitosa o falló
            try {
                WebElement iconoExito = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver); // Toma captura de pantalla en caso de error
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
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }

    @Step("Aceptar Mensaje de Timbre (BTN_YES)")
    private void AceptarMensajeTimbre() {
        try {
            WebElement botonAceptarTimbre = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_YES")));
            botonAceptarTimbre.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Sí para aceptar el mensaje de timbre");
        }
    }

}
