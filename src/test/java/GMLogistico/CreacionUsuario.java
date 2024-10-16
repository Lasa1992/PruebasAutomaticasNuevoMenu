package GMLogistico;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreacionUsuario {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("http://190.9.53.4:9093/");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion.")
    public void inicioSesion() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
            InicioSesionLog.fillForm(driver);
            InicioSesionLog.submitForm(wait);
            InicioSesionLog.handleAlert(wait);
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "Error durante el inicio de sesión.");
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    @Description("Acciones Entrar al Catalogo de usuarios")
    public void EntrarCatalogoUsuario() {
        EntrarCatalogos();
        EntrarUsuarios();
    }

    @RepeatedTest(500)
    @Order(3)
    @Description("Generar un usuario desde un archivo Excel y guardarlo")
    public void testCreacionUsuariosMasivos() {
        try {
            String rutaArchivo = "C:/Users/UsuarioY/Desktop/Pruebas Automaticas/XLSXPruebas/Usuarios.xlsx";
            List<Row> filas = leerDatosDesdeExcel(rutaArchivo);

            if (!filas.isEmpty()) {
                Row row = filas.get(0); // Procesar solo el primer usuario disponible
                String nombre = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                String apellido = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                String celular = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";
                String email = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";
                String contrasena = row.getCell(10) != null ? row.getCell(10).getStringCellValue() : "";

                try {
                    // Presionar el botón 'Agregar' para cada usuario
                    PresionarBotonAgregar();
                    Thread.sleep(1000); // Espera de 1 segundo después de presionar el botón 'Agregar'

                    // Llenar el formulario con los datos del usuario
                    AsignarNombreApellidos(nombre, apellido);
                    Thread.sleep(1000); // Espera de 1 segundo después de asignar nombre y apellidos

                    AsignarCelular(celular);
                    Thread.sleep(1000); // Espera de 1 segundo después de asignar el número de celular

                    AsignarEmail(email);
                    Thread.sleep(1000); // Espera de 1 segundo después de asignar el email

                    AsignarRol();
                    Thread.sleep(300); // Espera de 1 segundo después de asignar el rol

                    AsignarContrasenaYConfirmacion(contrasena);
                    Thread.sleep(300); // Espera de 1 segundo después de asignar la contraseña y confirmación

                    // Presionar el botón 'Guardar' para cada usuario y verificar si se guardó correctamente
                    if (PresionarBotonGuardar()) {
                        marcarUsuarioComoProcesado(row, rutaArchivo);
                        System.out.println("Usuario creado y marcado como procesado: " + nombre + " " + apellido);
                    } else {
                        System.out.println("No se pudo crear el usuario: " + nombre + " " + apellido);
                    }

                } catch (Exception e) {
                    UtilidadesAllureLog.manejoError(driver, e, "Error durante la creación del usuario: " + nombre + " " + apellido);
                }
            } else {
                System.out.println("No hay usuarios disponibles para procesar.");
            }
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "Error al procesar la creación de usuario.");
        }
    }


    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Entrar al catálogo principal")
    private void EntrarCatalogos() {
        try {
            WebElement catalogosButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/catalogos' and @role='button']")
            ));
            catalogosButton.click();
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo presionar el botón de Catálogos.");
        }
    }

    @Step("Entrar en la sección de Usuarios después de acceder al catálogo")
    private void EntrarUsuarios() {
        try {
            WebElement usuariosButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@class='MuiBox-root css-1culaby']/h2[contains(text(), 'Usuarios')]")
            ));
            usuariosButton.click();
            System.out.println("Entrando a Usuarios...");
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo entrar en la sección de Usuarios.");
        }
    }

    @Step("Leer datos de usuarios desde el archivo Excel")
    private List<Row> leerDatosDesdeExcel(String rutaArchivo) {
        List<Row> filas = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(new File(rutaArchivo));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar encabezado

                Cell procesadoCell = row.getCell(11);
                if (procesadoCell != null && "Si".equalsIgnoreCase(procesadoCell.getStringCellValue())) {
                   // System.out.println("El usuario ya fue procesado previamente, saltando fila: " + row.getRowNum());
                    continue; // Saltar usuarios ya procesados
                }

                filas.add(row);
            }
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "Error al leer el archivo Excel.");
        }
        return filas;
    }

    @Step("Presionar el botón 'Agregar' para iniciar la creación de usuario")
    private void PresionarBotonAgregar() {
        try {
            WebElement agregarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'MuiButtonBase-root') and text()='Agregar']")
            ));
            agregarButton.click();
            System.out.println("Botón 'Agregar' presionado correctamente.");
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo presionar el botón 'Agregar'.");
        }
    }

    @Step("Asignar nombre y apellidos al nuevo usuario")
    private void AsignarNombreApellidos(String nombre, String apellido) {
        try {
            WebElement nombreInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
            nombreInput.clear();
            nombreInput.sendKeys(nombre);

            WebElement apellidoInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("apellido")));
            apellidoInput.clear();
            apellidoInput.sendKeys(apellido);

        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo asignar el nombre y apellidos.");
        }
    }

    @Step("Asignar número de celular al nuevo usuario")
    private void AsignarCelular(String celular) {
        try {
            // Filtrar solo los números del string de celular
            String celularNumeros = celular.replaceAll("\\D", ""); // Elimina todos los caracteres no numéricos

            // Buscar el campo de entrada de celular usando un XPath que sea más confiable
            WebElement celularInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='tel' and contains(@class, 'MuiOutlinedInput-input')]")
            ));

            celularInput.clear();  // Limpiar cualquier valor existente en el campo
            celularInput.sendKeys(celularNumeros);  // Asignar el número de celular

            System.out.println("Celular asignado correctamente: " + celularNumeros);
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo asignar el número de celular.");
        }
    }

    @Step("Asignar correo electrónico al nuevo usuario")
    private void AsignarEmail(String email) {
        try {
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("correo")));
            emailInput.clear();
            emailInput.sendKeys(email);
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo asignar el correo electrónico.");
        }
    }

    @Step("Asignar el rol al nuevo usuario")
    private void AsignarRol() {
        try {
            WebElement abrirComboRolButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Open']")
            ));
            abrirComboRolButton.click();

            WebElement primeraOpcionRol = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@role='listbox']/li[1]")
            ));
            primeraOpcionRol.click();
            System.out.println("Rol asignado correctamente.");

        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo asignar el rol.");
        }
    }

    @Step("Asignar contraseña y confirmación al nuevo usuario")
    private void AsignarContrasenaYConfirmacion(String contrasena) {
        try {
            WebElement contrasenaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pass")));
            contrasenaInput.clear();
            contrasenaInput.sendKeys(contrasena);

            WebElement confirmacionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));
            confirmacionInput.clear();
            confirmacionInput.sendKeys(contrasena);
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo asignar la contraseña y confirmación.");
        }
    }
    @Step("Presionar el botón 'Guardar' para completar la creación de usuario")
    private boolean PresionarBotonGuardar() {
        try {
            // Localizar el botón "Guardar" y hacer scroll hacia él
            WebElement guardarButton = driver.findElement(By.xpath("//button[@type='submit' and contains(., 'Guardar')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", guardarButton);
            System.out.println("Botón 'Guardar' desplazado al centro de la vista.");

            // Hacer clic en el botón "Guardar" usando JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", guardarButton);
            System.out.println("Botón 'Guardar' presionado usando JavaScript.");

            // Esperar el mensaje de éxito específico después de presionar el botón
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement mensajeExito = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), '¡En hora buena!') and contains(text(), 'éxito')]")
            ));
            System.out.println("Mensaje de éxito encontrado: ¡En hora buena! La acción se ha realizado con éxito.");

            // Intentar cerrar el mensaje si hay un botón de cierre
            try {
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".w-6")
                ));
                closeButton.click();
                System.out.println("Mensaje de éxito cerrado.");
            } catch (TimeoutException | NoSuchElementException e) {
                System.out.println("No se pudo cerrar el mensaje de éxito: " + e.getMessage());
            }

            // Esperar a que el mensaje de éxito desaparezca completamente
            wait.until(ExpectedConditions.invisibilityOf(mensajeExito));
            System.out.println("Mensaje de éxito ha desaparecido.");

            return true;

        } catch (TimeoutException te) {
            System.out.println("No se encontró el mensaje de éxito dentro del tiempo esperado.");
            UtilidadesAllureLog.manejoError(driver, te, "El mensaje de éxito no apareció después de presionar el botón 'Guardar'.");
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "No se pudo presionar el botón 'Guardar' o ocurrió otro problema.");
        }
        return false;
    }

    @Step("Marcar usuario como procesado en el archivo Excel")
    private void marcarUsuarioComoProcesado(Row row, String rutaArchivo) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(rutaArchivo));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            // Obtener la hoja del archivo Excel donde se encuentran los datos
            Sheet sheet = workbook.getSheetAt(0);

            // Marcar la fila del usuario como procesado
            int rowIndex = row.getRowNum();
            Row currentRow = sheet.getRow(rowIndex);
            if (currentRow != null) {
                Cell procesadoCell = currentRow.createCell(11, CellType.STRING);
                procesadoCell.setCellValue("Si");
            } else {
                System.out.println("Fila no encontrada: " + rowIndex);
            }

            // Guardar los cambios en el archivo Excel
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(rutaArchivo))) {
                workbook.write(fileOutputStream);
                System.out.println("Usuario marcado como procesado: " + row.getCell(1).getStringCellValue());
            }

        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "Error al marcar el usuario como procesado en Excel.");
        }
    }
}