# IntelligentSystems - Home Energy Trading System 


##Environment Setup (for Development)

1. Install Jade, Eclipse & Git

2. Using Git, download repo on local machine

3. Create a new Java project in Eclipse from the local repo folder, ensuring that the default project folder location also points directly to the repo folder-

..* Git repository at: https://github.com/JaclynSeychell/IntelligentSystems-


4. Install and add required JAVA libraries to the project

..* jade.jar
..* commons-codec-1.3.jar
..* jgoodies-forms-1.8.0.jar
..* jgoodies-forms-1.8.0-sources.jar

##Configuration Setup (for Running)

1. In eclipse create an initial run configuration to start GUI
..* class: TradingProgram
..* parameters: [none]]
2. The JADE GUI will also open. In the JADE GUI, start running a sniffer agent to track all actions of agents
..* Tools > Start Sniffer
..* Select "Do sniff this agent" for applicable containers of agents in the order of Home, Broker, then Retailers R1, R2 & R3

3. With the ConfigGUI Window now open, set up the parameters of each agent, and press the 'Run Configuration' button to launch the ProgramGUI Window

4. From the ProgramGUI Window, you can now see a visualisation of the trading processes, with a global list of updates, and each agent represented diagrammatically to the left.

5. To launch a new configuration, close the ProgramGUI, update the parameters, and relaunch using the 'Run Configuration' button on the ConfigGUI.
