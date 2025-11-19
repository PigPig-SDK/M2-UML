# Welcome to the Microsoft2 UML Editor
**Team Members:** Adam, Aidan, Keetyn, Michelle, Sean, Ty Greene

# Downloading the program (No compilation required)
1. **Install Java Development Kit (JDK) (Specifically Java 25)**  
   Download and install the latest JDK from [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/).
2. **Download the executable**  
   [Gui 2.0 Release](https://github.com/jprichard1ewu/microsoft-2/releases/tag/Sprint3)

   Enjoy!

# Compiling the program 
1. **Install Java Development Kit (JDK)**  
   Download and install the latest JDK from [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/).
2. **Clone the Repository**  
3. **Build the Project**  
In the cloned repository, open your terminal and run  
`./mvnw clean package`  
Alternatively, if you only want to compile and run the project  
Execute `compilenrun.sh`

# Terminal commands
> **Note:** `_` represents a blank space.  
> Items in quotation marks, e.g., `'item'`, indicate runtime identifiers.

| Command | Description |
|---------|-------------|
| `help` | Prints helpful information about available commands. |
| `add <class/relationship/method/field/param>` | Adds a new item to your UML document. |
| `remove <class/relationship/method/field/param>` | Removes an existing item from your UML document. |
| `rename <class/relationship/method/field/param>` | Renames an existing item in your UML document. |
| `list <'classname'/class/relationships>` | Lists information about a class or all relationships. |
| `load <'file directory',_>` | Loads a previously saved UML document. |
| `save <'file directory',_>` | Saves the current UML document to disk. |
| `undo` | Revert the last change you made |
| `redo` | Reapply a change you previously undid |
| `quit` | Exits the UML editor application. |
| **EXPERIMENTAL** |  |
| `net <host <port>, connect <ip:port>, say, list, disconnect>` | Network commands for connecting to a server, disconnecting, hosting, talking, ect... |

