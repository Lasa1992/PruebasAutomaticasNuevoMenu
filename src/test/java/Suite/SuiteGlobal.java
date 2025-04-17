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

import java.util.*;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobal {

    private static final String[] NAVEGADORES = {"chrome", /*"firefox"*/};
    private static final int NUMERO_HILOS = 2;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUMERO_HILOS);

    private static final Map<String, Map<String, TestResult>> resultadosGlobales = new LinkedHashMap<>();

    @Test
    @Order(1)
    public void ejecutarPruebasEnTodosLosNavegadores() {
        for (String navegador : NAVEGADORES) {
            System.out.println("\n======================================");
            System.out.println("🚀 Ejecutando pruebas en: " + navegador.toUpperCase());
            System.out.println("======================================");

            long inicioNavegador = System.nanoTime();
            Map<String, TestResult> resultadosPorPrueba = ejecutarPruebasEnNavegador(navegador);
            double duracionNavegador = (System.nanoTime() - inicioNavegador) / 60_000_000_000.0;

            resultadosGlobales.put(navegador, resultadosPorPrueba);
            resultadosGlobales.get(navegador).put("TOTAL", new TestResult(true, duracionNavegador));
        }

        mostrarResumenGlobal();
    }

    private Map<String, TestResult> ejecutarPruebasEnNavegador(String navegador) {
        Class<?>[] pruebas = {
                // 👉 Aquí agregas tus pruebas
                retrasmision.class,
                retrasmision.class
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

    private TestResult ejecutarClaseJUnit(Class<?> testClass, String navegador) {
        long inicioPrueba = System.nanoTime();
        try {
            System.out.println("🚀 Iniciando prueba: " + testClass.getSimpleName() + " en " + navegador.toUpperCase());

            System.setProperty("navegador", navegador);
            System.setProperty("ejecutadoPorSuite", "true"); // Opcional, por si las pruebas quieren saber

            OverallTestListener listener = new OverallTestListener();
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            Launcher launcher = LauncherFactory.create();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);

            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 60_000_000_000.0;

            if (!listener.areAllTestsSuccessful()) {
                throw new RuntimeException("Uno o más tests fallaron en la clase " + testClass.getSimpleName());
            }

            return new TestResult(true, duracionPrueba);

        } catch (Exception e) {
            System.err.println("⚠ ERROR en " + testClass.getSimpleName() + " en " + navegador.toUpperCase() + ": " + e.getMessage());
            e.printStackTrace();
            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 60_000_000_000.0;
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

            for (Map.Entry<String, TestResult> entry : resultadosPorNavegador.entrySet()) {
                if (entry.getKey().equals("TOTAL")) continue;

                TestResult result = entry.getValue();
                String estado = result.exito ? "✅ EXITOSA" : "❌ FALLIDA";
                System.out.printf("%s - %s (⏱ %.2f min)%n", estado, entry.getKey(), result.duracion);

                if (result.exito) exitosas++;
                else fallidas++;
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

    private static class TestResult {
        boolean exito;
        double duracion;

        public TestResult(boolean exito, double duracion) {
            this.exito = exito;
            this.duracion = duracion;
        }
    }

    private static class OverallTestListener implements TestExecutionListener {
        private boolean allSuccessful = true;

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
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
