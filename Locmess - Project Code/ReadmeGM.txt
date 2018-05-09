install genymotion
setting in android studio file->setting->pluggins->type genymotions->install it
restart it
setting in android studio file->setting->other setting->Genymotion-> type path to ->%cd%\Genymobile\Genymotion
setting in genymotion->setting->ADB->type path to->%cd%\android\SDK'


open: Termite-Cli\etc\platform\windows\backends.conf
configure:
"sdk": the full path to Android SDK
"path": the full path to Genymotion
"vmiprefix": the original name of the virtual device, in this case: "TVD - 5.0.0 - API 21 - 768x1280"
"numclones": the number of available clones, in this case: 2


open: Termite-Cli/etc/termite.conf
set "init" to "simplechat-gm.termite"  //which correspondes to the name of a termite script located in Termite-Cli/etc/init

cmd:
set TERMITE_CLI_PATH=$env$\Termite-Cli
set TERMITE_PLATFORM=windows

termite.bat

newdevice A		//create devices
newdevide B

list devices	//see devices

assignaddr e1	//assign address to e1

list emus		//see emulator

binddevice A e1	//bind device to emulator

list devices	//see what changed

ping			//click "WiFi On" on emulator

move A (B)
list neighbors

creategroup A (B)	//create groups

list groups

joint B (A)			//join group

commit

**Error: KO: unknown command, try 'help**
erase c:\Users\<current_user>\.emulator_console_auth_token


END