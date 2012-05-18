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