import java.util.*;

// Message class
class Message {
    private String sender;
    private List<String> recipients;
    private Date timestamp;
    private String content;

    public Message(String sender, List<String> recipients, String content) {
        this.sender = sender;
        this.recipients = recipients;
        this.timestamp = new Date();
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public MessageMemento saveToMemento() {
        return new MessageMemento(content, timestamp);
    }

    public void restoreFromMemento(MessageMemento memento) {
        this.content = memento.getContent();
        this.timestamp = memento.getTimestamp();
    }
}

// Memento MessageMemento class
class MessageMemento {
    private String content;
    private Date timestamp;

    public MessageMemento(String content, Date timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

// User class
class User implements Iterable<Message> {
    private String username;
    private ChatServer chatServer;
    private ChatHistory chatHistory;

    public User(String username, ChatServer chatServer) {
        this.username = username;
        this.chatServer = chatServer;
        this.chatHistory = new ChatHistory();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(List<String> recipients, String content) {
        chatServer.sendMessage(this, recipients, content);
    }

    public void receiveMessage(Message message) {
        chatHistory.addMessage(message);
    }

    public void undoLastMessage() {
        Message lastMessage = chatHistory.getLastMessage();
        if (lastMessage != null) {
            for (User user : chatServer.getUsers()) {
                user.getChatHistory().undoLastMessage();
            }
        }

    }

    public void blockUser(String username) {
        chatServer.blockUser(this, username);
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    @Override
    public Iterator<Message> iterator() {
        return chatHistory.iterator();
    }

    public Iterator<Message> searchMessagesByUser(String userToSearch) {
        return new SearchMessagesByUserIterator(chatHistory, userToSearch);
    }
}

// Mediator ChatServer class
class ChatServer {
    private Map<String, User> users;
    private Map<String, Set<String>> blockedUsers;

    public ChatServer() {
        this.users = new HashMap<>();
        this.blockedUsers = new HashMap<>();
    }

    public void registerUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void unregisterUser(User user) {
        users.remove(user.getUsername());
    }
    
    public Collection<User> getUsers() {
    	return users.values();
    }

    public void sendMessage(User sender, List<String> recipients, String content) {
        Message message = new Message(sender.getUsername(), recipients, content);
        for (String recipient : recipients) {
            if (!isBlocked(recipient, sender.getUsername())) {
                User recipientUser = users.get(recipient);
                if (recipientUser != null) {
                    recipientUser.receiveMessage(message);
                }
            }
        }
    }

    public void blockUser(User blocker, String usernameToBlock) {
        Set<String> blockedSet = blockedUsers.getOrDefault(blocker.getUsername(), new HashSet<>());
        blockedSet.add(usernameToBlock);
        blockedUsers.put(blocker.getUsername(), blockedSet);
    }

    private boolean isBlocked(String username, String sender) {
        Set<String> blockedSet = blockedUsers.get(username);
        return blockedSet != null && blockedSet.contains(sender);
    }
}

// ChatHistory class
class ChatHistory implements Iterable<Message> {
    private List<Message> messages;

    public ChatHistory() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void undoLastMessage() {
        if (!messages.isEmpty()) {
            messages.remove(messages.size() - 1);
        }
    }

    public Message getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.iterator();
    }
}

// Iterator class
class SearchMessagesByUserIterator implements Iterator<Message> {
    private Iterator<Message> iterator;
    private String userToSearch;

    public SearchMessagesByUserIterator(ChatHistory chatHistory, String userToSearch) {
        this.iterator = chatHistory.iterator();
        this.userToSearch = userToSearch;
    }

    @Override
    public boolean hasNext() {
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (message.getSender().equals(userToSearch) || message.getRecipients().contains(userToSearch)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Message next() {
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (message.getSender().equals(userToSearch) || message.getRecipients().contains(userToSearch)) {
                return message;
            }
        }
        return null;
    }
}