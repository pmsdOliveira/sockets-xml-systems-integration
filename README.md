# Sockets XML Systems Integration

## Contains:

Consists of a simulation where cows move around in search of food and avoiding wolves. Wolves, on the contrary, eat cows and avoid dogs. Obstacles can't be stepped on by any entity except Miners. Miners walk around looking for obstacles (diamonds). Dogs are basically moving obstacles: they scare wolves away but don't charge at them.

A Java Program acts as Simulation, providing both backend logic and a GUI through Swing.

Cows and Wolves decisions are taken by 2 C# servers, transfering serialized XML data through Sockets.
Dogs and Miners are controlled by 2 Java servers, also communicating XML through Sockets.
The Java Simulation acts as a client for these 4 servers to get movement updates for all types of entities.

The Java Simulation also functions as a server for a Python client. This communication is held by Sockets and uses JSON serializations. The Python module displays info about the alive entities and a dynamic plot regarding the cow population evolution over time.

## To Do:

Cow breeding.
Stamina system for all the entities.
Java GUI should display "Wolf_X -> Cow_Y" when a kill occurs.
Display more dynamic plots about alive entities and kill/destruction counts for wolves/miners.

## Authors:

This work and was done for the "Systems Integrations" 2020/2021 course at FCT-UNL by Frederico Marques (47359), Guilherme Russo (50760) and Pedro Oliveira (50544) from the Integrated Master's in Computer and Electrical Engineering.
