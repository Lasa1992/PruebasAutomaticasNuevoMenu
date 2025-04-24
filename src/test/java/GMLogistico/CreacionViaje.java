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
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreacionViaje {

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


    @RepeatedTest(20)
    @Order(1)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AgregarSubasta() throws Exception {

        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();

        BotonComprometidos();
        BuscarSubasta();
        BotonAplicar();
        SeleccionarCrearViaje();
        SeleccionarOpcionAleatoria();
        BotonContinuar();
        BotonContinuar2();
        UsoCfdi();
        BotonContinuar3();
        BotonAceptarViaje();
        BotonAceptarFinal();



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

    @Description("Hace clic en el botón Comprometido")
    public void BotonComprometidos() {
        try {
            WebElement btnComprometido = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div/div[1]/div/div/div/div[2]/button")
            ));
            btnComprometido.click();
            System.out.println("✅ Botón Comprometido clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Comprometido: " + e.getMessage());
        }
    }


    @Description("Toma el siguiente folio para el RFC actual, lo coloca en el input de la UI y lo marca como procesado")
    public void BuscarSubasta() throws Exception {
        String ruta = "C:\\Users\\LuisSanchez\\Desktop\\Excel Logistico\\BD Logistico.xlsx";
        String folioParaBuscar = null;
        int filaEncontrada = -1;

        // 1) Abrir y recorrer el Excel para encontrar el folio
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {
            Sheet sheet = wb.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // RFC en columna 2
                String rfc = Optional.ofNullable(row.getCell(1))
                        .map(Cell::getStringCellValue)
                        .map(String::trim)
                        .orElse("");
                // Saltar si ya procesado (columna 7)
                Cell procCell = row.getCell(6);
                boolean yaProcesado = procCell != null
                        && procCell.getCellType() == CellType.STRING
                        && !procCell.getStringCellValue().trim().isEmpty();
                if (yaProcesado) continue;

                // Si coincide el RFC, extraer folio y detener búsqueda
                if (currentRFC.equals(rfc)) {
                    folioParaBuscar = Optional.ofNullable(row.getCell(0))
                            .map(Cell::getStringCellValue)
                            .map(String::trim)
                            .orElse(null);
                    filaEncontrada = i;
                    break;
                }
            }
        }

        // 2) Si no encontró folio, terminar
        if (folioParaBuscar == null) {
            System.out.println("ℹ️ No hay subastas pendientes para RFC " + currentRFC);
            return;
        }

        // 3) Colocar el folio en el input de la UI (sin buscar)
        WebElement buscarInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[2]/div/div[4]/div/div/input")
        ));
        buscarInput.clear();
        buscarInput.sendKeys(folioParaBuscar);
        System.out.println("🔢 Folio colocado en el input: " + folioParaBuscar);

        // 4) Marcar la fila como procesada en el Excel
        try (FileInputStream fis2 = new FileInputStream(ruta);
             Workbook wb2 = WorkbookFactory.create(fis2)) {
            Sheet sheet2 = wb2.getSheetAt(0);
            Row rowToMark = sheet2.getRow(filaEncontrada);
            Cell markCell = rowToMark.getCell(6);
            if (markCell == null) markCell = rowToMark.createCell(6);
            markCell.setCellValue("X");

            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb2.write(fos);
            }
            System.out.println("✅ Fila " + filaEncontrada + " marcada como procesada.");
        }
    }

    @Description("Hace clic en el botón Aplicar de la segunda sección")
    public void BotonAplicar() {
        try {
            WebElement btnAplicar2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[2]/div/div[5]/button")
            ));
            btnAplicar2.click();
            System.out.println("✅ Botón Aplicar (segunda sección) clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aplicar (segunda sección): " + e.getMessage());
        }
        // pausa de 1 segundo
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }


    @Description("Abre el menú de opciones del primer registro y selecciona 'Crear viaje'")
    public void SeleccionarCrearViaje() {
        try {
            // 1) Hacer clic en el ícono de opciones (svg) en la columna 16 del primer registro
            WebElement iconoOpciones = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[2]/div/div[6]/div/div[1]/table/tbody/tr/td[16]/div/button/svg")
            ));
            iconoOpciones.click();
            System.out.println("✅ Menú de opciones abierto.");

            // 2) Esperar a que aparezca la opción "Crear viaje" y hacer clic en ella
            WebElement crearViaje = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@class='list-none p-0 m-0']/li[2][normalize-space()='Crear viaje']")
            ));
            crearViaje.click();
            System.out.println("✅ Opción 'Crear viaje' seleccionada.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar 'Crear viaje': " + e.getMessage());
        }
    }

    @Description("Selecciona aleatoriamente la primera o segunda opción de un dropdown específico")
    public void SeleccionarOpcionAleatoria() {
        try {
            // 1) Hacer clic en el campo para desplegar las opciones
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"idTipoDocumento\"]//*[@id=\"idTipoDocumento\"]")
            ));
            dropdown.click();

            // 2) Esperar a que se despliegue la lista de opciones
            List<WebElement> opciones = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//ul[@role='listbox']/li")
            ));

            // 3) Si hay al menos dos opciones, elegir un índice aleatorio entre 0 y 1
            if (opciones.size() >= 2) {
                int index = new Random().nextInt(2); // 0 o 1
                opciones.get(index).click();
                System.out.println("✅ Opción " + (index + 1) + " seleccionada aleatoriamente.");
            } else {
                System.out.println("⚠️ No hay suficientes opciones para seleccionar (se requieren al menos 2).");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar opción aleatoria: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Continuar")
    public void BotonContinuar() {
        try {
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[2]/div/div[3]/button")
            ));
            botonContinuar.click();
            System.out.println("✅ Botón Continuar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Continuar: " + e.getMessage());
        }
    }

    @Description("Hace clic en el segundo botón Continuar")
    public void BotonContinuar2() {
        try {
            WebElement botonContinuar2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[2]/div/div[3]/button")
            ));
            botonContinuar2.click();
            System.out.println("✅ Botón Continuar2 clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Continuar2: " + e.getMessage());
        }
    }

    @Description("Selecciona aleatoriamente un uso de CFDI del listado desplegable")
    public void UsoCfdi() {
        try {
            // 1) Hacer clic en el campo de Uso CFDI para desplegar las opciones
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/div[1]/div/div/div/div/div[2]/div/div/div/div/div/div[8]/div/div/div")
            ));
            dropdown.click();
            System.out.println("✅ Dropdown de Uso CFDI abierto.");

            // 2) Esperar a que aparezcan las opciones en el listbox
            List<WebElement> opciones = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//ul[@role='listbox']/li")
            ));

            // 3) Seleccionar una opción aleatoria entre las disponibles
            if (!opciones.isEmpty()) {
                int index = new Random().nextInt(opciones.size());
                WebElement opcion = opciones.get(index);
                String texto = opcion.getText().trim();
                opcion.click();
                System.out.println("✅ Uso CFDI seleccionado aleatoriamente: " + texto);
            } else {
                System.out.println("⚠️ No se encontraron opciones de Uso CFDI.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar Uso CFDI: " + e.getMessage());
        }
    }

    @Description("Hace clic en el tercer botón Continuar")
    public void BotonContinuar3() {
        try {
            WebElement botonContinuar3 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[2]/div/div[3]/button")
            ));
            botonContinuar3.click();
            System.out.println("✅ Botón Continuar3 clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Continuar3: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptar Viaje")
    public void BotonAceptarViaje() {
        try {
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[2]/div/div[3]/button")
            ));
            botonAceptarViaje.click();
            System.out.println("✅ Botón Aceptar Viaje clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptar Viaje: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptar final del flujo")
    public void BotonAceptarFinal() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[3]/div[3]/div/div/div[3]/div/button")
            ));
            botonAceptar.click();
            System.out.println("✅ Botón Aceptar final clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptar final: " + e.getMessage());
        }
    }










}