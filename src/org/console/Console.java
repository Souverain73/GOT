package org.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.input.KeyCode.L;

/**
 * Created by Maksim on 20.02.2017.
 */
public class Console {
    public interface ICommand {
        String getName();
        String getInfo();
        String getDetailedInfo();
        String run(String ...args);
    }

    List<ICommand> commands;

    public Console() {
        commands = new ArrayList<>();

        addCommand(new ICommand() {
            @Override
            public String getName() {
                return "help";
            }

            @Override
            public String getInfo() {
                return "Справка по командам";
            }

            @Override
            public String getDetailedInfo() {
                return "Команда help отображает справку по командам консоли. Для детальной информации о команде наберите help [Имя команды]";
            }

            @Override
            public String run(String... args) {
                if (args.length == 2){
                    String commandName = args[1];
                    Optional<ICommand> command =  commands.stream().filter(cmd->cmd.getName().equals(commandName)).findFirst();

                    if (command.isPresent()){
                        return command.get().getDetailedInfo();
                    }else{
                        return "Команда " + commandName + " не найдена";
                    }
                }
                StringBuilder sb = new StringBuilder();
                for(ICommand cmd : commands){
                    sb.append(cmd.getName() + " - " + cmd.getInfo() + "\n");
                }
                return sb.toString();
            }
        });
    }

    public void addCommand(ICommand command) {
        commands.add(command);
    }

    public void start() {
        (new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String line = br.readLine();
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }

                    boolean executed = false;
                    String[] input = line.split("\\s");
                    for (ICommand cmd : commands) {
                        if (cmd.getName().equalsIgnoreCase(input[0])) {
                            executed = true;
                            try {
                                System.out.println(cmd.run(input));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("Недостаточно аргументов для выполнения команды");
                            }
                        }
                    }

                    if (!executed) {
                        System.out.println("Команда " + input[0] + " не найдена.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }
}
