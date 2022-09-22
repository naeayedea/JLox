package io.github.naeayedea.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Minify {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./src/io/github/naeayedea/data/statement.lox");
        Charset charset = StandardCharsets.UTF_8;

        String content = Files.readString(path, charset);
        content = content.replaceAll("\\s+", " ");
        System.out.println(content);
        Files.write(path, content.getBytes(charset));
    }
}
