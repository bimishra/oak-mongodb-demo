package org.samples.oak.demo.service;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;

import java.net.UnknownHostException;

import javax.jcr.Repository;

public class RepositoryBuilder {

    public static Repository getRepo(String host, final int port) throws UnknownHostException {
        String uri = "mongodb://" + host + ":" + port;
        System.out.println(uri);
        System.setProperty("oak.documentMK.disableLeaseCheck", "true");
        DocumentNodeStore ns = new DocumentMK.Builder().setMongoDB(uri, "oak_demo", 16).getNodeStore();
        Repository repo = new Jcr(new Oak(ns)).createRepository();
        System.out.println("oak.documentMK.disableLeaseCheck="+System.getProperty("oak.documentMK.disableLeaseCheck"));
        return repo;
    }
}
