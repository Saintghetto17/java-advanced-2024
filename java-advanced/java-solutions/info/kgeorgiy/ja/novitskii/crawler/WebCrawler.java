package info.kgeorgiy.ja.novitskii.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements NewCrawler {
    private final Downloader downloader;
    private final ExecutorService serviceDownloader;

    private final ExecutorService serviceExtractor;
    private int depth;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.serviceDownloader = Executors.newFixedThreadPool(downloaders);
        this.serviceExtractor = Executors.newFixedThreadPool(extractors);
    }

    private void extractOnDepth(Phaser ph, Document doc, ConcurrentLinkedQueue<String> nextLevel) {
        ph.register();
        this.serviceExtractor.execute(() -> {
            try {
                nextLevel.addAll(doc.extractLinks());
            } catch (IOException ignored) {
            } finally {
                ph.arrive();
            }
        });
    }

    private void downloadWithDepth(String url, Phaser ph,
                                   ConcurrentHashMap<String, IOException> errors, Set<String> downloaded,
                                   ConcurrentLinkedQueue<String> nextLevel, Set<String> excludes, int depthCurr) {
        ph.register();
        Runnable task = () -> {
            try {
                for (String substr : excludes) {
                    if (url.contains(substr)) {
                        return;
                    }
                }
                if (errors.containsKey(url) || !downloaded.add(url)) {
                    return;
                }
                Document doc = this.downloader.download(url);
                if (depthCurr == depth - 1) {
                    return;
                }
                extractOnDepth(ph, doc, nextLevel);
            } catch (IOException e) {
                errors.putIfAbsent(url, e);
                downloaded.remove(url);
            } finally {
                ph.arrive();
            }
        };
        serviceDownloader.execute(task);
    }

    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        // work with root url
        this.depth = depth;
        ConcurrentHashMap<String, IOException> errors = new ConcurrentHashMap<>();
        Set<String> downloaded = Collections.synchronizedSet(new HashSet<>());
        ConcurrentLinkedQueue<String> currentExtractedLinks = new ConcurrentLinkedQueue<>();
        currentExtractedLinks.add(url);
        for (int i = 0; i < depth; i++) {
            Phaser ph = new Phaser(1);
            ConcurrentLinkedQueue<String> nextLevel = new ConcurrentLinkedQueue<>();
            for (String urlCurr : currentExtractedLinks) {
                downloadWithDepth(urlCurr, ph, errors, downloaded, nextLevel, excludes, i);
            }
            ph.arriveAndAwaitAdvance();
            currentExtractedLinks = nextLevel;
        }
        return new Result(List.copyOf(downloaded), errors);
    }

    @Override
    public void close() {
        this.serviceDownloader.shutdown();
        this.serviceExtractor.shutdown();
        try {
            serviceDownloader.awaitTermination(1, TimeUnit.SECONDS);
            serviceExtractor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("args is null, so impossible to crawl");
            return;
        }
        if (args.length < 6) {
            System.err.println("Not enough parameters to start crawler");
        }
        String url = args[1];
        int depth = 0;
        int downloads = 0;
        int extractors = 0;
        int perHost = 0;
        try {
            depth = Integer.parseInt(args[2]);
            downloads = Integer.parseInt(args[3]);
            extractors = Integer.parseInt(args[4]);
            perHost = Integer.parseInt(args[5]);
        } catch (NumberFormatException ex) {
            System.err.println(ex.getLocalizedMessage() + ": parameters must have int type");
        }

        try (WebCrawler crawler = new WebCrawler(new CachingDownloader(1000), downloads, extractors, perHost)) {
            crawler.download(url, depth);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage() + ": creation of downloader failed");
        }
    }
}
