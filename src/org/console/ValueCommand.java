package org.console;


/**
 * Created by Souverain73 on 10.03.2017.
 */
public abstract class ValueCommand<T> implements Console.ICommand{
    private String name;

    protected abstract T get();
    protected abstract String set(T data);
    protected abstract T parse(String... data);
    protected abstract String format(T data);

    public ValueCommand(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return "Комманда для установки значения";
    }

    @Override
    public String getDetailedInfo() {
        return "Комманда для утсановки значения. " +
                "Вызов комманды без агрументов вернет текущее значениею." +
                "Вызов с агрументами установит новое, если это возможно.";
    }

    @Override
    final public String run(String... args) {
        if (args.length == 1){
            return format(get());
        }else{
            T value = parse(args);
            if (value == null) return "Неверное значение для установки";
            return set(value);
        }
    }
}
