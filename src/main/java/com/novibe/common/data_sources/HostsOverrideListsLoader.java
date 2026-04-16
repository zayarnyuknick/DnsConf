package com.novibe.common.data_sources;

import com.novibe.common.util.DataParser;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    @Override
    protected String listType() {
        return "Override";
    }

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> DataParser.parseHostsLine(line)
                .map(DataParser.HostsLine::ip)
                .filter(ip -> !HostsBlockListsLoader.isBlockIp(ip))
                .isPresent();
    }

    @Override
    protected BypassRoute toObject(String line) {
        DataParser.HostsLine hostsLine = DataParser.parseHostsLine(line)
                .orElseThrow(() -> new IllegalArgumentException("Malformed hosts entry: " + line));
        return new BypassRoute(hostsLine.ip(), DataParser.removeWWW(hostsLine.domain()));
    }

}
