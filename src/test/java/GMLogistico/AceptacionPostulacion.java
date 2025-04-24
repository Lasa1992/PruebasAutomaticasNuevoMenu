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
public class AceptacionPostulacion {

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

        listadocompeltas();
        BuscarSubasta();
        ClickIconoPostulaciones();
        BotonAceptorPostulacio();
        CheckDocumento();
        BotonEnviar();
        BotonAceptar();




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

    @Description("Abre el listado de completas haciendo clic en el botón correspondiente")
    public void listadocompeltas() {
        try {
            // Esperar hasta que el botón esté clickeable
            WebElement btnListado = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/button[2]")
            ));
            // Hacer clic en el botón
            btnListado.click();
            System.out.println("✅ Listado de completas abierto correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo abrir el listado de completas: " + e.getMessage());
        }
    }


    @Description("Busca la subasta para el RFC actual y marca la fila procesada")
    public void BuscarSubasta() throws Exception {
        // 1) Activar el campo de búsqueda en la UI
        WebElement buscarInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[3]/div/div[4]/div/div/input")
        ));
        buscarInput.click();

        // 2) Preparar variables para leer el Excel
        String ruta = "C:\\Users\\LuisSanchez\\Desktop\\Excel Logistico\\BD Logistico.xlsx";
        String folioParaBuscar = null;

        // 3) Abrir el workbook
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheetAt(0);

            // 4) Recorrer todas las filas desde la 1 (asumiendo encabezado en la 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // saltar filas vacías

                // 4.a) Leer RFC de la columna 2 (índice 1)
                Cell rfcCell = row.getCell(1);
                String rfc = (rfcCell != null)
                        ? rfcCell.getStringCellValue().trim()
                        : "";

                // 4.b) Leer marca de procesado de la columna 5 (índice 4)
                Cell procCell = row.getCell(4);
                boolean yaProcesado = procCell != null
                        && procCell.getCellType() == CellType.STRING
                        && !procCell.getStringCellValue().trim().isEmpty();

                // 4.c) Si el RFC coincide y NO está procesado, seleccionar este folio
                if (currentRFC.equals(rfc) && !yaProcesado) {
                    // Leer el folio de la columna 1 (índice 0)
                    folioParaBuscar = row.getCell(0).getStringCellValue().trim();

                    // Marcar la columna 5 como procesada ("X")
                    if (procCell == null) procCell = row.createCell(4);
                    procCell.setCellValue("X");
                    System.out.println("✅ Fila " + i + " marcada como procesada para RFC " + rfc);
                    break;
                }
                // De lo contrario, continúa al siguiente registro
            }

            // 5) Guardar cambios en el Excel
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }

        // 6) Si no se encontró ningún folio para buscar, informar y salir
        if (folioParaBuscar == null) {
            System.out.println("ℹ️ No hay nuevas subastas para RFC " + currentRFC);
            return;
        }

        // 7) Ejecutar la búsqueda del folio en la UI
        System.out.println("🔍 Buscando subasta con folio: " + folioParaBuscar);
        buscarInput.clear();
        buscarInput.sendKeys(folioParaBuscar, Keys.ENTER);
    }

    @Description("Forzar clic en el icono de postulaciones mediante JavaScript")
    public void ClickIconoPostulaciones() {
        By iconoBy = By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[3]/div/div[6]/div/div[1]/table/tbody/tr/td[2]/div/button");
        try {
            // 1) Esperar a que exista en el DOM (no depender de clickable)
            WebElement icono = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(iconoBy));
            // 2) Desplazarlo a la vista
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", icono);
            // 3) Forzar clic vía JS
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icono);
            System.out.println("✅ Icono de postulaciones clickeado vía JS.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo forzar clic en icono de postulaciones: " + e.getMessage());
        }
    }


    @Description("Hace clic en el botón Aceptor Postulacio")
    public void BotonAceptorPostulacio() {
        try {
            // Esperar hasta que el botón sea clickeable
            WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[5]/div/div/div/div/div/div[2]/div[3]/button")
            ));
            // Hacer clic en el botón
            boton.click();
            System.out.println("✅ Botón Aceptor Postulacio clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptor Postulacio: " + e.getMessage());
        }
    }

    @Description("Activa el checkbox de documento en la ventana de postulación")
    public void CheckDocumento() {
        try {
            // 1) Esperar hasta que el <span> del checkbox esté disponible y clickeable
            WebElement checkboxDoc = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/span")
            ));
            // 2) Hacer clic para activarlo
            checkboxDoc.click();
            System.out.println("✅ CheckDocumento activado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo activar CheckDocumento: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Enviar en la ventana de postulación")
    public void BotonEnviar() {
        try {
            WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div[1]/div[2]/button[2]")
            ));
            botonEnviar.click();
            System.out.println("✅ Botón Enviar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Enviar: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptar en el diálogo emergente")
    public void BotonAceptar() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[3]/div[3]/div/div/div[3]/div/button")
            ));
            botonAceptar.click();
            System.out.println("✅ Botón Aceptar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptar: " + e.getMessage());
        }
    }






}