git config core.hooksPath .githooks
if ($LASTEXITCODE -ne 0) {
    exit 1
}

if (-Not (Test-Path ".githooks/pre-commit")) {
    Write-Error "Missing .githooks/pre-commit"
    exit 1
}

try {
    $javaVersion = java -version 2>&1
} catch {
    exit 1
}

if (-Not (Test-Path "scripts/plantuml.jar")) {
    exit 1
}

New-Item -ItemType Directory -Force -Path "docs/uml" | Out-Null

Write-Host "Completed" -ForegroundColor Gray
