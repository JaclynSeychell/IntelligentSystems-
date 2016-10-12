# IntelligentSystems - Home Energy Trading System 


##Environment Setup (for Development)

1) Install Jade, Eclipse & Git

2) Using Git, download repo on local machine

3) Create a new Java project in Eclipse from the local repo folder, ensuring that the default project folder location also points directly to the repo folder


##Configuration Setup (for Running Partially)

1) In eclipse create an initial run configuration to start the Jade GUI
	-gui
	
2) Using the Jade GUI, start running a sniffer agent to track all actions of agents


3) In eclipse, create a run configuration for a broker agent:
	-agents b:agents.BrokerAgent
	
4) In eclipse, create a run configuration for 3 retailer agents:
	-agents r:agents.RetailerAgent
	
5) In eclipse, create a run configuration for a home agent:
	-agents h:agents.HomeAgent
	
6) ...