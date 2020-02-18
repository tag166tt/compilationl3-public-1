@ECHO OFF
rd /S /Q sc
del ..\test\input\*.sa /s
del ..\test\input\*.sc /s
del ..\test\input\*.ts /s
del ..\test\input\*.xml /s
del ..\test\input\*.c3a /s
java -jar ../sablecc.jar grammaireL.sablecc
