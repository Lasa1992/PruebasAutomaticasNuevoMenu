package Contabilidad;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportacionPolizasYPrepolizas {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String FolioPoliza = "";

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
    @Description("Acceder al módulo de Bancos")
    public void testIngresarModuloBancos() {
        ingresarModuloBancos();
        submoduloCheques();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Importación de Pólizas y Prepólizas")
    public void ImportacionPoliyPrep() {
        BotonImportar();
        MarcarCheckRenumerar();
        RevisionFechaExcel();
        Importararchivo();
        AceptarMensajeExito();
        GuardarFolioPoliza();
        SalirVentanaImportacion();


        // Bloque modificar
        BuscarPoliza();
        SeleccionarPoliza();
        BotonModificar();
        CampoConcepto();
        BotonModificarMovimientos();
        CampoReferenciaMov();
        AceptarModMovimiento();
        AceptarModificacionPoliza();

        BotonConsultar();
        CapturarConcepto();
        SalirConsultar();

        BotonImprimir();
        SeleccionarFormato();
        Imprimir();
        CerrarVistaPrevia();

        EliminarPoliza();
        AceptarAlertaEliminacion();



    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }


    @Step("Abrir el módulo de Bancos")
    private void ingresarModuloBancos() {
        try {
            WebElement botonBancos = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONTABILIDAD1')]")
            ));
            botonBancos.click();
            System.out.println("Módulo Bancos: Módulo de Bancos abierto correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el módulo de Contabilidad.");
        }
    }

    @Step("Abrir submódulo de Cheques")
    private void submoduloCheques() {
        try {
            WebElement subPolizascontables = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/CONTABILIDAD/POLIZASCONTABLES')]")
            ));
            subPolizascontables.click();
            System.out.println("Submódulo Cheques: Submódulo de Cheques abierto correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al abrir el submódulo Polizas Contables.");
        }
    }

    @Step("Registrar un nuevo Cheque")
    private void BotonImportar() {
        try {
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/input")
            ));
            botonRegistrar.click();
            System.out.println("Botón Importar: El botón Importar fue presionado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Importar.");
        }
    }

    private static void MarcarCheckRenumerar() {
        try {
            WebElement checkRenumerar = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/table/tbody/tr/td/label/input")
                    )
            );
            checkRenumerar.click();
            System.out.println("Check Renumerar: Check de renumeración marcado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al marcar el check de renumeración.");
        }
    }

    @Step("Revisar y actualizar la fecha en el archivo Excel")
    private void RevisionFechaExcel() {
        String filePath = "C:\\RepositorioPrueAuto\\XLSXPruebas\\ImportarPolizas.xlsx";
        try {
            FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Importar");
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date fechaActual = new Date();
            if (cell != null && DateUtil.isCellDateFormatted(cell)) {
                String fechaExcelStr = sdf.format(cell.getDateCellValue());
                String fechaActualStr = sdf.format(fechaActual);
                if (!fechaExcelStr.equals(fechaActualStr)) {
                    System.out.println("Fecha Anterior: " + cell.getDateCellValue());
                    cell.setCellValue(fechaActual);
                    FileOutputStream output = new FileOutputStream(filePath);
                    workbook.write(output);
                    output.close();
                    System.out.println("Fecha Actualizada: " + fechaActual);
                } else {
                    System.out.println("Fecha sin cambios: La fecha ya era la actual.");
                }
            } else {
                System.out.println("Error: La celda no contiene una fecha válida.");
            }
            workbook.close();
            file.close();
            System.out.println("Revision Excel: Revisión y actualización de fecha realizada correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al revisar y actualizar la fecha en Excel.");
        }
    }

    @Step("Importar archivo Excel")
    private void Importararchivo() {
        try {
            // Ruta del archivo a importar
            String rutaArchivo;
            if ("CACX7605101P8".equals(Variables.RFC)) {
                rutaArchivo = "C:\\RepositorioPrueAuto\\XLSXPruebas\\ImportarPolizas 2025-06-04 CACX.xlsx";
            } else {
                rutaArchivo = "C:\\RepositorioPrueAuto\\XLSXPruebas\\ImportarPolizas.xlsx";
            }

            WebElement fileInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div[1]/div/input")
            ));
            fileInput.sendKeys(rutaArchivo);

            WebElement uploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div[2]/div/table/tbody/tr/td/input")
            ));
            uploadButton.click();

            System.out.println("Importar Archivo: El archivo fue importado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al importar archivo.");
        }
    }


    @Step("Aceptar mensaje de éxito")
    private void AceptarMensajeExito() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")
            ));
            btnAceptar.click();
            System.out.println("Aceptar Mensaje: El mensaje de éxito fue aceptado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el mensaje de éxito.");
        }
    }

    @Step("Guardar Folio de la Póliza")
    private void GuardarFolioPoliza() {
        try {
            WebElement textArea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_LOGERRORES")
            ));
            String mensaje = textArea.getAttribute("value");
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("número:\\s*(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(mensaje);
            if (matcher.find()) {
                FolioPoliza = matcher.group(1);
                System.out.println("Folio de Póliza: Folio guardado: " + FolioPoliza);
            } else {
                System.out.println("Folio de Póliza: No se encontró folio en el mensaje.");
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al guardar el folio de la póliza.");
        }
    }

    @Step("Salir de la ventana de Importación")
    private void SalirVentanaImportacion() {
        try {
            WebElement btnSalir = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[3]/div[1]/div/table/tbody/tr/td/a/span")
            ));
            btnSalir.click();
            System.out.println("Salir Ventana: Se salió de la ventana de importación correctamente.");
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al salir de la ventana de importación.");
        }
    }

    @Step("Buscar Póliza")
    private void BuscarPoliza() {
        try {
            WebElement inputBuscar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[1]/div[1]/label/input")
            ));
            inputBuscar.clear();
            inputBuscar.sendKeys(FolioPoliza);
            System.out.println("Buscar Póliza: Se buscó la póliza con número " + FolioPoliza + " correctamente.");
            Thread.sleep(3000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la póliza con número " + FolioPoliza + ".");
        }
    }

    @Step("Seleccionar Póliza")
    private void SeleccionarPoliza() {
        try {
            WebElement registro = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table[@id='TABLE_ProPolizas']//td[normalize-space(text())='" + FolioPoliza + "']")
            ));
            registro.click();
            System.out.println("Seleccionar Póliza: Registro con número " + FolioPoliza + " seleccionado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la póliza con número " + FolioPoliza + ".");
        }
    }

    @Step("Presionar Botón Modificar")
    private void BotonModificar() {
        try {
            WebElement btnModificar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/input")
            ));
            btnModificar.click();
            System.out.println("Botón Modificar: Se hizo click en el botón Modificar correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Modificar.");
        }
    }

    @Step("Actualizar Campo Concepto")
    private void CampoConcepto() {
        try {
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            String valorActual = campoConcepto.getAttribute("value");
            String nuevoValor = valorActual + " Modificaado";
            campoConcepto.clear();
            campoConcepto.sendKeys(nuevoValor);
            System.out.println("CampoConcepto: Valor actualizado a: " + nuevoValor);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en CampoConcepto.");
        }
    }

    @Step("Presionar Botón Modificar Movimientos")
    private void BotonModificarMovimientos() {
        try {
            WebElement btnModificarMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div/div[1]/div[2]/div/table/tbody/tr/td/input")
            ));
            btnModificarMov.click();
            System.out.println("BotonModificarMovimientos: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en BotonModificarMovimientos.");
        }
    }

    @Step("Actualizar Campo Referencia Movimiento")
    private void CampoReferenciaMov() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[1]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            campoReferencia.clear();
            campoReferencia.sendKeys("movimiento modificado");
            System.out.println("CampoReferenciaMov: Texto 'movimiento modificado' ingresado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en CampoReferenciaMov.");
        }
    }

    @Step("Aceptar Modificación Movimiento")
    private void AceptarModMovimiento() {
        try {
            WebElement btnAceptarModMov = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/div[7]/table/tbody/tr/td/div[4]/div[1]/div/table/tbody/tr/td/a/span/span")
            ));
            btnAceptarModMov.click();
            System.out.println("AceptarModMovimiento: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en AceptarModMovimiento.");
        }
    }

    @Step("Aceptar Modificación Póliza")
    private void AceptarModificacionPoliza() {
        try {
            WebElement btnAceptarModPol = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTAR\"]")
            ));
            btnAceptarModPol.click();
            System.out.println("AceptarModificacionPoliza: Click realizado correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error en AceptarModificacionPoliza.");
        }
    }

    @Step("Presionar Botón Consultar")
    private void BotonConsultar() {
        try {
            WebElement btnConsultar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[4]/div/input")
            ));
            btnConsultar.click();
            System.out.println("Botón Consultar: Se presionó correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Consultar.");
        }
    }

    @Step("Capturar Concepto")
    private void CapturarConcepto() {
        try {
            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")
            ));
            String valorConcepto = campoConcepto.getAttribute("value");
            System.out.println("Capturar Concepto: Valor capturado: " + valorConcepto);
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al capturar el concepto.");
        }
    }

    @Step("Salir de Consultar")
    private void SalirConsultar() {
        try {
            WebElement btnSalir = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_CANCELAR\"]")
            ));
            btnSalir.click();
            System.out.println("Salir Consultar: Se presionó el botón de salir correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de salir en Consultar.");
        }
    }

    @Step("Hacer clic en el botón de impresión")
    public void BotonImprimir() {
        try {
            // Localizar el botón de impresión usando el XPath proporcionado
            WebElement botonImprimir = driver.findElement(
                    By.xpath("//*[@id=\"z_BTN_IMPRIMIR_IMG\"]/span"));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de impresión.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de impresión: " + e.getMessage());
        }
    }

    @Step("Seleccionar el último formato de impresión")
    public void SeleccionarFormato() {
        try {
            // Localizar el menú desplegable de formatos usando el XPath proporcionado
            WebElement comboFormatos = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/div/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select"));

            // Crear un objeto Select para interactuar con el menú desplegable
            Select selectFormato = new Select(comboFormatos);

            // Seleccionar la última opción del menú desplegable
            int ultimaOpcion = selectFormato.getOptions().size() - 1;
            selectFormato.selectByIndex(ultimaOpcion);
            System.out.println("Se seleccionó el último formato de impresión.");

        } catch (Exception e) {
            System.err.println("Error al seleccionar el formato de impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de imprimir")
    public void Imprimir() {
        try {
            // Localizar el botón de imprimir usando el XPath proporcionado
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/table/tbody/tr/td/input"));

            // Hacer clic en el botón
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
            // Localizar el botón para cerrar la vista previa usando el XPath proporcionado
            WebElement botonCerrar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[3]/div/table/tbody/tr/td/a/span"));

            // Hacer clic en el botón
            botonCerrar.click();
            System.out.println("Se hizo clic en el botón para cerrar la vista previa.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón para cerrar la vista previa: " + e.getMessage());
        }
    }

    @Step("Eliminar Póliza")
    private void EliminarPoliza() {
        try {
            WebElement btnEliminar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/input")
            ));
            btnEliminar.click();
            System.out.println("EliminarPoliza: Se presionó el botón de eliminar póliza correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de eliminar póliza.");
        }
    }

    @Step("Aceptar alerta de eliminación")
    private void AceptarAlertaEliminacion() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            System.out.println("AceptarAlertaEliminacion: Alerta aceptada correctamente.");
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar la alerta de eliminación.");
        }
    }



}
