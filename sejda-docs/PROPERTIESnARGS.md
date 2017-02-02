System properties
=============
A list of system properties that users can configure to alter Sejda behavior.

**sejda.config.file**  
*values:* a string with the configuration filename  
*default:* sejda.xml  
*description:* let the user specify the name of the Sejda config file. If specified, the file is searched in the classpath first and on the filesystem as fallback, if not specified the default 'sejda.xml' is searched in the classpath.  


**sejda.perform.schema.validation**  
*values:* true|false  
*default:* true  
*description:* if set to true Sejda will perform XSD validation of the supplied xml configuration file.  


**sejda.unethical.read**  
*values:* true|false  
*default:* false  
*description:* activates what in iText is called 'unethical read' which bypass document permissions where possible.  

