function activateAgent(agent)
{
	agent.scriptedActivateExecuted = true;
}

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

function endAgent(agent)
{
	agent.scriptedEndExecuted = true;
}
