package intcode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Computer {
    private Map<Long, Long> memory = new HashMap<>();
    private long pc = 0L;
    private long rel = 0L;
    private IO io;

    public Computer(Program program, IO io) {
        this.io = io;
        for (int i = 0; i < program.getValues().size(); i++) {
            setMemory(i, program.getValues().get(i));
        }
    }

    public void runProgram() {
        Parameter[] parameters = new Parameter[3];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = new Parameter();
        }
        while (true) {
            long instruction = getMemory(pc++);
            if (instruction == 99) {
                break;
            }
            long opcode = instruction % 100;
            if (opcode == 1) {
                getParameters(parameters, 3, instruction);
                parameters[2].put(parameters[0].get() + parameters[1].get());
            } else if (opcode == 2) {
                getParameters(parameters, 3, instruction);
                parameters[2].put(parameters[0].get() * parameters[1].get());
            } else if (opcode == 3) {
                getParameters(parameters, 1, instruction);
                parameters[0].put(io.read());
            } else if (opcode == 4) {
                getParameters(parameters, 1, instruction);
                io.write(parameters[0].get());
            } else if (opcode == 5) {
                getParameters(parameters, 2, instruction);
                if (parameters[0].get() != 0) {
                    pc = parameters[1].get();
                }
            } else if (opcode == 6) {
                getParameters(parameters, 2, instruction);
                if (parameters[0].get() == 0) {
                    pc = parameters[1].get();
                }
            } else if (opcode == 7) {
                getParameters(parameters, 3, instruction);
                parameters[2].put(parameters[0].get() < parameters[1].get() ? 1 : 0);
            } else if (opcode == 8) {
                getParameters(parameters, 3, instruction);
                parameters[2].put(parameters[0].get() == parameters[1].get() ? 1 : 0);
            } else if (opcode == 9) {
                getParameters(parameters, 1, instruction);
                rel += parameters[0].get();
            } else {
                throw new IllegalArgumentException("Illegal opcode");
            }
        }
    }

    private void getParameters(Parameter[] parameters, int count, long instruction) {
        instruction /= 100;
        for (int i = 0; i < count; i++) {
            parameters[i].parameter = getMemory(pc++);
            parameters[i].mode = (int) (instruction % 10);
            instruction /= 10;
        }
    }

    public long getMemory(long address) {
        return memory.getOrDefault(address, 0L);
    }

    public void setMemory(long address, long value) {
        memory.put(address, value);
    }

    private class Parameter {
        long parameter;
        int mode;

        long get() {
            if (mode == 0) {
                return getMemory(parameter);
            } else if (mode == 1) {
                return parameter;
            } else if (mode == 2) {
                return getMemory(rel + parameter);
            } else {
                throw new IllegalArgumentException("Illegal read parameter mode");
            }
        }

        void put(long value) {
            if (mode == 0) {
                setMemory(parameter, value);
            } else if (mode == 2) {
                setMemory(rel + parameter, value);
            } else {
                throw new IllegalArgumentException("Illegal write parameter mode");
            }
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "parameter=" + parameter +
                    ", mode=" + mode +
                    '}';
        }
    }
}
