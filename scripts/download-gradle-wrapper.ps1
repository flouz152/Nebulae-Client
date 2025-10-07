$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Resolve-Path (Join-Path $Root "..")
$WrapperDir = Join-Path $ProjectRoot "clickgui-mod/gradle/wrapper"
$JarPath = Join-Path $WrapperDir "gradle-wrapper.jar"
$GradleVersion = "7.6.1"
$WrapperUrl = "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip"
$TempDir = New-Item -ItemType Directory -Path ([System.IO.Path]::GetTempPath()) -Name ("gradle-wrapper-" + [System.Guid]::NewGuid())
$ZipPath = Join-Path $TempDir "gradle-$GradleVersion.zip"

Write-Host "Downloading Gradle $GradleVersion distribution..."
Invoke-WebRequest -Uri $WrapperUrl -OutFile $ZipPath -UseBasicParsing

Write-Host "Extracting gradle-wrapper.jar..."
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($ZipPath)
try {
    $entry = $zip.Entries | Where-Object { $_.FullName -eq "gradle-$GradleVersion/lib/gradle-wrapper.jar" }
    if (-not $entry) {
        throw "gradle-wrapper.jar not found in distribution archive."
    }
    New-Item -ItemType Directory -Force -Path $WrapperDir | Out-Null
    $entryStream = $entry.Open()
    $fileStream = [System.IO.File]::Create($JarPath)
    try {
        $entryStream.CopyTo($fileStream)
    } finally {
        $entryStream.Dispose()
        $fileStream.Dispose()
    }
    Write-Host "gradle-wrapper.jar saved to $JarPath"
} finally {
    $zip.Dispose()
    Remove-Item -Recurse -Force $TempDir
}
