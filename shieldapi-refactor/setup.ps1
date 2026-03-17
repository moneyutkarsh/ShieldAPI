$base = "d:\ShieldAPI\shieldapi\src\main\java\com\shieldapi\shieldapi"
$src = "C:\Users\Utkarsh Dubey\.gemini\antigravity\scratch\shieldapi-refactor"

# Create directories
$dirs = @("model","dto","repository","service\impl","controller","exception","security","threat")
foreach ($d in $dirs) {
    New-Item -ItemType Directory -Force -Path "$base\$d" | Out-Null
}

# Copy files
Copy-Item "$src\model\Threat.java" "$base\model\" -Force
Copy-Item "$src\dto\ThreatRequest.java" "$base\dto\" -Force
Copy-Item "$src\dto\ThreatResponse.java" "$base\dto\" -Force
Copy-Item "$src\repository\ThreatRepository.java" "$base\repository\" -Force
Copy-Item "$src\service\ThreatService.java" "$base\service\" -Force
Copy-Item "$src\service\impl\ThreatServiceImpl.java" "$base\service\impl\" -Force
Copy-Item "$src\controller\ThreatController.java" "$base\controller\" -Force
Copy-Item "$src\exception\GlobalExceptionHandler.java" "$base\exception\" -Force
Copy-Item "$src\security\SecurityConfig.java" "$base\security\" -Force
Copy-Item "$src\threat\ThreatAnalyzer.java" "$base\threat\" -Force

Write-Host ""
Write-Host "=== ShieldAPI Layered Architecture Setup Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Files created:"
Get-ChildItem -Recurse "$base" -Filter "*.java" | ForEach-Object { Write-Host "  [+] $($_.FullName)" -ForegroundColor Cyan }
