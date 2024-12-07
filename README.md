# JavaOctetEditor

This is the continue of JavaOctetEditor. 
Keep the latest library, remove upstream problems and absorb upstream ideas.

![GitHub all releases](https://img.shields.io/github/downloads/Ecdcaeb/JavaOctetEditor/total?style=flat-square)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/Ecdcaeb/JavaOctetEditor?style=flat-square)
![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Ecdcaeb/JavaOctetEditor?include_prereleases&style=flat-square)
![GitHub](https://img.shields.io/github/license/Ecdcaeb/JavaOctetEditor?style=flat-square)

![](https://user-images.githubusercontent.com/32991121/190947407-bbc6642e-2c9d-46f3-921c-6558c74272cf.png)
![](https://user-images.githubusercontent.com/32991121/190947409-9df48d03-e1b7-4c0a-ae1d-08e1ca2bc9aa.png)
![](https://user-images.githubusercontent.com/32991121/190947408-df6c6818-ea79-4a42-8b90-101b6daa3099.png)
![](https://user-images.githubusercontent.com/32991121/190947401-fc08fc4f-3714-49ca-a064-913e7312b191.png)
![](https://user-images.githubusercontent.com/32991121/190947410-4b8f224a-c589-4998-950a-e19618ce5734.png)

## Usage

### File Input
- From the top menu `File` > `Load...`
- From the top menu `File` > `Load Recent`
- Drag the file directly into the window

### File Output
The changes you make are not saved directly into the file, you need to export it.

- top menu `File` > `Save...` for jar.
- top menu `File` > `Save All Sources` for the decompiled source jar.

### Edit

#### Bytecode View
 view only

#### Decompile Edit
 view and edit.
 When it got save (ctrl + s (Configurable)), we will try to recompile it.
 
Edit Config `Application` > `makeDemoRecompileEnvironment` true or only parse Java basic symbols.

The DemoRecompileEnvironment get information from existing bytecode and create demo symbols, but it is not reliable.

#### Visitor Edit

Edit the Visitor code and save (ctrl + s (Configurable)) it.

#### Info Edit

Edit the outline info for class.

### Search
Search Field, Method, LDC Constant, Opcode...

Right click the result could have a operation menu.

### Remapping
- Drag the mapping file directly into the window
- From the top menu `Mapping`

## Summarize
This is a simple and easy to use editor that is much simpler to use than other editors and removes excessive packaging.


