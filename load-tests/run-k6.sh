#!/bin/bash
# Script para ejecutar pruebas de stress con k6

cd "$(dirname "$0")/k6"

echo "Ejecutando pruebas de stress con k6..."
echo "Nota: Asegúrate de que el backend esté corriendo en http://localhost:8080"
echo ""

# Ejecutar k6 con salida en consola
k6 run stress.js

# Opcional: guardar resultados en JSON
# k6 run stress.js --out json=results/k6_results.json

echo ""
echo "Prueba completada. Revisa los resultados arriba."
