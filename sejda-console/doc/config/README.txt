This folder contains alternative configuration files for Sejda. The sejda-console application is shipped with it's own sejda.xml but it can be instructed to used an alternative configuration file.
To use the alternative configuration:
- On Mac/Linux: edit the file bin/sejda-console adding: JAVA_OPTS='-Dsejda.config.file=PATH_TO_ALTERNATIVE_CONFIG'
- On Windows: edit the file bin/sejda-console.bat adding: set JAVA_OPTS='-Dsejda.config.file=PATH_TO_ALTERNATIVE_CONFIG'

-sambox-sejda.xml is a Sejda configuration file where sambox tasks implementations are used when available.