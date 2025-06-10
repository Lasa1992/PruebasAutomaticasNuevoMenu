package Mantenimiento;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenesServicio {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static String TipoServicio; // Variable para almacenar el tipo de servicio seleccionado
    private static String FolioOS; // Variable para almacenar el folio de la orden de servicio
    private static String FolioOC; // Variable para almacenar el folio de la orden de compra

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
    @Description("Se genera un flujo completo de Mantenimiento desde Requisición de Compras.")
    public void MantenimientoPA() throws InterruptedException {

        handleImageButton();
        handleSubMenuButton();

        //Flujo Creación, Edición y Terminación de Ordenes de Servicio
        BotonAgregarOS();
        IngresarCodigoUnidad();
        CreacionTipoServicio();
        //Busca la orden de compra generada y la selecciona.
        BuscarOS();
        SeleccionarOS();
        BotonServisoOS();
        //Seleccionada la orden de servicio, se procede a darle servicio a la orden de servicio.
        BotonAgregarSer();
        AgregarServicioOS();
        ServicioOS();
        //Se termina la orden de servicio.
        TerminarOS();
        ActualizarListadoOS();
        // Verificar que la orden de servicio sea Externa y que se genera el pasivo de la OS.
        BotonGenerarPasivoOS();
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Inventarios...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/MANTENIMIENTO1')]")));
            imageButton.click();
            System.out.println("Botón Módulo Mantenimiento seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Mantenimiento no funciona.");
            System.out.println("Botón Módulo Mantenimiento no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/MANTENIMIENTO/ORDENESSERVICIOS1')]")));
            subMenuButton.click();
            System.out.println("Botón Ordenes de Servicio seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Ordenes de Servicio no funciona.");
            System.out.println("Botón listado Ordenes de Servicio no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar")
    private static void BotonAgregarOS() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Orden de Servicio' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Orden de Servicio.");
        }
    }

    @Step("Ingresar código de unidad 'T-09'")
    private static void IngresarCodigoUnidad() {
        try {
            WebElement campoCodigoUnidad = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_CODIGOUNIDAD\"]")));
            campoCodigoUnidad.clear();
            campoCodigoUnidad.sendKeys("T-09");
            campoCodigoUnidad.sendKeys(Keys.TAB);
            System.out.println("Se ingresó el código de unidad: T-09 y se avanzó con TAB.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el código de unidad T-09.");
        }
    }

    @Step("Seleccionar tipo de servicio y completar información según el tipo en la orden de servicio")
    private static void CreacionTipoServicio() {
        try {
            // Seleccionar combo de servicio
            WebElement comboServicio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_SERVICIO']")));
            Select select = new Select(comboServicio);

            //Se guarda el folio de la orden de servicio para su uso posterior
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='EDT_FOLIO']")));
            FolioOS = campoFolio.getAttribute("value");
            System.out.println("Folio de requisición generado: " + FolioOS);

            // Selección aleatoria entre INTERNO y EXTERNO
            String[] opciones = {"INTERNO", "EXTERNO"};
            TipoServicio = opciones[new Random().nextInt(opciones.length)];
            select.selectByVisibleText(TipoServicio);
            System.out.println("Tipo de servicio seleccionado: " + TipoServicio);

            if (TipoServicio.equals("INTERNO")) {
                // Clic en el botón aceptar si es INTERNO
                WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTAR']")));
                botonAceptar.click();
                System.out.println("Se generó la Orden de Servicio con Tipo INTERNO con Folio: " + FolioOS);
            } else {
                // EXTERNO: llenar proveedor
                WebElement campoProveedor = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_NUMEROPROVEEDOR']")));
                campoProveedor.clear();
                campoProveedor.sendKeys(Variables.PROVEEDOR);
                System.out.println("Proveedor ingresado: " + Variables.PROVEEDOR);

                // Llenar campo factura con FA-XXX
                WebElement campoFactura = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_FACTURA']")));
                String factura = "FA-" + (new Random().nextInt(900) + 100);
                campoFactura.clear();
                campoFactura.sendKeys(factura);
                System.out.println("Factura generada: " + factura);

                // Lugar de reparación
                WebElement campoLugar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_LUGARDEREPARACION']")));
                campoLugar.clear();
                campoLugar.sendKeys("Taller Proveedor");
                System.out.println("Lugar de reparación ingresado.");

                // Aceptar
                WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTAR']")));
                botonAceptar.click();
                System.out.println("Se generó la Orden de Servicio con Tipo EXTERNO con Folio: " + FolioOS);
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en la selección y procesamiento del tipo de servicio.");
        }
    }

    private static void BuscarOS() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"TABLE_ProOrdenesServicios_filter\"]/label/input")));

            busquedaField.clear();
            busquedaField.sendKeys(FolioOS);
            System.out.println("Se ingresó el folio de la Orden de Servicio para su búsqueda: " + FolioOS);
            busquedaField.sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='TABLE_ProOrdenesServicios']//tbody"), FolioOS));

            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la orden de servicio: " + FolioOS);
        }
    }

    @Step("Seleccionar Requisición en el Listado")
    private static void SeleccionarOS() {
        try {
            Thread.sleep(3000);
            WebElement tablaFacturas = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TABLE_ProOrdenesServicios")));

            WebElement fila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@id='TABLE_ProOrdenesServicios']//tr[td[contains(text(),'" + FolioOS + "')]]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
            try {
                fila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fila);
            }
            System.out.println("Orden de Servicio seleccionada correctamente: " + FolioOS);
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No se encontró la orden de servicio con folio: " + FolioOS);
            UtilidadesAllure.manejoError(driver, e, "No se encontró la orden de servicio con folio: " + FolioOS);
        } catch (TimeoutException e) {
            System.out.println("ERROR: La tabla no cargó los resultados a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "La tabla de ordenes de servicio no cargó correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR: Ocurrió un problema al seleccionar la orden de servicio.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la orden de servicio.");
        }
    }

    @Step("Seleccionar botón Serv. O/S")
    private static void BotonServisoOS() {
        try {
            WebElement botonAutorizar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"bzBTN_SERVICIOSDEORDEN\"]")));
            botonAutorizar.click();
            System.out.println("Botón 'Autorizar' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Autorizar.");
        }
    }

    @Step("Seleccionar botón Agregar servicio")
    private static void BotonAgregarSer() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_AGREGAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Servicio' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Servicio.");
        }
    }

    @Step("Agregar Servicio en la Orden de Servicio")
    private static void AgregarServicioOS() {
        try {
            // 1. Seleccionar el campo de código de servicio e indicar el número 32
            WebElement campoCodigoServicio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='EDT_CODIGOSERVICIO']")));
            campoCodigoServicio.clear();
            campoCodigoServicio.sendKeys("32");
            System.out.println("Código de servicio ingresado: 32");

            if (TipoServicio.equals("INTERNO")) {
                System.out.println("Iniciando proceso para tipo de servicio INTERNO...");

                WebElement campoMecanico = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_CODIGOMECANICO']")));
                campoMecanico.clear();
                campoMecanico.sendKeys("1");
                System.out.println("Código de mecánico ingresado: 1");

                WebElement btnAgregarParteInterna = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_AGREGARPARTEINTERNA']")));
                btnAgregarParteInterna.click();
                System.out.println("Clic en botón 'Agregar Parte Interna'");
                Thread.sleep(5000);

                WebElement comboAlmacen = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='COMBO_CATALMACENES']")));
                Select selectAlmacen = new Select(comboAlmacen);
                selectAlmacen.selectByVisibleText("ALMACÉN PA");
                System.out.println("Almacén seleccionado: ALMACÉN PA");
                Thread.sleep(500);

                WebElement campoArticulo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_CODIGOARTICULO']")));
                campoArticulo.clear();
                campoArticulo.sendKeys("AR-5216");
                System.out.println("Código de artículo ingresado: AR-5216");
                Thread.sleep(500);

                WebElement campoCantidad = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_CANTIDAD']")));
                campoCantidad.clear();
                campoCantidad.sendKeys("1");
                campoCantidad.sendKeys(Keys.TAB);
                campoCantidad.sendKeys(Keys.TAB);
                System.out.println("Cantidad ingresada: 1");
                Thread.sleep(5000);

                int precio = new Random().nextInt(900) + 100;
                WebElement campoPrecioUni = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"EDT_PRECIOUNITARIO\"]")));
                campoPrecioUni.clear();
                campoPrecioUni.sendKeys(String.valueOf(precio));
                System.out.println("Precio unitario ingresado: " + precio);

                WebElement btnAceptarParteInterna = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTARPINTERNA']")));
                btnAceptarParteInterna.click();
                System.out.println("Clic en botón 'Aceptar Parte Interna'");
                Thread.sleep(500);

                // Esperar que desaparezca el overlay y forzar clic con JS
                esperarYClickConJS("//*[@id='BTN_ACEPTAR']");
                System.out.println("Clic final en botón 'Aceptar'");

            } else if (TipoServicio.equals("EXTERNO")) {
                System.out.println("Iniciando proceso para tipo de servicio EXTERNO...");

                WebElement tabExterno = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='TAB_DETALLESERVICIO_2']")));
                tabExterno.click();
                System.out.println("Pestaña 'Detalle Externo' seleccionada");

                WebElement btnAgregarPExterna = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_AGREGARPEXTERNA']")));
                btnAgregarPExterna.click();
                System.out.println("Clic en botón 'Agregar Parte Externa'");
                Thread.sleep(5000);

                int cantidadExt = new Random().nextInt(9) + 1;
                WebElement campoCantidadExt = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_CANTIDADPARTEEXTERNO']")));
                campoCantidadExt.clear();
                campoCantidadExt.sendKeys(String.valueOf(cantidadExt));
                System.out.println("Cantidad parte externa ingresada: " + cantidadExt);

                WebElement campoDescripcion = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_DESCRIPCIONEXTERNO']")));
                campoDescripcion.clear();
                campoDescripcion.sendKeys("Parte Externa PA");
                System.out.println("Descripción ingresada: Parte Externa PA");

                int costoExt = new Random().nextInt(56) + 100;
                WebElement campoCostoUnitario = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_COSTOUNITARIOEXTERNO']")));
                campoCostoUnitario.clear();
                campoCostoUnitario.sendKeys(String.valueOf(costoExt));
                System.out.println("Costo unitario parte externa ingresado: " + costoExt);

                WebElement btnAceptarPExterna = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTARPEXTERNA']")));
                btnAceptarPExterna.click();
                System.out.println("Clic en botón 'Aceptar Parte Externa'");

                int manoObra = new Random().nextInt(900) + 100;
                WebElement campoManoObra = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_COSTOMANODEOBRA']")));
                campoManoObra.clear();
                campoManoObra.sendKeys(String.valueOf(manoObra));
                System.out.println("Costo mano de obra ingresado: " + manoObra);

                // Esperar que desaparezca el overlay y forzar clic con JS
                esperarYClickConJS("//*[@id='BTN_ACEPTAR']");
                System.out.println("Clic final en botón 'Aceptar'");
            }

        } catch (Exception e) {
            System.err.println("Ocurrió un problema en el método AgregarServicioOS.");
            UtilidadesAllure.manejoError(driver, e, "Error en la ejecución del método AgregarServicioOS.");
        }
    }

    // Método auxiliar para esperar que desaparezca el overlay y forzar clic con JS
    private static void esperarYClickConJS(String xpathBoton) {
        try {
            // Esperar que desaparezca un overlay que esté tapando
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[contains(@style, 'z-index: 989')]")));
        } catch (TimeoutException te) {
            System.out.println("Overlay no desapareció, se forzará el clic con JS.");
        }

        WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathBoton)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);
    }


    @Step("Finalizar Servicio en Orden de Servicio")
    private static void ServicioOS() {
        try {
            System.out.println("Iniciando método ServicioOS con tipo de servicio: " + TipoServicio);

            if (TipoServicio.equalsIgnoreCase("INTERNO")) {
                System.out.println("Tipo de servicio es INTERNO");

                WebElement btnAfectarInventario = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_AFECTARINVENTARIO']")));
                btnAfectarInventario.click();
                System.out.println("Clic en botón 'Afectar Inventario'");

                WebElement btnYes = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_YES']")));
                btnYes.click();
                System.out.println("Clic en botón 'YES'");

                WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_OK']")));
                btnOk.click();
                System.out.println("Clic en botón 'OK'");

                WebElement btnSalir = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='z_BTN_SALIR_IMG']")));
                btnSalir.click();
                System.out.println("Clic en botón 'Salir'");

            } else if (TipoServicio.equalsIgnoreCase("EXTERNO")) {
                System.out.println("Tipo de servicio es EXTERNO");

                WebElement btnGenerarOC = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_GENERAROC']")));
                btnGenerarOC.click();
                System.out.println("Clic en botón 'Generar OC'");

                WebElement comboAlmacen = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='COMBO_CATALMACENES']")));
                Select selectAlmacen = new Select(comboAlmacen);
                selectAlmacen.selectByVisibleText("ALMACÉN PA");
                System.out.println("Selección de almacén: ALMACÉN PA");

                WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='EDT_FOLIO']")));
                FolioOC = campoFolio.getAttribute("value");
                System.out.println("Folio de orden de compra de la OS Externa generado: " + FolioOC);

                WebElement btnAceptar1 = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTAR']")));
                btnAceptar1.click();
                System.out.println("Clic en botón 'Aceptar' (1)");

                WebElement btnYes = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_YES']")));
                btnYes.click();
                System.out.println("Clic en botón 'YES'");

                WebElement btnAceptar2 = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_ACEPTAR']")));
                btnAceptar2.click();
                System.out.println("Clic en botón 'Aceptar' (2)");

                WebElement btnSalir = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='z_BTN_SALIR_IMG']")));
                btnSalir.click();
                System.out.println("Clic en botón 'Salir'");
            }

            System.out.println("Proceso ServicioOS finalizado correctamente.");

        } catch (Exception e) {
            System.err.println("Error en el método ServicioOS.");
            UtilidadesAllure.manejoError(driver, e, "Error al ejecutar ServicioOS");
        }
    }

    public void TerminarOS() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Esperar y hacer clic en el botón "Terminar"
            WebElement botonTerminar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_TERMINAR']"))
            );
            botonTerminar.click();
            System.out.println("✅ Botón 'Terminar' clickeado.");

            // Esperar y hacer clic en el botón "Aceptar"
            WebElement botonAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_ACEPTAR']"))
            );
            botonAceptar.click();
            System.out.println("✅ Se termina la orden de servicio con folio: " + FolioOS);

        } catch (TimeoutException e) {
            System.err.println("❌ Uno de los botones no se encontró o no fue clickeable a tiempo.");
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en los botones 'Terminar' o 'Aceptar'.");
            throw e;
        }
    }

    @Step("Actualizar listado de Ordenes de Servicio")
    public void ActualizarListadoOS() {
        try {
            WebElement botonActualizar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_APLICAR']")));
            botonActualizar.click();
            System.out.println("✅ Listado de Ordenes de Servicio actualizado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "❌ Error al actualizar el listado de Ordenes de Servicio.");
        }
    }

    public void BotonGenerarPasivoOS() {
        try {
            if (!"EXTERNO".equalsIgnoreCase(TipoServicio)) {
                System.out.println("🔁 Tipo de servicio no es 'EXTERNO', no se ejecuta el método.");
                return;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que la opción principal "Generar" en el menú sea clickeable
            WebElement menuGenerar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"OPT_GENERAR\"]")));
            menuGenerar.click();
            System.out.println("✅ Se selecciona el botón de generar pasivo a la orden de servicio.");

            // Se genera el pasivo de la orden de servicio
            WebElement campoSerie = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_SERIEDOCUMENTO\"]")));
            campoSerie.clear();
            campoSerie.sendKeys("PAOS");

            // 3. Número random de 4 dígitos para el folio
            int folioRandom = new Random().nextInt(9000) + 1000; // 1000 - 9999
            WebElement campoFolio = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_FOLIODOCUMENTO\"]")));
            campoFolio.clear();
            campoFolio.sendKeys(String.valueOf(folioRandom));
            System.out.println("Folio generado: " + folioRandom);

            // 4. Seleccionar "PA" en el combo de categorías de pasivos
            //si no se tiene el check de varias categorias de pasivos en pasivos se usa este check
            WebElement comboCategoria = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"COMBO_CATCATEGORIASPASIVOS\"]")));
            Select selectCategoria = new Select(comboCategoria);
            selectCategoria.selectByVisibleText("PA");

//            WebElement campoCategoria = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//*[@id=\"EDT_CODIGO\"]")));
//            campoCategoria.click();
//            campoCategoria.sendKeys("16");

            // 5. Seleccionar "PPD" en el combo de método de pago
            WebElement comboMetodoPago = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"COMBO_METODOPAGO\"]")));
            Select selectMetodo = new Select(comboMetodoPago);
            selectMetodo.selectByVisibleText("PPD");

            WebElement botonAceptarP = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTAR\"]")));
            botonAceptarP.click();

            //se acepta poliza del pasivo
            WebElement botonAceptarPoliza = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_OK\"]")));
            botonAceptarPoliza.click();

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "❌ Error al seleccionar la opción 'Generar Pasivo'.");
        }
    }
}
