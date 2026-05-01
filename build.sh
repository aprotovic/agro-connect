#!/bin/bash

# ===== Agro-Connect Build Script =====
# Compiles Java sources and prepares the application

echo "======================================"
echo "   Agro-Connect Build Script"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo -e "${RED}✗ Java compiler (javac) not found!${NC}"
    echo "Please install JDK 8 or higher"
    exit 1
fi

echo -e "${GREEN}✓ Java compiler found${NC}"
java -version

# Create build directory
echo ""
echo "Creating build directory..."
mkdir -p build
echo -e "${GREEN}✓ Build directory created${NC}"

# Check for MySQL JDBC driver
echo ""
echo "Checking for MySQL JDBC driver..."
if [ ! -f "lib/mysql-connector-java-8.0.33.jar" ] && [ ! -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    echo -e "${YELLOW}⚠ MySQL JDBC driver not found in lib/ directory${NC}"
    echo "Please download mysql-connector-java from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    echo "And place it in the lib/ directory"
    echo ""
    echo "Note: The application will compile but won't run without the JDBC driver"
    echo ""
fi

# Compile Java sources
echo ""
echo "Compiling Java sources..."
find src -name "*.java" > sources.txt

CLASSPATH="lib/*"
javac -d build -cp "$CLASSPATH" @sources.txt

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
    rm sources.txt
else
    echo -e "${RED}✗ Compilation failed${NC}"
    rm sources.txt
    exit 1
fi

# Count compiled classes
CLASS_COUNT=$(find build -name "*.class" | wc -l)
echo -e "${GREEN}✓ Compiled $CLASS_COUNT class files${NC}"

echo ""
echo "======================================"
echo "   Build Complete!"
echo "======================================"
echo ""
echo "Next steps:"
echo "1. Make sure MySQL is running in XAMPP"
echo "2. Import database/schema.sql"
echo "3. Import database/sample_data.sql"
echo "4. Run: ./run.sh"
echo ""
