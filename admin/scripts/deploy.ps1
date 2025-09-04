# server config
$remoteDir = "/opt/iris/admin"

# clean
Remove-Item -Force -Recurse dist*

npm run build
if ($LASTEXITCODE -ne 0) {
    exit 0
}

# compress dist directory
$sevenZip = "C:\Program Files\7-Zip\7z.exe"
& "$sevenZip" a dist.7z ./dist/*
if ($LASTEXITCODE -ne 0) {
    exit 0
}

Write-Host "upload dist.7z ..."
scp -P $env:srvPort dist.7z "$env:srvUser@${env:srvHost}:"
if ($LASTEXITCODE -ne 0) {
    exit 0
}
# remote extract
Write-Host " extract dist.7z ..."
ssh -p $env:srvPort $env:srvUser@$env:srvHost "rm -rf $remoteDir/* && 7za x dist.7z -o$remoteDir && find $remoteDir -type d -exec chmod 755 {} \;"

Write-Host "deploy finished"
