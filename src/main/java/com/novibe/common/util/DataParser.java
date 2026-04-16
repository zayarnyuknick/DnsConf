package com.novibe.common.util;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DataParser {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public record HostsLine(String ip, String domain) {
    }

    public static String removeWWW(String domain) {
        if (domain.startsWith("www.")) {
            return domain.substring("www.".length());
        }
        return domain;
    }

    public static boolean isComment(String line) {
        return line.stripLeading().startsWith("#");
    }

    public static Optional<HostsLine> parseHostsLine(String line) {
        String sanitizedLine = stripInlineComment(line).strip();
        if (sanitizedLine.isBlank()) {
            return Optional.empty();
        }
        String[] columns = WHITESPACE.split(sanitizedLine, 3);
        if (columns.length < 2 || columns[1].isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new HostsLine(columns[0], columns[1]));
    }

    public static Stream<String> splitByEol(String data) {
        return Pattern.compile("\\r?\\n").splitAsStream(data);
    }

    private static String stripInlineComment(String line) {
        int commentIndex = line.indexOf('#');
        if (commentIndex >= 0) {
            return line.substring(0, commentIndex);
        }
        return line;
    }
}
