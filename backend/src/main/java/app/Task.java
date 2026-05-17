package app;

public class Task {
    private int id;
    private String title;
    private boolean completed;
    private String createdAt;

    public Task(int id, String title, boolean completed, String createdAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public String getCreatedAt() { return createdAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"title\":\"%s\",\"completed\":%b,\"createdAt\":\"%s\"}",
            id, escapeJson(title), completed, createdAt
        );
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
