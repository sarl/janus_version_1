require "java"

import "org.janusproject.kernel.message.Message";
import "org.janusproject.kernel.message.StringMessage";

def live(receiver)
      if receiver.getMailboxSize()>0
        m = receiver.getMessage();
        puts m.toString();
        ack=StringMessage.new("ack sended");
        receiver.sendMessage(ack, m.getSender());
        receiver.killMe();
      end
end