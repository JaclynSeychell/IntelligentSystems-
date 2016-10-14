# IntelligentSystems - Home Energy Trading System 


##Environment Setup (for Development)

1. Install Jade, Eclipse & Git

2. Using Git, download repo on local machine

3. Create a new Java project in Eclipse from the local repo folder, ensuring that the default project folder location also points directly to the repo folder


##Configuration Setup (for Running)

1. In eclipse create an initial run configuration to start the Jade GUI
..* arguments: -gui

2. In eclipse, create a run configuration for a broker agent:
..* arguments: -container -container-name Brokers b:agents.BrokerAgent
	
3. Create a run configuration for 3 retailer agents:
..* arguments: -container -container-name Retailers r1:agents.RetailerAgent;r2:agents.RetailerAgent;r3:agents.RetailerAgent
	
4. Create a run configuration for a home agent:
..* arguments: -container -container-name Homes h:agents.HomeAgent
	
5. Alternatively create a combined run configuration after step 2:
..* arguments: -container -container-name Brokers b:agents.BrokerAgent -container -container-name Retailers r1:agents.RetailerAgent;r2:agents.RetailerAgent;r3:agents.RetailerAgent -container -container-name Homes h:agents.HomeAgent

6. Using the Jade GUI, start running a sniffer agent to track all actions of agents
..* Tools > Start Sniffer
..* Select "Do sniff this agent" for applicable containers of agents in the order of Home, Broker, then Retailers R1, R2 & R3
