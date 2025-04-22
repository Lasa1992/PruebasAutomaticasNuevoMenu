package GMLogistico;


import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class SubastaLog {

        public static WebDriver driver;
        public static WebDriverWait wait;


    // Guardamos aquí el RFC elegido en cada iteración
    public static String currentRFC;


    // Clase interna Cliente
        static class Cliente {
            String rfc;
            String email;
            String contrasena;

            public Cliente(String rfc, String email, String contrasena) {
                this.rfc = rfc;
                this.email = email;
                this.contrasena = contrasena;
            }
        }

        @BeforeEach
        public void setup() {
            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(4));

            driver.manage().window().maximize();

            driver.get("https://logisticav1.gmtransport.co/");
        }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }




    @RepeatedTest(5)
    @Order(1)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AgregarSubasta() throws Exception {

        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
        BotonCrearSubasta();

        EstatusSubasta();

        CampoCliente();
        CampoRuta();
        CampoFechacarga();
        CampoFechadescarga();
        CampoOrigen();
        CampoDestino();

        PestanaMateriales();
        CampoMaterial();
        CampoCantidad();
        SeleccionarEmbalaje();
        CampoPeso();
        SeleccionarUnidadPeso();
        ActivarMaterialPeligroso();
        SeleccionarMaterialPeligroso();
        IngresarUNNumber();
        BotonAgregarMaterial();
        BotonContinuar();

        EspecificacionesTransportista();
        SeleccionarDocumentoAleatorioYAgregar();
        EspecificacionesUnidad();
        TipoDeUnidad();
        Region();
        BotonContinuarEspec();

        SeleccionarCondicionPago();
        SeleccionarFormaDePago();
        SeleccionarMetodoPago();
        SeleccionarConceptoFacturacion();
        IngresarImporte();
        BotonAgregarFactura();
        BotonContinuar();
        ActivarContraoferta();
        BotonEnviarSubasta();
        guardarFolioEnExcel();
        aceptarBoton();







    }


        @Description("Llena los campos de inicio de sesion con informacion.")
        public static void Iniciosesion() {
            Cliente[] clientes = {
                    new Cliente("IIA040805DZ4", "elisa.logistica@gmtransporterp.com", "123456"),
                    new Cliente("LOGI2222224T5", "logistico2@gmail.com", "123456"),
                    new Cliente("LOGI3333335T6", "logi3@gmail.com", "123456"),
                  //  new Cliente("LOGI4444445T6", "logi4@gmail.com", "123456"),
                   // new Cliente("LOGI1111112Q4", "logistico1@gmail.com", "123456")



            };

            Random random = new Random();
            Cliente cliente = clientes[random.nextInt(clientes.length)];

            currentRFC = cliente.rfc;


            WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-rfc-login\"]")));
            WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-email-login\"]")));
            WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-password-login\"]")));


            inputRFC.clear();
            inputEmail.clear();
            inputContrasena.clear();

            inputRFC.sendKeys(cliente.rfc);
            inputEmail.sendKeys(cliente.email);
            inputContrasena.sendKeys(cliente.contrasena);

            System.out.println("Intentando iniciar sesión con: " + cliente.email);
        }

        @Description("Da clic en el botón de 'Iniciar sesión' para ingresar al sistema.")
        public static void BotonIniciosesion() {
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
            ));
            submitButton.click();
        }

        @Description("Maneja la alerta de inicio de sesion en caso de tener una sesion ya abierta con el usuario.")
        public static void MensajeAlerta() {
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

    @Description("Maneja la alerta de inicio de sesión en caso de tener una sesión ya abierta con el usuario.")
    public static void BotonCrearSubasta() {
        try {
            WebElement BotonCrear = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*//*[@id=\"root\"]/div/div[1]/main/div/div/div[2]/div[1]/div/div[2]/div/button[2]")));
            BotonCrear.click();

            WebElement OpcionCompleta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"split-button-menu\"]/li[2]")));
            OpcionCompleta.click();

            WebElement BotonCompleta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div[1]/main/div/div/div[2]/div[1]/div/div[2]/div/button[1]")));
            BotonCompleta.click();

            System.out.println("✅ Se comienza la creación de subasta completa.");

            // Esperar 2 segundos
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("❌ No se logró acceder a la creación de subasta completa: " + e.getMessage());
        }
    }


    @Description("Selecciona el estatus de la subasta en la segunda opción del listado desplegable.")
    public static void EstatusSubasta() {
        try {
            // Clic en el campo para abrir el dropdown
            WebElement campoEstatus = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div[1]/main/div/div/div/div[2]/div[1]/div[1]/div[2]/div/div/div")
            ));
            campoEstatus.click();

            // Esperar 2 segundos
            Thread.sleep(2000);

            // Espera y clic en la segunda opción
            WebElement opcionEstatus = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/ul/li[2]")
            ));
            opcionEstatus.click();

            System.out.println("✅ Estatus de subasta seleccionado correctamente.");


        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar el estatus de la subasta: " + e.getMessage());
        }
    }


    @Description("Selecciona un cliente del listado desplegable.")
    public static void CampoCliente() {
        try {
            WebElement campoCliente = wait.until(ExpectedConditions.elementToBeClickable(By.id("idCliente")));
            campoCliente.click();

            WebElement lista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiAutocomplete-listbox")));
            WebElement opcion = lista.findElement(By.xpath(".//li[1]"));
            opcion.click();

            System.out.println("Cliente seleccionado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar cliente.");
        }
    }

    @Description("Selecciona la ruta del trayecto.")
    public static void CampoRuta() {
        try {
            WebElement campoRuta = wait.until(ExpectedConditions.elementToBeClickable(By.id("idRutaTreyecto")));
            campoRuta.click();

            WebElement lista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiAutocomplete-listbox")));
            WebElement opcion = lista.findElement(By.xpath(".//li[1]"));
            opcion.click();

            System.out.println("Ruta seleccionada correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar ruta.");
        }
    }

    @Description("Llena los campos de fecha y hora de carga.")
    public static void CampoFechacarga() {
        try {
            WebElement fechaCarga = wait.until(ExpectedConditions.elementToBeClickable(By.name("fechaCargaOrigen")));
            fechaCarga.clear();
            fechaCarga.sendKeys("04-22-2026");

            WebElement horaCarga = wait.until(ExpectedConditions.elementToBeClickable(By.name("horaCargaOrigen")));
            horaCarga.clear();
            horaCarga.sendKeys("14:30");

            System.out.println("Fecha y hora de carga ingresadas correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al llenar fecha/hora de carga.");
        }
    }

    @Description("Llena los campos de fecha y hora de descarga.")
    public static void CampoFechadescarga() {
        try {
            WebElement fechaDescarga = wait.until(ExpectedConditions.elementToBeClickable(By.name("fechaCargaDestino")));
            fechaDescarga.clear();
            fechaDescarga.sendKeys("04-25-2026");

            WebElement horaDescarga = wait.until(ExpectedConditions.elementToBeClickable(By.name("horaCargaDestino")));
            horaDescarga.clear();
            horaDescarga.sendKeys("14:30");

            System.out.println("Fecha y hora de descarga ingresadas correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al llenar fecha/hora de descarga.");
        }
    }

    @Description("Selecciona el origen del viaje.")
    public static void CampoOrigen() {
        try {
            WebElement inputOrigen = wait.until(ExpectedConditions.elementToBeClickable(By.id("idOrigen")));
            inputOrigen.click();

            WebElement lista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiAutocomplete-listbox")));
            WebElement opcion = lista.findElement(By.xpath(".//li[1]"));
            opcion.click();

            System.out.println("Origen seleccionado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar origen.");
        }
    }

    @Description("Selecciona el destino del viaje.")
    public static void CampoDestino() {
        try {
            WebElement inputDestino = wait.until(ExpectedConditions.elementToBeClickable(By.id("idDestino")));
            inputDestino.click();

            WebElement lista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiAutocomplete-listbox")));
            WebElement opcion = lista.findElement(By.xpath(".//li[1]"));
            opcion.click();

            System.out.println("Destino seleccionado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar destino.");
        }
    }

    @Description("Accede a la pestaña de materiales usando JavaScript.")
    public static void PestanaMateriales() {
        try {
            WebElement pestanaMateriales = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/form/div[1]/div/div[1]/div/div/button[2]")
            ));

            // Ejecutar clic vía JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pestanaMateriales);

            System.out.println("✅ Se accedió a la pestaña de materiales usando JavaScript.");
        } catch (Exception e) {
            System.out.println("❌ Error al acceder a la pestaña de materiales con JavaScript: " + e.getMessage());
        }
    }


    @Description("Llena el campo de material.")
    public static void CampoMaterial() {
        try {
            WebElement campoMaterial = wait.until(ExpectedConditions.elementToBeClickable(By.id("idMaterial")));
            campoMaterial.sendKeys("Texto del material");
            System.out.println("Campo material llenado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al llenar el campo de material.");
        }
    }

    @Description("Llena el campo de cantidad.")
    public static void CampoCantidad() {
        try {
            WebElement campoCantidad = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/form/div[1]/div/div[3]/div/div[1]/div/div/div/div[2]/div/div/div/div/div/div[2]/div/div/input")));
            campoCantidad.sendKeys("50");
            System.out.println("Cantidad ingresada correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar la cantidad.");
        }
    }

    @Description("Selecciona el tipo de embalaje.")
    public static void SeleccionarEmbalaje() {
        try {
            WebElement embalaje = wait.until(ExpectedConditions.elementToBeClickable(By.id("embalajeSelected")));
            embalaje.click();

            WebElement opcionEmbalaje = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@role='listbox']/li[5]")));
            opcionEmbalaje.click();

            System.out.println("Embalaje seleccionado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar el embalaje.");
        }
    }

    @Description("Llena el campo de peso.")
    public static void CampoPeso() {
        try {
            WebElement peso = wait.until(ExpectedConditions.elementToBeClickable(By.id("pesoUnidadEmbalaje")));
            peso.sendKeys("900");
            System.out.println("Peso ingresado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar el peso.");
        }
    }

    @Description("Selecciona la unidad de peso.")
    public static void SeleccionarUnidadPeso() {
        try {
            WebElement unidadPeso = wait.until(ExpectedConditions.elementToBeClickable(By.id("unidadPesoSelected")));
            unidadPeso.click();

            WebElement opcionPeso = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@role='listbox']/li[2]")));
            opcionPeso.click();

            System.out.println("Unidad de peso seleccionada correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar la unidad de peso.");
        }
    }

    @Description("Activa el checkbox de material peligroso.")
    public static void ActivarMaterialPeligroso() {
        try {
            WebElement checkPeligroso = driver.findElement(By.id("materialPeligrosoEnabled"));
            checkPeligroso.click();
            System.out.println("Material peligroso activado.");
        } catch (Exception e) {
            System.out.println("❌ Error al activar material peligroso.");
        }
    }

    @Description("Selecciona el tipo de material peligroso.")
    public static void SeleccionarMaterialPeligroso() {
        try {
            WebElement selectorPeligroso = wait.until(ExpectedConditions.elementToBeClickable(By.id("idMaterialPeligroso")));
            selectorPeligroso.click();

            WebElement opcionPeligroso = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@role='listbox']/li[3]")));
            opcionPeligroso.click();

            System.out.println("Material peligroso seleccionado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar material peligroso.");
        }
    }

    @Description("Ingresa el número UN.")
    public static void IngresarUNNumber() {
        try {
            WebElement unNumber = driver.findElement(By.id("unNumber"));
            unNumber.sendKeys("1234");
            System.out.println("Número UN ingresado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar número UN.");
        }
    }


    @Description("Hace clic en el botón Agregar para confirmar el material.")
    public static void BotonAgregarMaterial() {
        try {
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"panel1a-content\"]/div/div/div[13]/button")));
            botonAgregar.click();
            System.out.println("Material agregado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al hacer clic en el botón Agregar.");
        }
    }

    @Description("Hace clic en el botón Continuar para avanzar en la subasta.")
    public static void BotonContinuar() {
        try {
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div[1]/main/div/div/div/div[2]/div[3]/div/div[3]/button")));
            botonContinuar.click();
            System.out.println("Se continuó al siguiente paso.");
        } catch (Exception e) {
            System.out.println("❌ Error al hacer clic en el botón Continuar.");
        }
    }

    @Description("Agrega las especificaciones del transportista en el textarea correspondiente.")
    public static void EspecificacionesTransportista() {
        try {
            WebElement especificaciones = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("especificacionesTransportista")));
            especificaciones.sendKeys("Este es el texto que quiero agregar.");
            System.out.println("Especificaciones del transportista ingresadas.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar especificaciones del transportista.");
        }
    }

    @Description("Selecciona un documento aleatorio del combobox y hace clic en el botón Agregar.")
    public static void SeleccionarDocumentoAleatorioYAgregar() {
        try {
            // 1. Clic en el campo que despliega la lista
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(By.id("id")));
            campo.click();

            // 2. Esperar la lista de opciones y obtener todos los elementos <li>
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@role='listbox']")));
            java.util.List<WebElement> opciones = driver.findElements(By.xpath("//ul[@role='listbox']/li"));

            // 3. Elegir una opción aleatoria
            if (opciones.size() > 0) {
                int index = new Random().nextInt(opciones.size());
                WebElement opcionAleatoria = opciones.get(index);
                String textoOpcion = opcionAleatoria.getText();
                opcionAleatoria.click();
                System.out.println("✅ Opción seleccionada: " + textoOpcion);
            } else {
                System.out.println("❌ No se encontraron opciones en el combobox.");
            }

            // 4. Hacer clic en el botón Agregar
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"panel1a-content\"]/div/div/div[2]/div[2]/div[2]/button")
            ));
            botonAgregar.click();
            System.out.println("✅ Se hizo clic en el botón Agregar.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar documento y hacer clic en Agregar: " + e.getMessage());
        }
    }

    @Description("Agrega las especificaciones del transportista en el textarea correspondiente.")
    public static void EspecificacionesUnidad() {
        try {
            WebElement especificaciones = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"especificacionesUnidad\"]")));
            especificaciones.sendKeys("Este es el texto que quiero agregar.");
            System.out.println("Especificaciones del transportista ingresadas.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar especificaciones del transportista.");
        }
    }

    @Description("Selecciona aleatoriamente un tipo de unidad del autocomplete.")
    public static void TipoDeUnidad() {
        try {
            // 1. Hacer clic en el campo de tipo de unidad
            WebElement campoUnidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("idTipoUnidad")));
            campoUnidad.click();

            // 2. Esperar que se despliegue la lista
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@role='listbox']")));

            // 3. Obtener todas las opciones del listado
            java.util.List<WebElement> opciones = driver.findElements(By.xpath("//ul[@role='listbox']/li"));

            // 4. Elegir una opción aleatoria
            if (!opciones.isEmpty()) {
                int index = new Random().nextInt(opciones.size());
                WebElement opcionAleatoria = opciones.get(index);
                String textoSeleccionado = opcionAleatoria.getText();
                opcionAleatoria.click();

                System.out.println("✅ Tipo de unidad seleccionado aleatoriamente: " + textoSeleccionado);
            } else {
                System.out.println("❌ No se encontraron opciones de tipo de unidad.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar tipo de unidad: " + e.getMessage());
        }
    }

    @Description("Selecciona aleatoriamente una región del combobox y hace clic en el botón Agregar.")
    public static void Region() {
        try {
            // 1. Hacer clic en el campo para desplegar el combo
            WebElement campoRegion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/form/div[3]/div/div/div/div/div[2]/div/div/div/div/div/div[2]/div[1]/div[1]/div/div")));
            campoRegion.click();

            // 2. Esperar la lista desplegada
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@role='listbox']")));

            // 3. Obtener todas las opciones
            java.util.List<WebElement> opciones = driver.findElements(By.xpath("//ul[@role='listbox']/li"));

            // 4. Seleccionar una opción aleatoria
            if (!opciones.isEmpty()) {
                int index = new Random().nextInt(opciones.size());
                WebElement opcionAleatoria = opciones.get(index);
                String textoSeleccionado = opcionAleatoria.getText();
                opcionAleatoria.click();

                System.out.println("✅ Región seleccionada aleatoriamente: " + textoSeleccionado);
            } else {
                System.out.println("❌ No se encontraron opciones de región.");
            }

            // 5. Hacer clic en el botón Agregar
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"panel1a-content\"]/div/div/div[2]/div[1]/div[2]/button")
            ));
            botonAgregar.click();

            System.out.println("✅ Se hizo clic en el botón Agregar región.");

        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar región o hacer clic en Agregar: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Continuar dentro de la sección de Especificaciones.")
    public static void BotonContinuarEspec() {
        try {
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div[1]/main/div/div/div/div[2]/div[3]/div/div[3]/button")
            ));
            botonContinuar.click();
            System.out.println("✅ Se hizo clic en el botón Continuar de Especificaciones.");

            // Esperar 2 segundos
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("❌ Error al hacer clic en el botón Continuar de Especificaciones: " + e.getMessage());
        }


    }

    @Description("Fuerza la apertura del combo 'Condición de pago' con eventos y selecciona una opción aleatoria.")
    public void SeleccionarCondicionPago() {
        try {
            // Elemento que dispara el desplegable
            WebElement comboTrigger = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("condicionPagoSelected")
            ));

            // Simular clic vía eventos
            String script = """
            var element = arguments[0];
            ['mousedown','mouseup','click'].forEach(function(evt) {
                element.dispatchEvent(new MouseEvent(evt, {bubbles:true}));
            });
        """;
            ((JavascriptExecutor) driver).executeScript(script, comboTrigger);

            // Esperar que aparezcan las opciones
            WebElement listaOpciones = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@role='listbox']")
            ));
            java.util.List<WebElement> opciones = listaOpciones.findElements(By.tagName("li"));

            if (!opciones.isEmpty()) {
                WebElement opcion = opciones.get(new Random().nextInt(opciones.size()));
                String texto = opcion.getText().trim();
                opcion.click();
                System.out.println("✅ Condición de pago seleccionada: " + texto);
            } else {
                System.out.println("❌ No se encontraron opciones en la lista.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error en selección de condición de pago: " + e.getMessage());
        }
    }


    @Description("Selecciona aleatoriamente una forma de pago del listado desplegable.")
    public static void SeleccionarFormaDePago() {
        try {
            // 1. Hacer clic en el campo de forma de pago
            WebElement formaPago = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/div[1]/div/div/div/div/div[2]/div/div/div/div/div/div[6]/div/div/div")
            ));
            formaPago.click();

            // 2. Esperar a que aparezca la lista desplegable
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@role='listbox']")));

            // 3. Obtener todas las opciones disponibles
            java.util.List<WebElement> opciones = driver.findElements(By.xpath("//ul[@role='listbox']/li"));

            // 4. Seleccionar una opción aleatoria
            if (!opciones.isEmpty()) {
                int index = new Random().nextInt(opciones.size());
                WebElement opcionAleatoria = opciones.get(index);
                String textoSeleccionado = opcionAleatoria.getText();
                opcionAleatoria.click();
                System.out.println("✅ Forma de pago seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("❌ No se encontraron opciones de forma de pago.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar forma de pago: " + e.getMessage());
        }
    }

    @Description("Selecciona un método de pago.")
    public static void SeleccionarMetodoPago() {
        try {
            WebElement metodoPago = driver.findElement(By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/div[1]/div/div/div/div/div[2]/div/div/div/div/div/div[7]/div/div/div"));
            metodoPago.click();

            WebElement opcionMetodo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@role='listbox']/li[2]")));
            opcionMetodo.click();

            System.out.println("✅ Método de pago seleccionado.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar método de pago: " + e.getMessage());
        }
    }

    @Description("Selecciona un concepto de facturación.")
    public static void SeleccionarConceptoFacturacion() {
        try {
            WebElement concepto = driver.findElement(By.id("conceptoFacturacionId"));
            concepto.click();

            WebElement opcionConcepto = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@role='listbox']/li[1]")));
            opcionConcepto.click();

            System.out.println("✅ Concepto de facturación seleccionado.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar concepto de facturación: " + e.getMessage());
        }
    }

    @Description("Ingresa el importe de la subasta.")
    public static void IngresarImporte() {
        try {
            WebElement importe = driver.findElement(By.id("importe"));
            importe.sendKeys("12345");

            System.out.println("✅ Importe ingresado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al ingresar importe: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Agregar.")
    public static void BotonAgregarFactura() {
        try {
            WebElement botonAgregar = driver.findElement(By.xpath("//button[contains(text(),'Agregar')]"));
            botonAgregar.click();

            System.out.println("✅ Botón Agregar presionado.");
        } catch (Exception e) {
            System.out.println("❌ Error al hacer clic en Agregar: " + e.getMessage());
        }
    }

    @Description("Activa la opción permite contraoferta.")
    public static void ActivarContraoferta() {
        try {
            WebElement contraoferta = driver.findElement(By.id("permiteContraoferta"));
            contraoferta.click();

            System.out.println("✅ Contraoferta activada.");
        } catch (Exception e) {
            System.out.println("❌ Error al activar contraoferta: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Enviar.")
    public static void BotonEnviarSubasta() {
        try {
            WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[3]/div/div[3]/button")));
            botonEnviar.click();

            System.out.println("✅ Subasta enviada.");
        } catch (Exception e) {
            System.out.println("❌ Error al enviar la subasta: " + e.getMessage());
        }
    }

    @Description("Valida y anota el folio y el RFC en ese orden en el Excel")
    void guardarFolioEnExcel() throws Exception {
        String ruta = "C:\\Users\\LuisSanchez\\Desktop\\Excel Logistico\\BD Logistico.xlsx";

        // 1) Esperar a que aparezca cualquier texto con "LG-"
        WebElement alerta = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'LG-')]")
                ));
        String texto = alerta.getText();

        // 2) Extraer el folio
        Matcher m = Pattern.compile("(LG-\\d+)").matcher(texto);
        if (!m.find()) {
            System.out.println("❌ No se encontró folio en: " + texto);
            return;
        }
        String folio = m.group(1);
        System.out.println("✅ Folio detectado: " + folio);

        // 3) Abrir el Excel y buscar la primera fila vacía en la columna A
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheetAt(0);
            int rowIndex = 0;
            while (true) {
                Row row = sh.getRow(rowIndex);
                boolean empty = (row == null)
                        || row.getCell(0) == null
                        || row.getCell(0).getCellType() == CellType.BLANK
                        || row.getCell(0).getStringCellValue().trim().isEmpty();

                if (empty) {
                    if (row == null) row = sh.createRow(rowIndex);
                    // Ahora: Columna A ← Folio; Columna B ← RFC
                    row.createCell(0).setCellValue(folio);
                    row.createCell(1).setCellValue(currentRFC);
                    System.out.println("✅ Escrito en fila " + rowIndex
                            + " → Folio: " + folio
                            + ", RFC: " + currentRFC);
                    break;
                }
                rowIndex++;
            }

            // 4) Guardar de vuelta en el archivo
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }

        System.out.println("📄 Excel actualizado con Folio y RFC.");
    }



    @Description("Hace clic en el botón de aceptar")
    public static void aceptarBoton() {
        try {
            // Esperar a que el botón esté visible (opcional, recomendado)
            Thread.sleep(1000); // o usa WebDriverWait si prefieres algo más pro

            // Localiza el botón por XPath
            WebElement botonAceptar = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/div[3]/div/button"));

            // Haz clic en el botón
            botonAceptar.click();

            System.out.println("Botón de aceptar clickeado exitosamente.");

        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en el botón de aceptar.");
            e.printStackTrace();
        }
    }

}


