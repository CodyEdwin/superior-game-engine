# Maven Wrapper Script

# Resolve symbolic links
SCRIPT_PATH="$0"
while [ -h "$SCRIPT_PATH" ]; do
  SCRIPT_PATH=$(readlink "$SCRIPT_PATH")
done

# Get directory containing the script
SCRIPT_DIR=$(dirname "$SCRIPT_PATH")

# Get Java command
JAVA_CMD=java

# Check if Java 21 is available
JAVA_VERSION=$($JAVA_CMD -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -eq 21 ] || [ "$JAVA_VERSION" -eq 25 ]; then
    echo "✓ Java 21+ detected"
else
    echo "✗ Java 21+ required. Current version: $JAVA_VERSION"
    exit 1
fi

# Navigate to project directory
cd "$SCRIPT_DIR"

# Maven goals
GOAL=$1
shift

case "$GOAL" in
    "build"|"compile")
        echo "Building Superior Game Engine..."
        mvn clean compile
        ;;
    "test")
        echo "Running tests..."
        mvn test
        ;;
    "demo")
        echo "Running demo game..."
        mvn exec:java -Dexec.mainClass="com.minimax.game.engine.demo.DemoGame"
        ;;
    "package")
        echo "Packaging engine..."
        mvn clean package
        echo "Engine packaged to target/superior-game-engine-1.0.0.jar"
        ;;
    "clean")
        echo "Cleaning project..."
        mvn clean
        ;;
    "run")
        echo "Running engine..."
        mvn exec:java -Dexec.mainClass="com.minimax.game.engine.demo.DemoGame"
        ;;
    "install")
        echo "Installing dependencies..."
        mvn install
        ;;
    "javadoc")
        echo "Generating Javadoc..."
        mvn javadoc:javadoc
        echo "Documentation available in target/site/apidocs/"
        ;;
    "help")
        echo "Superior Game Engine - Maven Wrapper"
        echo ""
        echo "Available commands:"
        echo "  ./mvnw build     - Compile the engine"
        echo "  ./mvnw test      - Run unit tests"
        echo "  ./mvnw demo      - Run the demo game"
        echo "  ./mvnw package   - Create JAR package"
        echo "  ./mvnw clean     - Clean build artifacts"
        echo "  ./mvnw run       - Alias for demo"
        echo "  ./mvnw install   - Install dependencies"
        echo "  ./mvnw javadoc   - Generate documentation"
        echo "  ./mvnw help      - Show this help"
        ;;
    *)
        echo "Unknown command: $GOAL"
        echo "Use './mvnw help' for available commands"
        exit 1
        ;;
esac
