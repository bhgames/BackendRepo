package BattlehardFunctions;

import java.util.ArrayList;

public class UserMessagePack {

	ArrayList<UserMessage> messages;

	public UserMessagePack() {
		messages = new ArrayList<UserMessage>();
	}

	public ArrayList<UserMessage> getMessages() {
		return messages;
	}

	public UserMessage getMessage(int index) {
		return messages.get(index);
	}
	public void addMessage(UserMessage message) {
		messages.add(message);
	}
	public int size() {
		return messages.size();
	}
	/*
	public void mergeMessage(UserMessage message) {
		int i = 0;UserMessage msg;
		while(i<getMessages().size()) {
			msg = getMessages().get(i);
			if(msg.getOriginalMessageID()==message.getOriginalMessageID()) {msg.addPidTo(message.getPidTo()[0]); break; }
			
			i++;
		}
	}*/
	
	
}
