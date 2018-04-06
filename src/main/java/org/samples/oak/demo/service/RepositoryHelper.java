package org.samples.oak.demo.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.samples.oak.demo.bean.AssetDetail;
import org.samples.oak.demo.bean.FileDetail;
import org.samples.oak.demo.bean.FileResponse;
import org.samples.oak.demo.type.NodeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;

public class RepositoryHelper {

    public static FileResponse getFileContents(Session session, String basePath, String fileName)
            throws RepositoryException, IOException {
        Node node = session.getNode(basePath);
        Node fileHolder = node.getNode(fileName);
        Node fileContent = fileHolder.getNode("theFile").getNode("jcr:content");
        Binary bin = fileContent.getProperty("jcr:data").getBinary();
        InputStream stream = bin.getStream();
        byte[] bytes = IOUtils.toByteArray(stream);
        bin.dispose();
        stream.close();

        FileResponse fileResponse = new FileResponse();
        fileResponse.setBytes(bytes);
        fileResponse.setContentType(fileContent.getProperty("jcr:mimeType").getString());
        return fileResponse;

    }

    public static List<AssetDetail> getAssets(Session session, String basePath) throws RepositoryException {
        List<AssetDetail> assets = Collections.emptyList();

        if (session.nodeExists(basePath)) {
            Node baseNode = session.getNode(basePath);
            NodeIterator iterator = baseNode.getNodes();
            if (iterator.getSize() > 0) {
                assets = new ArrayList<>((int) iterator.getSize());
                for (; iterator.hasNext();) {

                    Node node = iterator.nextNode();
                    AssetDetail asset = new AssetDetail();
                    String nodeType = node.getProperty("jcr:nodeType").getString();
                    asset.setAssetType(nodeType);

                    asset.setAssetName(node.getName()); // Set asset name
                    if (node.hasProperty("jcr:createdBy")) {
                        asset.setCreatedBy(node.getProperty("jcr:createdBy").getString()); // Set created by if present
                    }

                    if (NodeType.FILE == NodeType.toEnum(nodeType)) {
                        // FIXME add check for node exist
                        Node file = node.getNode("theFile");
                        Node content = file.getNode("jcr:content");
                        long size = content.getProperty("jcr:data").getLength();
                        asset.setSize(size);

                        asset.setCreatedOn(content.getProperty("jcr:lastModified").getString());
                    }
                    assets.add(asset);
                }
            }
        }
        return assets;
    }

    public static void addFileNode(Session session, String absPath, File file, String userName)
            throws RepositoryException, IOException {

        Node node = createNodes(session, absPath);
        if (node.hasNode(file.getName())) {
            System.out.println("File already added.");
            return;
        }

        Node fileHolder = node.addNode(file.getName()); // Created a node with that of file Name
        fileHolder.addMixin("mix:versionable");
        fileHolder.setProperty("jcr:createdBy", userName);
        fileHolder.setProperty("jcr:nodeType", NodeType.FILE.getValue());
        fileHolder.setProperty("size", file.length());

        Node file1 = fileHolder.addNode("theFile", "nt:file"); // create node of type file.

        Date now = new Date();
        now.toInstant().toString();

        Node content = file1.addNode("jcr:content", "nt:resource");
        String contentType = Files.probeContentType(file.toPath());
        content.setProperty("jcr:mimeType", contentType);
        FileInputStream is = new FileInputStream(file);
        Binary binary = session.getValueFactory().createBinary(is);

        content.setProperty("jcr:data", binary);
        content.setProperty("jcr:lastModified", now.toInstant().toString());
        session.save();
        VersionManager vm = session.getWorkspace().getVersionManager();
        vm.checkin(fileHolder.getPath());
        is.close();

        System.out.println("File Saved...");
    }

    public static void addFileNode(Session session, String absPath, FileDetail fileDetail)
            throws RepositoryException, IOException {
        // FIXME add null check for all incoming parameters
        // FIXME refactor this method to reduce duplicate codes
        Node node = createNodes(session, absPath);
        if (node.hasNode(fileDetail.getFileName())) {
            System.out.println("File already added.");
            return;
        }

        Node fileHolder = node.addNode(fileDetail.getFileName()); // Created a node with that of file Name
        fileHolder.addMixin("mix:versionable");
        fileHolder.setProperty("jcr:createdBy", fileDetail.getCreatedBy());
        fileHolder.setProperty("jcr:nodeType", NodeType.FILE.getValue());
        fileHolder.setProperty("size", fileDetail.getSize());

        Node file1 = fileHolder.addNode("theFile", "nt:file"); // create node of type file.

        Date now = new Date();
        now.toInstant().toString();

        Node content = file1.addNode("jcr:content", "nt:resource");
        content.setProperty("jcr:mimeType", fileDetail.getContentType());

        Binary binary = session.getValueFactory().createBinary(fileDetail.getFileData());

        content.setProperty("jcr:data", binary);
        content.setProperty("jcr:lastModified", now.toInstant().toString());
        session.save();
        VersionManager vm = session.getWorkspace().getVersionManager();
        vm.checkin(fileHolder.getPath());
        System.out.println("File Saved...");
    }

    public static void editFile(Session session, String absPath, FileDetail fileDetail)
            throws RepositoryException, IOException {
        // FIXME add null check for all incoming parameters
        // FIXME refactor this method to reduce duplicate codes
        if (session.nodeExists(absPath) && session.getNode(absPath).hasNode(fileDetail.getFileName())) {

            VersionManager vm = session.getWorkspace().getVersionManager();

            // FIXME make use of VersionManager
            Node node = session.getNode(absPath);
            Node fileHolder = node.getNode(fileDetail.getFileName());
            vm.checkout(fileHolder.getPath());

            fileHolder.setProperty("jcr:createdBy", fileDetail.getCreatedBy());
            fileHolder.setProperty("size", fileDetail.getSize());

            Node fileNode = fileHolder.getNode("theFile");

            Node content = fileNode.getNode("jcr:content");

            Binary binary = session.getValueFactory().createBinary(fileDetail.getFileData());
            content.setProperty("jcr:data", binary);
            session.save();
            // FIXME close resources with try-with-resource
            vm.checkin(fileHolder.getPath());
        } else {
            // FIXME created custom exceptions
            throw new RepositoryException("The path:" + absPath + "does not exist...");
        }
    }

    public static void editFile(Session session, String absPath, File file, String userName)
            throws RepositoryException, IOException {
        if (session.nodeExists(absPath) && session.getNode(absPath).hasNode(file.getName())) {

            VersionManager vm = session.getWorkspace().getVersionManager();

            // FIXME make use of VersionManager
            Node node = session.getNode(absPath);
            Node fileHolder = node.getNode(file.getName());
            vm.checkout(fileHolder.getPath());

            fileHolder.setProperty("jcr:createdBy", userName);
            fileHolder.setProperty("size", file.length());

            Node fileNode = fileHolder.getNode("theFile");

            Node content = fileNode.getNode("jcr:content");

            FileInputStream is = new FileInputStream(file);
            Binary binary = session.getValueFactory().createBinary(is);
            content.setProperty("jcr:data", binary);
            session.save();
            // FIXME close resources with try-with-resource
            vm.checkin(fileHolder.getPath());
            is.close();
        } else {
            // FIXME created custom exceptions
            throw new RepositoryException("The path:" + absPath + "does not exist...");
        }
    }

    public static Node createNodes(Session session, String absPath) throws RepositoryException {
        if (session.itemExists(absPath)) {
            System.out.println("Nodes already exist!!!");
            return session.getNode(absPath);
        }
        String[] nodeNames = (null != absPath) ? absPath.split("/") : null;
        Node node = createNodes(session, nodeNames);
        session.save();
        return node;
    }

    public static void deleteNode(Session session, String path, String nodeName)
            throws PathNotFoundException, RepositoryException {
        if (session.itemExists(path)) {
            System.out.println("Nodes already exist!!!");
            Node node = session.getNode(path);
            if (node.hasNode(nodeName)) {
                node.getNode(nodeName).remove();
                session.save();
                System.out.println("Node removed..");
            }
        }
    }

    public static void addProperty(Session session, String nodePath, String propertyName, String propertyValue)
            throws RepositoryException {
        // FIXME do null check for input params.
        Node node = session.getNode(nodePath);
        node.setProperty(propertyName, propertyValue);
        session.save();
    }

    private static Node createNodes(Session session, String[] nodes) throws RepositoryException {
        Node parentNode = session.getRootNode();
        for (String childNode : nodes) {
            if (StringUtils.isNotBlank(childNode)) {
                addChild(parentNode, childNode);
                parentNode = parentNode.getNode(childNode);
                parentNode.setProperty("jcr:nodeType", NodeType.FOLDER.getValue()); // set the node type
            }
        }
        return parentNode;

    }

    private static boolean addChild(Node parentNode, String childNode) throws RepositoryException {
        boolean nodeAdded = false;
        if (!parentNode.isNode()) {
            throw new RepositoryException("The parentNode does not exist..");
        }
        if (!parentNode.hasNode(childNode)) {
            parentNode.addNode(childNode);
            nodeAdded = true;
        }
        return nodeAdded;
    }
}
