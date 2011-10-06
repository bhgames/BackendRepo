package BattlehardFunctions;

import java.util.ArrayList;

public class UserMessagePack {

	ArrayList<UserMessage> messages;

	public UserMessagePack() {
		messages = new ArrayList<UserMessage>();
	}

	/**
	 * 
	 * @return an arraylist of the messages this pack contains
	 */
	public ArrayList<UserMessage> getMessages() {
		return messages;
	}

	/**
	 * Gets the message at the specified index from this message pack
	 * 
	 * @param index the index of the message in this message pack
	 * 
	 * @return the message at the specified index.
	 */
	public UserMessage getMessage(int index) {
		return messages.get(index);
	}
	
	/**
	 * Adds a message to this message pack
	 * 
	 * @param message the message to add to this message pack
	 */
	public void addMessage(UserMessage message) {
		messages.add(message);
	}
	
	/**
	 * removes a message from this message pack
	 * 
	 * @param message the message to be removed from this message pack
	 */
	public void removeMessage(UserMessage message) {
		messages.remove(message);
	}
	
	/**
	 * 
	 * @return the number of messages in this pack
	 */
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
