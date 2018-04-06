package org.samples.oak.demo.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.samples.oak.demo.bean.FileResponse;
import org.samples.oak.demo.bean.VersionHistory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

public class RepoHistoryHelper {

    public static void restoreVersion(Session session, String versionId)
            throws UnsupportedRepositoryOperationException, RepositoryException {
        VersionManager vm = session.getWorkspace().getVersionManager();
        Version version = (Version) session.getNodeByIdentifier(versionId);
        vm.restore(version, false);
        System.out.println("Version: " + versionId + " restored...");
    }

    public static void deleteVersionHistories(Session session, String path, String nodeName)
            throws PathNotFoundException, RepositoryException {
        if (session.itemExists(path)) {
            System.out.println("Nodes already exist!!!");
            Node node = session.getNode(path);
            if (node.hasNode(nodeName)) {
                VersionManager vm = session.getWorkspace().getVersionManager();
                vm.getVersionHistory(node.getNode(nodeName).getPath()).remove();
                session.save();
                System.out.println("Version removed removed..");
            }
        }
    }

    public static List<VersionHistory> getVersionHistory(Session session, String basePath, String itemName) {

        List<VersionHistory> versions = new ArrayList<>();
        try {
            VersionManager vm = session.getWorkspace().getVersionManager();

            String filePath = basePath + "/" + itemName;
            javax.jcr.version.VersionHistory versionHistory = vm.getVersionHistory(filePath);
            Version currentVersion = vm.getBaseVersion(filePath);

            VersionIterator itr = versionHistory.getAllVersions();

            @SuppressWarnings("unchecked")
            Iterable<Version> iterable = () -> itr;

            versions = StreamSupport.stream(iterable.spliterator(), false).map(i -> {
                try {
                    return getVersionHistory(i, currentVersion);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return versions;
    }

    private static VersionHistory getVersionHistory(Version version, Version currentVersion)
            throws RepositoryException {
        VersionHistory history = null;
        Node fileNode = version.getFrozenNode();
        if (fileNode.hasProperty("size")) {
            System.out.println("creating version...");
            history = new VersionHistory();
            history.setModifiedOn(version.getCreated().getTime());

            String identifier = version.getIdentifier();
            history.setIdentifier(identifier);

            history.setFileName(fileNode.getName());
            Node file = fileNode.getNode("theFile");

            Node content = file.getNode("jcr:content");
            history.setSize(content.getProperty("jcr:data").getLength());
            history.setContent(content);
            history.setModifiedBy(fileNode.getProperty("jcr:createdBy").getString());
            if (currentVersion.getIdentifier().equals(version.getIdentifier())) {
                history.setCurrent(true);
            }
        }
        return history;
    }

    public static FileResponse getVersion(Session session, String basePath, String fileName, String versionId)
            throws RepositoryException, IOException {
        Binary bin = null;
        String mimeType = null;
        byte[] bytes = null;
        List<VersionHistory> versions = getVersionHistory(session, basePath, fileName);
        VersionHistory versionHistory = versions.stream().filter(history -> history.getIdentifier().equals(versionId))
                .findFirst().orElse(null);
        if (versionHistory != null) {
            Node content = versionHistory.getContent();
            bin = content.getProperty("jcr:data").getBinary();
            mimeType = content.getProperty("jcr:mimeType").getString();
        }
        if (null != bin && StringUtils.isNotBlank(mimeType)) {
            InputStream stream = bin.getStream();
            bytes = IOUtils.toByteArray(stream);
            bin.dispose();
            stream.close();
        }

        FileResponse fileResponse = new FileResponse();
        fileResponse.setBytes(bytes);
        fileResponse.setContentType(mimeType);
        return fileResponse;

    }
}
