import java.util.*;

public class MessageDriver {

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
        User user1 = new User("Michael", chatServer);
        User user2 = new User("Ashley", chatServer);
        User user3 = new User("Clay", chatServer);

        chatServer.registerUser(user1);
        chatServer.registerUser(user2);
        chatServer.registerUser(user3);

        // Users sending messages to either one or more people
        // Creating delays to simulate chat time
        try {
            user1.sendMessage(Arrays.asList("Ashley", "Clay"), "Hello Ashley and Clay");
            Thread.sleep(1000);
            
            user2.sendMessage(Collections.singletonList("Michael"), "Hi Michael");
            Thread.sleep(1000);

            user2.sendMessage(Collections.singletonList("Clay"), "Hey Clay");
            Thread.sleep(1000);
            
            user3.sendMessage(Collections.singletonList("Michael"), "Yo Michael");
            Thread.sleep(1000);
            
            user3.sendMessage(Collections.singletonList("Ashley"), "What's up Ashley");
            Thread.sleep(1000);
            
            user2.sendMessage(Arrays.asList("Michael", "Clay"), "Clay why is Michael messaging us...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Display full chat
        List<Message> allMessages = new ArrayList<>();
        for (User user : Arrays.asList(user1, user2, user3)) {
            for (Message message : user.getChatHistory()) {
                allMessages.add(message);
            }
        }
        
    	// Display full chat in chronological order
        Collections.sort(allMessages, Comparator.comparing(Message::getTimestamp));
        System.out.println("Chat App Log:");
        System.out.println("------------------------");
        for (Message message : allMessages) {
            System.out.println("[" + message.getTimestamp() + "] " + message.getSender() + ": " + message.getContent());
        }
        System.out.println();

        // Chat history
        System.out.println("Chat history:");
        System.out.println("------------------------");
        for (User user : Arrays.asList(user1, user2, user3)) {
            System.out.println("Chat history for " + user.getUsername() + ":");
            for (Message message : user.getChatHistory()) {
                System.out.println("[" + message.getTimestamp() + "] " + message.getSender() + ": " + message.getContent());
            }
            System.out.println();
        }

        // Undo last message
        user2.undoLastMessage();

        // Display updated chat after undo
        System.out.println("Updated chat after undo:");
        System.out.println("------------------------");
        for (User user : Arrays.asList(user1, user2, user3)) {
            System.out.println("Chat history for " + user.getUsername() + ":");
            for (Message message : user.getChatHistory()) {
                System.out.println("[" + message.getTimestamp() + "] " + message.getSender() + ": " + message.getContent());
            }
            System.out.println();
        }
        
        user2.sendMessage(Arrays.asList("Michael", "Clay"), "omg sorry I meant to send that to just Clay");
        // Block user2
        user1.blockUser("Ashley");
        user2.sendMessage(Arrays.asList("Michael", "Clay"), "Clay I think Michael blocked me...");
        user2.sendMessage(Arrays.asList("Michael", "Clay"), "Michael, hello?");
        user3.sendMessage(Arrays.asList("Michael", "Ashley"), "Michael did you block Ashley?");
        user1.sendMessage(Collections.singletonList("Clay"), "Yep");

        // Display updated chat after block
        System.out.println("Updated chat after block:");
        System.out.println("------------------------");
        for (User user : Arrays.asList(user1, user2, user3)) {
            System.out.println("Chat history for " + user.getUsername() + ":");
            for (Message message : user.getChatHistory()) {
                System.out.println("[" + message.getTimestamp() + "] " + message.getSender() + ": " + message.getContent());
            }
            System.out.println();
        }
	}
}
