function activateAgent(agent)
{
	agent.scriptedActivateExecuted = true;
}

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

function endAgent(agent)
{
	agent.scriptedEndExecuted = true;
}
