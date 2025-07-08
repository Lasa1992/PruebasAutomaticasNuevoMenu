package Inventarios;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Compras {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static String ARTICULO = "AR-5216"; // Código del artículo a buscar
    private static String FolioRequisicion;
    private static String FolioOC;
    private static String FolioCompra;
    //Se requiere generar en la bd que se valide la categoria de pasivo "PA" y almacen "ALMACÉN PA"

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
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
    }

    @RepeatedTest(1)
    @Order(4)
    @Description("Se genera un flujo completo de Inventarios desde Requisición de Compras.")
    public void InventariosPA() throws InterruptedException {

        handleImageButton();
        handleSubMenuButton();
        //Generar y Autorizar Requisición
        BotonAgregarListadoRecq();
        IngresarNumeroProveedor();
        SeleccionarAlmacenPA();
        IntroducirReferencia();
        BotonAArticulo();
        IngresarCodigoArticulo();
        IngresarDetalleArticulo();
        SeleccionarImpuestoIVA();
        GenerarRequisicion();
        //Busca la requisición generada y la selecciona para autorizarla y generar la OC
        BuscarRequisicion();
        SeleccionarRequisicion();
        BotonAutorizar();
        //Generar la OC a partir de la requisición autorizada
        BotonGenerarOC();
        IntroducirReferencia();
        GenerarOC();
        //Autoriza la OC generada
        BotonAutorizarOC();
        AutorizarOC();
        //Realizamos la compra y pasivo de la OC generada
        handleImageButton();
        handleSubMenuButtonCom();
        BotonAgregarCompra();
        IngresarNumeroProveedor();
        SeleccionarOC();
        CrearPasivoCompra();
        BotonAceptarCompra();
    }


    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Inventarios...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/INVENTARIOS1')]")));
            imageButton.click();
            System.out.println("Botón Módulo Inventarios seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Inventarios no funciona.");
            System.out.println("Botón Módulo Inventarios no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/INVENTARIOS/REQUISICIONESOC1')]")));
            subMenuButton.click();
            System.out.println("Botón requisiciones seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de requisiciones no funciona.");
            System.out.println("Botón listado requisiciones no funciona.");
        }
    }

    private static void BotonAgregarListadoRecq() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            additionalButton.click();
            System.out.println("Botón Agregar Requisición seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón agregar requisición no encontrado o no clickeable.");
            System.out.println("Botón agregar requisición no encontrado o no clickeable.");
        }
    }

    private void IngresarNumeroProveedor() {
        try {
            WebElement campoProveedor = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_NUMEROPROVEEDOR']")));
            campoProveedor.clear(); // Limpia el campo si ya tiene texto
            campoProveedor.sendKeys(Variables.PROVEEDOR); // Asigna el valor de la variable
            System.out.println("Número de proveedor ingresado: " + Variables.PROVEEDOR);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el número de proveedor.");
        }
    }

    private void SeleccionarAlmacenPA() {
        try {
            WebElement comboAlmacenes = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_CATALMACENES']")));

            Select select = new Select(comboAlmacenes);
            select.selectByVisibleText("ALMACÉN PA");

            System.out.println("Se seleccionó 'ALMACÉN PA' del combo de almacenes.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar 'ALMACÉN PA' en el combo de almacenes.");
        }
    }

    @Step("Introducir referencia")
    public void IntroducirReferencia() {
        try {
            System.out.println("Seleccionar registrar la referencia");

            // Buscar el campo por XPath correctamente
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='EDT_CARGARA']")));

            campoReferencia.clear();

            // Generar número aleatorio de 3 dígitos
            Random rand = new Random();
            int numeroAleatorio = rand.nextInt(900) + 100; // Genera entre 100 y 999

            // Construir la referencia con el número aleatorio
            String referencia = "Referencia Automatica PA #" + numeroAleatorio;
            campoReferencia.sendKeys(referencia);
            System.out.println("Referencia ingresada: " + referencia);

        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia.");
            System.out.println("Error al ingresar la referencia.");
        }
    }

    @Step("Seleccionar botón Agregar Artículo")
    public void BotonAArticulo() {
        try {
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_AGREGAR']")));
            botonAgregar.click();
            System.out.println("Se hizo clic en el botón Agregar Artículo.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Artículo.");
            System.out.println("Error al hacer clic en el botón Agregar Artículo.");
        }
    }

    @Step("Ingresar código de artículo AR-5216")
    public void IngresarCodigoArticulo() {
        try {
            WebElement campoCodigo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_CODIGOARTICULO']")));

            campoCodigo.clear(); // Limpia el campo antes de escribir
            campoCodigo.sendKeys(ARTICULO);
            campoCodigo.sendKeys(Keys.ENTER); // Simula un "tap" o confirmación

            System.out.println("Código de artículo ingresado: "+ ARTICULO);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el código de artículo.");
            System.out.println("Error al ingresar el código de artículo.");
        }
    }

    @Step("Ingresar cantidad y precio unitario aleatorio, luego aceptar detalle")
    public void IngresarDetalleArticulo() {
        try {
            // Generar número aleatorio de 2 dígitos entre 3 y 12
            Random random = new Random();
            int cantidad = random.nextInt(10) + 3; // 3 a 12
            int precioUnitario = random.nextInt(78) + 112; // 112 a 189

            // Ingresar cantidad
            WebElement campoCantidad = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_CANTIDAD']")));
            campoCantidad.clear();
            campoCantidad.sendKeys(String.valueOf(cantidad));
            System.out.println("Cantidad ingresada: " + cantidad);

            // Ingresar precio unitario
            WebElement campoPrecio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_PRECIOUNITARIO']")));
            campoPrecio.clear();
            campoPrecio.sendKeys(String.valueOf(precioUnitario));
            System.out.println("Precio unitario ingresado: " + precioUnitario);

            // Hacer clic en el botón Aceptar y se agrega el artículo
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTARDETALLE']")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar y se agregó el artículo correctamente.");

            // Hacer clic en el botón Cancelar para salir del detalle del artículo
            WebElement botonCancelar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_CANCELARDETALLE']")));
            botonCancelar.click();
            System.out.println("Se hizo clic en el botón Cancelar y se cierra la ventana de detalle del artículo.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar los datos del detalle del artículo.");
            System.out.println("Error al ingresar los datos del detalle del artículo.");
        }
    }

    @Step("Seleccionar IVA 16% en el combo de impuestos trasladados")
    public void SeleccionarImpuestoIVA() {
        try {
            // Esperar el combo y hacer clic para desplegar opciones
            WebElement comboImpuestos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='bzCOMBO_CATIMPUESTOSTRASLADOS']")));
            comboImpuestos.click();

            // Esperar y seleccionar la opción "IVA 16%" (ajusta si es necesario)
            WebElement opcionIVA = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//option[contains(text(),'IVA 16%')]")));
            opcionIVA.click();

            System.out.println("Se seleccionó la opción 'IVA 16%' correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'IVA 16%' del combo de impuestos.");
            System.out.println("Error al seleccionar la opción 'IVA 16%' del combo de impuestos.");
        }
    }

    @Step("Generar Requisición - Clic en botón Aceptar y obtener folio")
    public void GenerarRequisicion() {
        try {
            // toma el folio de la requisición
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='EDT_FOLIO']")));
            FolioRequisicion = campoFolio.getAttribute("value");
            System.out.println("Folio de requisición generado: " + FolioRequisicion);

            // Hacer clic en el botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar para generar la requisición.");
            // Esperar un momento para asegurarse de que la requisición se haya generado
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar requisición o capturar folio.");
            System.out.println("Error al hacer clic en el botón Aceptar o al obtener el folio.");
        }
    }

    private static void BuscarRequisicion() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"TABLE_ProRequisiciones_filter\"]/label/input")));

            busquedaField.clear();
            busquedaField.sendKeys(FolioRequisicion);
            System.out.println("Se ingresó el folio de la requisición para su búsqueda: " + FolioRequisicion);
            busquedaField.sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='TABLE_ProRequisiciones']//tbody"), FolioRequisicion));

            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la requisición: " + FolioRequisicion);
        }
    }

    @Step("Seleccionar Requisición en el Listado")
    private static void SeleccionarRequisicion() {
        try {
            Thread.sleep(3000);
            WebElement tablaFacturas = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TABLE_ProRequisiciones")));

            WebElement fila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@id='TABLE_ProRequisiciones']//tr[td[contains(text(),'" + FolioRequisicion + "')]]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
            try {
                fila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fila);
            }
            System.out.println("Requisición seleccionada correctamente: " + FolioRequisicion);
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No se encontró la requisición con folio: " + FolioRequisicion);
            UtilidadesAllure.manejoError(driver, e, "No se encontró la requisición con folio: " + FolioRequisicion);
        } catch (TimeoutException e) {
            System.out.println("ERROR: La tabla no cargó los resultados a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "La tabla de requisiciones no cargó correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR: Ocurrió un problema al seleccionar la requisición.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la requisición.");
        }
    }

    @Step("Seleccionar botón Autorizar")
    private static void BotonAutorizar() {
        try {
            WebElement botonAutorizar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"bzBTN_AUTORIZAR\"]")));
            botonAutorizar.click();
            System.out.println("Botón 'Autorizar' del listado de requisiciones fue clickeado correctamente.");
            Thread.sleep(4000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Autorizar.");
        }
    }

    @Step("Seleccionar botón GenerarOC y confirmar conceptos")
    private static void BotonGenerarOC() {
        try {
            // 1. Esperar a que desaparezca el overlay (si existe) usando XPath
            List<WebElement> overlays = driver.findElements(
                    By.xpath("//div[contains(@style, 'z-index') and contains(@style,'opacity')]"));
            if (!overlays.isEmpty()) {
                wait.until(ExpectedConditions.invisibilityOf(overlays.get(0)));
            }

            // 2. Clic en el botón GenerarOC
            WebElement botonGenerarOC = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_GENERAOC']")));
            botonGenerarOC.click();
            System.out.println("Botón 'GenerarOC' fue clickeado correctamente desde el listado de Requisiciones.");

            // 3. Clic en checkbox de marcar todos los artículos
            WebElement checkMarcarTodos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"CBOX_MARCARTODOSCONCEPTOS_1\"]")));
            checkMarcarTodos.click();
            System.out.println("Checkbox 'Marcar todos artículos' fue seleccionado.");

            // 4. Clic en botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();
            System.out.println("Botón 'Aceptar' fue clickeado correctamente.");

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar la OC y confirmar los conceptos.");
        }
    }


    @Step("Generar Orden de Compra - Clic en botón Aceptar y obtener folio de la OC")
    public void GenerarOC() {
        try {
            // toma el folio de la Orden de Compra
            WebElement campoFolioOC = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='EDT_FOLIO']")));
            FolioOC = campoFolioOC.getAttribute("value");
            System.out.println("Folio de Orden de Compra generado: " + FolioOC);

            // Hacer clic en el botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar para generar la Orden de Compra.");
            // Esperar un momento para asegurarse de que la Orden de Compra se haya generado
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar la Orden de Compra o capturar folio.");
            System.out.println("Error al hacer clic en el botón Aceptar o al obtener el folio.");
        }
    }

    @Step("Autorizar Orden de Compra")
    private static void BotonAutorizarOC() {
        try {
            WebElement botonYes = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_YES\"]")));
            botonYes.click();
            System.out.println("Botón 'YES' para ir a autorizar la OC fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'YES' para autorizar la OC.");
        }
    }

    @Step("Botón Autorizar Orden de Compra")
    private static void AutorizarOC() {
        try {
            WebElement botonYes = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTAR\"]")));
            botonYes.click();
            System.out.println("Se autorizo correctamente la Orden de Compra con Folio : " + FolioOC);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'YES' para autorizar la OC.");
        }
    }

    private static void handleSubMenuButtonCom() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/INVENTARIOS/COMPRAS1')]")));
            subMenuButton.click();
            System.out.println("Botón compras seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de compras no funciona.");
            System.out.println("Botón listado compras no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar Compra")
    private static void BotonAgregarCompra() {
        try {
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            botonAgregar.click();
            System.out.println("Botón 'Agregar Compra' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'Agregar Compra'.");
        }
    }

    @Step("Seleccionar la orden de compra autorizada")
    private static void SeleccionarOC() {
        try {
            WebElement icono = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROORDENESCOMPRA_TITRES_TRI_3\"]")));

            icono.click();
            Thread.sleep(500); // Pequeña pausa entre clics, si es necesario
            icono.click();

            System.out.println("Se ordeno ascendentemente la tabla de órdenes de compra");

            WebElement Select = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"_0_TABLE_PROORDENESCOMPRA_1\"]")));

            Select.click();
            Thread.sleep(500);
            System.out.println("Se selecciona la orden de compra con folio: " + FolioOC);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ordenas o seleccionar la orden de compra.");
        }
    }

    @Step("Crear Pasivo de Compra")
    private static void CrearPasivoCompra() {
        try {
            // 1. Seleccionar el check de generar pasivo
            WebElement checkPasivo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"CBOX_GENERARPASIVOCXP_1\"]")));
            checkPasivo.click();

            // 2. Ingresar "PA" en el campo de serie
            WebElement campoSerie = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_SERIEDOCUMENTO\"]")));
            campoSerie.clear();
            campoSerie.sendKeys("PA");

            // 3. Número random de 4 dígitos para el folio
            int folioRandom = new Random().nextInt(9000) + 1000; // 1000 - 9999
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_FOLIODOCUMENTO\"]")));
            campoFolio.clear();
            campoFolio.sendKeys(String.valueOf(folioRandom));
            System.out.println("Folio generado: " + folioRandom);

            // 4. Seleccionar "PA" en el combo de categorías de pasivos
            WebElement comboCategoria = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"COMBO_CATEGORIASPROVEEDOR\"]")));
            Select selectCategoria = new Select(comboCategoria);
            selectCategoria.selectByVisibleText("PA");

            // 5. Seleccionar "PPD" en el combo de método de pago
            WebElement comboMetodoPago = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"COMBO_METODOPAGO\"]")));
            Select selectMetodo = new Select(comboMetodoPago);
            selectMetodo.selectByVisibleText("PPD");

            System.out.println("Pasivo de compra creado correctamente: PA-" + folioRandom);

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al crear el pasivo de compra.");
        }
    }

    @Step("Generar Compra - Clic en botón Aceptar y obtener folio de la compra")
    public void BotonAceptarCompra() {
        try {
            // toma el folio de la compra
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_FOLIO\"]")));
            FolioCompra = campoFolio.getAttribute("value");
            System.out.println("Folio de compra generado: " + FolioCompra);

            // Hacer clic en el botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar para generar la compra.");
            // Esperar un momento para asegurarse de que la compra se haya generado
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar compra o capturar folio.");
            System.out.println("Error al hacer clic en el botón Aceptar o al obtener el folio.");
        }
    }

}