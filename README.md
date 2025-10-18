# Welcome to the Microsoft2 UML Editor
**Team Members:** Adam, Aidan, Keetyn, Michelle, Sean, Ty Greene
# Basic commands
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
| `quit` | Exits the UML editor application. |

# Compiling & Running the program
1. **Install Java Development Kit (JDK)**  
   Download and install the latest JDK from [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/).
2. **Clone the Repository**  
3. **Build the Project** 
In the cloned repository, Run the Maven wrapper depending on your operating system:

| Operating System | Command |
|-----------------|---------------------|
| Windows         | `.\mvnw package`     |
| Mac / Linux     | `./mvnw package`     |

6. **Navigate to the 'Target' Sub-directory**
```bash
cd target
```
7. **Run the application**
```bash
java -jar UMLProject-1.0-SNAPSHOT.jar
```
8.**Verify that this process didn’t totally blow your socks off.**

Success! You should have compiled the app and successfully opened it in your terminal
