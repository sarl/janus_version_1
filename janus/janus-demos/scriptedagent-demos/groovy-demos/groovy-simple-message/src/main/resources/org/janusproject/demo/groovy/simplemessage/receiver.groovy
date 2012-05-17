package org.janusproject.demo.groovy.simplemessage

import org.janusproject.kernel.message.Message
import org.janusproject.kernel.message.StringMessage

def live(receiver)
{
	if(receiver.getMailboxSize()>0)
	{
		m = receiver.getMessage();
		println m.toString();
		ack = new StringMessage("ack sended");
		receiver.sendMessage(ack, m.getSender());
		receiver.killMe();
	}
	
}