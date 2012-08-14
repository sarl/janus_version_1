function liveAgent(agent)
{
	if (agent.scriptedLiveExecuted == true) {
		agent.killMe();
		agent.scriptedKilledExecuted = true;
	}
	else {
		agent.scriptedLiveExecuted = true;		
	}
}
