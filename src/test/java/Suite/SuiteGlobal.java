package Suite;

import Bancos.*;
import Cobranza.*;
import Contabilidad.*;
import CuentasPorPagar.*;
import Facturacion.*;
import Indicadores.*;
import Trafico.*;
import org.junit.jupiter.api.*;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobal {

    private static final String[] NAVEGADORES = {"chrome",/*firefox""edge"*/};
    private static final int NUMERO_HILOS =2;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUMERO_HILOS);

    // Mapa global para almacenar los resultados de todas las pruebas en todos los navegadores
    private static final Map<String, Map<String, TestResult>> resultadosGlobales = new LinkedHashMap<>();

    @Test
    @Order(1)
    public void ejecutarPruebasEnTodosLosNavegadores() {
        for (String navegador : NAVEGADORES) {
            System.out.println("\n======================================");
            System.out.println("🚀 Ejecutando pruebas en: " + navegador.toUpperCase());
            System.out.println("======================================");

            long inicioNavegador = System.nanoTime(); // Inicia el temporizador del navegador
            Map<String, TestResult> resultadosPorPrueba = ejecutarPruebasEnNavegador(navegador);
            // Convertir nanosegundos a minutos
            double duracionNavegador = (System.nanoTime() - inicioNavegador) / 60_000_000_000.0;

            resultadosGlobales.put(navegador, resultadosPorPrueba);

            // Almacenar duración total del navegador
            resultadosGlobales.get(navegador).put("TOTAL", new TestResult(true, duracionNavegador));
        }

        // Una vez ejecutadas todas las pruebas, mostramos el resumen global
        mostrarResumenGlobal();
    }

    private Map<String, TestResult> ejecutarPruebasEnNavegador(String navegador) {
        Class<?>[] pruebas = {

                // Indicadores
                IndicadoresTest.class,
                ParametrosGenerales.class,

// Bancos
                ChequesModElim.class,
                MovimientoBancarioModElim.class,

// Cobranza
                PagoFacturaConcepto.class,
                PagoFacturaViaje.class,

// Cuentas por pagar
                PagoPasivos.class,

// Contabilidad
                ImportacionPolizasYPrepolizas.class,
                PolizaManual.class,

// Facturación
                FacturacionGeneral.class,
                FacturacionGeneralDescImpr.class,
                FacturacionGeneralSustitucion.class,
                FacturaConceptoTimbrada.class,
                FacturacionConceptoSustitucion.class,
                FacturacionConceptoDescImpr.class,
                FacturacionListadoViajes.class,
                FacturacionViajeSustitucion.class,

// Tráfico
                CartaPorteComercioExterior.class,
                CartaPorteImpresionDescarga.class,
                CartaPorteSustitucion.class,
                CopiarCartaPorte.class,
                ViajeACartaPorte.class,
                LiquidacionFiscal.class,
                LiquidacionOperativa.class
        };

        Map<String, TestResult> resultados = new LinkedHashMap<>();
        List<Callable<TestResult>> tareas = new ArrayList<>();

        for (Class<?> testClass : pruebas) {
            tareas.add(() -> ejecutarClaseJUnit(testClass, navegador));
        }

        try {
            List<Future<TestResult>> futuros = executorService.invokeAll(tareas);
            for (int i = 0; i < pruebas.length; i++) {
                TestResult resultado = futuros.get(i).get();
                resultados.put(pruebas[i].getSimpleName(), resultado);
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR en ejecución en " + navegador.toUpperCase() + ": " + e.getMessage());
        }

        return resultados;
    }

    /**
     * Este método ejecuta una clase de prueba y utiliza un listener personalizado que
     * verifica si TODOS los tests de la clase pasaron. Si alguno falla, se marca la
     * ejecución de la clase como fallida.
     */
    private TestResult ejecutarClaseJUnit(Class<?> testClass, String navegador) {
        long inicioPrueba = System.nanoTime(); // Inicia el temporizador de la prueba
        try {
            System.out.println("🚀 Iniciando prueba: " + testClass.getSimpleName() + " en " + navegador.toUpperCase());

            System.setProperty("navegador", navegador);

            // Crear el listener que evaluará la ejecución de todos los tests de la clase.
            OverallTestListener listener = new OverallTestListener();

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            Launcher launcher = LauncherFactory.create();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);

            // Cerrar sesión (si es necesario)
            InicioSesion.cerrarSesion();

            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 60_000_000_000.0; // minutos

            // Si alguno de los tests falló, se considera la clase como fallida.
            if (!listener.areAllTestsSuccessful()) {
                throw new RuntimeException("Uno o más tests fallaron en la clase " + testClass.getSimpleName());
            }
            return new TestResult(true, duracionPrueba);
        } catch (Exception e) {
            System.err.println("⚠ ERROR en " + testClass.getSimpleName() + " en " + navegador.toUpperCase() + ": " + e.getMessage());
            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 60_000_000_000.0; // minutos
            return new TestResult(false, duracionPrueba);
        }
    }

    private void mostrarResumenGlobal() {
        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN GLOBAL DE PRUEBAS");
        System.out.println("======================================");

        int totalExitosas = 0;
        int totalFallidas = 0;
        double tiempoTotal = 0;

        for (String navegador : resultadosGlobales.keySet()) {
            Map<String, TestResult> resultadosPorNavegador = resultadosGlobales.get(navegador);
            int exitosas = 0;
            int fallidas = 0;

            //System.out.println("\n🌐 Navegador: " + navegador.toUpperCase());
            for (Map.Entry<String, TestResult> entry : resultadosPorNavegador.entrySet()) {
                if (entry.getKey().equals("TOTAL")) continue; // Saltar el tiempo total del navegador aquí

                TestResult result = entry.getValue();
                String estado = result.exito ? "✅ EXITOSA" : "❌ FALLIDA";
                System.out.printf("%s - %s (⏱ %.2f min)%n", estado, entry.getKey(), result.duracion);

                if (result.exito) {
                    exitosas++;
                } else {
                    fallidas++;
                }
            }

            double tiempoNavegador = resultadosPorNavegador.get("TOTAL").duracion;
            tiempoTotal += tiempoNavegador;

            System.out.println("📌 Resumen en " + navegador.toUpperCase() + ": " + exitosas + " exitosas, " + fallidas + " fallidas.");
            System.out.printf("⏳ Tiempo total en %s: %.2f min%n", navegador.toUpperCase(), tiempoNavegador);
            totalExitosas += exitosas;
            totalFallidas += fallidas;
        }

        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN FINAL");
        System.out.println("✅ Total pruebas exitosas: " + totalExitosas);
        System.out.println("❌ Total pruebas fallidas: " + totalFallidas);
        System.out.printf("⏳ Tiempo total de ejecución: %.2f min%n", tiempoTotal);
        System.out.println("======================================\n");
    }

    // Clase para almacenar el resultado y duración de cada prueba (duración en minutos)
    private static class TestResult {
        boolean exito;
        double duracion; // Duración en minutos

        public TestResult(boolean exito, double duracion) {
            this.exito = exito;
            this.duracion = duracion;
        }
    }

    /**
     * Listener personalizado que registra el estado de cada método de prueba ejecutado.
     * Se asume que si algún método falla, toda la clase se considera fallida.
     */
    private static class OverallTestListener implements TestExecutionListener {
        private boolean allSuccessful = true;

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            // Solo consideramos los métodos de prueba (no contenedores)
            if (testIdentifier.isTest()) {
                if (testExecutionResult.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
                    allSuccessful = false;
                }
            }
        }

        public boolean areAllTestsSuccessful() {
            return allSuccessful;
        }
    }

}