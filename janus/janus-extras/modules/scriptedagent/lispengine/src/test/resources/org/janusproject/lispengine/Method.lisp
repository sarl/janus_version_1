(defun printMyMsg(theObject theMessage)
	(print (jcall "buildMessage" theObject theMessage)))