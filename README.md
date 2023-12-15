This project started off as a school project for CS 240, but has some more complicated elements. 
1. Database access objects: The models and their database counterparts where implemented in a extensible manner using the idea of Field objects and TableSpec object so that new fields can be easily implemented within code. 
2. Winapi usage: Using another project [winapi-terminal](bob), java uses jni to take advantage of winapi method calls for writing to the terminal and receiving input events from the console. This is not nessecary assuming a purely ANSI based implementation, but this allowed practice using jni.
3. JWT-like console rendering: Taking inspiration from the organization of jwt, a component system was developed with the ability to render text and accept input using text fields using coordinates.
4. Using maven: The project was developed using intellij, but learning about and implementing maven makes the project buildable from any computer and ide.
