function liveAgent(agent)
{
	if (agent.scriptedLiveExecuted) {
		agent.invoke("killMe");
		agent.scriptedKilledExecuted = true;
	}
	else {
		agent.scriptedLiveExecuted = true;
	}
}

