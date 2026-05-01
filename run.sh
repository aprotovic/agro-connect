#!/bin/bash

# ===== Agro-Connect Run Script =====
# Starts the server application

echo "======================================"
echo "   Starting Agro-Connect Server"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if build directory exists
if [ ! -d "build" ] || [ -z "$(ls -A build 2>/dev/null)" ]; then
    echo -e "${RED}✗ Build directory not found or empty${NC}"
    echo "Please run ./build.sh first"
    exit 1
fi

# Find MySQL JDBC driver
MYSQL_JAR=$(find lib -name "mysql-connector*.jar" 2>/dev/null | head -n 1)

if [ -z "$MYSQL_JAR" ]; then
    echo -e "${RED}✗ MySQL JDBC driver not found in lib/ directory${NC}"
    echo "Please download mysql-connector-java from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

echo -e "${GREEN}✓ Using JDBC driver: $MYSQL_JAR${NC}"

# Start the server
echo ""
echo "Starting server on http://localhost:8080"
echo "Press Ctrl+C to stop the server"
echo ""
echo "======================================"
echo ""

java -cp "build:lib/*" AgroConnectServer

echo ""
echo "Server stopped"
