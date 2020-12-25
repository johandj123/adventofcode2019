package intcode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program {
    private List<Long> values = new ArrayList<>();

    public Program(String filename) throws IOException {
        String input = Files.readString(new File(filename).toPath());
        String[] parts = input.replace("\n", "").split(",");
        for (int i = 0; i < parts.length; i++) {
            values.add(Long.parseLong(parts[i]));
        }
    }

    public List<Long> getValues() {
        return Collections.unmodifiableList(values);
    }
}
