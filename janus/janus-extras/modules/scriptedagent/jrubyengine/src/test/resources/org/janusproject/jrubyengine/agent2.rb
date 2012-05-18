def activateAgent(agent)
	agent.scriptedActivateExecuted = true;
end

def liveAgent(agent)
	if (agent.scriptedLiveExecuted) then
		agent.killMe()
		agent.scriptedKilledExecuted = true;
	else
		agent.scriptedLiveExecuted = true;
	end
end

def endAgent(agent)
	agent.scriptedEndExecuted = true;
end
