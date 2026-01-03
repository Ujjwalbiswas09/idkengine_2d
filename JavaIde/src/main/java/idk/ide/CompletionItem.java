package idk.ide;

public interface CompletionItem {
    String getDisplayName();
    int getType();
    String getCompleteBeforeCursor();
    String getCompleteAfterCursor();
}
