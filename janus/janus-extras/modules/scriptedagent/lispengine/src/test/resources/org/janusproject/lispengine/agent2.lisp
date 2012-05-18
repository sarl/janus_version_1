(defun activateAgent(agent)
	(jfield "scriptedActivateExecuted" agent +TRUE+))

(defun liveAgent(agent)
	(if (jfield "scriptedLiveExecuted" agent)
	   	(cons (jcall "invoke" agent "killMe")
	   	      (jfield "scriptedKilledExecuted" agent +TRUE+))
	   	(jfield "scriptedLiveExecuted" agent +TRUE+)))

(defun endAgent(agent)
	(jfield "scriptedEndExecuted" agent +TRUE+))
