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

##Configuration Setup (for Running))

1. In eclipse create an initial run configuration to start GUII
..* class: TradingProgram
..* parameters: [none]]
2. The JADE GUI will also open. In the JADE GUI, start running a sniffer agent to track all actions of agentss
..* Tools > Start Snifferr
..* Select "Do sniff this agent" for applicable containers of agents in the order of Home, Broker, then Retailers R1, R2 & R33
