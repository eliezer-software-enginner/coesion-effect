# Tutorial

This tutorial will guide you through setting up your desktop application with everything ready to go.

## 💖 Support the project and get featured as a contributor!

If this project has been helpful or inspiring, consider making a donation to help it grow.

As a thank you, your name will be proudly listed in the README.
[Donate now](https://buymeacoffee.com/plantfall)

## Getting the Application

Here you going to see how to initialize your own project.

See Video Tutorial
[Watch Now](https://youtu.be/HJPHG8Bqq98)

Initialize your own project:

1. Clone this project

```bash
git clone https://github.com/plantfall/Coesion-Effect.git
```

## Building your App

```bash
mvn clean package
```

## Distributing Your App

After you have builded it you can distribute easily:

1. Open your terminal and run create-installer.bat

```bash
.\scripts\create-installer.bat
```

After that, your app will be generated in the `dist` folder:

- The `.exe` will be inside `dist/MyApp`
- The `.msi` installer will be inside `dist/`

## Customizing Your App

To update metadata like description, icon, version, and vendor name, edit the `jpackage` section inside `scripts/create-installer.bat.`

## Contribute

Want to contribute?
Feel free to open a PR and become part of the team behind this open-source project!

## Requirements

Make sure you have the following installed for building purposes:

- WiX Toolset (required to generate MSI installers)
  [Download WixToolset 3.14.1(wix314.exe)](https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm)

Then install the app and procced with installation steps. After that you have to set the variable path.
![wix_tollset_path](https://github.com/user-attachments/assets/d92cc6ec-fdd9-4eac-bb82-1c878fa66937)
