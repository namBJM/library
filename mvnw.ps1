$ErrorActionPreference = 'Stop'
$mavenVersion = '3.9.11'
$cacheDir = Join-Path $env:LOCALAPPDATA 'LibMan\tools'
$mavenDir = Join-Path $cacheDir "apache-maven-$mavenVersion"
$mavenCmd = Join-Path $mavenDir 'bin\mvn.cmd'
if (-not (Test-Path -LiteralPath $mavenCmd)) {
    New-Item -ItemType Directory -Path $cacheDir -Force | Out-Null
    $zipFile = Join-Path $cacheDir "apache-maven-$mavenVersion-bin.zip"
    $baseUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
    Write-Host "Downloading Apache Maven $mavenVersion..."
    Invoke-WebRequest -UseBasicParsing -Uri $baseUrl -OutFile $zipFile
    $expectedHash = (Invoke-WebRequest -UseBasicParsing -Uri "$baseUrl.sha512").Content.Trim().Split(' ')[0]
    $hasher = [System.Security.Cryptography.SHA512]::Create()
    $stream = [System.IO.File]::OpenRead($zipFile)
    try { $actualHash = [BitConverter]::ToString($hasher.ComputeHash($stream)).Replace('-', '') }
    finally { $stream.Dispose(); $hasher.Dispose() }
    if ($actualHash -ine $expectedHash) { throw 'Maven checksum mismatch.' }
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zipFile, $cacheDir)
}
Push-Location $PSScriptRoot
try { & $mavenCmd @args; $resultCode = $LASTEXITCODE } finally { Pop-Location }
exit $resultCode
