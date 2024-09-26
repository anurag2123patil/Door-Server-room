package logicBox;

public interface EventLock {
    void eventLockFree(boolean lockStatus, String accessLevel); //true new lock, false lock already assigned
}
