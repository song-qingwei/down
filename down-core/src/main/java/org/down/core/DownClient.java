package org.down.core;

import org.down.core.boot.HttpDownBootstrap;
import org.down.core.boot.UrlHttpDownBootstrapBuilder;
import org.down.core.dispatch.ConsoleHttpDownCallback;
import org.down.core.entity.HttpDownConfigInfo;
import org.down.core.entity.HttpResponseInfo;
import org.down.core.exception.BootstrapBuildException;
import org.down.core.exception.BootstrapResolveException;
import org.down.core.proxy.ProxyConfig;
import org.down.core.proxy.ProxyType;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * <p>{@link DownClient}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/4 14:01
 */
public class DownClient {

    public static void main(String[] args) {
        try {
            start(args);
        } catch (BootstrapBuildException e) {
            if (e.getCause() instanceof TimeoutException) {
                System.out.println("Connection failed, please check the network.");
            } else if (e.getCause() instanceof BootstrapResolveException) {
                System.out.println(e.getCause().getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }

    private static void start(String[] args) {
        HelpFormatter formatter = new HelpFormatter();
        Options options = build();
        if (Objects.isNull(args) || args.length <= 0) {
            formatter.printHelp("parse error", options);
            return;
        }
        if (args[0].trim().charAt(0) != '-' && !"-h".equals(args[0]) && !"--help".equals(args[0])) {
            args[0] = "-U=" + args[0];
        }
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                formatter.printHelp("down <url> <options>", options);
                return;
            }
            String url = line.getOptionValue("U");
            if (StringUtils.isBlank(url)) {
                formatter.printHelp("URL can't be empty", options);
                return;
            }
            UrlHttpDownBootstrapBuilder builder = HttpDownBootstrap.builder(line.getOptionValue("U"));
            String[] headsStr = line.getOptionValues("H");
            if (Objects.nonNull(headsStr) && headsStr.length > 0) {
                Map<String, String> heads = new LinkedHashMap<>();
                for (String headStr : headsStr) {
                    String[] headArray = headStr.split(":");
                    heads.put(headArray[0], headArray[1]);
                }
                builder.setHeads(heads);
            }
            builder.setBody(line.getOptionValue("B"));
            if (line.hasOption("N")) {
                builder.setResponse(new HttpResponseInfo(line.getOptionValue("N")));
            }
            HttpDownConfigInfo downConfig = new HttpDownConfigInfo()
                    .setFilePath(line.getOptionValue("P"))
                    .setConnections(Integer.parseInt(line.getOptionValue("C", "0")))
                    .setSpeedLimit(Integer.parseInt(line.getOptionValue("S", "0")));
            builder.setDownConfig(downConfig);
            String proxy = line.getOptionValue("X");
            if (StringUtils.isNotBlank(proxy)) {
                ProxyType proxyType = ProxyType.HTTP;
                String[] proxyArray;
                int protocolIndex = proxy.indexOf("://");
                if (protocolIndex != -1) {
                    proxyType = ProxyType.valueOf(proxy.substring(0, protocolIndex).toUpperCase());
                    proxyArray = proxy.substring(protocolIndex + 3).split(":");
                } else {
                    proxyArray = proxy.split(":");
                }
                builder.setProxyConfig(new ProxyConfig(proxyType, proxyArray[0], Integer.parseInt(proxyArray[1])));
            }
            HttpDownBootstrap bootstrap = builder.setCallback(new ConsoleHttpDownCallback()).build();
            bootstrap.start();
        } catch (ParseException e) {
            formatter.printHelp("Unrecognized option", options);
        }
    }

    private static Options build() {
        Options options = new Options();
        options.addOption(Option.builder("U")
                .longOpt("url")
                .hasArg()
                .desc("Set the request URL.")
                .build());
        options.addOption(Option.builder("H")
                .longOpt("heads")
                .hasArg()
                .desc("Set the HTTP request head<s>.")
                .build());
        options.addOption(Option.builder("B")
                .longOpt("body")
                .hasArg()
                .desc("Set the HTTP request body.")
                .build());
        options.addOption(Option.builder("P")
                .longOpt("path")
                .hasArg()
                .desc("Set download path.")
                .build());
        options.addOption(Option.builder("N")
                .longOpt("name")
                .hasArg()
                .desc("Set the name of the download file.")
                .build());
        options.addOption(Option.builder("C")
                .longOpt("connections")
                .hasArg()
                .desc("Set the number of download connections.")
                .build());
        options.addOption(Option.builder("S")
                .longOpt("speedLimit")
                .hasArg()
                .desc("Set download maximum speed limit(B/S).")
                .build());
        options.addOption(Option.builder("X")
                .longOpt("proxy")
                .hasArg()
                .desc("[protocol://]host:port Set proxy,support HTTP,SOCKS4,SOCKS5.")
                .build());
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("See the help.")
                .build());
        return options;
    }
}
