@echo off
java --module-path lib --add-modules javafx.controls --enable-native-access=javafx.graphics -Djava.library.path=lib -Dprism.verbose=true -Dprism.order=sw -cp out hum.Launcher
