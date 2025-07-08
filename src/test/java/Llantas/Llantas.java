package Llantas;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Llantas {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static String Economico; // Ejemplo de número económico

    static Random random = new Random();
    static int kilometrosRecord = 1000 + random.nextInt(801);

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
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
    }

    @RepeatedTest(1)
    @Order(4)
    @Description("Se genera un flujo completo de Llantas desde Requisición de Compras.")
    public void LlantasPA() throws InterruptedException {

        handleImageButton();
        handleSubMenuButton();

        //Creamos una llanta
        BotonAgregarLlanta();
        IngresarNumeroEconomico();
        IngresarCodigoModeloLlanta();
        IngresarPresionActual();
        SeleccionarEstatusLlantaNueva();
        IngresarVidaUtilAleatoria();
        IngresarCostoLlanta();
        BotonRegistrarLlanta();

        BotonAsignar();

        //Desasignamos la llanta
        BotonDesasignarPosicion1();
        seleccionarLlantaAsignada();
        kilometrajeActualizado();
        DesasignarLlanta();

        //Asignamos la llanta a una unidad
        SeleccionarUnidad();
        seleccionarLlanta();
        posicionLlanta();
        presionLlanta();
        kilometrosLlanta();
        BotonAsignarLlanta();

        //Rotamos la llanta
        RotacionLlanta();
        PosicionesLlantas();
        BotonAceptarRotacion();

        //Revisión de la llanta
        RevisionLlanta();
        seleccionarLlantaAsignada();
        kilometrajeActualizado();
        profundidadLlanta();
        comentarioRevisionLlanta();
        guardarRevisionLlanta();

        //Desasignamos la llanta
        BotonDesasignar();
        seleccionarLlantaAsignada();
        kilometrajeActualizado();
        DesasignarLlanta();

        //Cerramos la ventana de Llantas
        CerrarVentanaLlantas();

        //nos aseguramos de que el WebDriver se libere correctamente

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Llantas...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/LLANTAS1')]")));
            imageButton.click();
            System.out.println("Botón Módulo Llantas seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Llantas no funciona.");
            System.out.println("Botón Módulo Llantas no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/LLANTAS/LLANTAS1')]")));
            subMenuButton.click();
            System.out.println("Botón Ordenes de Servicio seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Llantas no funciona.");
            System.out.println("Botón listado Llantas no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar Llantas")
    private static void BotonAgregarLlanta() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_REGISTRAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Llanta.");
        }
    }

    private static void IngresarNumeroEconomico() {
        try {
            // Generar número aleatorio de 6 dígitos
            int numero = new Random().nextInt(900000) + 100000; // entre 100000 y 999999
            String numeroEconomico = "PA" + numero;

            // Esperar el campo y enviar el número económico
            WebElement campoNumeroEconomico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_NUMEROECONOMICO']")));
            campoNumeroEconomico.clear();
            campoNumeroEconomico.sendKeys(numeroEconomico);

            Economico = numeroEconomico; // Guardar el número económico para uso posterior

            System.out.println("Se ingresó el número económico: " + Economico);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el número económico.");
        }
    }

    private static void IngresarCodigoModeloLlanta() {
        try {
            // Esperar que el campo esté visible y sea clickeable
            WebElement campoCodigoLlanta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_CODIGOMODELOLLANTA']")));

            // Limpiar el campo y enviar el número 1
            campoCodigoLlanta.clear();
            campoCodigoLlanta.sendKeys("1");

            System.out.println("Se ingresó el número '1' en el campo Código Modelo Llanta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el código del modelo de llanta.");
        }
    }

    private static void IngresarPresionActual() {
        try {
            // Esperar que el campo esté visible y sea clickeable
            WebElement campoPresionActual = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_PRESIONACTUAL']")));

            // Limpiar el campo y enviar el valor 35
            campoPresionActual.clear();
            campoPresionActual.sendKeys("35");

            System.out.println("Se ingresó el valor '35' en el campo Presión Actual.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la presión actual.");
        }
    }

    private static void SeleccionarEstatusLlantaNueva() {
        try {
            // Esperar que el combo esté visible
            WebElement comboEstatusLlanta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_CATESTATUSLLANTA']")));

            // Crear el objeto Select y seleccionar por texto visible
            Select selectEstatus = new Select(comboEstatusLlanta);
            selectEstatus.selectByVisibleText("NUEVA");

            System.out.println("Se seleccionó la opción 'NUEVA' en el combo Estatus Llanta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'NUEVA' en el combo Estatus Llanta.");
        }
    }

    private static void IngresarVidaUtilAleatoria() {
        try {
            // Generar número aleatorio entre 30000 y 50000
            Random random = new Random();
            int vidaUtil = 30000 + random.nextInt(20001); // 50000 - 30000 + 1

            // Esperar a que el campo sea clickeable
            WebElement campoVidaUtil = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_VIDAUTIL']")));

            // Ingresar el valor generado
            campoVidaUtil.clear();
            campoVidaUtil.sendKeys(String.valueOf(vidaUtil));

            System.out.println("Se ingresó la vida útil: " + vidaUtil);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la vida útil en el campo EDT_VIDAUTIL.");
        }
    }

    private static void IngresarCostoLlanta() {
        try {

            // Generar número aleatorio entre 1000 y 1800
            Random random = new Random();
            int costoLlanta = 1000 + random.nextInt(801); // 1800 - 1000 + 1

            // Esperar a que el campo sea clickeable
            WebElement campoCostoLlanta = driver.findElement(
                    By.xpath("//*[@id=\"EDT_COSTOLLANTA\"]"));

            // Ingresar el valor generado
            campoCostoLlanta.click();
            Thread.sleep(3000);
            campoCostoLlanta.sendKeys(String.valueOf(costoLlanta));
            campoCostoLlanta.sendKeys(Keys.TAB);

            System.out.println("Se ingresó el Costo de Llanta: $" + costoLlanta);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la vida útil en el campo Costo Llanta.");
        }
    }

    // Método auxiliar para verificar si hay una alerta
    private static boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException ex) {
            return false;
        }
    }


    private static void BotonRegistrarLlanta() {
        try {
            // Esperar a que el botón sea visible y clickeable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));

            // Hacer clic en el botón
            botonAceptar.click();

            if (isAlertPresent()) {
                String alertText = driver.switchTo().alert().getText();
                System.out.println("⚠️ Alerta detectada: " + alertText);
                driver.switchTo().alert().accept(); // Cierra la alerta
            }

            System.out.println("Botón 'Registrar Llanta' fue clickeado correctamente con número Económico: " + Economico);

            System.out.println("Se hizo clic en el botón 'Registrar Llanta' (BTN_ACEPTAR).");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'Registrar Llanta'.");
        }
    }


    @Step("Seleccionar Botón Asignar/Desasignar Llantas")
    private static void BotonAsignar() {
        try {
            WebElement botonAutorizar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ASIGNARDESASIGNAR\"]")));
            botonAutorizar.click();
            System.out.println("Botón 'Asignar/Desasignar' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Asignar/Desasignar.");
        }
    }

    private static void SeleccionarUnidad() {
        try {
            // Esperar que el combo esté visible
            WebElement codigoUnidad = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_CODIGOUNIDAD\"]")));
            codigoUnidad.click();
            codigoUnidad.clear();
            codigoUnidad.sendKeys("T-045");
            codigoUnidad.sendKeys(Keys.TAB);
            Thread.sleep(5000);
            System.out.println("Se seleccionó la unidad con número económico: T-045");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la unidad con número económico: T-045");
        }
    }

    private static void seleccionarLlanta() {
        try {
            Thread.sleep(5000);
            WebElement numeroEconomicoField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_NUMECONOMICO\"]")));
            numeroEconomicoField.click();
            numeroEconomicoField.sendKeys(Economico);
            numeroEconomicoField.sendKeys(Keys.TAB);
            System.out.println("Llanta seleccionada correctamente: " + Economico);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la llanta con número económico: " + Economico);
        }
    }

    private static void posicionLlanta() {
        try {
            WebElement posicion = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_POSICIONLLANTA\"]")));
            posicion.click();
            posicion.sendKeys("1");
            posicion.sendKeys(Keys.TAB);

            System.out.println("Se posicionó la llanta correctamente en la posición 1.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al posicionar la llanta en la posicion 1.");
        }
    }

    private static void kilometrosLlanta() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera hasta que el campo esté visible
            WebElement kilometros = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"EDT_KILOMETRAJELLANTA\"]")));

            kilometros.click();
            kilometros.clear();
            kilometros.sendKeys(String.valueOf(kilometrosRecord));
            System.out.println("Se ingresaron los kilómetros correctamente: " + kilometrosRecord);

        } catch (TimeoutException e) {
            System.out.println("El campo de kilómetros no apareció en el tiempo esperado.");
            UtilidadesAllure.manejoError(driver, e, "Timeout esperando campo de kilómetros.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar los kilómetros de la llanta.");
        }
    }


    private static void presionLlanta() {
        try {
            WebElement presion = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_PRESIONACTUAL\"]")));
            presion.click();
            presion.clear();
            presion.sendKeys("35");
            presion.sendKeys(Keys.TAB);
            System.out.println("Se ingresó la presión de la llanta: 35 PSI.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la presión de la llanta.");
        }
    }

    private static void BotonAsignarLlanta() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ASIGNARDESASIGNAR\"]")));
            botonAceptar.click();
            System.out.println("Botón 'Asignar Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aasignar Llanta.");
        }
    }

    private static void RotacionLlanta() {
        try {
            WebElement botonRotacion = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"RADIO_ASIGNARDESASIGNAR_3\"]")));
            botonRotacion.click();
            System.out.println("Radio button 'Rotación de Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el radio button Rotación de Llanta.");
        }
    }

    private static void PosicionesLlantas() {
        try {
            // Combo Origen
            WebElement comboOrigenElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_PROLLANTASASIGNADAS2']")));
            Select comboOrigen = new Select(comboOrigenElement);
            List<WebElement> opcionesOrigen = comboOrigen.getOptions();

            if (opcionesOrigen.size() > 1) {
                comboOrigen.selectByIndex(1); // Segunda opción (índice 1)
                String seleccionOrigen = comboOrigen.getFirstSelectedOption().getText();
                System.out.println("Se seleccionó del combo origen: " + seleccionOrigen);
            } else {
                System.out.println("No hay suficientes opciones en el combo origen.");
            }

            // Combo Destino
            WebElement comboDestinoElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_PROLLANTASASIGNADAS3']")));
            Select comboDestino = new Select(comboDestinoElement);
            List<WebElement> opcionesDestino = comboDestino.getOptions();

            if (opcionesDestino.size() > 1) {
                comboDestino.selectByIndex(1); // Segunda opción (índice 1)
                String seleccionDestino = comboDestino.getFirstSelectedOption().getText();
                System.out.println("Se seleccionó del combo destino: " + seleccionDestino);
            } else {
                System.out.println("No hay suficientes opciones en el combo destino.");
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar posiciones de llantas.");
        }
    }

    private static void BotonAceptarRotacion() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTARROTAR\"]")));
            botonAceptar.click();
            System.out.println("Botón 'Aceptar Rotación' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar Rotación.");
        }
    }

    private static void RevisionLlanta() {
        try {
            WebElement botonRevision = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"RADIO_ASIGNARDESASIGNAR_4\"]")));
            botonRevision.click();
            System.out.println("Radio button 'Revisión de Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el radio button Revisión de Llanta.");
        }
    }

    private static void seleccionarLlantaAsignada() {
        try {
            WebElement comboElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='COMBO_PROLLANTASASIGNADAS1']")));

            Select combo = new Select(comboElement);
            boolean encontrado = false;

            System.out.println("Opciones disponibles en el combo:");
            for (WebElement opcion : combo.getOptions()) {
                System.out.println(" - " + opcion.getText());
            }

            for (WebElement opcion : combo.getOptions()) {
                String textoOpcion = opcion.getText().trim().toLowerCase();
                String economicoBuscado = Economico.trim().toLowerCase();

                if (textoOpcion.contains(economicoBuscado)) {
                    combo.selectByVisibleText(opcion.getText());
                    System.out.println("✅ Se seleccionó la opción (ignorando mayúsculas/minúsculas): " + opcion.getText());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new NoSuchElementException("❌ No se encontró una opción en el combo que contenga (ignorando mayúsculas): " + Economico);
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción que contiene (sin importar mayúsculas): " + Economico);
        }
    }


    private static void kilometrajeActualizado() {
        try {
            // Esperar a que el campo sea visible y clickeable
            WebElement campoKilometraje = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_KILOMETRAJELLANTA3\"]")));
            // Limpiar el campo y enviar el valor del kilometraje actualizado
            campoKilometraje.clear();
            kilometrosRecord = kilometrosRecord + 100; // Incrementar el kilometraje en 100
            campoKilometraje.sendKeys(String.valueOf(kilometrosRecord));
            campoKilometraje.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó el kilometraje actualizado: " + kilometrosRecord);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el kilometraje actualizado.");
        }
    }

    private static void profundidadLlanta() {
        try {
            int AuxProfundidad; // Profundidad de llanta de ejemplo
            // Toma el campo profundidad interna y le resta 1 al valor actual
            WebElement campoProfundidadInterna = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_PROFUNDIDADINTERNAACTUALREV\"]")));

            AuxProfundidad = Integer.valueOf(campoProfundidadInterna.getText())-1;
            campoProfundidadInterna.sendKeys(String.valueOf(AuxProfundidad)); // Valor de profundidad de ejemplo
            System.out.println("Se ingresó la profundidad Interna de la llanta: "+ AuxProfundidad);
            AuxProfundidad = 0; // Reiniciar el valor para la profundidad externa

            // Toma el campo profundidad Media y le resta 1 al valor actual
            WebElement campoProfundidadMedia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_PROFUNDIDADMEDIAACTUALREV\"]")));
            AuxProfundidad = Integer.valueOf(campoProfundidadMedia.getText())-1;
            campoProfundidadMedia.sendKeys(String.valueOf(AuxProfundidad)); // Valor de profundidad de ejemplo
            System.out.println("Se ingresó la profundidad Media de la llanta: "+ AuxProfundidad);
            AuxProfundidad = 0; // Reiniciar el valor para la profundidad de la llanta

            // Toma el campo profundidad externa y le resta 1 al valor actual
            WebElement campoProfundidadExterna = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_PROFUNDIDADEXTERNAACTUALREV\"]")));
            AuxProfundidad = Integer.valueOf(campoProfundidadExterna.getText())-1;
            campoProfundidadExterna.sendKeys(String.valueOf(AuxProfundidad)); // Valor de profundidad de ejemplo
            System.out.println("Se ingresó la profundidad Externa de la llanta: "+ AuxProfundidad);
            AuxProfundidad = 0; // Reiniciar el valor para la profundidad de la llanta
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la profundidad de la llanta.");
        }
    }

    private static void comentarioRevisionLlanta() {
        try {
            // Esperar a que el campo de comentarios sea visible y clickeable
            WebElement campoComentario = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_COMENTARIOS\"]")));

            // Limpiar el campo y enviar un comentario
            campoComentario.clear();
            campoComentario.sendKeys("Revisión de llanta completada correctamente por PA.");

            System.out.println("Se ingresó el comentario de revisión de la llanta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el comentario de revisión de la llanta.");
        }
    }

    private static void guardarRevisionLlanta() {
        try {
            // Esperar a que el botón de guardar sea visible y clickeable
            WebElement botonGuardar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTARREVISION\"]")));

            // Hacer clic en el botón de guardar
            botonGuardar.click();

            if (isAlertPresent()) {
                String alertText = driver.switchTo().alert().getText();
                System.out.println("⚠️ Alerta detectada: " + alertText);
                driver.switchTo().alert().accept(); // Cierra la alerta
            }

            System.out.println("Revisión de llanta guardada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al guardar la revisión de la llanta.");
        }
    }

    private static void BotonDesasignar() {
        try {
            WebElement botonDesasignar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"RADIO_ASIGNARDESASIGNAR_2\"]")));
            botonDesasignar.click();
            System.out.println("Radio button 'Desasignar Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el radio button Desasignar Llanta.");
        }
    }

    private static void DesasignarLlanta() {
        try {
            WebElement botonDesasignar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ASIGNARDESASIGNAR\"]")));
            botonDesasignar.click();
            System.out.println("La llanta fue desasignada correctamente: " + Economico);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Desasignar Llanta.");
        }
    }

    private static void CerrarVentanaLlantas() {
        try {
            Thread.sleep(3000);
            WebElement botonCerrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_SALIR\"]")));
            botonCerrar.click();
            System.out.println("Ventana de Llantas cerrada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al cerrar la ventana de Llantas.");
        }
    }

    private static void BotonDesasignarPosicion1() {
        try {
            Thread.sleep(3000);
            WebElement comboElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"COMBO_PROLLANTASASIGNADAS\"]")));

            Select combo = new Select(comboElement);
            boolean encontrado = false;

            System.out.println("Opciones disponibles en el combo:");
            for (WebElement opcion : combo.getOptions()) {
                System.out.println(" - " + opcion.getText());
            }

            for (WebElement opcion : combo.getOptions()) {
                String textoOpcion = opcion.getText().trim().toLowerCase();
                String economicoBuscado = "[Posición: 1]";
                economicoBuscado = economicoBuscado.trim().toLowerCase();

                if (textoOpcion.contains(economicoBuscado)) {
                    combo.selectByVisibleText(opcion.getText());
                    System.out.println("✅ Se seleccionó la opción (ignorando mayúsculas/minúsculas): " + opcion.getText());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new NoSuchElementException("❌ No se encontró una opción en el combo que contenga (ignorando mayúsculas): " + Economico);
            }

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción que contiene (sin importar mayúsculas): " + Economico);
        }
    }
}