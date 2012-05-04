require "java"

import "org.janusproject.kernel.message.Message";
import "org.janusproject.kernel.message.StringMessage";

def live(isMessageSended,theAgentOfTheDeathAddress,sender)
      if !isMessageSended
        mess=StringMessage.new("message sended");  
        sender.sendMessage(mess, theAgentOfTheDeathAddress);
        isMessageSended = true
      end
      if sender.getMailboxSize()>0
        puts sender.getMessage().toString();
        puts "ack received, commit suicide";
        sender.killMe();
    end
    return isMessageSended
end




