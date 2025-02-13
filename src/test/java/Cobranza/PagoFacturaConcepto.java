package Cobranza;
import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;


import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoFacturaConcepto {

    private static WebDriver driver;
    private static WebDriverWait wait;
    //Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();
    private static String FolioFactura;
    private static double MontoaPAGAR;


    @BeforeAll
    public static void setup() {
        //System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }


    @RepeatedTest(3)
    @Order(3)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionporConcepto() {
        // Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);


        handleImageButton(); // Abre el módulo de Facturación
        handleSubMenuButton(); // Abre el submenú de Facturas por Concepto
        BotonAgregarListado(); // Agrega un nuevo concepto
        NumeroFactura();  // Obtiene el número de factura
        AsignarCliente(); // Asigna el cliente
        MonedaFactura(); // Selecciona la moneda de la factura
        ConceptofacturacionAgregar(); // Abre el formulario de facturación de concepto
        IngresaValorCantidad(); // Ingresa la cantidad
        AsignarCodigoConceptoFacturacion(); // Aquí le pasas el código único
        IngresaPrecioUnitario(); // Ingresa el precio unitario
        BotonAgregarConcepto(); // Agrega el concepto
        AceptarFactura(); // Acepta la factura
        BotonTimbre(); // Timbrar la factura
        ValidarYEnviarCorreo();// Validar posibles errores
        BotonPoliza(); // Aceptar la póliza
        BotonImpresion(); // Regresar a la pantalla principal


        //Bloque donde se controla el pago de la factura
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonPagosAbonos(); // Seleccion Submodulo pagos/abonos.
        BotonRegistrarPagoAbono(); // Da clic en el boton para agregar pago/abono.
        IntroducirFechaActual(); // Introduce la fecha actual en el campo de fecha.
        CodigoClientPago(); // Introduce el código del cliente para el pago.
        SeleccionarCuentaBancariaAleatoria(); // Selecciona una cuenta bancaria aleatoria para el pago.
        BusquedaFacturaPagar(); // Busca la factura a pagar.
        SeleccionFactura(); // Selecciona la factura a pagar.
        ValidarConversion(); // Validar la conversión de la moneda.
        IntroducirMontoPago(); // Introduce el monto a pagar.
        IntroducirReferencia(); // Introduce una referencia para el pago.
        AceptarPagoAbono(); // Acepta el pago/abono.
        TimbrePago(); // Timbra el pago/abono.
        EnvioCorreoPago(); // Envía el correo de confirmación del pago.
        AceptarPolizaPago(); // Acepta la póliza del pago.
        IntroducirFechaActual();
        SalirVentanaPago(); // Sale de la ventana de pago/abono.

    }


    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1')]")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void BotonAgregarListado() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (Exception e) {
            //manejarBotonesCancelar();
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón agregar no encontrado o no clickeable.");
            System.out.println("Botón agregar no encontrado o no clickeable.");
        }
    }

    @Step("Manejar Número de Factura")
    private void NumeroFactura() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            FolioFactura = folioField.getAttribute("value"); // Guarda el valor en la variable
            System.out.println("El numero de factura es " + FolioFactura);

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Factura");
        }
    }

    @Step("Asignar El cliente que se le va Facturar")
    private static void AsignarCliente() {
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


    private static void MonedaFactura() {
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

    private void ConceptofacturacionAgregar() {
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
            Thread.sleep(1000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement campoCodigo = driver.findElement(By.id("EDT_CODIGOCONCEPTOFACTURACION"));
            WebElement campoConcepto = driver.findElement(By.id("EDT_CONCEPTOFACTURACION"));

            int intentos = 0;

            // Intentar diferentes valores entre 1 y 10
            while (intentos < 10) {
                intentos++;

                // Generar el valor entre 1 y 10
                int codigoConcepto = intentos;

                // Limpiar el campo y asignar el valor
                campoCodigo.clear();
                campoCodigo.sendKeys(String.valueOf(codigoConcepto));
                campoCodigo.sendKeys(Keys.TAB); // Cambiar de foco para activar validaciones

                // Esperar hasta que el campo de concepto esté visible
                wait.until(ExpectedConditions.attributeToBeNotEmpty(campoConcepto, "value"));
                String valorConcepto = campoConcepto.getAttribute("value");

                if (valorConcepto != null && !valorConcepto.equals("El concepto de facturación no está activo, Revisar")) {
                    System.out.println("Valor válido encontrado: " + codigoConcepto);
                    System.out.println("El concepto de facturación es: " + valorConcepto);

                    // Se agrega información al reporte de Allure
                    informacionConcepto.append("Número Concepto: ").append(codigoConcepto).append("\n");
                    informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");

                    // Retornar y detener el flujo
                    return;  // Aquí el script se detiene y sale del método
                } else {
                    System.out.println("Intento " + intentos + ": Valor " + codigoConcepto + " produjo el mensaje de error.");
                }
            }

            // Si no se encuentra un valor válido después de 10 intentos
            System.out.println("No se encontró un valor válido después de 10 intentos.");
            informacionConcepto.append("Concepto: Error, no se encontró un valor válido después de 10 intentos.\n");

            return; // También detendría el flujo en caso de no encontrar un valor válido

        } catch (Exception e) {
            // Captura el mensaje de error y lo despliega en el reporte de Allure
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera 3 segundos antes de continuar
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Intentar localizar el botón "Aceptar"
            WebElement botonAceptar;
            try {
                botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            } catch (Exception noButton) {
                System.out.println("El botón de aceptar Timbre no está disponible. Continuando...");
                return;
            }

            // Si el botón se encontró, hacer clic
            botonAceptar.click();
            System.out.println("Se presionó el botón de aceptar Timbre");

        } catch (Exception e) {
            System.out.println("Error al presionar el botón de aceptar Timbre. Continuando...");
            e.printStackTrace();
        }
    }

    public void ValidarYEnviarCorreo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Buscar todos los botones con "onclick"
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));

            // Filtrar botones que son visibles y clickeables
            List<WebElement> botonesValidos = botones.stream()
                    .filter(b -> b.isDisplayed() && b.isEnabled())
                    .collect(Collectors.toList());

            // Si no hay botones disponibles, continuar sin fallar
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }

            // Imprimir información de los botones válidos
            System.out.println("Botones válidos encontrados:");
            for (WebElement boton : botonesValidos) {
                //System.out.println(" - ID: " + boton.getAttribute("id") +
                //", Name: " + boton.getAttribute("name") +
                // ", OnClick: " + boton.getAttribute("onclick"));
            }

            // Seleccionar aleatoriamente un botón de la lista de botones válidos
            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));

            // Intentar hacer clic en el botón seleccionado
            try {
                System.out.println("Se hizo clic en el botón con ID: " + botonSeleccionado.getAttribute("id"));
                botonSeleccionado.click();
            } catch (Exception e) {
                System.out.println("Click() falló, intentando con JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
                System.out.println("Click ejecutado con JavaScript en el botón con ID: " + botonSeleccionado.getAttribute("id"));
            }

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón. Continuando...");
            e.printStackTrace();
        }
    }


    private void BotonPoliza() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Buscar el botón con XPath usando "onclick"
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and contains(@onclick, 'BTN_OK')]")
            ));

            // Intentar hacer clic
            botonAceptar.click();
            System.out.println("Se presionó el botón BTN_OK");

        } catch (Exception e) {
            System.out.println("No se encontró el botón BTN_OK o no se pudo hacer clic.");
        }
    }

    private void BotonImpresion() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

            // Buscar el botón con "onclick" que contiene "BTN_REGRESAR"
            WebElement botonRegresar = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and @name='BTN_REGRESAR' and contains(@onclick, 'BTN_REGRESAR')]")
            ));

            if (botonRegresar != null) {
                botonRegresar.click();
                System.out.println("Se presionó el botón de regresar");
            } else {
                System.out.println("No se encontró el botón de regresar, continuando con la ejecución.");
            }
        } catch (TimeoutException e) {
            System.out.println("El botón de regresar no se mostró, continuando la ejecución normalmente");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar");
            System.out.println("Error al presionar el botón de regresar");
            e.printStackTrace();
        }
    }
    public void BotonModuloCobranza() {
        try {
            // Espera explícita hasta que el botón (imagen) de Cobranza sea clicable
            WebElement ModuloBotonCobranza = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA1.jpg')]")
                    )
            );

            // Hacer clic en el botón una vez esté listo
            ModuloBotonCobranza.click();
            System.out.println("Botón Módulo Cobranza presionado correctamente.");

        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, " Botón Módulo Cobranza no funciona.");
        }
    }


    public void BotonPagosAbonos() {
        try {
            // Espera explícita hasta que el botón de Pagos/Abonos sea clicable
            WebElement pagosAbonosBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA/PAGOSABONOS1.jpg')]")));

            // Hacer clic en el botón
            pagosAbonosBoton.click();
            System.out.println("Submódulo Pagos/Abonos seleccionado correctamente.");
        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, "Botón Pagos/Abonos no funciona.");
        }
    }

    public void BotonRegistrarPagoAbono() {
        try {
            // Espera a que la opción principal "Registrar" en el menú sea clickeable
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("OPT_REGISTRARMENU")));

            // Hacer clic en la opción "Registrar" para desplegar el submenú
            menuRegistrar.click();
            System.out.println("Menú 'Registrar' abierto.");

            // Espera a que la opción específica "Registrar" dentro del submenú sea clickeable
            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("OPT_REGISTRAR")));

            // Hacer clic en la opción "Registrar"
            opcionRegistrar.click();
            System.out.println("Opción 'Registrar' seleccionada correctamente.");

        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }

    }

    public void IntroducirFechaActual() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Espera a que el campo de fecha esté visible y obtenlo
            WebElement campoFecha = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FECHA")));

            // Obtener el valor actual del campo
            String fechaActualCampo = campoFecha.getAttribute("value").trim();

            // Si el campo está vacío, introducir la fecha actual
            if (fechaActualCampo.isEmpty()) {
                // Obtener la fecha actual en formato "DD/MM/YYYY"
                String fechaHoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                // Limpiar el campo y enviar la fecha
                campoFecha.clear();
                campoFecha.sendKeys(fechaHoy);
                campoFecha.sendKeys(Keys.TAB); // Para que el sistema lo valide

                System.out.println("Fecha ingresada en el campo: " + fechaHoy);
            } else {
                System.out.println("El campo ya tiene una fecha: " + fechaActualCampo);
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la fecha en el campo EDT_FECHA.");
            System.out.println("Error al ingresar la fecha en el campo EDT_FECHA.");
        }
    }


    public void CodigoClientPago() {
        try {
            // Espera explícita hasta que el campo de texto del código de cliente esté visible
            WebElement codigoClienteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROCLIENTE")));

            // Limpiar el campo antes de ingresar un nuevo valor
            codigoClienteInput.clear();

            // Ingresar el código del cliente
            codigoClienteInput.sendKeys("000001");

            // Simular presionar la tecla TAB para validar el campo
            //codigoClienteInput.sendKeys(Keys.TAB);

            System.out.println("Código de Cliente ingresado correctamente.");
        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el Código de Cliente.");
        }
    }

    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void SeleccionarCuentaBancariaAleatoria() {
        try {
            // Localizar el combo box usando XPath en lugar de ID
            WebElement comboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[contains(@class, 'COMBO_CATCUENTASBANCARIAS')]")));

            // Instanciar el objeto Select para manipular el dropdown
            Select comboBox = new Select(comboBoxElement);

            // Obtener todas las opciones disponibles en el combo box
            List<WebElement> opcionesDisponibles = comboBox.getOptions();

            // Verificar si hay opciones disponibles
            if (opcionesDisponibles.size() > 1) { // Excluye la primera opción si es un placeholder
                // Generar un índice aleatorio para seleccionar una opción válida (excluyendo la primera)
                Random random = new Random();
                int indiceAleatorio = random.nextInt(opcionesDisponibles.size() - 1) + 1; // Evita seleccionar la opción 0 si es vacía

                // Obtener la opción seleccionada
                WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
                String textoSeleccionado = opcionSeleccionada.getText();

                // Seleccionar la opción aleatoria por índice
                comboBox.selectByIndex(indiceAleatorio);

                // Confirmar la selección
                System.out.println("Cuenta bancaria seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("No hay cuentas bancarias disponibles para seleccionar.");
            }
        } catch (TimeoutException | NoSuchElementException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria aleatoria.");
            System.out.println("Error al seleccionar una cuenta bancaria aleatoria.");
        }
    }



    @Step("Buscar factura para pagar")
    public void BusquedaFacturaPagar() {
        try {
            // Localizar el campo de búsqueda usando XPath en lugar de name
            WebElement campoBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'EDT_BUSQUEDAGENERAL')]")));

            // Limpiar el campo antes de ingresar el número de viaje
            campoBusqueda.clear();

            // Ingresar el número de viaje del cliente
            campoBusqueda.sendKeys(FolioFactura);

            System.out.println("Número de viaje ingresado en búsqueda: " + FolioFactura);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda de factura: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error en la búsqueda de factura.");
        }
    }



    public void SeleccionFactura() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        try {
            // Espera explícita hasta que el checkbox de la factura sea visible y clicable
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROFACTURAS_0")));

            // Seleccionar el checkbox de la factura\]
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
            System.out.println("Error al seleccionar la factura");
        }
    }

    @Step("Validar moneda y calcular el monto a pagar")
    public void ValidarConversion() {
        try {
            // Obtener la moneda seleccionada
            WebElement monedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_MONEDA")));
            String monedaSeleccionada = monedaElement.getText().trim().toUpperCase();
            System.out.println("Moneda detectada: " + monedaSeleccionada);

            // Obtener valores de los campos relevantes
            WebElement montoPesosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEPESOS")));
            WebElement montoDolaresElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEDLLS")));
            WebElement tipoCambioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            WebElement conversionMonedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_CONVERSIONDEMONEDA")));

            // Obtener valores y limpiar caracteres extraños
            String valorPesos = montoPesosElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorDolares = montoDolaresElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorTipoCambio = tipoCambioElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String textoConversion = conversionMonedaElement.getText().trim();

            // Extraer número de `tzSTC_CONVERSIONDEMONEDA`
            double conversionAutomatica = 0.0;
            Pattern pattern = Pattern.compile("\\$([0-9]+\\.?[0-9]*)"); // Buscar "$" seguido de números
            Matcher matcher = pattern.matcher(textoConversion);
            if (matcher.find()) {
                conversionAutomatica = Double.parseDouble(matcher.group(1));
            }

            // Convertir valores a double (manejo de errores)
            double montoPesos = (!valorPesos.isEmpty() && !valorPesos.equals("0.00")) ? Double.parseDouble(valorPesos) : 0.0;
            double montoDolares = (!valorDolares.isEmpty() && !valorDolares.equals("0.00")) ? Double.parseDouble(valorDolares) : 0.0;
            double tipoCambio = (!valorTipoCambio.isEmpty() && !valorTipoCambio.equals("0.00")) ? Double.parseDouble(valorTipoCambio) : 1.0;

            // Lógica de cálculo basada en la moneda detectada
            if (monedaSeleccionada.equals("PESOS")) {
                if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos; // Guardar directamente
                } else if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares * tipoCambio; // Convertir si hay monto en dólares
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica * tipoCambio; // Usar conversión automática
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else if (monedaSeleccionada.equals("DÓLARES")) {
                if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares; // Guardar directamente
                } else if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos / tipoCambio; // Convertir si hay monto en pesos
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica; // Usar conversión automática
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else {
                System.out.println("Error: Moneda no reconocida.");
                UtilidadesAllure.manejoError(driver, new Exception("Moneda no válida"), "Error al validar la moneda.");
            }

            // Imprimir el monto final
            System.out.println("Monto calculado: " + String.format(Locale.US, "%.2f", MontoaPAGAR));

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener la moneda y calcular el monto.");
            System.out.println("Error al validar la moneda y calcular el monto.");
        }
    }



    @Step("Introducir monto calculado en el campo de pago")
    public void IntroducirMontoPago() {
        // Asegurar que la variable está inicializada con un valor válido
        if (MontoaPAGAR == 0.0) {
            System.out.println("Advertencia: El monto a pagar es inválido o cero, no se ingresará en el campo.");
            return;
        }

        try {
            // Esperar hasta que el campo de importe sea visible
            WebElement campoImporte = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTE")));

            // Limpiar cualquier valor previo en el campo
            campoImporte.clear();

            // Convertir el monto a String correctamente formateado con dos decimales
            String montoFormateado = String.format(Locale.US, "%.2f", MontoaPAGAR);

            // Ingresar el monto almacenado en la variable MontoaPAGAR
            campoImporte.sendKeys(montoFormateado);

            // Confirmar que el monto ha sido ingresado
            System.out.println("Monto ingresado en el campo de pago: " + montoFormateado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el monto en el campo de pago.");
            System.out.println("Error al encontrar o ingresar el monto en el campo de pago.");
        }
    }


    @Step("Introducir referencia bancaria")
    public void IntroducirReferencia() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIABANCARIA")));

            // Limpiar el campo antes de ingresar el valor
            campoReferencia.clear();


            // Generar la referencia y enviarla al campo
            String referencia = "pago" + FolioFactura;
            campoReferencia.sendKeys(referencia);

            System.out.println("Referencia ingresada: " + referencia);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia bancaria.");
            System.out.println("Error al ingresar la referencia bancaria.");
        }
    }


    @Step("Aceptar Pago o Abono")
    public void AceptarPagoAbono() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_ACEPTAR')]")));
            btnAceptar.click();
            System.out.println("Pago/Abono aceptado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el pago o abono.");
            System.out.println("Error al aceptar el pago o abono.");
        }
    }

   @Step("Generar Timbre del Pago")
    public void TimbrePago() {
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_YES')]")));
            btnSi.click();
            System.out.println("Timbre del pago generado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar el timbre del pago.");
            System.out.println("Error al generar el timbre del pago.");
        }
    }

    @Step("Enviar Comprobante de Pago por Correo")
    public void EnvioCorreoPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Buscar todos los botones con "onclick"
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));

            // Filtrar botones que son visibles y clickeables
            List<WebElement> botonesValidos = botones.stream()
                    .filter(b -> b.isDisplayed() && b.isEnabled())
                    .collect(Collectors.toList());

            // Si no hay botones disponibles, continuar sin fallar
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }

            // Imprimir información de los botones válidos
            System.out.println("Botones válidos encontrados:");
            for (WebElement boton : botonesValidos) {

            }

            // Seleccionar aleatoriamente un botón de la lista de botones válidos
            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));

            // Intentar hacer clic en el botón seleccionado
            try {
                System.out.println("Se hizo clic en el botón con ID: " + botonSeleccionado.getAttribute("id"));
                botonSeleccionado.click();
            } catch (Exception e) {
                System.out.println("Click() falló, intentando con JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
                System.out.println("Click ejecutado con JavaScript en el botón con ID: " + botonSeleccionado.getAttribute("id"));
            }

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón. Continuando...");
            e.printStackTrace();
        }
    }

    @Step("Aceptar Póliza Contable del Pago")
    public void AceptarPolizaPago() {
        try {
            WebElement btnOk = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_OK')]")));
            btnOk.click();
            System.out.println("Póliza contable del pago aceptada.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar la póliza contable del pago.");
            System.out.println("Error al aceptar la póliza contable del pago.");
        }
    }

    @Step("Salir de la Ventana de Pago")
    public void SalirVentanaPago() {
        try {
            WebElement btnCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_CANCELAR')]")));
            btnCancelar.click();
            System.out.println("Salida de la ventana de pago realizada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al salir de la ventana de pago.");
            System.out.println("Error al salir de la ventana de pago.");
        }
    }
}

