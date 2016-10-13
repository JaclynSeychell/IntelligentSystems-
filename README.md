# IntelligentSystems - Home Energy Trading System 


##Environment Setup (for Development)

1. Install Jade, Eclipse & Git

2. Using Git, download repo on local machine

3. Create a new Java project in Eclipse from the local repo folder, ensuring that the default project folder location also points directly to the repo folder


##Configuration Setup (for Running)

1. In eclipse create an initial run configuration to start the Jade GUI
* * arguments: -gui
	
2. Using the Jade GUI, start running a sniffer agent to track all actions of agents
* * Tools > Start Sniffer
* * Select "Do sniff this agent" for applicable agents

3. In eclipse, create a run configuration for a broker agent:
* * arguments: -agents b:agents.BrokerAgent
	
4. Create a run configuration for 3 retailer agents:
* * arguments: -agents r:agents.RetailerAgent
	
5. Create a run configuration for a home agent:
* * arguments: -agents h:agents.HomeAgent
	
6. Alternatively create a combined run configuration after step 2:
* * arguments: -agents b:agents.BrokerAgent;r:agents.RetailerAgent; h:agents.HomeAgent
