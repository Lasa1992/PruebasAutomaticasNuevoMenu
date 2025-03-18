

package Suite;

import Bancos.ChequesModElim;
import Cobranza.PagoFacturaConcepto;
import Cobranza.PagoFacturaViaje;
import Contabilidad.ImportacionPolizasYPrepolizas;
import Contabilidad.PolizaManual;
import CuentasPorPagar.PagoPasivos;
import Facturacion.*;
import Indicadores.InicioSesion;
import Indicadores.ParametrosGenerales;
import Trafico.*;
import org.junit.jupiter.api.*;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.*;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobal {

    private static final String[] NAVEGADORES = {"chrome", "firefox", "edge"};
    private static final int NUMERO_HILOS = 4;
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
            long duracionNavegador = (System.nanoTime() - inicioNavegador) / 1_000_000; // Convertir a milisegundos

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
                InicioSesion.class,
                ParametrosGenerales.class,

                // Bancos
                ChequesModElim.class,

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
            String nombrePrueba = testClass.getSimpleName();
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
        long inicioPrueba = System.nanoTime(); // Inicia el temporizador de la prueba
        try {
            System.out.println("🚀 Iniciando prueba: " + testClass.getSimpleName() + " en " + navegador.toUpperCase());

            System.setProperty("navegador", navegador);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            Launcher launcher = LauncherFactory.create();
            launcher.execute(request);

            InicioSesion.cerrarSesion();
            long duracionPrueba = (System.nanoTime() - inicioPrueba) / 1_000_000; // Convertir a milisegundos
            return new TestResult(true, duracionPrueba);
        } catch (Exception e) {
            System.err.println("⚠ ERROR en " + testClass.getSimpleName() + " en " + navegador.toUpperCase() + ": " + e.getMessage());
            long duracionPrueba = (System.nanoTime() - inicioPrueba) / 1_000_000; // Convertir a milisegundos
            return new TestResult(false, duracionPrueba);
        }
    }

    private void mostrarResumenGlobal() {
        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN GLOBAL DE PRUEBAS");
        System.out.println("======================================");

        int totalExitosas = 0;
        int totalFallidas = 0;
        long tiempoTotal = 0;

        for (String navegador : resultadosGlobales.keySet()) {
            Map<String, TestResult> resultadosPorNavegador = resultadosGlobales.get(navegador);
            int exitosas = 0;
            int fallidas = 0;

            System.out.println("\n🌐 Navegador: " + navegador.toUpperCase());
            for (Map.Entry<String, TestResult> entry : resultadosPorNavegador.entrySet()) {
                if (entry.getKey().equals("TOTAL")) continue; // Saltar el tiempo total del navegador aquí

                TestResult result = entry.getValue();
                String estado = result.exito ? "✅ EXITOSA" : "❌ FALLIDA";
                System.out.printf("%s - %s (⏱ %d ms)%n", estado, entry.getKey(), result.duracion);

                if (result.exito) {
                    exitosas++;
                } else {
                    fallidas++;
                }
            }

            long tiempoNavegador = resultadosPorNavegador.get("TOTAL").duracion;
            tiempoTotal += tiempoNavegador;

            System.out.println("📌 Resumen en " + navegador.toUpperCase() + ": " + exitosas + " exitosas, " + fallidas + " fallidas.");
            System.out.println("⏳ Tiempo total en " + navegador.toUpperCase() + ": " + tiempoNavegador + " ms");
            totalExitosas += exitosas;
            totalFallidas += fallidas;
        }

        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN FINAL");
        System.out.println("✅ Total pruebas exitosas: " + totalExitosas);
        System.out.println("❌ Total pruebas fallidas: " + totalFallidas);
        System.out.println("⏳ Tiempo total de ejecución: " + tiempoTotal + " ms");
        System.out.println("======================================\n");
    }

    // Clase para almacenar el resultado y duración de cada prueba
    private static class TestResult {
        boolean exito;
        long duracion; // Duración en milisegundos

        public TestResult(boolean exito, long duracion) {
            this.exito = exito;
            this.duracion = duracion;
        }
    }
}
