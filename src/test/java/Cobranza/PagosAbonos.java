package Cobranza;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class PagosAbonos {

    public static WebDriver driver;
    public static WebDriverWait wait;
    public static int contador = 1;
    public static boolean facturaDolares = false;
    public static boolean cuentaBancariaDolares = false;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }
    @Test
    @Order(1)
    @DisplayName("Inicio de Sesion")
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @DisplayName("Alertas - Inicio Sesion")
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @Test
    @Order(3)
    @DisplayName("Ingresar a Cobranza")
    @Description("Da clic en el boton del modulo de cobranza.")
    public void ingresarCobranza() {
        // Definir el número de repeticiones
        try {
            handleImageButton();
            handleSubMenuButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RepeatedTest(20)
    @Order(4)
    @DisplayName("Registrar Pago/Abono")
    @Description("Se genera registra un pago de una factura.")
    public void registrarPago(){
        try{
            manejarBotonesCancelar();
            registrarPagoAbono();
            aceptarPagoAbono();
            BotonTimbre();
            BotonEnvioCorreo();
            validarProdigia();
            //aceptarPoliza();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //@Test
    //@Order(5)
    @DisplayName("Registrar Pago/Abono Factoraje")
    @Description("Se genera registra un pago/abono de factoraje.")
    public void registrarFactoraje(){
        try{
            registrarPagoFactoraje();
            aceptarPagoAbono();
            BotonTimbre();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void registrarPagoAbono() throws InterruptedException {
        WebElement registrar = wait.until(ExpectedConditions.elementToBeClickable(By.id("tzOPT_REGISTRARMENU")));
        registrar.click();
        WebElement opcionRegistrar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='MENU_SELECCIONREGISTRAR']//tr[@id='tzOPT_REGISTRAR']//a")));
        opcionRegistrar.click();

        WebElement cliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
        Thread.sleep(3000);
        cliente.sendKeys("3" + Keys.TAB);

        WebElement cuentaBancaria = wait.until(ExpectedConditions.elementToBeClickable(By.id("COMBO_CATCUENTASBANCARIAS")));
        Select selectCuentaBancaria = new Select(cuentaBancaria);
        List<WebElement> opciones = selectCuentaBancaria.getOptions();

        // Generar un número aleatorio en el rango de las opciones
        Random random = new Random();
        int indiceAleatorio = random.nextInt(opciones.size());  // Genera un número entre 0 y tamaño de opciones
        selectCuentaBancaria.selectByIndex(indiceAleatorio);
        WebElement opcionSeleccionada = selectCuentaBancaria.getFirstSelectedOption();
        System.out.println("Cuenta Bancaria Seleccionada: " + opcionSeleccionada.getText());

        try {
            WebElement moneda = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_MONEDA")));
            String valorMoneda = moneda.getText();
            cuentaBancariaDolares = valorMoneda.equalsIgnoreCase("DÓLARES");
            if (cuentaBancariaDolares) {
                System.out.println("La Cuenta Bancaria está en DÓLARES.");
            } else {
                System.out.println("La Cuenta Bancaria no está en DÓLARES.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            WebElement selectFacturas = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='checkbox' and contains(@id,'TABLE_PROFACTURAS')]")));
            selectFacturas.click();
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar facturas");
        }

        try {
            WebElement columnaMoneda = driver.findElement(By.xpath("//table[@id='TABLE_PROFACTURAS_TB']//tr[@id='TABLE_PROFACTURAS_0']//div[@id='TABLE_PROFACTURAS_0_12']"));
            String textoMoneda = columnaMoneda.getText();

            facturaDolares = textoMoneda.equalsIgnoreCase("DÓLARES");
            if (facturaDolares) {
                System.out.println("La factura está en DÓLARES.");
            } else {
                System.out.println("La factura no está en DÓLARES.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesAllure.manejoError(driver, e, "Error al identificar la moneda de la factura");
        }

        try {
            WebElement importePagarPesos = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEPESOS")));
            WebElement importePagarDolares = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEDLLS")));
            WebElement importeDepositado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTE")));
            String importeConvertido = "";

            if (facturaDolares != cuentaBancariaDolares) {
                try {
                    WebElement tipoCambioElement = driver.findElement(By.id("EDT_TIPOCAMBIO"));
                    String tipoCambioText = Objects.requireNonNull(tipoCambioElement.getAttribute("value")).replace("$", "").replace(",", "").trim();

                    double tipoCambio = Double.parseDouble(tipoCambioText);
                    if (tipoCambio > 0) {
                        if (facturaDolares) {
                            String saldoDolaresText = Objects.requireNonNull(importePagarDolares.getAttribute("value")).replace(",", "");
                            double saldoDolares = Double.parseDouble(saldoDolaresText);
                            importeConvertido = new DecimalFormat("#.00").format(saldoDolares * tipoCambio);
                        } else {
                            String saldoPesosText = Objects.requireNonNull(importePagarPesos.getAttribute("value")).replace(",", "");
                            double saldoPesos = Double.parseDouble(saldoPesosText);
                            importeConvertido = new DecimalFormat("#.00").format(saldoPesos / tipoCambio);
                        }
                        importeDepositado.clear();
                        Thread.sleep(2000);
                        importeDepositado.sendKeys(importeConvertido);
                        System.out.println("Importe convertido: " + importeConvertido);
                    } else {
                        throw new IllegalArgumentException("Tipo de cambio no válido.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilidadesAllure.manejoError(driver, e, "Error al convertir la moneda");
                }
            } else {
                if (facturaDolares && cuentaBancariaDolares) {
                    importeConvertido = Objects.requireNonNull(importePagarDolares.getAttribute("value")).replace(",", "");
                } else {
                    importeConvertido = Objects.requireNonNull(importePagarPesos.getAttribute("value")).replace(",", "");
                }
                importeDepositado.clear();
                Thread.sleep(2000);
                importeDepositado.sendKeys(importeConvertido + Keys.TAB);
                System.out.println("Importe sin conversión: " + importeConvertido);
            }
            WebElement referenciaBancaria = driver.findElement(By.id("EDT_REFERENCIABANCARIA"));
            referenciaBancaria.sendKeys("PruebaAutomatica - " + contador + Keys.TAB);
            System.out.println("Referencia Bancaria: PruebaAutomatica - " + contador);
            contador++;
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesAllure.manejoError(driver, e, "Error al colocar el importe depositado o referencia bancaria.");
        }
    }

    private static void aceptarPagoAbono(){
        try {
            Thread.sleep(2000);
            WebElement botonAceptar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registrarPagoFactoraje(){
        WebElement registrar = driver.findElement(By.id("tzOPT_REGISTRARMENU"));
        registrar.click();
        WebElement opcionRegistrarFactoraje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='MENU_SELECCIONREGISTRAR']//tr[@id='tzOPT_REGISTRARFACTORAJE']//a")));
        opcionRegistrarFactoraje.click();

    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA1.jpg')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Cobranza no funciona.");
        }
    }
    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA/PAGOSABONOS1.jpg')]")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón Pagos/Abonos no funciona.");
        }
    }

    public static void manejarBotonesCancelar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            boolean botonesPresentes = true; // Controla si algún botón sigue presente

            while (botonesPresentes) {
                botonesPresentes = false; // Suponemos que no hay botones al inicio de cada iteración

                // Verificar si el botón "BTN_IMPRIMIR" está presente
                List<WebElement> btnImprimir = driver.findElements(By.id("BTN_PAGOCONSOLIDADO"));
                if (!btnImprimir.isEmpty() && btnImprimir.get(0).isDisplayed()) {
                    System.out.println("El botón 'Imprimir' está presente. Se detiene la ejecución.");
                    break; // Salir del ciclo si el botón "Imprimir" está presente
                }

                // Buscar el botón "BTN_CANCELARDETALLE"
                List<WebElement> btnCancelarDetalle = driver.findElements(By.id("BTN_CANCELARDETALLE"));
                if (!btnCancelarDetalle.isEmpty() && btnCancelarDetalle.get(0).isDisplayed()) {
                    // Si el botón "BTN_CANCELARDETALLE" está presente y visible, hacer clic en él
                    WebElement botonCancelarDetalle = btnCancelarDetalle.get(0);
                    wait.until(ExpectedConditions.elementToBeClickable(botonCancelarDetalle));
                    botonCancelarDetalle.click();
                    System.out.println("Se hizo clic en el botón 'Cancelar Detalle'.");

                    // Después de hacer clic, asumimos que puede aparecer el botón "BTN_CANCELAR"
                    botonesPresentes = true; // Mantiene el ciclo activo
                }

                // Buscar el botón "BTN_CANCELAR" si no está activo el otro
                List<WebElement> btnCancelar = driver.findElements(By.id("BTN_CANCELAR"));
                if (!btnCancelar.isEmpty() && btnCancelar.get(0).isDisplayed()) {
                    // Si el botón "BTN_CANCELAR" está presente y visible, hacer clic en él
                    WebElement botonCancelar = btnCancelar.get(0);
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

    private static void BotonTimbre() {
        try {
            // Espera a que el mensaje de validación sea visible y esté en el viewport
            WebElement mensajeValidacion = wait.until(driver -> {
                WebElement elemento = driver.findElement(By.id("dwwBTN_YES"));
                return (elemento.isDisplayed() && elemento.getRect().getWidth() > 0 && elemento.getRect().getHeight() > 0) ? elemento : null;
            });
            if (mensajeValidacion != null && mensajeValidacion.isEnabled()) {
                System.out.println("El botón de YES está habilitado y visible.");
            }

            // Localizar el botón "Sí" dentro del mensaje de validación, y esperar su visibilidad
            WebElement botonYes = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));

            // Agregar una pausa breve antes de hacer clic, para evitar problemas de sincronización
            Thread.sleep(1000);

            // Hacer clic en el botón "Sí"
            botonYes.click();
            System.out.println("Se ha hecho clic en el botón 'Sí' del mensaje de validación.");

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "No se detectó el mensaje de validación dentro del tiempo esperado.");
            System.out.println("No se detectó el mensaje de validación dentro del tiempo esperado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Ocurrió un problema al timbrar");
            System.out.println("Se ha producido un error al aceptar el mensaje de validación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void validarProdigia() {
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
                // Continuar con el siguiente paso
                List<WebElement> botonContinuarList = driver.findElements(By.id("BOTON_CONTINUAR"));
                if (!botonContinuarList.isEmpty()) {
                    // Si la lista no está vacía, significa que el botón está presente
                    WebElement botonContinuar = botonContinuarList.get(0);  // Obtenemos el primer (y único) elemento de la lista
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
    }

    public static void aceptarPoliza() {
        try {
            // Intentar localizar el mensaje "tzSTC_DESCRIPTION"
            WebElement mensaje = wait.until(driver -> {
                try {
                    WebElement elemento = driver.findElement(By.id("tzSTC_DESCRIPTION"));
                    return elemento.isDisplayed() ? elemento : null;
                } catch (NoSuchElementException e) {
                    // Si no se encuentra el elemento, devolver null y continuar sin error
                    return null;
                } catch (TimeoutException e) {
                    // Capturar TimeoutException para evitar el error
                    System.out.println("Elemento 'tzSTC_DESCRIPTION' no se cargó a tiempo.");
                    return null;
                }
            });

            // Si el mensaje no se encuentra, salir del método sin hacer nada
            if (mensaje == null) {
                System.out.println("Mensaje de póliza no encontrado; continuando sin hacer clic en BTN_OK.");
                return;
            }

            // Obtener el texto del mensaje y verificar si contiene "Póliza"
            String textoMensaje = mensaje.getText();
            if (textoMensaje.contains("Póliza")) {
                System.out.println("Mensaje contiene 'Póliza'. Haciendo clic en BTN_OK.");
                WebElement botonOK = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_OK")));
                botonOK.click();
            } else {
                System.out.println("Mensaje no contiene 'Póliza'. No se hace clic en BTN_OK.");
            }

        } catch (Exception e) {
            // Manejar cualquier otra excepción que ocurra
            e.printStackTrace();
            UtilidadesAllure.manejoError(driver, e, "Error al verificar el mensaje o hacer clic en el botón OK.");
        }
    }



    public static void BotonEnvioCorreo() {
        try {
            // Esperar hasta que el mensaje con las opciones sea visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Verificar si el elemento con ID "tzSTC_DESCRIPTION" está presente y visible
            List<WebElement> elementosDescripcion = driver.findElements(By.id("tzSTC_DESCRIPTION"));

            if (!elementosDescripcion.isEmpty() && elementosDescripcion.get(0).isDisplayed()) {
                WebElement mensajeOpciones = elementosDescripcion.get(0);

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
/*    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }*/
}