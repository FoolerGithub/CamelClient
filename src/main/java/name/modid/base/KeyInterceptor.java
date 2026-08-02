package name.modid.base;

public interface KeyInterceptor {
    /**
     * Called on every key press (action == 1) after module toggle matching.
     * No need to check {@code isEnabled()} — the caller does that.
     *
     * @param key GLFW key code that was pressed
     */
    void onKeyPress(int key);
}
