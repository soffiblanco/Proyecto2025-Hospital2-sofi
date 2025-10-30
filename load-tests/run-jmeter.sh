#!/bin/bash
# Script para ejecutar pruebas de stress con JMeter

cd "$(dirname "$0")/jmeter"

echo "Ejecutando pruebas de stress con JMeter..."
echo "Nota: Asegúrate de que el backend esté corriendo en http://localhost:8080"
echo ""

# Crear directorio de resultados si no existe
mkdir -p results

# Ejecutar JMeter en modo headless (sin GUI)
# -n: modo no-GUI
# -t: archivo de prueba
# -l: archivo de log de resultados (.jtl)
# -j: archivo de log de JMeter
# -e: generar reporte HTML al finalizar
# -o: directorio de salida del reporte HTML

jmeter -n -t stress_test.jmx \
       -l results/stress_results.jtl \
       -j results/jmeter.log \
       -e -o results/html-report

echo ""
echo "Prueba completada."
echo ""
echo "Resultados:"
echo "  - Log de resultados: results/stress_results.jtl"
echo "  - Reporte HTML: results/html-report/index.html"
echo "  - Log de JMeter: results/jmeter.log"
echo ""
echo "Para ver el reporte HTML, abre: results/html-report/index.html"
