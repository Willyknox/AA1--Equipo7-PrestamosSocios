$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$JdkPath = Join-Path $ScriptDir ".tools\jdk-21.0.10"
$MavenPath = Join-Path $ScriptDir ".tools\apache-maven-3.9.6"

if (-not (Test-Path "$JdkPath\bin\java.exe")) {
    Write-Error "No se encontró java.exe en $JdkPath\bin"
    exit 1
}

$env:JAVA_HOME = $JdkPath
$env:PATH = "$JdkPath\bin;$MavenPath\bin;$env:PATH"

Write-Host "Entorno configurado correctamente."
Write-Host "Usando Java: $(java -version 2>&1 | Select-Object -First 1)"
Write-Host "Usando Maven: $(mvn -version 2>&1 | Select-Object -First 1)"
Write-Host "Iniciando..."

mvn javafx:run
