# Welcome to the Microsoft2 UML Editor
##### Team members: 
Sean, Adam, Aidan, Michelle, Keetyn
# Basic commands
> _ means blank space

> items in quotation, such as 'item' implies a runtime identifier
```
help
add <class/relationship/method/field/param>
remove <class/relationship/method/field/param>
rename <class/relationship/method/field/param>
list <'classname'/class/relationships>
load <'file directory',_>
save <'file directory',_>
quit
```
# Running the program
1. Download and install the java development kit: https://www.oracle.com/java/technologies/downloads/
2. Install Maven
##### For windows
Download Maven from https://maven.apache.org/download.cgi (Specifically the binary zip archive)  
Unzip the maven folder to 'C:/' directory. You should now have a directory called 'C:\apache-maven-3.9.11'.  
Then press 'win + r', in the newly opened diologue box, type 'sysdm.cpl' and click 'ok'.  
A 'System Properties' diologue box should open. Navigate to the 'Advanced' tab. Click 'Environment Variables...'  
Select the 'path' option and select 'edit'.  
Inside of 'Edit environment variable' diologue box, click 'new' and write the following text  
'C:\apache-maven-3.9.11\bin'  
This should reflect the directory you made previously.  
Maven should now be setup.  
##### For linux  
In your terminal write 'sudo apt install maven'  
Maven should now be setup.  
3. Clone this respository  
