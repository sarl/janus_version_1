def activateAgent(agent) :
	agent.scriptedActivateExecuted = True

def liveAgent(agent) :
	if (agent.scriptedLiveExecuted) :
		agent.killMe()
		agent.scriptedKilledExecuted = True
	else :
		agent.scriptedLiveExecuted = True

def endAgent(agent) :
	agent.scriptedEndExecuted = True
