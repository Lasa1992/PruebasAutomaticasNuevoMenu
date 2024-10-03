package Porclases;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionConceptoTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    //Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();


    @BeforeAll
    public static void setup() {
        //System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        fillForm();
        submitForm();
        handleAlert();
    }
    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        handleTipoCambio();
        handleNovedadesScreen();
    }

    @Test
    @Order(3)
    @Description("Ingresar al modulo de facturación.")
    public void ingresarModuloFacturacion() {
        handleImageButton();
        handleSubMenuButton();
    }

    @RepeatedTest(800)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void testFacturacionporConcepto() {
        //Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);
        handleBotonAgregarListado();
        handleAsignaCliente();
        handleCondiciondePago();
        HandleComboMetodoPago();
        HandleComboUsoCFDI();
        HandleMonedas();
        HandleConceptofacturacionAgregar();
        IngresaValorCantidad();
        AsignarCodigoConceptoFacturacion();
        IngresaPrecioUnitario();
        SeleccionarObjetoImpuestos();
        InteractuarConIVASyRetenciones();
        BotonAgregarConcepto();
        AceptarFactura();
        BotonTimbre();
        ValidarPosibleErrorPRODIGIA();
        BotonEnvioCorre();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Llenar el formulario")
    private static void fillForm() {
        WebElement inputEmpresa = driver.findElement(By.id("EDT_EMPRESA"));
        WebElement inputUsuario = driver.findElement(By.id("EDT_USUARIO"));
        WebElement inputContrasena = driver.findElement(By.id("EDT_CONTRASENA"));

        inputEmpresa.sendKeys("KIJ0906199R1");
        inputUsuario.sendKeys("ALEJANDRO");
        inputContrasena.sendKeys("Calidad01.");
    }

    @Step("Enviar el formulario")
    private static void submitForm() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ENTRAR")));
        submitButton.click();
    }

    private static void handleAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró una alerta o ocurrió un error.");
        }
    }

    private static void handleTipoCambio() {
        try {
            WebElement extraField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            extraField.sendKeys("20");
            WebElement extraButton = driver.findElement(By.id("BTN_ACEPTAR"));
            extraButton.click();
        } catch (Exception e) {
            System.out.println("Ventana tipo de Cambio no encontrada.");
        }
    }

    private static void handleNovedadesScreen() {
        try {
            WebElement novedadesScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN")));
            WebElement acceptButton = novedadesScreen.findElement(By.id("z_BTN_ACEPTAR_IMG"));
            acceptButton.click();
        } catch (Exception e) {
            System.out.println("Pantalla de novedades no encontrada.");
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1.JPG')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1.JPG')]")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void handleBotonAgregarListado() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (Exception e) {
            manejarBotonesCancelar();
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón agregar no encontrado o no clickeable.");
            System.out.println("Botón agregar no encontrado o no clickeable.");
        }
    }

    @Step("Asignar El cliente que se le va Facturar")
    private static void handleAsignaCliente() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        try {
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            numeroCliente.click();
            numeroCliente.sendKeys("000001");
            informacionFactura.append("Numero Cliente: 000001 \n");
            numeroCliente.sendKeys(Keys.TAB);
            fluentWait.until(driver -> {
                WebElement field = driver.findElement(By.id("EDT_NUMEROCLIENTE"));
                return !Objects.requireNonNull(field.getAttribute("value")).isEmpty();
            });

            System.out.println("El campo de cliente tiene información.");
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al realizar la acción en el campo 'Número de Cliente'.");

            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
            e.printStackTrace();
        }
    }

    private void handleCondiciondePago() {
        try {
            // Encuentra el primer combo box (select) por ID
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CONDICIONESPAGO")));

            // Elige aleatoriamente entre "Contado" y "Crédito"
            Random random = new Random();
            boolean esContado = random.nextBoolean(); // true para contado, false para crédito

            if (esContado) {
                // Selecciona la opción "Contado" en el primer combo box
                primerComboBox.selectByVisibleText("CONTADO");
                informacionFactura.append("Condicion de Pago: CONTADO \n");
                // Espera a que el segundo combo box se haga visible
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement segundoComboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMETODOSPAGOS")));

                // Encuentra el segundo combo box y abre la lista de opciones
                Select segundoComboBox = new Select(segundoComboBoxElement);

                // Obtiene todas las opciones del segundo combo box
                List<WebElement> opciones = segundoComboBox.getOptions();

                // Verifica que haya más de una opción
                if (opciones.size() > 1) {
                    // Elige una opción aleatoria
                    int indexToSelect = random.nextInt(opciones.size());
                    // Selecciona la opción aleatoria
                    segundoComboBox.selectByIndex(indexToSelect);

                    // Imprime la opción seleccionada
                    System.out.println("Opción seleccionada en segundo combo box: " + opciones.get(indexToSelect).getText());
                } else {
                    System.out.println("El Combo Forma de Pago esta vacio");
                    informacionFactura.append("Condicion de Pago: El Combo Forma de Pago esta vacio \n");
                }
            } else {
                // Selecciona la opción "Crédito" en el primer combo box
                primerComboBox.selectByVisibleText("CREDITO");
                System.out.println("Se seleccionó 'Crédito'. La Forma de pago es POR DEFINIR por default");
                informacionFactura.append("Condicion de Pago: CREDITO \n");
            }
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error: " + e.getMessage());

            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void HandleComboMetodoPago() {
        // Encuentra el combo box por su id
        WebElement comboBoxElement = driver.findElement(By.id("COMBO_METODOPAGO"));

        // Verifica si el combo box está habilitado
        if (comboBoxElement.isEnabled()) {
            // Inicializa el combo box con Selenium Select
            Select comboBox = new Select(comboBoxElement);

            // Obtiene todas las opciones del combo box
            List<WebElement> opciones = comboBox.getOptions();

            // Verifica que haya más de una opción disponible
            if (opciones.size() > 1) {
                Random random = new Random();
                double probabilidad = random.nextDouble(); // Genera un número entre 0.0 y 1.0

                // Si la probabilidad es menor a 0.80, intenta seleccionar "POR DEFINIR"
                if (probabilidad < 0.80) {
                    for (WebElement opcion : opciones) {
                        if (opcion.getText().equalsIgnoreCase("POR DEFINIR")) {
                            comboBox.selectByVisibleText("POR DEFINIR");
                            System.out.println("Opción seleccionada en combo box (Método de Pago): POR DEFINIR");
                            informacionFactura.append("Metodo de Pago: POR DEFINIR \n");
                            //Allure.addAttachment("Metodo de Pago", "POR DEFINIR");
                            return; // Termina aquí si se seleccionó "POR DEFINIR"
                        }
                    }
                }

                // Si no se seleccionó "POR DEFINIR", elige una opción aleatoria
                int indexToSelect = random.nextInt(opciones.size());
                comboBox.selectByIndex(indexToSelect);
                System.out.println("Opción seleccionada en combo box (Método de Pago): " + opciones.get(indexToSelect).getText());
                informacionFactura.append("Metodo de Pago: ").append(opciones.get(indexToSelect).getText()).append("\n");

            } else {
                System.out.println("Error: no hay suficientes opciones disponibles en el Método de pago.");
                informacionFactura.append("Metodo de Pago: Error: no hay suficientes opciones disponibles en el Método de pago. \n");
                //Allure.addAttachment("Metodo de Pago", "Error: no hay suficientes opciones disponibles en el Método de pago.");
            }
        } else {
            // Si el combo box no está habilitado
            System.out.println("El check Permitir seleccionar Método de pago está deshabilitado.");
            informacionFactura.append("Metodo de Pago: El check Permitir seleccionar Método de pago está deshabilitado. \n");
            //Allure.addAttachment("Metodo de Pago","El check Permitir seleccionar Método de pago está deshabilitado.");
        }
    }


    private void HandleComboUsoCFDI() {
        // Encuentra el combo box por su id
        WebElement comboBoxElement = driver.findElement(By.id("COMBO_USOCFDI"));

        // Inicializa el combo box con Selenium Select
        Select comboBox = new Select(comboBoxElement);

        // Obtiene todas las opciones del combo box
        List<WebElement> opciones = comboBox.getOptions();

        // Verifica que haya más de una opción disponible
        if (opciones.size() > 1) {
            Random random = new Random();
            double probabilidad = random.nextDouble(); // Genera un número entre 0.0 y 1.0

            // Si la probabilidad es menor a 0.80, intenta seleccionar "SIN EFECTOS FISCALES"
            if (probabilidad < 0.80) {
                for (WebElement opcion : opciones) {
                    if (opcion.getText().equalsIgnoreCase("SIN EFECTOS FISCALES")) {
                        comboBox.selectByVisibleText("SIN EFECTOS FISCALES");
                        System.out.println("Opción seleccionada en combo (Uso CFDI): SIN EFECTOS FISCALES");
                        informacionFactura.append("Uso CFDI: SIN EFECTOS FISCALES \n");
                        //Allure.addAttachment("Uso CFDI", "Opción seleccionada en combo (Uso CFDI): SIN EFECTOS FISCALES");
                        return; // Termina aquí si se seleccionó "SIN EFECTOS FISCALES"
                    }
                }
            }

            // Si no se seleccionó "SIN EFECTOS FISCALES", elige una opción aleatoria
            int indexToSelect = random.nextInt(opciones.size());
            comboBox.selectByIndex(indexToSelect);
            System.out.println("Opción seleccionada en combo (Uso CFDI): " + opciones.get(indexToSelect).getText());
            informacionFactura.append("Uso CFDI: ").append(opciones.get(indexToSelect).getText()).append("\n");
            //Allure.addAttachment("Uso CFDI", opciones.get(indexToSelect).getText());
        } else {
            System.out.println("El combo Uso de CFDI no tiene información disponible.");
            informacionFactura.append("Uso CFDI: El combo Uso de CFDI no tiene información disponible. \n");
            //Allure.addAttachment("Uso CFDI", "El combo Uso de CFDI no tiene información disponible.");
        }
    }

    private void HandleMonedas() {
        try {
            // Encuentra el primer combo box (select) por ID
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CATMONEDAS")));

            // Define las opciones disponibles
            List<String> opciones = List.of("PESOS", "DÓLARES");

            // Elige aleatoriamente una opción
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));

            // Selecciona la opción en el primer combo box
            primerComboBox.selectByVisibleText(opcionSeleccionada);

            // Imprime la opción seleccionada
            System.out.println("La Moneda es: " + opcionSeleccionada);
            informacionFactura.append("Moneda: ").append(opcionSeleccionada).append("\n\n");
            //Allure.addAttachment("Moneda", opcionSeleccionada);

            //Agrega al reporte de Allure la información de la factura generada.
            Allure.addAttachment("Informacion Factura", informacionFactura.toString());
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error: " + e.getMessage());

            // Maneja cualquier excepción que ocurra
            System.out.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void HandleConceptofacturacionAgregar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Espera explícita hasta que el botón sea clicable
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            botonAgregar.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Agregar: " + e.getMessage());

            System.out.println("Error al presionar el botón Agregar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void IngresaValorCantidad() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement nuevoCampo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));
            nuevoCampo.click();

            Random random = new Random();
            double valorAleatorio = 1.0000 + (99.9999 - 1.0000) * random.nextDouble();

            nuevoCampo.sendKeys(String.format("%.4f", valorAleatorio));
            informacionConcepto.append("Cantidad del Concepto: ").append(valorAleatorio).append("\n");
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la cantidad: " + e.getMessage());

            System.out.println("Error al ingresar la cantidad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void AsignarCodigoConceptoFacturacion() {
        try {
            Thread.sleep(3000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement campoCodigo = driver.findElement(By.id("EDT_CODIGOCONCEPTOFACTURACION"));

            Random random = new Random();
            int valorAleatorio = 1 + random.nextInt(5);
            campoCodigo.clear();
            campoCodigo.sendKeys(String.valueOf(valorAleatorio));
            campoCodigo.sendKeys(Keys.TAB);

            WebElement campoConcepto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CONCEPTOFACTURACION")));
            String valorConcepto = campoConcepto.getAttribute("value");

            assert valorConcepto != null;
            if (valorConcepto.isEmpty() || valorConcepto.equals("El concepto de facturación no está activo, Revisar")) {
                System.out.println("Error: El concepto de facturación no está activo.");
                informacionConcepto.append("Concepto: Error, el concepto de facturación no esta activo.\n");
            } else {
                System.out.println("Concepto número " + valorAleatorio + " asignado correctamente.");
                System.out.println("El concepto de facturación es: " + valorConcepto);

                //Se agrega información del número y nombre del concepto al reporte de allure
                informacionConcepto.append("Numero Concepto: ").append(valorAleatorio).append("\n");
                informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");
            }
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el código de concepto de facturación: " + e.getMessage());
            System.out.println("Error al asignar el código de concepto de facturación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void IngresaPrecioUnitario() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement CampoPrecioUnitario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_PRECIOUNITARIO")));

            Random random = new Random();
            double valorAleatorio = 1 + (1000 - 1) * random.nextDouble();
            CampoPrecioUnitario.sendKeys(Keys.TAB);
            CampoPrecioUnitario.sendKeys(String.format("%.2f", valorAleatorio));
            informacionConcepto.append("Precio Unitario: ").append(valorAleatorio).append("\n");

        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el precio unitario: " + e.getMessage());
            System.out.println("Error al ingresar el precio unitario: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void SeleccionarObjetoImpuestos() {
        try {
            // Localizar el combo box directamente por su ID
            WebElement comboBoxElement = driver.findElement(By.id("COMBO_OBJETOIMP"));
            Select comboBox = new Select(comboBoxElement);

            // Opciones y sus probabilidades
            List<OpcionConProbabilidad> opcionesConProbabilidades = List.of(
                    new OpcionConProbabilidad("NO OBJETO DE IMPUESTO", 0.10),
                    new OpcionConProbabilidad("SI OBJETO DE IMPUESTO", 0.80),
                    new OpcionConProbabilidad("SI OBJETO DE IMPUESTO Y NO OBLIGADO AL DESGLOSE", 0.05),
                    new OpcionConProbabilidad("Sí objeto del impuesto y no causa impuesto", 0.05)
            );

            // Seleccionar una opción basada en probabilidades
            String opcionSeleccionada = seleccionarOpcionAleatoria(opcionesConProbabilidades);

            // Seleccionar la opción basándose en el texto
            comboBox.selectByVisibleText(opcionSeleccionada);

            // Imprimir el texto de la opción seleccionada
            System.out.println("Opción seleccionada: " + opcionSeleccionada);

            //Enviá la opción elegida al reporte de allure.
            informacionConcepto.append("Tipo Objeto Impuesto: ").append(opcionSeleccionada).append("\n");
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una opción del combo box: " + e.getMessage());

            // Manejar cualquier excepción que ocurra
            System.out.println("Error al seleccionar una opción del combo box: " + e.getMessage());
            e.printStackTrace();
            //Se manda llamar funcion para que cierre la ventana de concepto y facturacion. Regresa al listado de facturacion.
            manejarBotonesCancelar();
        }
    }

    private String seleccionarOpcionAleatoria(List<OpcionConProbabilidad> opcionesConProbabilidades) {
        // Crear una lista de opciones basadas en las probabilidades
        List<String> listaOpciones = new ArrayList<>();
        Random random = new Random();

        // Llenar la lista con las opciones basadas en sus probabilidades
        for (OpcionConProbabilidad opcion : opcionesConProbabilidades) {
            int cantidad = (int) (opcion.probabilidad * 100); // Convertir la probabilidad en una cantidad de apariciones
            for (int i = 0; i < cantidad; i++) {
                listaOpciones.add(opcion.opcion);
            }
        }

        // Seleccionar un elemento aleatorio de la lista
        return listaOpciones.get(random.nextInt(listaOpciones.size()));
    }

    // Clase interna para representar una opción con su probabilidad
    private static class OpcionConProbabilidad {
        String opcion;
        double probabilidad;

        OpcionConProbabilidad(String opcion, double probabilidad) {
            this.opcion = opcion;
            this.probabilidad = probabilidad;
        }
    }


    private void InteractuarConIVASyRetenciones() {
        try {
            // Localizar el primer checkbox
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement checkboxTrasladaIVA = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("CBOX_TRASLADAIVA_1")));

            // Si el elemento es visible, validar si es clickeable
            if (checkboxTrasladaIVA != null && checkboxTrasladaIVA.isDisplayed()) {
                try {
                    // Esperar a que el elemento sea clickeable
                    System.out.println("El elemento es visible y clickeable.");
                } catch (TimeoutException e) {
                    System.out.println("El elemento es visible, pero no es clickeable.");
                }
            } else {
                System.out.println("El elemento no es visible.");
            }

            // Verificar el estado del primer checkbox
            assert checkboxTrasladaIVA != null;
            if (checkboxTrasladaIVA.isSelected()) {
                // Si el primer checkbox está activado
                System.out.println("Checkbox 'Traslada IVA' está activado.");

                //Agrega al reporte de allure si se tiene el check activo en 'traslada iva' o no en una sección aparte.
                informacionConcepto.append("CHECKBOX 'TRASLADA IVA' ESTA ACTIVADO \n");

                // Activar el segundo checkbox si no está activado
                WebElement checkboxRetieneIVA = driver.findElement(By.id("CBOX_RETIENEIVA_1"));
                if (!checkboxRetieneIVA.isSelected()) {
                    checkboxRetieneIVA.click();
                    System.out.println("Checkbox 'Retiene IVA' activado.");

                    //Agrega al reporte de allure si se tiene el check activo en 'retiene iva' o no en una sección aparte.
                    informacionConcepto.append("CHECKBOX 'RETIENE IVA' ACTIVADO \n");
                }

                // Localizar el combo box COMBO_CATIMPUESTOSRETENCION
                WebElement comboBoxRetencion = driver.findElement(By.id("COMBO_CATIMPUESTOSRETENCION"));
                Select selectRetencion = new Select(comboBoxRetencion);

                // Verificar que el combo box esté habilitado antes de intentar seleccionar una opción
                if (comboBoxRetencion.isEnabled()) {
                    // Obtener todas las opciones disponibles del combo box de retención
                    List<WebElement> opcionesRetencion = selectRetencion.getOptions();
                    System.out.println("Opciones disponibles en 'Retención IVA':");

                    // Imprimir las opciones disponibles
                    for (WebElement opcion : opcionesRetencion) {
                        System.out.println(opcion.getText());
                    }

                    // Seleccionar una opción aleatoria del combo box de retención
                    Random random = new Random();
                    String textoSeleccionadoRetencion = opcionesRetencion.get(random.nextInt(opcionesRetencion.size())).getText();
                    selectRetencion.selectByVisibleText(textoSeleccionadoRetencion);
                    System.out.println("Opción seleccionada en 'COMBO_CATIMPUESTOSRETENCION': " + textoSeleccionadoRetencion);

                    informacionConcepto.append("Opcion de Impuesto seleccionado: Retencion ").append(textoSeleccionadoRetencion).append(" \n\n");
                } else {
                    System.out.println("Error: El combo 'Retención IVA' está deshabilitado.");

                    informacionConcepto.append("Opcion de Impuesto seleccionado: ERROR el combo 'Retencion IVA' esta deshabilitado \n\n");
                }

            } else {
                // Si el primer checkbox está desactivado
                System.out.println("Checkbox 'Traslada IVA' está desactivado.");

                informacionConcepto.append("Opcion de Impuesto seleccionada: Checkbox 'Traslada IVA' está desactivado.\n\n");

                // Asegurarse de que el segundo checkbox esté desactivado
                WebElement checkboxRetieneIVA = driver.findElement(By.id("CBOX_RETIENEIVA_1"));
                if (checkboxRetieneIVA.isSelected()) {
                    checkboxRetieneIVA.click();
                    System.out.println("Checkbox 'Retiene IVA' desactivado.");

                    informacionConcepto.append("Opcion de Impuesto seleccionada: Checkbox 'Retiene IVA' desactivado.\n\n");
                }

                // Localizar el combo box COMBO_CATIMPUESTOSTRASLADO
                WebElement comboBoxTraslado = driver.findElement(By.id("COMBO_CATIMPUESTOSTRASLADO"));
                Select selectTraslado = new Select(comboBoxTraslado);

                // Verificar que el combo box esté habilitado antes de intentar seleccionar una opción
                if (comboBoxTraslado.isEnabled()) {
                    // Obtener todas las opciones disponibles del combo box de traslado
                    List<WebElement> opcionesTraslado = selectTraslado.getOptions();
                    System.out.println("Opciones disponibles en 'Traslado IVA':");

                    // Imprimir las opciones disponibles
                    for (WebElement opcion : opcionesTraslado) {
                        System.out.println(opcion.getText());
                    }

                    // Seleccionar una opción aleatoria del combo box de traslado
                    Random random = new Random();
                    String textoSeleccionadoTraslado = opcionesTraslado.get(random.nextInt(opcionesTraslado.size())).getText();
                    selectTraslado.selectByVisibleText(textoSeleccionadoTraslado);
                    System.out.println("Opción seleccionada en 'COMBO_CATIMPUESTOSTRASLADO': " + textoSeleccionadoTraslado);

                    informacionConcepto.append("Opcion de Impuesto seleccionada: Traslado ").append(textoSeleccionadoTraslado).append("\n");
                } else {
                    System.out.println("Error: El combo 'Traslado IVA' está deshabilitado.");
                }
            }
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Error al interactuar con los checkboxes y combos: " + e.getMessage());
            // Manejar cualquier excepción que ocurra
            System.out.println("Error al interactuar con los checkboxes y combos: " + e.getMessage());
            e.printStackTrace();
            //Se manda llamar funcion para que cierre la ventana de concepto y facturacion. Regresa al listado de facturacion.
            manejarBotonesCancelar();
        }
    }


    private void BotonAgregarConcepto() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTARDETALLE"));

            // Hacer clic en el botón "Agregar"
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

            // Verificar si el mensaje de confirmación está presente
            try {
                WebElement botonConfirmar = driver.findElement(By.id("BTN_YES"));
                if (botonConfirmar.isDisplayed()) {
                    System.out.println("Se ha mostrado un mensaje de confirmación.");

                    // Hacer clic en el botón "Sí" para confirmar
                    botonConfirmar.click();
                    System.out.println("Se ha hecho clic en el botón 'Sí' para confirmar.");
                }
            } catch (NoSuchElementException e) {
                // El mensaje de confirmación no está presente
                System.out.println("No se ha mostrado un mensaje de confirmación.");
            }

        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, null);
            // Manejar cualquier excepción que ocurra
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
        Allure.addAttachment("Informacion del Concepto", informacionConcepto.toString());
    }

    private void AceptarFactura() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));

            // Hacer clic en el botón
            botonAgregar.click();

            // Imprimir un mensaje para confirmar que se ha hecho clic en el botón
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, null);
            // Manejar cualquier excepción que ocurra
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void BotonTimbre() {
        try {
            // Esperar hasta que el mensaje de validación sea visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement mensajeValidacion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwBTN_YES")));
            if (mensajeValidacion.isEnabled()) {
                System.out.println("El botón de YES está habilitado.");
            }

            // Localizar el botón "Sí" dentro del mensaje de validación
            WebElement botonYes = driver.findElement(By.id("BTN_YES"));

            // Hacer clic en el botón "Sí"
            botonYes.click();
            System.out.println("Se ha hecho clic en el botón 'Sí' del mensaje de validación.");

        } catch (TimeoutException e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e,"No se detectó el mensaje de validación dentro del tiempo esperado.");
            // Si el mensaje de validación no aparece en el tiempo esperado
            System.out.println("No se detectó el mensaje de validación dentro del tiempo esperado.");
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e,"Ocurrió un problema al timbrar");
            // Manejar cualquier otra excepción que ocurra
            System.out.println("Se ha producido un error al aceptar el mensaje de validación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ValidarPosibleErrorPRODIGIA() {
        try {
            // Esperar hasta que el mensaje de validación sea visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que el mensaje se muestre después de presionar el botón anterior
            WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dwwCELL_POPUPMAIN"))); // Ajusta el ID según sea necesario

            // Obtener el texto del mensaje
            String mensajeTexto = mensaje.getText();
            System.out.println("Mensaje mostrado: " + mensajeTexto);

            // Verificar si el mensaje contiene la palabra "PRODIGIA"
            if (mensajeTexto.contains("PRODIGIA")) {
                // Guardar el mensaje si contiene "PRODIGIA"
                System.out.println("El mensaje contiene 'PRODIGIA': " + mensajeTexto);
                //informacionTimbrado.append("PRODIGIA - Mensaje obtenido: " + mensajeTexto + "\n\n");

                // Localizar y hacer clic en el botón correspondiente
                WebElement botonContinuar = driver.findElement(By.id("dwwBTN_OK")); // Ajusta el ID según sea necesario
                botonContinuar.click();
                System.out.println("Se ha hecho clic en el botón 'OK'.");

            } else {
                // Si el mensaje no contiene "PRODIGIA", aceptar el mensaje y continuar
                WebElement botonAceptar = driver.findElement(By.id("BTN_OK")); // Ajusta el ID según sea necesario
                botonAceptar.click();
                System.out.println("Se ha hecho clic en el botón 'Aceptar'.");

                informacionTimbrado.append("Mensaje Obtenido al Timbrar: ").append(mensajeTexto).append("\n\n");
                // Continuar con el siguiente paso
                List<WebElement> botonContinuarList = driver.findElements(By.id("BOTON_CONTINUAR"));
                if (!botonContinuarList.isEmpty()) {
                    // Si la lista no está vacía, significa que el botón está presente
                    WebElement botonContinuar = botonContinuarList.getFirst();  // Obtenemos el primer (y único) elemento de la lista
                    botonContinuar.click();
                    System.out.println("Se ha hecho clic en el botón 'Continuar'.");
                } else {
                    System.out.println("El botón 'Continuar' no está presente en la página.");
                }
            }

        } catch (TimeoutException e) {
            // Si el mensaje no aparece en el tiempo esperado
            System.out.println("No se detectó el mensaje dentro del tiempo esperado.");
            //UtilidadesAllure.manejoError(driver, e,"No se detectó el mensaje dentro del tiempo esperado");
        } catch (Exception e) {
            // Manejar cualquier otra excepción que ocurra
            System.out.println("Se ha producido un error al validar el mensaje: " + e.getMessage());
            e.printStackTrace();
            UtilidadesAllure.manejoError(driver, e, null);
        }

        Allure.addAttachment("Timbrado", informacionTimbrado.toString());
    }

    private void BotonEnvioCorre() {
        try {
            // Esperar hasta que el mensaje con las opciones sea visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Verificar si el elemento con ID "tzSTC_DESCRIPTION" está presente y visible
            List<WebElement> elementosDescripcion = driver.findElements(By.id("tzSTC_DESCRIPTION"));

            if (!elementosDescripcion.isEmpty() && elementosDescripcion.getFirst().isDisplayed()) {
                WebElement mensajeOpciones = elementosDescripcion.getFirst();

                if (mensajeOpciones.isEnabled()) {
                    System.out.println("El botón de Descripcion está habilitado.");

                    // Localizar los botones de opciones por su ID
                    WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                    WebElement botonNoEnviar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));

                    // Lista de botones disponibles
                    List<WebElement> botonesOpciones = List.of(botonEnviar, botonNoEnviar);

                    // Seleccionar una opción aleatoria
                    Random random = new Random();
                    WebElement botonSeleccionado = botonesOpciones.get(random.nextInt(botonesOpciones.size()));

                    // Volver a verificar que el botón esté presente y clickeable antes de interactuar
                    wait.until(ExpectedConditions.elementToBeClickable(botonSeleccionado));
                    // Imprimir en la consola el valor del botón seleccionado
                    System.out.println("Se ha hecho clic en el botón de enviar correo: " + botonSeleccionado.getText() + " (ID: " + botonSeleccionado.getAttribute("id") + ")");
                    Allure.addAttachment("ENVIO CORREO", botonSeleccionado.getText());
                    // Hacer clic en el botón seleccionado
                    botonSeleccionado.click();
                } else {
                    System.out.println("El botón de Descripcion no está habilitado.");
                }
            } else {
                System.out.println("El elemento 'tzSTC_DESCRIPTION' no está visible.");
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error al seleccionar la opción aleatoria: " + e.getMessage());
            // Manejar cualquier otra excepción que ocurra
            System.out.println("Se ha producido un error al seleccionar la opción aleatoria: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void manejarBotonesCancelar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            boolean botonesPresentes = true; // Controla si algún botón sigue presente

            while (botonesPresentes) {
                botonesPresentes = false; // Suponemos que no hay botones al inicio de cada iteración

                // Verificar si el botón "BTN_IMPRIMIR" está presente
                List<WebElement> btnImprimir = driver.findElements(By.id("BTN_IMPRIMIR"));
                if (!btnImprimir.isEmpty() && btnImprimir.getFirst().isDisplayed()) {
                    System.out.println("El botón 'Imprimir' está presente. Se detiene la ejecución.");
                    break; // Salir del ciclo si el botón "Imprimir" está presente
                }

                // Buscar el botón "BTN_CANCELARDETALLE"
                List<WebElement> btnCancelarDetalle = driver.findElements(By.id("BTN_CANCELARDETALLE"));
                if (!btnCancelarDetalle.isEmpty() && btnCancelarDetalle.getFirst().isDisplayed()) {
                    // Si el botón "BTN_CANCELARDETALLE" está presente y visible, hacer clic en él
                    WebElement botonCancelarDetalle = btnCancelarDetalle.getFirst();
                    wait.until(ExpectedConditions.elementToBeClickable(botonCancelarDetalle));
                    botonCancelarDetalle.click();
                    System.out.println("Se hizo clic en el botón 'Cancelar Detalle'.");

                    // Después de hacer clic, asumimos que puede aparecer el botón "BTN_CANCELAR"
                    botonesPresentes = true; // Mantiene el ciclo activo
                }

                // Buscar el botón "BTN_CANCELAR" si no está activo el otro
                List<WebElement> btnCancelar = driver.findElements(By.id("BTN_CANCELAR"));
                if (!btnCancelar.isEmpty() && btnCancelar.getFirst().isDisplayed()) {
                    // Si el botón "BTN_CANCELAR" está presente y visible, hacer clic en él
                    WebElement botonCancelar = btnCancelar.getFirst();
                    wait.until(ExpectedConditions.elementToBeClickable(botonCancelar));
                    botonCancelar.click();
                    System.out.println("Se hizo clic en el botón 'Cancelar'.");

                    // Después de hacer clic, asumimos que puede aparecer el botón "BTN_CANCELARDETALLE"
                    botonesPresentes = true; // Mantiene el ciclo activo
                }

                // Si ambos botones no están presentes, el ciclo terminará
                if (!botonesPresentes) {
                    System.out.println("No se encontraron más botones 'Cancelar Detalle' o 'Cancelar'.");
                }
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error al intentar cancelar para salir de las ventanas: " + e.getMessage());
            System.out.println("Ocurrió un error al intentar manejar los botones de cancelación: " + e.getMessage());
            e.printStackTrace();
        }
    }

}








/*
    @Step("Cancelar factura")
    public static void handleBotonCancelarFactura() {
        try {
            // Espera explícita de 15 segundos antes de intentar encontrar el botón
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // Espera hasta que el botón de cancelar sea clickeable
            WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_CANCELAR")));
            cancelButton.click();
            // Espera opcional para asegurarse de que el botón ya no sea clickeable (opcional)
            wait.until(ExpectedConditions.stalenessOf(cancelButton));
        } catch (Exception e) {
            System.out.println("Botón de cancelar factura no encontrado o no clickeable.");
            e.printStackTrace();
        }
    }
}
*/