# System Documentation

The System Documentation site was created in order to document the details 
of the configuration of all of my systems. Each page documents a specific 
area of the system configuration.  THe following areas are documented:

| System | System |
| --- | --- |
| Windows 11 Laptop | Nook Reader        |
| Windows 10 Laptop | LinkSys Router     |
| Android Phone     | HP LaserJet P1102w |



"C:\Program Files\Git\git-bash.exe" --cd="C:\Src\txt2tags\docGen\src"

Credential Manager - Remove Window git entries

https://docs.github.com/en/account-and-profile/how-tos/setting-up-and-managing-your-personal-account-on-github/managing-your-personal-account/managing-multiple-accounts


git config --global credential.https://github.com.useHttpPath true
git push --set-upstream origin main


git remote show
git remote get-url origin


Create new repository
echo "# JTxt2Tags" >> README.md
git init
git add .
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/EdGit97/JTxt2Tags.git
git push -u origin ain