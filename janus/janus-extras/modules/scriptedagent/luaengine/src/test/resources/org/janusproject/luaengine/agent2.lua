function activateAgent(agent)
	agent.scriptedActivateExecuted = true
end

function liveAgent(agent)
	if (agent.scriptedLiveExecuted) then
		agent:killMe()
		agent.scriptedKilledExecuted = true
	else
		agent.scriptedLiveExecuted = true
	end
end

function endAgent(agent)
	agent.scriptedEndExecuted = true
end
