def activateAgent(agent)
{
	agent.scriptedActivateExecuted = true
}

def liveAgent(agent)
{
	if (agent.scriptedLiveExecuted) {
		agent.killMe()
		agent.scriptedKilledExecuted = true
	}
	else {
		agent.scriptedLiveExecuted = true
	}
}

def endAgent(agent)
{
	agent.scriptedEndExecuted = true
}