package Retrasmision;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RetrasmisionDLR {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String NUMERO_CLIENTE = Variables.CLIENTE;
    private static final String NUMERO_RUTA = Variables.RUTA;
    private static final String TIPO_DOCUMENTO = Variables.DocumentoTraslado;
    private static final String rutaArchivo = Variables.Docmateriales;

    @BeforeEach
    public void setup() {
        String navegador = System.getProperty("navegador", "chrome");
        System.out.println("🌍 Configurando pruebas en: " + navegador.toUpperCase());
        InicioSesion.setup(navegador);
        driver = InicioSesion.getDriver();
        wait = InicioSesion.getWait();
    }

    @Test
    @Order(1)
    @Description("Inicio de Sesión")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
        System.out.println("✅ Sesión iniciada correctamente.");
    }

    @Test
    @Order(2)
    @Description("Manejo de tipo de Cambio y novedades")
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
        System.out.println("✅ Tipo de Cambio y novedades gestionados.");
    }

    @Test
    @Order(3)
    @Description("Entrar al listado de viajes")
    public void EntrarAViajes() {
        BotonModuloTrafico();
        BotonListadoViajes();
    }

    @Test
    @Order(4)
    @Description("Crear y timbrar viaje con control de reinicio por tiempo")
    public void testCrearViajeConControl() {

        int totalEjecuciones = Integer.parseInt(System.getProperty("totalEjecuciones", "50"));
        int tiempoReinicioMin = Integer.parseInt(System.getProperty("tiempoReinicioMin", "180"));
        long tiempoReinicioMs = tiempoReinicioMin * 60 * 1000L;

        int ejecucionesRealizadas = 0;
        long tiempoInicio = System.currentTimeMillis();

        while (ejecucionesRealizadas < totalEjecuciones) {
            try {
                BotonAgregarCartaPorte();
                TipoDocumentoTraslado();
                NumeroViajeCliente();
                CodigoCliente();
                MonedaCartaPorte();
                FolioRuta();
                NumeroEconomicoConvoy();
                SeleccionarPestanaMateriales();
                BotonImportarMaterial();
                ImportacionMaterial();
                BotonAceptarImportacion();
                BotonAceptarViaje();
                EnvioCorreo();
                Mensajeretramision();
                BotonImpresion();

                ejecucionesRealizadas++;
                System.out.println("✅ Viaje creado correctamente. Total: " + ejecucionesRealizadas + "/" + totalEjecuciones);

            } catch (Exception e) {
                UtilidadesAllure.manejoError(driver, e, "Error durante la ejecución del viaje");
            }

            if (System.currentTimeMillis() - tiempoInicio >= tiempoReinicioMs) {
                System.out.println("🔄 Reiniciando navegador tras " + tiempoReinicioMin + " minutos...");
                InicioSesion.cerrarSesion();
                setup();
                BotonModuloTrafico();
                BotonListadoViajes();
                tiempoInicio = System.currentTimeMillis();
            }
        }

        System.out.println("🎯 Ejecución completada. Total viajes creados: " + ejecucionesRealizadas);
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver...");
        InicioSesion.cerrarSesion();
    }

    @Step("Ingresar al Módulo Tráfico")
    private static void BotonModuloTrafico() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1')]"))).click();
            System.out.println("✅ Ingreso al Módulo Tráfico.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    @Step("Acceder al Listado de Viajes")
    private static void BotonListadoViajes() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1')]]"))).click();
            System.out.println("✅ Acceso al Listado de Viajes.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    @Step("Presionar botón Agregar Carta Porte")
    private static void BotonAgregarCartaPorte() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR"))).click();
            System.out.println("✅ Botón Agregar Carta Porte presionado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }

    @Step("Seleccionar Tipo de Documento")
    public void TipoDocumentoTraslado() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")))
                    .findElement(By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"))
                    .click();
            System.out.println("✅ Tipo de Documento seleccionado: " + TIPO_DOCUMENTO);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento.");
        }
    }

    @Step("Asignar Número de Viaje")
    private void NumeroViajeCliente() {
        try {
            String folio = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO"))).getDomProperty("value");
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            campo.clear();
            campo.sendKeys("PA" + folio);
            System.out.println("✅ Número de Viaje asignado: PA" + folio);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el Número de Viaje.");
        }
    }

    @Step("Asignar Cliente al Viaje")
    private void CodigoCliente() {
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            campo.click();
            campo.sendKeys(NUMERO_CLIENTE);
            campo.sendKeys(Keys.TAB);
            Thread.sleep(500);
            System.out.println("✅ Cliente asignado: " + NUMERO_CLIENTE);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar Cliente.");
        }
    }

    @Step("Seleccionar Moneda")
    private void MonedaCartaPorte() {
        try {
            Select combo = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS"))));
            List<WebElement> opciones = combo.getOptions();
            opciones.stream().filter(o -> List.of("PESOS", "DÓLARES").contains(o.getText().trim().toUpperCase())).findAny()
                    .ifPresent(o -> combo.selectByVisibleText(o.getText()));
            System.out.println("✅ Moneda seleccionada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar Moneda.");
        }
    }

    @Step("Asignar Folio de Ruta")
    private void FolioRuta() {
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            campo.click();
            campo.sendKeys(NUMERO_RUTA);
            campo.sendKeys(Keys.TAB);
            Thread.sleep(500);
            System.out.println("✅ Folio de Ruta asignado: " + NUMERO_RUTA);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar Folio de Ruta.");
        }
    }

    @Step("Asignar Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            campo.sendKeys("E3");
            campo.sendKeys(Keys.ENTER);
            Thread.sleep(500);
            System.out.println("✅ Número Económico del Convoy asignado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar Número Económico.");
        }
    }

    @Step("Seleccionar pestaña de Materiales")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestana = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            pestana.findElement(By.tagName("a")).click();
            Thread.sleep(500);
            System.out.println("✅ Pestaña de Materiales seleccionada.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales.");
        }
    }

    @Step("Presionar Botón Importar Material")
    private void BotonImportarMaterial() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES"))).click();
            System.out.println("✅ Botón Importar Material presionado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Importar Material.");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) throw new Exception("Archivo no encontrado: " + rutaArchivo);
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));
            input.sendKeys(rutaArchivo);
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR"))).click();
            Thread.sleep(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
            System.out.println("✅ Archivo de Materiales importado exitosamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al importar archivo de materiales.");
        }
    }

    @Step("Aceptar Importación de Materiales")
    private void BotonAceptarImportacion() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR"))).click();
            System.out.println("✅ Importación de Materiales aceptada.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar la importación de materiales.");
        }
    }

    @Step("Aceptar Viaje")
    // Hace clic en el botón para aceptar y guardar el viaje.
    private void BotonAceptarViaje() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR"))).click();
            System.out.println("✅ Viaje aceptado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el viaje.");
        }
    }

    @Step("Enviar Correo de Confirmación")
    // Gestiona la opción de envío de correo, eligiendo "No" por defecto.
    private void EnvioCorreo() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO"))).click();
            System.out.println("✅ Opción No seleccionada para el envío de correo.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al gestionar el envío de correo.");
        }
    }

    @Step("Aceptar mensaje de Retransmisión")
    // Acepta el mensaje de confirmación de retransmisión del viaje.
    private void Mensajeretramision() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK"))).click();
            System.out.println("✅ Mensaje de Retransmisión aceptado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el mensaje de retransmisión.");
        }
    }

    @Step("Cerrar Ventana de Impresión")
    // Cierra la ventana de impresión tras finalizar el viaje.
    private void BotonImpresion() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR"))).click();
            System.out.println("✅ Ventana de impresión cerrada.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al cerrar la ventana de impresión.");
        }
    }
}
