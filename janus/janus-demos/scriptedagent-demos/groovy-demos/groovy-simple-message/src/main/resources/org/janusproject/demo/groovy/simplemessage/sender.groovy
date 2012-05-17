package org.janusproject.demo.groovy.simplemessage

import org.janusproject.kernel.message.Message
import org.janusproject.kernel.message.StringMessage

def live(isMessageSended,theAgentOfTheDeathAddress,sender)
{
      if(!isMessageSended)
	  {
        mess = new StringMessage("message sended");  
        sender.sendMessage(mess, theAgentOfTheDeathAddress);
        isMessageSended = true
      }
      if(sender.getMailboxSize()>0)
	  {
        println sender.getMessage().toString();
        println "ack received, commit suicide";
        sender.killMe();
    }
    return isMessageSended
}




