package CuentasPorPagar;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Reposiciones {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String CODIGO_PROVEEDOR = Variables.PROVEEDOR;
    private static final String CODIGO_BENEFICIARIO = Variables.BENEFICIARIO;

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
        InicioSesion.handleTipoCambio();       // ✅ Sin parámetros
        InicioSesion.handleNovedadesScreen();  // ✅ Sin parámetros
    }

    @RepeatedTest(1)
    @Order(2)
    @Description("Se genera una Reposición")
    public void Reposicion() {

        //Crear Reposición Agregando manualmente un Pasivo
        BotonCuentasPorPagar();
        BotonReposiciones();
        AgregarReposicion();
        CapturarConcepto();
        SeleccionarBeneficiario();
        CodigoBeneficiario();
        MonedaReposicion();
        BotonAgregar();
        AgregarProveedorPasivo();
        NoDocumento();
        SubtotalPasivo();
        FechaRecibido();
        AceptarPasivo();
        AceptarResposicion();

        //Crear Reposición Seleccionando Pasivo
        AgregarReposicion();
        CapturarConcepto();
        SeleccionarBeneficiario();
        CodigoBeneficiario();
        MonedaReposicion();
        BotonIntegrarPasivo();
        seleccionarCheckboxPasivo();
        AceptarIntegrarPasivo();
        AceptarResposicion();

        //Pagar Reposición
        SeleccionarReposicion();
        PagarReposicion();
        CuentaBancaria();
        ChequeTransferencia();
        BotonAceptarPago();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Reposiciones...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Hacer clic en el botón de Cuentas por Pagar")
    public void BotonCuentasPorPagar() {
        try {
            // Esperar a que el botón de Cuentas por Pagar esté presente y clickeable
            WebElement botonCuentasPorPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"sidebar\"]/div/ul/li[8]")));

            // Hacer clic en el botón
            botonCuentasPorPagar.click();
            System.out.println("Se hizo clic en el botón de Cuentas por Pagar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Cuentas por Pagar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de Reposiciones ")
    public void BotonReposiciones() {
        try {
            // Esperar a que el botón de Reposiciones esté presente y clickeable
            WebElement botonPagos = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"submenuCUENTASPORPAGAR\"]/li[5]/a")));

            // Hacer clic en el botón
            botonPagos.click();
            System.out.println("Se hizo clic en el botón de Reposiciones.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Reposiciones: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Registrar")
    public void AgregarReposicion(){
        try{
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            //Esperar a que el botón Registrar este visible
            WebElement BotonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath
                    ("//*[@id=\"BTN_REGISTRAR\"]")));

            //Hacer clic en botón Registrar
            BotonRegistrar.click();
            System.out.println("Se hizo clic en botón Registrar.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en el botón Registrar: " + e.getMessage());
        }
    }

    @Step("Capturar Concepto")
    public void CapturarConcepto(){
        try{
            //Esperar a que el campo concepto sea visible
            WebElement concepto = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"EDT_CONCEPTO\"]")));
            //Capturar Concepto
            concepto.sendKeys("Reposición PA");
            System.out.println("Se ingreso Concepto.");
        }catch (Exception e){
            System.out.println("Error al capturar concepto: " + e.getMessage());
        }
    }

    @Step("Seleccionar Beneficiario en combo")
    private static void SeleccionarBeneficiario() {
        try {
            By localizarCampo = By.xpath("//*[@id=\"COMBO_TIPOBENEFICIARIO\"]");

            // Esperar que el combo esté visible
            WebElement comboBeneficiario = wait.until(ExpectedConditions.elementToBeClickable(localizarCampo));
            // Crear el objeto Select y seleccionar por texto visible
            Select selectBeneficiario = new Select(comboBeneficiario);
            selectBeneficiario.selectByVisibleText("BENEFICIARIO");

            System.out.println("Se seleccionó la opción 'BENEFICIARIO' en el combo.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'BENEFICIARIO' en el combo.");
        }
    }

    @Step("Ingresar el número 18 en el campo Código de Beneficiario")
    public void CodigoBeneficiario() {
        try {
            WebElement inputCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_NUMEROBENEFICIARIO\"]")));
            inputCodigo.clear();
            inputCodigo.sendKeys(CODIGO_BENEFICIARIO);
            System.out.println("Se ingresó el número " + CODIGO_BENEFICIARIO + " en el campo Código de Beneficiario.");
        } catch (Exception e) {
            System.err.println("Error en CódigoBeneficiario: " + e.getMessage());
        }
    }

    @Step("Seleccionar entre PESOS y DÓLARES en el campo Moneda")
    public void MonedaReposicion() {
        try {
            WebElement selectMoneda = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"COMBO_CATMONEDAS\"]")));

            Select select = new Select(selectMoneda);
            List<WebElement> opciones = select.getOptions();
            int indiceAleatorio = new Random().nextInt(opciones.size());
            select.selectByIndex(indiceAleatorio);
            System.out.println("Se seleccionó la moneda: " + opciones.get(indiceAleatorio).getText());
        } catch (Exception e) {
            System.err.println("Error al seleccionar moneda: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Agregar")
    public void BotonAgregar(){
        try{
            //Esperar a que el botón Agregar sea visible
            WebElement botonagregar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_AGREGAR\"]")));
            //Hacer clic en el botón Agregar
            botonagregar.click();
            System.out.println("Se hizo clic en el botón Agregar.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en el botón Agregar: " + e.getMessage());
        }
    }

    @Step("Ingresar Proveedor 1 al agregar pasivo")
    public void AgregarProveedorPasivo(){
        try {
            //Esperar a que campo Proveedor en pasivo sea visible
            WebElement ProveedorPasivo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"EDT_NUMEROPROVEEDORPASIVO\"]")));
            //Ingresar proveedor 1
            ProveedorPasivo.sendKeys(CODIGO_PROVEEDOR);
            System.out.println("Se ingresó el número " + CODIGO_PROVEEDOR + " en el campo Código de Proveedor.");

        }catch (Exception e){
            System.out.println("Error al ingresar Proveedor: " + e.getMessage());
        }
    }

    @Step("Ingresar 'RE' en el primer campo de NoDocumento, copiar el folio al segundo campo y concatenarlos correctamente")
    public void NoDocumento() {
        try {
            // **Localizar y escribir 'RE' en el primer campo de NoDocumento**
            WebElement inputPrimerCampo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_SERIEDOCUMENTO\"]")));
            inputPrimerCampo.clear();
            inputPrimerCampo.sendKeys("RE");
            System.out.println("Se ingresó 'RE' en el primer campo de NoDocumento.");

            // **Localizar el campo del folio y copiar su valor**
            WebElement inputFolio = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_FOLIOREPOSICION\"]")));
            String folioCopiado = inputFolio.getAttribute("value").trim(); // Obtener y limpiar el valor del folio
            System.out.println("Se copió el folio: " + folioCopiado);

            // **Localizar el segundo campo de NoDocumento y pegar el folio**
            WebElement inputSegundoCampo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_FOLIODOCUMENTO\"]")));
            inputSegundoCampo.clear();
            inputSegundoCampo.sendKeys(folioCopiado);
            System.out.println("Se pegó el folio en el segundo campo de NoDocumento.");

        } catch (Exception e) {
            System.err.println("Error en NoDocumento: " + e.getMessage());
        }
    }

    @Step("Ingresar un valor aleatorio entre 99 y 999.99 en el campo Subtotal")
    public void SubtotalPasivo() {
        try {
            WebElement inputSubtotal = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_IMPORTE\"]")));

            double valorAleatorio = 99 + (new Random().nextDouble() * 900);
            String valorTexto = String.format("%.2f", valorAleatorio);
            inputSubtotal.clear();
            inputSubtotal.sendKeys(valorTexto);
            System.out.println("Se ingresó el valor: " + valorTexto + " en el campo Subtotal.");
        } catch (Exception e) {
            System.err.println("Error en SubtotalPasivo: " + e.getMessage());
        }
    }

    public void FechaRecibido(){
        try{
            // **Localizar el campo fehca y copiar su valor**
           WebElement inputFecha = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_FECHAPASIVO\"]")));
            String fechaCopiada = inputFecha.getAttribute("value").trim(); // Obtener y limpiar el valor de la fecha
            System.out.println("Se copió la fecha: " + fechaCopiada);

            // **Localizar el campo Fecha Recibido y pegar valores de campo Fecha
            WebElement fecharecibida = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_FECHARECIBIDO\"]")));
            fecharecibida.click();
            fecharecibida.sendKeys(fechaCopiada);
            System.out.println("Se pego información en campo Fecha Recibida: " + fechaCopiada);

        }catch (Exception e){
            System.out.println("Error al ingresar Fecha Recibido: " + e.getMessage());
        }

    }

    @Step("Aceptar el formulario")
    public void AceptarPasivo() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"BTN_ACEPTARDETALLE\"]")));
            wait.until(ExpectedConditions.elementToBeClickable(botonAceptar));
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar Pasivo.");
        } catch (Exception e) {
            System.err.println("Error en Aceptar Pasivo: " + e.getMessage());
        }
    }

    @Step("Aceptar Reposicion")
    public void AceptarResposicion() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"BTN_ACEPTAR\"]")));
            wait.until(ExpectedConditions.elementToBeClickable(botonAceptar));
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar Reposición.");
        } catch (Exception e) {
            System.err.println("Error en Aceptar Reposición: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Integrar Pasivos")
    public void BotonIntegrarPasivo(){
        try{
            //Esperar a que el botón Integrar Pasivos sea visible
            WebElement botonagregar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_INTEGRARPASIVOS\"]")));
            //Hacer clic en el botón Integrar Pasivos
            botonagregar.click();
            System.out.println("Se hizo clic en el botón Integrar Pasivos.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en el botón Integrar Pasivos: " + e.getMessage());
        }
    }

    @Step("Seleccionar pasivo de listado")
    public void seleccionarCheckboxPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROPASIVOSABIERTOS_TITRES_RECH_2\"]")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys("PA");
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento.");
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_NUMERODOCUMENTO1')]]")));
            System.out.println("Se encontró la fila del documento.");

            // **Buscar el checkbox dentro de la fila seleccionada**
            WebElement checkBox = filaSeleccionada.findElement(By.xpath(".//td[contains(@class, 'wbcolCOL_MARCAR')]//input[@type='checkbox']"));

            // **Imprimir el ID del checkbox encontrado**
            String checkBoxId = checkBox.getAttribute("id");
            System.out.println("Checkbox encontrado con ID: " + checkBoxId);

            // **Si el checkbox no es visible, hacer scroll hasta él**
            if (!checkBox.isDisplayed()) {
                System.out.println("El checkbox no es visible, desplazándose hacia él...");
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", checkBox);
                Thread.sleep(500);
            }

            // **Si el checkbox está deshabilitado, habilitarlo**
            if (!checkBox.isEnabled()) {
                System.out.println("El checkbox está deshabilitado, intentando habilitarlo...");
                js.executeScript("arguments[0].removeAttribute('disabled');", checkBox);
            }

            // **Verificar si ya está marcado antes de hacer clic**
            if (!checkBox.isSelected()) {
                try {
                    checkBox.click();
                    System.out.println("Se marcó el checkbox con `click()` estándar.");
                } catch (Exception e) {
                    System.out.println("`click()` falló, probando con JavaScript...");
                    js.executeScript("arguments[0].click();", checkBox);
                    System.out.println("Se marcó el checkbox con `JavaScriptExecutor`.");
                }
            } else {
                System.out.println("El checkbox ya estaba marcado.");
            }

            Thread.sleep(2000);

        } catch (NoSuchElementException e) {
            System.err.println("Elemento no encontrado: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    @Step("Aceptar al seleccionar pasivos")
    public void AceptarIntegrarPasivo() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"BTN_ACEPTAR2\"]")));
            wait.until(ExpectedConditions.elementToBeClickable(botonAceptar));
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar Pasivo.");
        } catch (Exception e) {
            System.err.println("Error en Aceptar Pasivo: " + e.getMessage());
        }
    }

    @Step("Seleccionar la ultima Reposición del listado")
    private static void SeleccionarReposicion() {
        try {
            Thread.sleep(3000); // Espera por carga visual (considera mejorar con esperas explícitas si es constante)

            // Esperar a que la tabla esté visible
            WebElement tabla = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TABLE_ProReposiciones")));

            // Obtener todas las filas de la tabla, excepto encabezados (puedes ajustar el XPath si tu tabla usa <thead>)
            List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr[td]")); // Solo tr que tienen celdas

            if (filas.isEmpty()) {
                throw new NoSuchElementException("No hay Reposiciones listados en la tabla.");
            }

            // Tomar la última fila con contenido
            WebElement ultimaFila = filas.get(filas.size() - 1);

            try {
                ultimaFila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ultimaFila);
            }
            Thread.sleep(5000); // Espera por carga visual (considera mejorar con esperas explícitas si es constante)
            System.out.println("✅ Última Reposición seleccionada correctamente.");

        } catch (NoSuchElementException e) {
            System.out.println("❌ ERROR: No se encontró Reposición en la tabla.");
            UtilidadesAllure.manejoError(driver, e, "No se encontró ningúna Reposición.");
        } catch (TimeoutException e) {
            System.out.println("⏳ ERROR: La tabla de Reposiciones no cargó a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "Timeout al cargar la tabla de Reposiciones.");
        } catch (Exception e) {
            System.out.println("❌ ERROR: Ocurrió un problema al seleccionar la Reposición.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la Reposición.");
        }
    }

    @Step("Hacer clic en el botón Pagar")
    public void PagarReposicion(){
        try{
            // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            //Esperar a que el botón Pagar este visible
            WebElement BotonPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath
                    ("//*[@id=\"BTN_GENERARMOVIMIENTO\"]")));

            //Hacer clic en botón Registrar
            BotonPagar.click();
            System.out.println("Se hizo clic en botón Pagar.");

        }catch (Exception e){
            System.out.println("Error al hacer clic en el botón Pagar: " + e.getMessage());
        }
    }

    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void CuentaBancaria() {
        try {
            // Localizar el campo de selección de cuenta bancaria
            WebElement selectCuenta = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"COMBO_CATCUENTASBANCARIAS\"]")));

            // Crear objeto Select
            Select select = new Select(selectCuenta);

            // Obtener todas las opciones disponibles
            List<WebElement> opciones = select.getOptions();

            if (opciones.size() > 1) {
                // Elegir aleatoriamente una opción (sin contar la primera si es "Seleccionar")
                int indiceAleatorio = new Random().nextInt(opciones.size() - 1) + 1;
                select.selectByIndex(indiceAleatorio);
                System.out.println("Se seleccionó la cuenta bancaria: " + opciones.get(indiceAleatorio).getText());
            } else {
                System.err.println("No hay cuentas bancarias disponibles para seleccionar.");
            }

            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error en CuentaBancaria: " + e.getMessage());
        }
    }

    @Step("Seleccionar aleatoriamente entre Cheque o Transferencia")
    public void ChequeTransferencia() {
        try {
            // Localizar ambos inputs (Cheque y Transferencia)
            WebElement opcionCheque = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"RADIO_TIPOMOVIMIENTO_1\"]")));

            WebElement opcionTransferencia = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"RADIO_TIPOMOVIMIENTO_2\"]")));

            // Crear una lista con ambas opciones
            WebElement[] opciones = {opcionCheque, opcionTransferencia};

            // Elegir aleatoriamente una opción
            int indiceAleatorio = new Random().nextInt(opciones.length);
            WebElement opcionSeleccionada = opciones[indiceAleatorio];

            // Hacer clic en la opción seleccionada
            opcionSeleccionada.click();
            System.out.println("Se seleccionó la opción de pago: " + (indiceAleatorio == 0 ? "Cheque" : "Transferencia"));

            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("Error en ChequeTransferencia: " + e.getMessage());
        }
    }

    public void BotonAceptarPago(){
        try{
            WebElement AceptarPago = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_ACEPTAR\"]")));
            AceptarPago.click();
            System.out.println("Se hizo clic en el botón Aceptar");

        }catch (Exception e){
            System.out.println("Error al dar clic en botón Aceptar");
        }
    }


}
