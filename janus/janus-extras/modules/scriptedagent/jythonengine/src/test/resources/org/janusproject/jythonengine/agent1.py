def liveAgent(agent) :
	if (agent.scriptedLiveExecuted) :
		agent.killMe()
		agent.scriptedKilledExecuted = True
	else :
		agent.scriptedLiveExecuted = True

