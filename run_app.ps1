$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$JdkPath = Join-Path $ScriptDir ".tools\jdk-21.0.10"
$MavenPath = Join-Path $ScriptDir ".tools\apache-maven-3.9.6"

if (Test-Path "$JdkPath\bin\java.exe") {
    $env:JAVA_HOME = $JdkPath
    $env:PATH = "$JdkPath\bin;$MavenPath\bin;$env:PATH"
}
else {
    # Try checking common paths if environment is not set
    $IntelliJJava = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.5\jbr"
    $IntelliJMaven = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.5\plugins\maven\lib\maven3"

    if (-not $env:JAVA_HOME -and (Test-Path "$IntelliJJava\bin\java.exe")) {
        Write-Host "Usando Java de IntelliJ: $IntelliJJava"
        $env:JAVA_HOME = $IntelliJJava
        $env:PATH = "$IntelliJJava\bin;$env:PATH"
    }
    elseif ($env:JAVA_HOME) {
        Write-Host "Usando JAVA_HOME del sistema: $env:JAVA_HOME"
    }
    else {
        # Try to find java in path
        if (Get-Command "java" -ErrorAction SilentlyContinue) {
            Write-Host "Java encontrado en el PATH del sistema."
        }
        else {
            Write-Error "No se encontró Java en el sistema, .tools, ni IntelliJ. Por favor instala Java 21."
            exit 1
        }
    }

    # Maven setup
    if (Test-Path "$MavenPath\bin\mvn.cmd") {
        $env:PATH = "$MavenPath\bin;$env:PATH"
    }
    elseif (Test-Path "$IntelliJMaven\bin\mvn.cmd") {
        Write-Host "Usando Maven de IntelliJ: $IntelliJMaven"
        $env:PATH = "$IntelliJMaven\bin;$env:PATH"
    }
    else {
        # Check if mvn is in path
        if (-not (Get-Command "mvn" -ErrorAction SilentlyContinue)) {
            Write-Error "No se encontró Maven. Por favor instálalo o verifica tu configuración."
            exit 1
        }
    }
}

Write-Host "Entorno configurado correctamente."
Write-Host "Usando Java: $(java -version 2>&1 | Select-Object -First 1)"
Write-Host "Usando Maven: $(mvn -version 2>&1 | Select-Object -First 1)"
Write-Host "Iniciando..."

mvn javafx:run
