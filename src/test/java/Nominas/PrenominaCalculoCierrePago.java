package Nominas;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrenominaCalculoCierrePago {

    private static WebDriver driver;
    private static WebDriverWait wait;
    // Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    // Atributo de clase para guardar el periodo seleccionado
    private String periodoSeleccionado;

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
    @Order(3)
    @Description("Se genera una factura con conceptos aleatorios")
    public void PrinPrenomina() throws InterruptedException {
        // 🛠️ Navegar a la página de "Nómina"
        ModuloNominas();
        Prenomina();
        SeleccionTipoPeriodo();
        SeleccionarEjercicio();
        SeleccionarPeriodo();
        SeleccionarBotonAplicar();
        BotonCalcularNomina();
        CalcularNomina();
        AceptarCalculo();
        ValidarCalculo2Plano();
        AceptarCierre();
        // 🛠️ Pago de Recibos de nómina
        BotonPagar();
        SeleccionarPeriodoAPagar();
        SeleccionarMovimientoBanAleatorio();
        SeleccionarTipoMovimientoAleatorio();
        IngresarReferencia();
        SeleccionarBotonAceptarPago();
        MensajesPagos();
        SalirVentanaPago();
        // 🛠️ Timbrado de Recibos de Nómina
        SeleccionarTimbrar();
        SeleccionarPeriodoTimbrar();
        BotonAceptarParaTimbre();
        ValidarTimbrados();
        SalirVentanaTimbrar();
        // 🛠️ Cerrar el navegador al final de la prueba
        cerrarNavegador();
    }

    private void SeleccionTipoPeriodo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el combo esté presente y visible usando XPath
            WebElement comboTipoPeriodo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"COMBO_TIPOPERIODO\"]")));

            // Crea el objeto Select para manipular el combo
            Select selectTPeriodo = new Select(comboTipoPeriodo);

            // Puedes seleccionar por visible text, value o índice
            selectTPeriodo.selectByVisibleText("SEMANAL 2025");

            System.out.println("✅ Se seleccionó el tipo de período correctamente.");
        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el tipo de período.");
            e.printStackTrace();
        }
    }

    private void SeleccionarEjercicio() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que el combo esté presente
            WebElement combo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"tzCOMBO_EJERCICIO\"]")));

            boolean encontrado = false;

            // Intentar hasta que no haya más "StaleElementReferenceException"
            int intentos = 0;
            while (intentos < 3 && !encontrado) {
                try {
                    // Buscar nuevamente las opciones cada vez para evitar referencias caducadas
                    List<WebElement> opciones = combo.findElements(By.tagName("option"));

                    // Recorrer las opciones y seleccionar la que coincide con el valor "2026"
                    for (WebElement opcion : opciones) {
                        String textoOpcion = opcion.getText().trim();
                        // Comparar sin importar mayúsculas o minúsculas
                        if (textoOpcion.equalsIgnoreCase("2026")) {
                            opcion.click(); // Seleccionar la opción
                            System.out.println("✅ Opción seleccionada: 2026");
                            encontrado = true;
                            break;
                        }
                    }

                } catch (StaleElementReferenceException e) {
                    // Si ocurre StaleElementReferenceException, esperar y volver a intentar
                    System.out.println("⚠ Elemento desactualizado. Reintentando...");
                    intentos++;
                    Thread.sleep(1000); // Esperar un segundo antes de reintentar
                }
            }

            if (!encontrado) {
                System.out.println("⚠ No se encontró el ejercicio 2026");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el ejercicio:");
            e.printStackTrace();
        }
    }


    private static void ModuloNominas() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[14]/a/img")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Nóminas no funciona.");
            System.out.println("Botón Módulo Nóminas no funciona.");
        }
    }

    private static void Prenomina() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[14]/ul/li[2]/a/img")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Nóminas no funciona.");
            System.out.println("Botón Módulo Nóminas no funciona.");
        }
    }

    private void SeleccionarBotonAplicar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que el botón esté presente
            WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"BTN_APLICAR\"]")));  // XPath del botón BTN_APLICAR

            // Hacer clic en el botón
            boton.click();
            System.out.println("✅ Botón 'Aplicar' seleccionado");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el botón 'Aplicar':");
            e.printStackTrace();
        }
    }


    private void SeleccionarPeriodo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Ubicar el combo con XPath
            WebElement combo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='COMBO_FILTRARPORPERIODO']")));

            // Obtener las opciones del combo
            List<WebElement> opciones = combo.findElements(By.tagName("option"));

            boolean encontrado = false;

            for (WebElement opcion : opciones) {
                String estilo = opcion.getAttribute("style");

                if (estilo != null && estilo.contains("color: green")) {
                    opcion.click(); // Selecciona la opción con estilo verde
                    periodoSeleccionado = opcion.getText(); // Guarda el texto en la variable
                    System.out.println("✅ Opción verde seleccionada: " + periodoSeleccionado);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                System.out.println("⚠ No se encontró ninguna opción verde.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar opción verde:");
            e.printStackTrace();
        }
    }


    private void BotonCalcularNomina() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón esté visible y clickeable
            WebElement botonCalcular = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_CALCULAR\"]")));

            // Hace clic en el botón
            botonCalcular.click();

            System.out.println("✅ Se hizo clic en el botón Calcular Nómina.");
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón Calcular Nómina.");
            e.printStackTrace();
        }
    }

    private void CalcularNomina() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón esté visible y clickeable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ACEPTAR\"]")));

            // Hace clic en el botón
            botonAceptar.click();

            System.out.println("✅ Se hizo clic en el botón Aceptar para calcular la nómina.");
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón Aceptar.");
            e.printStackTrace();
        }
    }

    private void AceptarCalculo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón esté visible y clickeable
            WebElement botonOk = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_OK']")));

            // Hace clic en el botón
            botonOk.click();

            System.out.println("✅ Se hizo clic en el botón OK para aceptar el cálculo.");
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón OK.");
            e.printStackTrace();
        }
    }


    private void ValidarCalculo2Plano() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Espera a que el botón esté visible y clickeable
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='z_BTN_APLICAR_IMG']")));

            // Hace clic en el botón
            botonAplicar.click();

            System.out.println("✅ Se hizo clic en el botón APLICAR.");
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón APLICAR.");
            e.printStackTrace();

            //validar si se puede que revise la columna neto que no este en 0 en los empleados y lo notifique
        }
    }

    private void AceptarCierre() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
        int maxIntentos = 10;
        int intentos = 0;

        // Espera inicial al botón "Cerrar Periodo"
        WebElement botonCerrarPeriodo = wait2.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='BTN_CERRARPERIODO']")));
        botonCerrarPeriodo.click();

        while (intentos < maxIntentos) {
            try {
                intentos++;

                // Aceptar alerta si aparece
                Alert alerta = wait.until(ExpectedConditions.alertIsPresent());
                System.out.println("🔔 Alerta detectada: " + alerta.getText());
                alerta.accept();
                System.out.println("✅ Alerta aceptada.");

                Thread.sleep(1000);

                List<WebElement> botonesOk = driver.findElements(By.xpath("//*[@id='BTN_OK']"));

                if (!botonesOk.isEmpty() && botonesOk.get(0).isDisplayed()) {
                    System.out.println("🔁 Botón 'OK' encontrado. Haciendo clic...");
                    botonesOk.get(0).click();
                    Thread.sleep(1000);

                    // Buscar nuevamente el botón "Cerrar Periodo" y hacer clic
                    botonCerrarPeriodo = wait2.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id='BTN_CERRARPERIODO']")));
                    botonCerrarPeriodo.click();
                    Thread.sleep(1000);
                } else {
                    System.out.println("✅ Se cerro el periodo correctamente.");
                    break;
                }

            } catch (TimeoutException e) {
                System.out.println("⏳ No se encontró una alerta en el tiempo esperado. Reintentando...");
            } catch (Exception e) {
                System.err.println("❌ Error en AceptarCierre:");
                e.printStackTrace();
                break;
            }
        }

        if (intentos >= maxIntentos) {
            System.err.println("⚠️ Se alcanzó el número máximo de intentos. Finalizando proceso.");
        }
    }

    private void cerrarNavegador() {
        if (driver != null) {
            driver.quit(); // Cierra todas las ventanas y finaliza el proceso del driver
            System.out.println("🛑 Navegador cerrado y pruebas finalizadas.");
        } else {
            System.out.println("⚠️ El driver ya estaba cerrado o no fue inicializado.");
        }
    }

    private void BotonPagar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón sea visible y clickeable
            WebElement botonPagar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='tzBTN_PAGAR']")));

            // Hace clic en el botón
            botonPagar.click();
            System.out.println("💰 Botón 'PAGAR' clickeado correctamente.");

        } catch (TimeoutException e) {
            System.out.println("⏳ El botón 'PAGAR' no se encontró en el tiempo esperado.");
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón 'PAGAR':");
            e.printStackTrace();
        }
    }

    private void SeleccionarPeriodoAPagar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Esperar a que el combo esté presente
            WebElement combo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"COMBO_FILTRARPORPERIODO\"]")));

            boolean encontrado = false;

            // Intentar hasta que no haya más "StaleElementReferenceException"
            int intentos = 0;
            while (intentos < 3 && !encontrado) {
                try {
                    // Buscar nuevamente las opciones cada vez para evitar referencias caducadas
                    List<WebElement> opciones = combo.findElements(By.tagName("option"));

                    // Recorrer las opciones y seleccionar la que coincide con el valor de 'seleccionarPeriodo'
                    for (WebElement opcion : opciones) {
                        String textoOpcion = opcion.getText().trim();
                        System.out.println("En busqueda del periodo: " + periodoSeleccionado);
                        // Comparar sin importar mayúsculas o minúsculas
                        if (textoOpcion.toLowerCase().equals(periodoSeleccionado.toLowerCase())) {
                            opcion.click(); // Seleccionar la opción
                            periodoSeleccionado = textoOpcion;
                            System.out.println("✅ Opción seleccionada: " + periodoSeleccionado);
                            encontrado = true;
                            break;
                        }
                    }

                } catch (StaleElementReferenceException e) {
                    // Si ocurre StaleElementReferenceException, esperar y volver a intentar
                    System.out.println("⚠ Elemento desactualizado. Reintentando...");
                    intentos++;
                    Thread.sleep(1000); // Esperar un segundo antes de reintentar
                }
            }

            if (!encontrado) {
                System.out.println("⚠ No se encontró el periodo: " + periodoSeleccionado);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el periodo:");
            e.printStackTrace();
        }
    }


    private void SeleccionarMovimientoBanAleatorio() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que el combo esté presente
            WebElement combo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='COMBO_TIPOMOVIMIENTO']")));

            // Opciones posibles
            String[] opcionesValidas = {"CHEQUE", "TRANSFERENCIA"};
            String seleccion = opcionesValidas[new Random().nextInt(opcionesValidas.length)];

            // Obtener todas las opciones del combo
            List<WebElement> opciones = combo.findElements(By.tagName("option"));

            boolean encontrado = false;

            for (WebElement opcion : opciones) {
                if (opcion.getText().trim().equalsIgnoreCase(seleccion)) {
                    opcion.click();
                    System.out.println("✅ Opción aleatoria seleccionada: " + seleccion);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                System.out.println("⚠ No se encontró la opción aleatoria seleccionada: " + seleccion);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar movimiento aleatorio:");
            e.printStackTrace();
        }
    }

    private void SeleccionarTipoMovimientoAleatorio() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Crear array con los XPath de los radio buttons
            String[] radiosXPath = {
                    "//*[@id='RADIO_TIPOMOVIMIENTO_1']",
                    "//*[@id='RADIO_TIPOMOVIMIENTO_2']"
            };

            // Elegir aleatoriamente uno
            int indiceSeleccionado = new Random().nextInt(radiosXPath.length);
            String xpathSeleccionado = radiosXPath[indiceSeleccionado];

            // Esperar y seleccionar el radio button
            WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathSeleccionado)));
            radio.click();

            System.out.println("✅ Radio button seleccionado aleatoriamente: " + xpathSeleccionado);

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar tipo de movimiento aleatorio:");
            e.printStackTrace();
        }
    }


    private void IngresarReferencia() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar el campo de referencia
            WebElement campoReferencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_REFERENCIA']")));

            // Generar número aleatorio de 6 dígitos
            int numeroAleatorio = new Random().nextInt(900000) + 100000; // entre 100000 y 999999
            String referencia = String.valueOf(numeroAleatorio);

            // Ingresar la referencia
            campoReferencia.clear();
            campoReferencia.sendKeys(referencia);

            System.out.println("✅ Referencia ingresada: " + referencia);

        } catch (Exception e) {
            System.err.println("❌ Error al ingresar referencia:");
            e.printStackTrace();
        }
    }

    private void SeleccionarBotonAceptarPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón esté visible y clickeable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));

            // Hace clic en el botón
            botonAceptar.click();
            System.out.println("✅ Botón 'Aceptar' clickeado.");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el botón 'Aceptar':");
            e.printStackTrace();
        }
    }


    private void MensajesPagos() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            while (true) {
                List<WebElement> botonesOk = driver.findElements(By.xpath("//*[@id='BTN_OK']"));

                if (!botonesOk.isEmpty() && botonesOk.get(0).isDisplayed()) {
                    System.out.println("🔁 Botón 'OK' visible. Haciendo clic...");
                    botonesOk.get(0).click();
                    Thread.sleep(1000); // Pequeña espera para permitir actualización de la página
                } else {
                    System.out.println("✅ Botón 'OK' ya no está visible. Finalizando.");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error al hacer clic en el botón 'OK':");
            e.printStackTrace();
        }
    }

    public void SalirVentanaPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonCancelar;

            try {
                botonCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_CANCELAR']")));
            } catch (Exception noButton) {
                System.out.println("El botón 'Cancelar' no está disponible. Continuando...");
                return;
            }

            botonCancelar.click();
            System.out.println("Botón 'Cancelar' presionado.");
        } catch (Exception e) {
            System.out.println("Error al presionar el botón 'Cancelar'. Continuando...");
            e.printStackTrace();
        }
    }

    public void CancelarAccion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonCancelar;

            try {
                botonCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_CANCELAR']")));
            } catch (Exception noButton) {
                System.out.println("El botón 'Cancelar' no está disponible. Continuando...");
                return;
            }

            botonCancelar.click();
            System.out.println("Botón 'Cancelar' presionado.");
        } catch (Exception e) {
            System.out.println("Error al presionar el botón 'Cancelar'. Continuando...");
            e.printStackTrace();
        }
    }

    @Step("Presionar botón 'Timbrar'")
    public void SeleccionarTimbrar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonTimbrar;

            try {
                botonTimbrar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_TIMBRAR']")));
            } catch (Exception noButton) {
                System.out.println("El botón 'Timbrar' no está disponible. Continuando...");
                return;
            }

            botonTimbrar.click();
            System.out.println("Botón 'Timbrar' presionado.");
        } catch (Exception e) {
            System.out.println("Error al presionar el botón 'Timbrar'. Continuando...");
            e.printStackTrace();
        }
    }

    private void SeleccionarPeriodoTimbrar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que el combo esté presente
            WebElement combo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"COMBO_FILTRARPORPERIODO\"]")));

            boolean encontrado = false;

            // Intentar hasta que no haya más "StaleElementReferenceException"
            int intentos = 0;
            while (intentos < 3 && !encontrado) {
                try {
                    // Buscar nuevamente las opciones cada vez para evitar referencias caducadas
                    List<WebElement> opciones = combo.findElements(By.tagName("option"));

                    // Recorrer las opciones y seleccionar la que coincide con el valor de 'seleccionarPeriodo'
                    for (WebElement opcion : opciones) {
                        String textoOpcion = opcion.getText().trim();
                        if (textoOpcion.equals(periodoSeleccionado)) {
                            opcion.click(); // Seleccionar la opción
                            periodoSeleccionado = textoOpcion;
                            System.out.println("✅ Opción seleccionada: " + periodoSeleccionado);
                            encontrado = true;
                            break;
                        }
                    }

                } catch (StaleElementReferenceException e) {
                    // Si ocurre StaleElementReferenceException, esperar y volver a intentar
                    System.out.println("⚠ Elemento desactualizado. Reintentando...");
                    intentos++;
                    Thread.sleep(1000); // Esperar un segundo antes de reintentar
                }
            }

            if (!encontrado) {
                System.out.println("⚠ No se encontró el periodo: " + periodoSeleccionado);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el periodo:");
            e.printStackTrace();
        }
    }

    private void BotonAceptarParaTimbre() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera a que el botón esté visible y clickeable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));

            // Hace clic en el botón
            botonAceptar.click();
            System.out.println("✅ Botón 'Aceptar' clickeado se realizá el proceso de timbrado.");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar el botón 'Aceptar' del timbrado:");
            e.printStackTrace();
        }
    }

    @Step("Validar los primeros 3 registros timbrados en la tabla de nóminas")
    public void ValidarTimbrados() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar a que la tabla esté visible
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='dwwTABLE_NOMINASTIMBRARMASIVO']")));

            // Obtener todas las filas disponibles
            List<WebElement> filas = driver.findElements(By.xpath("//*[@id='dwwTABLE_NOMINASTIMBRARMASIVO']//tbody/tr"));

            // Limitar el recorrido a máximo 3 registros
            int totalFilas = Math.min(filas.size(), 3);

            for (int i = 1; i <= totalFilas; i++) {
                try {
                    WebElement colEstado = driver.findElement(By.xpath("//*[@id='dwwTABLE_NOMINASTIMBRARMASIVO']//tbody/tr[" + i + "]/td[5]"));
                    String estado = colEstado.getText().trim();

                    if ("Timbrado".equalsIgnoreCase(estado)) {
                        WebElement colInfo = driver.findElement(By.xpath("//*[@id='dwwTABLE_NOMINASTIMBRARMASIVO']//tbody/tr[" + i + "]/td[4]"));
                        String info = colInfo.getText().trim();
                        System.out.println("Registro " + i + " timbrado. Info columna 3: " + info);
                    } else {
                        System.out.println("Registro " + i + " no está timbrado.");
                    }
                } catch (StaleElementReferenceException se) {
                    System.out.println("Elemento obsoleto en fila " + i + ". Continuando...");
                } catch (Exception e) {
                    System.out.println("Error procesando fila " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error general al validar registros timbrados.");
            e.printStackTrace();
        }
    }



    @Step("Salir de la ventana de timbrado")
    public void SalirVentanaTimbrar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonCancelar;

            try {
                botonCancelar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_CANCELAR']")));
            } catch (Exception noButton) {
                System.out.println("El botón 'Cancelar / Salir' no está disponible. Continuando...");
                return;
            }

            botonCancelar.click();
            System.out.println("Se presionó el botón 'Cancelar / Salir' en la ventana de timbrado.");
        } catch (Exception e) {
            System.out.println("Error al intentar salir de la ventana de timbrado. Continuando...");
            e.printStackTrace();
        }
    }

}