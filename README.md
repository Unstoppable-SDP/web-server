# web-server

This is a multi-part Java application that simulates a web server by using a thread pool to handle incoming requests from clients. The application consists of several classes that work together to manage the thread pool, handle incoming requests, and log server activity.

## Requirements

- Java JDK SE8 or higher

## Running

you can pass diffrents arguments to the server by adding the following commands:

```bash
1> java -classpath .;webserve.jar WebServer 5003 8 16 DRPT or
2> java -classpath .;webserve.jar WebServer 5003 8 16 or
3> java -classpath .;webserve.jar WebServer 5003 8 or
4> java -classpath .;webserve.jar WebServer 5003 or
5> java -classpath .;webserve.jar WebServer
```

To run the application using the provided scripts:

### Windows

use the clean.bat script to clean the project directory

use run.bat script to compile and run the application. This will compile all the .java files in the project directory and run the SimpleWebServer class, which starts the server.

### Linux/macOS

_note: After transferring Makefile.txt to a Linux system, you should remove the .txt extension_
Makefile
The Makefile is used to compile and run the application.

To run the application, type:

```bash
make run
```

This will run the ThreadPool class, which starts the server.

To clean the project directory by removing any compiled .class files, type:

```bash
make clean
```

## Classes

### ThreadPool

The ThreadPool class represents a thread pool that manages a fixed number of threads to handle multiple client requests in a server application. It uses a buffer to store incoming requests and a semaphore to manage thread concurrency. It also provides methods for setting the pool size, buffer size, and overload handling method, as well as a method for adding incoming requests to the buffer. The destroy method is used to stop all threads and clear the buffer.

### PoolSingleThread

The PoolSingleThread class represents an individual thread in the thread pool managed by the ThreadPool class. Each thread is responsible for dequeuing requests from the buffer and serving them using the ServeWebRequest class. The class implements the Runnable interface and provides methods for starting, stopping, and checking the status of the thread. It also uses semaphores to manage thread concurrency and a flag to indicate when the thread is done.

### ServeWebRequest

The ServeWebRequest class represents the logic for serving incoming requests from clients. It takes a Socket object as a parameter and serves the request by reading the input stream from the socket and writing the response to the output stream. It also logs server activity using the LogFile class.

### LogFile

The LogFile class provides a method for writing messages to a log file named "webserver-log". The logFileOutput method takes a message parameter and uses a semaphore to ensure that only one thread can access the log file at a time. It first checks if the log file exists, and creates it if it does not. It then appends the message to the log file and releases the semaphore to allow other threads to access the file.

### monitor

The monitor class represents a thread that monitors the status of the threads in the thread pool managed by the ThreadPool class. It checks if any of the threads have finished running and replaces them with new threads if needed. The class uses a list of PoolSingleThread objects to represent the threads in the pool, and a queue to store incoming requests. It also uses semaphores to manage thread concurrency and a flag to indicate when the thread is done.

```

```
