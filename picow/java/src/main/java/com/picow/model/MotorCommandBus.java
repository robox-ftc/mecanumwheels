package com.picow.model;
import java.util.Map;
import java.util.concurrent.*;
import com.picow.model.commands.MotorCommand;

public class MotorCommandBus {
    public static final String ANTI_COLLISION = "AntiCollision";
    public static final String AUTONOMOUS = "Automous";
    public static final String GAMEPAD = "GamePad";
    public static final String KEYBOARD = "Keyboard";

    private final Map<String, MotorCommand> latestCommands = new ConcurrentHashMap<>();
    private static final Map<String, Integer> priorities = Map.of(
        GAMEPAD, 40,
        KEYBOARD, 30,
        ANTI_COLLISION, 20,
        AUTONOMOUS, 10
    );

    public MotorCommandBus(){

    }

    public void updateCommand(String source, MotorCommand command) {
        if (priorities.containsKey(source))
            latestCommands.put(source, command);
    }
    
    public void clearCommand(String source, MotorCommand command) {
        updateCommand(source, null);
    }

    public MotorCommand getHighestPriorityCommand(long timestamp) {
        return latestCommands.entrySet().stream()
        .filter(entry -> entry.getValue() != null) // skip null values
            .sorted((a, b) -> priorities.get(b.getKey()) - priorities.get(a.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(new MotorCommand(new int[]{0, 0, 0, 0}, timestamp));
    }
}
