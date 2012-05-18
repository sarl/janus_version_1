function liveAgent(agent)
	if (agent.scriptedLiveExecuted) then
		agent:killMe()
		agent.scriptedKilledExecuted = true
	else
		agent.scriptedLiveExecuted = true
	end
end

