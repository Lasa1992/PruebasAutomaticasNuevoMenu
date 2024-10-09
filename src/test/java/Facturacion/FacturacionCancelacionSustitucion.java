package Facturacion;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionCancelacionSustitucion {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para almacenar información de facturas y conceptos para los reportes de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
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
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @Test
    @Order(3)
    @Description("Ingresar al módulo de facturación.")
    public void ingresarModuloFacturacion() {
        handleImageButton();
        handleSubMenuButton();
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
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    @RepeatedTest(8)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios y se selecciona una para cancelación o sustitución si aplica.")
    public void testFacturacionporConcepto() {
        // Limpia las variables de Allure para los reportes
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        // Buscar y seleccionar un registro válido
        boolean facturaSeleccionada = buscarYSeleccionarFactura();

        // Si se selecciona una factura válida, continuar con la cancelación
        if (facturaSeleccionada) {
            System.out.println("Factura seleccionada, procediendo con la cancelación...");
            realizarCancelacion();
        } else {
            System.out.println("No se seleccionó ninguna factura.");
        }
    }

    @Description("Buscar un registro en la tabla de facturación y revisar la columna 0 (Documento). Ajustar la fecha si no se encuentran resultados válidos.")
    public boolean buscarYSeleccionarFactura() {
        boolean registroValidoEncontrado = false;
        int intentos = 0;
        int maxIntentos = 5;  // Máximo número de intentos antes de fallar

        try {
            // Verificar si ya hay registros en la tabla desde el principio
            while (!registroValidoEncontrado && intentos < maxIntentos) {
                registroValidoEncontrado = SelecionarFactura();  // Intentar seleccionar una factura válida

                if (!registroValidoEncontrado) {
                    System.out.println("No se encontraron registros válidos en el intento " + (intentos + 1) + ". Ajustando fecha...");

                    // Si no se encontraron registros, ajustar la fecha y volver a buscar
                    ajustarFechaBusqueda();

                    // Presionar el botón "Aplicar" para buscar con la nueva fecha
                    WebElement botonAplicar = driver.findElement(By.id("BTN_APLICAR"));
                    botonAplicar.click();

                    // Esperar un momento para que la tabla se actualice
                    Thread.sleep(2000);

                    intentos++;
                }

                if (registroValidoEncontrado) {
                    System.out.println("Factura seleccionada en el intento " + (intentos + 1));
                    break;  // Salir del bucle si se selecciona una factura
                }
            }

            if (!registroValidoEncontrado) {
                System.out.println("No se encontraron registros válidos después de " + maxIntentos + " intentos.");
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar registros en la tabla.");
            System.out.println("Error al buscar registros en la tabla: " + e.getMessage());
        }

        return registroValidoEncontrado;
    }

    private void ajustarFechaBusqueda() {
        try {
            WebElement campoFecha = driver.findElement(By.id("EDT_DESDE"));
            String fechaActualStr = campoFecha.getAttribute("value");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaActual = LocalDate.parse(fechaActualStr, formatter);

            // Resta 10 días a la fecha
            LocalDate nuevaFecha = fechaActual.minusDays(10);
            campoFecha.clear();
            campoFecha.sendKeys(nuevaFecha.format(formatter));

            System.out.println("Fecha ajustada a: " + nuevaFecha.format(formatter));
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ajustar la fecha de búsqueda.");
            System.out.println("Error al ajustar la fecha de búsqueda: " + e.getMessage());
        }
    }

    @Step("Seleccionar un registro aleatorio de la tabla y validar columnas 16 (Fecha Cancelación) y 21 (Fecha Sustitución).")
    private boolean SelecionarFactura() {
        try {
            WebElement tabla = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TABLE_ProFacturasPorConcepto")));
            List<WebElement> filasTabla = tabla.findElements(By.cssSelector("tbody tr"));

            if (filasTabla.isEmpty()) {
                System.out.println("No se encontraron registros en la tabla.");
                return false;  // No se encontraron registros
            }

            System.out.println("Número total de filas encontradas: " + filasTabla.size());

            // Lista para guardar los índices de las filas que ya fueron seleccionadas
            List<Integer> indicesVisitados = new ArrayList<>();

            // Mientras no se encuentre un registro válido
            while (indicesVisitados.size() < filasTabla.size()) {
                int indiceAleatorio = (int) (Math.random() * filasTabla.size());

                // Evitar seleccionar la misma fila más de una vez
                if (indicesVisitados.contains(indiceAleatorio)) continue;

                // Marcar este índice como visitado
                indicesVisitados.add(indiceAleatorio);

                // Obtener la fila aleatoria
                WebElement fila = filasTabla.get(indiceAleatorio);

                // Verificar si la fila es visible y está habilitada
                if (!fila.isDisplayed()) {
                    System.out.println("Fila no visible. Saltando...");
                    continue;
                }

                // Obtener las celdas (columnas) de esa fila
                List<WebElement> columnas = fila.findElements(By.tagName("td"));

                // Verificar el número de columnas para evitar errores
                if (columnas.size() < 23) {
                    System.out.println("La fila no tiene suficientes columnas. Número de columnas: " + columnas.size());
                    continue;
                }

                // Obtener el valor del "Documento" (Columna 0)
                String documentoSeleccionado = columnas.getFirst().getText().trim();
                System.out.println("Documento seleccionado: " + documentoSeleccionado);

                if (documentoSeleccionado.isEmpty()) {
                    System.out.println("Documento vacío en la fila seleccionada. Saltando...");
                    continue;
                }

                // Obtener los valores de las columnas 16 (Fecha Cancelación) y 21 (Fecha Sustitución)
                String fechaCancelacion = columnas.get(16).getText().trim();
                String fechaSustitucion = columnas.get(21).getText().trim();

                // Si alguna de las columnas tiene valor, ignorar la fila
                if (!fechaCancelacion.isEmpty() || !fechaSustitucion.isEmpty()) {
                    System.out.println("El documento " + documentoSeleccionado + " no cumple con las condiciones.");
                    continue;  // Pasar a la siguiente fila
                }

                // Si ambas columnas están vacías, proceder
                System.out.println("El documento " + documentoSeleccionado + " cumple con las condiciones.");
                informacionFactura.append("Documento seleccionado: ").append(documentoSeleccionado);

                fila.click();  // Seleccionar la fila
                return true;  // Registro válido encontrado

            }

            return false;  // No se encontró ningún registro válido

        } catch (NoSuchElementException e) {
            System.out.println("No se encontró la tabla o las filas: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error al seleccionar el registro: " + e.getMessage());
            return false;
        }
    }

    @Step("Realizar la cancelación de la factura seleccionada.")
    private void realizarCancelacion() {
        try {
            // Presionar el botón "Cancelar"
            WebElement btnCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_CANCELAR")));
            btnCancelar.click();
            System.out.println("Botón 'Cancelar' presionado.");

            // Esperar a que aparezca la ventana emergente de cancelación
            WebElement ventanaEmergente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("CELL_POPUPMAIN")));
            ventanaEmergente.isDisplayed();
            System.out.println("Ventana emergente de cancelación visible.");

            // Seleccionar una opción en el combo box "Motivo de Cancelación"
            WebElement comboMotivo = driver.findElement(By.id("COMBO_MOTIVOCANCELACION"));
            Select selectMotivo = new Select(comboMotivo);

            // Selección de motivo con probabilidad
            Random random = new Random();
            int valorAleatorio = random.nextInt(100);
            if (valorAleatorio < 75) {
                selectMotivo.selectByValue("1");  // 75% de probabilidad para la primera opción
                System.out.println("Motivo seleccionado: Comprobante emitido con errores con relación (01)");
            } else if (valorAleatorio < 85) {
                selectMotivo.selectByValue("2");
                System.out.println("Motivo seleccionado: Comprobante emitido con errores sin relación (02)");
            } else if (valorAleatorio < 95) {
                selectMotivo.selectByValue("3");
                System.out.println("Motivo seleccionado: No se llevó a cabo la operación (03)");
            } else {
                selectMotivo.selectByValue("4");
                System.out.println("Motivo seleccionado: Operación nominativa relacionada en una factura global (04)");
            }

            // Introducir motivo en el campo de texto
            WebElement campoMotivo = driver.findElement(By.id("EDT_MOTIVO"));
            campoMotivo.sendKeys("Prueba automática");
            System.out.println("Motivo de cancelación introducido.");

            // Presionar el botón "Aceptar"
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            btnAceptar.click();
            System.out.println("Botón 'Aceptar' presionado para confirmar la cancelación.");

        } catch (Exception e) {
            System.out.println("Error al realizar la cancelación: " + e.getMessage());
        }
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
