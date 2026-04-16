package com.novibe.common.data_sources;

import com.novibe.common.util.DataParser;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class HostsBlockListsLoader extends ListLoader<String> {

    private static final String[] BLOCK_IPS = {"0.0.0.0", "127.0.0.1", "::1"};
    private static final String[] LOCALHOST_NAME = {"localhost", "ip6-localhost"};

    @Override
    protected String listType() {
        return "Block";
    }

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> DataParser.parseHostsLine(line)
                .filter(hostsLine -> isBlockIp(hostsLine.ip()))
                .filter(hostsLine -> !isLocalhost(hostsLine.domain()))
                .isPresent();
    }

    @Override
    protected String toObject(String line) {
        DataParser.HostsLine hostsLine = DataParser.parseHostsLine(line)
                .orElseThrow(() -> new IllegalArgumentException("Malformed hosts entry: " + line));
        return DataParser.removeWWW(hostsLine.domain());
    }

    public static boolean isBlock(String line) {
        return DataParser.parseHostsLine(line)
                .map(DataParser.HostsLine::ip)
                .filter(HostsBlockListsLoader::isBlockIp)
                .isPresent();
    }

    static boolean isBlockIp(String ip) {
        for (String blockIp : BLOCK_IPS) {
            if (blockIp.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLocalhost(String domain) {
        for (String localhost : LOCALHOST_NAME) {
            if (domain.equals(localhost))
                return true;
        }
        return false;
    }

}
