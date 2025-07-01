package Indicadores;

import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ParametrosGenerales {

    public static WebDriver driver;
    public static WebDriverWait wait;

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

    @Test()
    @Order(3)
    @DisplayName("Ingresar a Configuracion")
    @Description("Da clic en el boton de agregar indicador, para despues seleccionar un indicador, lo agrega y lo quita. Continua con los demas indicadores sucesivamente.")
    public void ingresarConfiguracion() {
        // Definir el número de repeticiones
        try {
            handleImageButton();
            handleSubMenuButton();
            parametrosGenerales();
            UtilidadesAllure.capturaImagen(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[2]/a/img")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }
    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[2]/ul/li[1]/a/img")));
            subMenuButton.click();
        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void parametrosGenerales(){
        //Informacion del cliente
        WebElement RFC = driver.findElement(By.id("EDT_RFC"));
        WebElement registroPatronal = driver.findElement(By.id("EDT_REGISTROPATRONAL"));
        WebElement nombreFiscal = driver.findElement(By.id("EDT_NOMBREFISCAL"));
        WebElement nombreComercial = driver.findElement(By.id("EDT_NOMBRECOMERCIAL"));
        WebElement regimenFiscal = driver.findElement(By.id("COMBO_REGIMENFISCAL"));
        WebElement retencionIVA = driver.findElement(By.id("COMBO_CATIMPUESTOSRETENCIONES"));
        //Informacion del domicilio fiscal.
        WebElement pais = driver.findElement(By.id("COMBO_CATPAISES"));
        WebElement codigoPostal = driver.findElement(By.id("EDT_CODIGOPOSTAL"));
        WebElement estado = driver.findElement(By.id("COMBO_CATESTADOS"));
        WebElement municipio = driver.findElement(By.id("EDT_MUNICIPIO"));
        WebElement localidad = driver.findElement(By.id("EDT_LOCALIDAD"));
        WebElement colonia = driver.findElement(By.id("EDT_COLONIA"));
        WebElement calle = driver.findElement(By.id("EDT_CALLE"));
        WebElement numExterior = driver.findElement(By.id("EDT_NOEXT"));
        WebElement numInterior = driver.findElement(By.id("EDT_NOINT"));
        WebElement telefonos = driver.findElement(By.id("EDT_TELEFONOS"));

        //Si necesitas obtener el valor que contiene el input (si es un campo de texto)
        String valorRFC = RFC.getAttribute("value");
        String valorRegistroPatronal = registroPatronal.getAttribute("value");
        String valorNombreFiscal = nombreFiscal.getAttribute("value");
        String valorNombreComercial = nombreComercial.getAttribute("value");
        String valorMunicipio = municipio.getAttribute("value");
        String valorLocalidad = localidad.getAttribute("value");
        String valorColonia = colonia.getAttribute("value");
        String valorCalle = calle.getAttribute("value");
        String valorNumExterior = numExterior.getAttribute("value");
        String valorNumInterior = numInterior.getAttribute("value");
        String valorTelefono = telefonos.getAttribute("value");


        //valores de los campos del Domicilio Fiscal
        String valorCodigoPostal = codigoPostal.getAttribute("value");
        //Se crea objeto para seleccionar la informacion del combobox.
        Select selectRegimen = new Select(regimenFiscal);
        Select selectIVA = new Select(retencionIVA);
        Select selectPais = new Select(pais);
        Select selectEstado = new Select(estado);

        //Se selecciona la informacion del combobox.
        String valorRegimenFiscalSeleccionado = selectRegimen.getFirstSelectedOption().getText();
        String valorRetencionIVA = selectIVA.getFirstSelectedOption().getText();
        String valorPais = selectPais.getFirstSelectedOption().getText();
        String valorEstado = selectEstado.getFirstSelectedOption().getText();
        //Imprime la informacion de los parametros generales.
        System.out.println("RFC: " + valorRFC + "\nRegistro Patronal: "+ valorRegistroPatronal +"\nNombre Fiscal: "+ valorNombreFiscal + "\nNombre Comercial: "
        + valorNombreComercial + "\nRegimen Fiscal: " + valorTelefono +" - "+ valorRegimenFiscalSeleccionado + "\nRetencion IVA: " + valorRetencionIVA + "\n");
        //Imprime la informacion del domicilio Fiscal.
        System.out.println("**INFORMACION DOMICILIO FISCAL**" + "\nPais: " + valorPais + "\nCodigo Postal: " + valorCodigoPostal + "\nEstado: " + valorEstado +
                "\nMunicipio: " + valorMunicipio + "\nLocalidad: " + valorLocalidad + "\nColonia: " + valorColonia + "\nCalle: " + valorCalle + "\nNo. Exterior: " + valorNumExterior
        + " - " + "No.Interior: " + valorNumInterior + "\nTelefonos: " + valorTelefono);
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

}
